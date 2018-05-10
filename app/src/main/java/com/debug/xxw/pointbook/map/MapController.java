package com.debug.xxw.pointbook.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.debug.xxw.pointbook.activity.MainActivity;
import com.debug.xxw.pointbook.map.cluster.ClusterClickListener;
import com.debug.xxw.pointbook.map.cluster.ClusterItem;
import com.debug.xxw.pointbook.map.cluster.ClusterOverlay;
import com.debug.xxw.pointbook.map.cluster.ClusterRender;
import com.debug.xxw.pointbook.activity.FeedActivity;
import com.debug.xxw.pointbook.model.RegionItem;
import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.model.ReportPoint;
import com.debug.xxw.pointbook.model.Tag;
import com.debug.xxw.pointbook.net.MarkerNetter;
import com.debug.xxw.pointbook.net.RequestManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * @author xxw
 * @date 2017/10/20
 */

public class MapController implements ClusterRender, ClusterClickListener {

    private AMap mAMap;
    private MarkerNetter mMarkerNetter;

    private Map<Integer, Drawable> mBackDrawAbles = new HashMap<>();
    private ClusterOverlay mClusterOverlay;
    private ClusterOverlay searchResultOverlay;
    private final Context mainContext;
    private Activity mainActivity;
    private float clusterRadius = 35;
    /**
     * 活动范围（公里）
     */
    private int activityScope = 10;
    private LatLng mLocation;

    private Marker reportMarker;
    private LinearLayout rbl;
    private Circle reportRegion;
    private ReportPoint mReportPoint;
    //只是为了应用首开的时候调整镜头至合适大小。
    private boolean needMoveToCenter = true;
    private Location lastLocation = null;

    @SuppressLint("SdCardPath")
    public MapController(Context mMainContext, Activity mMainActivity, MapView mv, Bundle savedInstanceState) {
        mainActivity = mMainActivity;
        mainContext = mMainContext;
        mMarkerNetter = new MarkerNetter(mainContext);
        mv.onCreate(savedInstanceState);
        mAMap = mv.getMap();
        mAMap.setCustomMapStylePath("/sdcard/Pointbook/custom_map");
        mAMap.setMapCustomEnable(true);

        rbl = mainActivity.findViewById(R.id.report_button_Layout);
        rbl.setVisibility(View.INVISIBLE);

        listenerInit();
    }

    private void listenerInit() {
        Button reportSubmit = mainActivity.findViewById(R.id.reportSubmit);
        Button reportCancel = mainActivity.findViewById(R.id.cancle);
        reportSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDialogHidden();
                inputTitleDialog();
            }
        });
        reportCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReportPoint = null;
                reportMarker.remove();
                reportMarker = null;
                reportDialogHidden();
                Toast.makeText(mainContext, "你取消了申报", Toast.LENGTH_SHORT).show();
            }
        });

        //初始化地图交互事件
        getAmap().setOnPOIClickListener(new AMap.OnPOIClickListener() {
            @Override
            public void onPOIClick(Poi poi) {
                Intent intent = new Intent();
                intent.setClass(mainActivity, FeedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("entry_id", poi.getPoiId());
                bundle.putString("name", poi.getName());
                bundle.putString("type", "poi");
                intent.putExtras(bundle);
                mainActivity.startActivity(intent);
            }
        });

        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                remenberReportLocation(cameraPosition.target);
                if (null != mClusterOverlay) {
                    mClusterOverlay.assignClusters();
                }
            }
        });

        mAMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (null == reportMarker) {
                    if (mReportPoint == null) {
                        mReportPoint = new ReportPoint();
                    }
                    addMarkerInScreenCenter();
                    reportDialogShow();
                }
                Toast.makeText(mainContext, "申请活动点开始...", Toast.LENGTH_SHORT).show();
            }
        });

        mAMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (mMarkerNetter == null) return;
                mLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (needMoveToCenter) {
                    needMoveToCenter = false;
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 14f));
                    mMarkerNetter.queryMarker(location, activityScope);
                }

                if (reportRegion != null) {
                    reportRegion.setCenter(mLocation);
                } else {
                    reportRegion = mAMap.addCircle(new CircleOptions().center(mLocation).radius(1000 * activityScope).strokeWidth(5));
                }
                //当两次定位距离大于50m的时候重新拉取数据
                if (lastLocation != null && location.distanceTo(lastLocation) > 50) {
                    //发起异步请求获取周围的活动点集
                    mMarkerNetter.queryMarker(location, activityScope);
                }

                lastLocation = location;
            }
        });

        // 网络
        mMarkerNetter.setMarkerRequestCallBack(new RequestManager.ReqCallBack() {
            @Override
            public void onReqSuccess(Object result) {
                List<ClusterItem> markerList = parseResultToMarkerList(result);
                if (null == mClusterOverlay) {
                    mClusterOverlay = new ClusterOverlay(mAMap, markerList,
                            dp2px(mainContext, clusterRadius),
                            mainContext);
                    mClusterOverlay.setClusterRenderer(MapController.this);
                    mClusterOverlay.setOnClusterClickListener(MapController.this);
                } else {
                    mClusterOverlay.setmClusterItems(markerList);
                    mClusterOverlay.assignClusters();
                }
            }

            /***
             * 转换服务器的返回结果为可聚合点集
             * @param result json字符串
             * @return 聚合点集
             */
            private List<ClusterItem> parseResultToMarkerList(Object result) {
                JSONArray ja = null;
                try {
                    ja = new JSONArray((String) result);
                } catch (JSONException je) {
                    Log.e(TAG, "违法的json返回字符串");
                }

                List<ClusterItem> items = new ArrayList<>();

                if (ja != null) {
                    int itemCount = ja.length();
                    for (int i = 0; i < itemCount; i++) {
                        try {
                            JSONObject jo = ja.getJSONObject(i);

                            double lat = jo.getDouble("lat");
                            double lon = jo.getDouble("lon");
                            String sid = jo.getString("sid");
                            String title = jo.getString("title");

                            //获取tag列表
                            List<Tag> tags = new LinkedList<>();
                            JSONArray tagArray = jo.getJSONArray("tags");
                            int tagSize = tagArray.length();
                            for (int j = 0; j < tagSize; j++) {
                                JSONObject tag = tagArray.getJSONObject(j);
                                Tag tagIns = new Tag().setTid(tag.getInt("id"))
                                        .setMid(tag.getString("mid"))
                                        .setContent(tag.getString("content"))
                                        .setUid(tag.getInt("uid"));
                                tags.add(tagIns);
                            }

                            LatLng latLng = new LatLng(lat, lon, false);
                            RegionItem regionItem = new RegionItem(sid, latLng, title);
                            regionItem.setTags(tags);

                            items.add(regionItem);
                        } catch (JSONException je) {
                            Log.e(TAG, "json对象不存在某个键");
                        }
                    }
                }

                return items;
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.e("queryMakerCallback", errorMsg);
            }
        });

        mMarkerNetter.setMarkerAddCallBack(new RequestManager.ReqCallBack() {
            @Override
            public void onReqSuccess(Object result) {

                Toast.makeText(mainContext, "上报直接通过~", Toast.LENGTH_LONG).show();
                if (mClusterOverlay != null) {
                    mClusterOverlay.addClusterItem(new RegionItem((String) result, mReportPoint.getLocation(), mReportPoint.getTitle()));
                }
                reportMarker.remove();
                reportMarker = null;
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(mainContext, "无法连接网络...请稍后重试", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void beginLocation() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.radiusFillColor(Color.parseColor("#3F6bbbec"))
                .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mainContext.getResources(), R.drawable.navi_map_gps_locked)));
        mAMap.setMyLocationStyle(myLocationStyle);
        // 设置默认定位按钮是否显示，非必需设置。
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.setMyLocationEnabled(true);
    }

    /**
     * 刷新Markers
     */
    public void refreshMarkers() {
        if (lastLocation != null) {
            mMarkerNetter.queryMarker(lastLocation, activityScope);
        }
    }

    /**
     * 添加选点marker
     */
    private void addMarkerInScreenCenter() {
        LatLng latLng = mAMap.getCameraPosition().target;
        Point screenPosition = mAMap.getProjection().toScreenLocation(latLng);
        reportMarker = mAMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
        //设置Marker在屏幕上,不跟随地图移动
        reportMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
    }

    private void reportDialogShow() {
        rbl.setVisibility(View.VISIBLE);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setInterpolator(AnimationUtils
                .loadInterpolator(mainContext,
                        android.R.anim.accelerate_interpolator));
        rbl.startAnimation(translateAnimation);

    }

    private void reportDialogHidden() {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setInterpolator(AnimationUtils
                .loadInterpolator(mainContext,
                        android.R.anim.accelerate_interpolator));
        rbl.startAnimation(translateAnimation);
        rbl.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ClusterItem clusterItem : clusterItems) {
            LatLng p = clusterItem.getPosition();
            //LatLng p1 = new LatLng((center.latitude * 2) - p.latitude, (center.longitude * 2) - p.longitude);
            builder.include(p);
            //builder.include(p1);
        }
        LatLngBounds latLngBounds = builder.build();
        if (clusterItems.size() > 1) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
        } else {
            goToWeibo((RegionItem) clusterItems.get(0));
        }

    }

    /**
     * 传递marker的id给微博窗口由它发起请求获取微博列表
     *
     * @param item
     */
    private void goToWeibo(RegionItem item) {
        //todo: 检测点的空属性
        Intent intent = new Intent();
        intent.setClass(mainActivity, FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("entry_id", item.getId());
        bundle.putString("name", item.getTitle());
        bundle.putString("type", "marker");
        bundle.putSerializable("tags", (Serializable) item.getTags());
        intent.putExtras(bundle);

        mainActivity.startActivityForResult(intent, 0);
    }

    public void onDestroy() {
        if (getmClusterOverlay() != null) {
            getmClusterOverlay().onDestroy();
        }
        mBackDrawAbles.clear();
        if (reportMarker != null) {
            reportMarker.remove();
        }
        reportMarker = null;
        mMarkerNetter = null;
        System.gc();
    }

    /**
     * 显示搜索结果图层
     *
     * @param list
     */
    public void openSearchResultOverlay(List<ClusterItem> list) {
        if (null != searchResultOverlay) {
            searchResultOverlay.onDestroy();
        }

        //镜头移动
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng center = new LatLng(mAMap.getMyLocation().getLatitude(), mAMap.getMyLocation().getLongitude());
        builder.include(center);
        for (ClusterItem clusterItem : list) {
            LatLng p = clusterItem.getPosition();
            builder.include(p);
        }
        LatLngBounds latLngBounds = builder.build();
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));

        searchResultOverlay = new ClusterOverlay(mAMap, list,
                dp2px(mainContext, clusterRadius),
                mainContext);
        searchResultOverlay.setClusterRenderer(this);
        searchResultOverlay.setOnClusterClickListener(this);
    }

    /**
     * 关闭搜索结果图层
     */
    public void closeSearchResultOverlay() {
        if (searchResultOverlay != null) {
            searchResultOverlay.onDestroy();
        }
    }


    //********************************工具函数************************************//

    private void inputTitleDialog() {
        final EditText input = new EditText(mainActivity);
        input.setFocusable(true);

        new AlertDialog.Builder(mainActivity)
                //提示框标题
                .setTitle("填写申报点的地址（15字以内）")
                .setView(input)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mReportPoint.setTitle(input.getText().toString());
                        mMarkerNetter.addMarker(mReportPoint);
                    }
                })
                .setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mReportPoint = null;
                        reportMarker.remove();
                        reportMarker = null;
                    }
                })
                .create()
                .show();
    }

    private void remenberReportLocation(LatLng target) {
        if (reportMarker != null) {
            if (reportRegion != null) {
                if (reportRegion.contains(target)) {
                    mReportPoint.setLocation(target);
                } else {
                    Toast.makeText(mainActivity, "微调距离不可超过" + activityScope * 1000 + "米", Toast.LENGTH_SHORT).show();
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 13f));
                }
            } else {
                beginLocation();
                Toast.makeText(mainActivity, "重新定位中。。。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 聚合点的样式
     *
     * @param clusterNum
     * @return
     */
    @Override
    public Drawable getDrawAble(int clusterNum) {
        final int onlyOne = 1;
        final int notTooMuch = 5;
        final int soMuch = 10;
        int radius = dp2px(mainContext, 60);
        if (clusterNum == onlyOne) {
            Drawable bitmapDrawable = mBackDrawAbles.get(1);
            if (bitmapDrawable == null) {
                bitmapDrawable =
                        mainContext.getResources().getDrawable(
                                R.drawable.icon_openmap_mark_small);
                mBackDrawAbles.put(1, bitmapDrawable);
            }
            return bitmapDrawable;
        } else if (clusterNum < notTooMuch) {
            Drawable bitmapDrawable = mBackDrawAbles.get(2);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(159, 210, 154, 6)));
                mBackDrawAbles.put(2, bitmapDrawable);
            }
            return bitmapDrawable;
        } else if (clusterNum < soMuch) {
            Drawable bitmapDrawable = mBackDrawAbles.get(3);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(199, 217, 114, 0)));
                mBackDrawAbles.put(3, bitmapDrawable);
            }
            return bitmapDrawable;
        } else {
            Drawable bitmapDrawable = mBackDrawAbles.get(4);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(235, 215, 66, 2)));
                mBackDrawAbles.put(4, bitmapDrawable);
            }
            return bitmapDrawable;
        }
    }

    private Bitmap drawCircle(int radius, int color) {

        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        RectF rectF = new RectF(0, 0, radius * 2, radius * 2);
        paint.setColor(color);
        canvas.drawArc(rectF, 0, 360, true, paint);
        return bitmap;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public AMap getAmap() {
        return mAMap;
    }

    public void setAmap(AMap amap) {
        this.mAMap = amap;
    }

    public ClusterOverlay getmClusterOverlay() {
        return mClusterOverlay;
    }
}
