package com.debug.xxw.pointbook.net;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.debug.xxw.pointbook.model.ReportPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Exchanger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xxw on 2017/10/20.
 */

public class MarkerNetter {

    private String TAG = "apiReq";
    RequestManager.ReqCallBack mGetCallback;
    RequestManager.ReqCallBack mAddCallback;
    private Context mContext;

    public MarkerNetter(Context c) {
        mContext = c;
    }

    public void setMarkerRequestCallBack(RequestManager.ReqCallBack c) {
        mGetCallback = c;
    }

    public void setMarkerAddCallBack(RequestManager.ReqCallBack c) {
        mAddCallback = c;
    }

    public void queryMarker(Location location, int scope) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat", String.valueOf(location.getLatitude()));
        params.put("lon", String.valueOf(location.getLongitude()));
        params.put("scope", String.valueOf(scope));

        RequestManager.getInstance(mContext).requestAsyn(ConstURL.MARKER_GET, RequestManager.TYPE_GET, params, mGetCallback);
    }

    public void addMarker(ReportPoint rp) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat", String.valueOf(rp.getLocation().latitude));
        params.put("lon", String.valueOf(rp.getLocation().longitude));
        params.put("title", String.valueOf(rp.getTitle()));

        RequestManager.getInstance(mContext).requestAsyn(ConstURL.MARKER_ADD, RequestManager.TYPE_GET, params, mAddCallback);
    }
}
