package com.debug.xxw.pointbook.net;

import android.content.Context;
import android.location.Location;

import com.debug.xxw.pointbook.model.ReportPoint;

import java.util.HashMap;

/**
 * Created by xxw on 2017/10/20.
 */

public class MarkerNetter {

    RequestManager.ReqCallBack mGetCallback;
    RequestManager.ReqCallBack mAddCallback;
    private String TAG = "apiReq";
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
