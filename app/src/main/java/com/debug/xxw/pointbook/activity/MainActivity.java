package com.debug.xxw.pointbook.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.MapView;
import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.map.MapController;
import com.debug.xxw.pointbook.map.cluster.ClusterItem;
import com.debug.xxw.pointbook.map.cluster.ClusterOverlay;
import com.debug.xxw.pointbook.model.RegionItem;
import com.debug.xxw.pointbook.model.Tag;
import com.debug.xxw.pointbook.model.User;
import com.debug.xxw.pointbook.utils.ElasticOutInterpolator;
import com.debug.xxw.pointbook.utils.PermissionUtil;
import com.debug.xxw.pointbook.model.viewmodel.HintDialogFragment;
import com.debug.xxw.pointbook.model.viewmodel.RecommendSearchFragment;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xxw
 */
public class MainActivity extends AppCompatActivity implements HintDialogFragment.DialogFragmentCallback, Toolbar.OnMenuItemClickListener, IOnSearchClickListener {

    //TODO：静态用户对象保持会话，SharePrefrence本地持久登录信息
    public static User user = null;
    public static boolean anonymity_me = false;
    Toolbar toolbar;
    MapController mMapController;
    MapView mMapView;
    FloatingActionButton mFab;
    RecommendSearchFragment searchFragment;
    RelativeLayout searchStatusBar;
    private boolean closeOverlay = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //读取本地用户信息
        SharedPreferences sp = getSharedPreferences("config", 0);
        user = User.getUserSingleton(sp);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        searchFragment = new RecommendSearchFragment();
        toolbar.setOnMenuItemClickListener(this);
        searchFragment.setOnSearchClickListener(this);

        //隐藏搜索状态栏事件初始化
        searchStatusBar = (RelativeLayout) findViewById(R.id.search_status_bar);
        (searchStatusBar.getChildAt(1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapController.closeSearchResultOverlay();
                mMapController.getmClusterOverlay().showOverlay();
                searchStatusBar.setVisibility(View.GONE);
            }
        });

        mMapView = (MapView) findViewById(R.id.map);
        mMapController = new MapController(getApplicationContext(), this, mMapView, savedInstanceState);

        //权限请求
        if (PermissionUtil.checkLocationPermission(this)) {
            mMapController.beginLocation();
        }
        PermissionUtil.checkStoragePermission(this);
        //TODO:异步等待网络连接恢复后重启定位
        //TODO:缓存放在设置界面清楚

        mFab = (FloatingActionButton) this.findViewById(R.id.more_fab);
        if (mFab == null) {
            throw new AssertionError("初始化more_fab失败");
        }
        //mFab.setBackgroundDrawable(new LetterDrawable("Q"));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValueAnimator anim = ObjectAnimator.ofFloat(view, "translationY", 0, view.getHeight() * 2);
                anim.setDuration(200);
                anim.setInterpolator(new DecelerateInterpolator());
                anim.addListener(new ButtonDropDownAnimationListener());
                anim.start();
            }
        });
    }

    private void ShowAndSetSearchBar(String keyword) {
        searchStatusBar.setVisibility(View.VISIBLE);
        TextView tv = (TextView) searchStatusBar.getChildAt(0);
        tv.setText("正在浏览：" + keyword + " 相关.");
    }

    @Override
    public void OnSearchClick(String keyword) {

        //todo: 显示相关的cluster，做出状态栏表示当前搜索词。
        if (mMapController.getmClusterOverlay() == null) {
            Toast.makeText(MainActivity.this, "网络出错，请稍后再试~", Toast.LENGTH_LONG).show();
            return;
        }
        List<ClusterItem> clusters = mMapController.getmClusterOverlay().getmClusterItems();
        List<ClusterItem> filteredClusters = new LinkedList<>();

        for (ClusterItem ci : clusters) {
            RegionItem ri = (RegionItem) ci;

            //先搜索一下title，相关则加入
            if (ri.getTitle().contains(keyword)) {
                filteredClusters.add((RegionItem) ri.clone());
                continue;
            }

            List<Tag> tags = ri.getTags();
            if (tags != null) {
                for (Tag tag : tags) {
                    if (tag.getContent().contains(keyword)) {
                        filteredClusters.add((ClusterItem) ri.clone());
                    }
                }
            }
        }

        if (0 == filteredClusters.size()) {
            Toast.makeText(MainActivity.this, "没有相关内容 试试别的吧.", Toast.LENGTH_LONG).show();
        } else {
            ShowAndSetSearchBar(keyword);
            mMapController.getmClusterOverlay().hiddenOverlay();
            mMapController.openSearchResultOverlay(filteredClusters);
        }
        //TODO:上传用户搜素信息

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                searchFragment.show(getSupportFragmentManager(), SearchFragment.TAG);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.LOCATION_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("MY", "定位权限已获取");
                    mMapController.beginLocation();
                } else {
                    Toast.makeText(MainActivity.this, "你不给定位权限...那就都别玩了", Toast.LENGTH_SHORT).show();
                    Log.i("MY", "定位权限被拒绝");
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        DialogFragment newFragment = HintDialogFragment.newInstance(R.string.location_description_title,
                                R.string.location_description_why_we_need_the_permission,
                                requestCode);
                        newFragment.show(getFragmentManager(), HintDialogFragment.class.getSimpleName());
                        Log.i("MY", "false 勾选了不再询问，并引导用户去设置中手动设置");
                        return;
                    }
                }
                return;
            }
            case PermissionUtil.STORAGE_PERMISSION_CODE: {
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "存储权限被拒绝", Toast.LENGTH_SHORT).show();
                    Log.i("MY", "定位权限被拒绝");
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        DialogFragment newFragment = HintDialogFragment.newInstance(R.string.storage_description_title,
                                R.string.storage_description_why_we_need_the_permission,
                                requestCode);
                        newFragment.show(getFragmentManager(), HintDialogFragment.class.getSimpleName());
                        Log.i("MY", "false 勾选了不再询问，并引导用户去设置中手动设置");
                    }
                    return;
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null && requestCode == 100) {
            Bundle bundle = data.getExtras();
            closeOverlay = bundle.getBoolean("closeOverlay");
            ClusterOverlay ins = mMapController.getmClusterOverlay();
            if (ins == null) {
                return;
            }
            if (closeOverlay) {
                ins.hiddenOverlay();
            } else {
                ins.showOverlay();
            }
        }
        mMapController.refreshMarkers();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void doPositiveClick(int requestCode) {
        String permission = PermissionUtil.getPermissionString(requestCode);
        if (!PermissionUtil.isEmptyOrNullString(permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{permission},
                        requestCode);
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }

    @Override
    public void doNegativeClick(int requestCode) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onBackPressed() {
        //TODO：需连按两次返回建才可退出
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (null != mFab && mFab.getVisibility() == View.GONE) {
            mFab.setVisibility(View.VISIBLE);
            ValueAnimator anim = ObjectAnimator.ofFloat(mFab, "translationY", mFab.getHeight() * 2, 0);
            anim.setDuration(500);
            anim.setInterpolator(new ElasticOutInterpolator());
            anim.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁资源
        mMapController.onDestroy();
    }

    class ButtonDropDownAnimationListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mFab.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            bundle.putBoolean("closeOverlay", closeOverlay);
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            i.putExtras(bundle);
            startActivityForResult(i, 100);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
