package com.debug.xxw.pointbook.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Poi;
import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.map.MapController;
import com.debug.xxw.pointbook.utils.ElasticOutInterpolator;
import com.debug.xxw.pointbook.utils.PermissionUtil;
import com.debug.xxw.pointbook.viewmodel.HintDialogFragment;

/**
 * @author xxw
 */
public class MainActivity extends AppCompatActivity implements HintDialogFragment.DialogFragmentCallback {

    MapController mMapController;
    MapView mMapView;
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        //初始化地图视图和地图交互事件
        mMapView = (MapView) findViewById(R.id.map);
        mMapController = new MapController(getApplicationContext(), this, mMapView, savedInstanceState);
        mMapController.getAmap().setOnPOIClickListener(new AMap.OnPOIClickListener() {
            @Override
            public void onPOIClick(Poi poi) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, FeedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("entry_id", poi.getPoiId());
                bundle.putString("name", poi.getName());
                bundle.putString("type", "poi");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

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

    class ButtonDropDownAnimationListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mFab.setVisibility(View.GONE);
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(i);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
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
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (null != mFab && mFab.getVisibility() == View.GONE) {
            mFab.setVisibility(View.VISIBLE);
            ValueAnimator anim = ObjectAnimator.ofFloat(mFab, "translationY", mFab.getHeight()*2, 0);
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
}
