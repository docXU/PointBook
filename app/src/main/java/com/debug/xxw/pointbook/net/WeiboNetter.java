package com.debug.xxw.pointbook.net;

import android.content.Context;
import android.util.Log;

import com.debug.xxw.pointbook.model.Weibo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xxw on 2017/10/20.
 */

public class WeiboNetter {

    //字段
    public static String like_counter = "likecount";
    public static String low_counter = "lowcount";
    public static String collect_counter = "commentcount";

    public static void queryWeiboList(Context mContext, String markerId, RequestManager.ReqCallBack c) {
        HashMap<String, String> params = new HashMap<>();
        params.put("entryId", markerId);

        RequestManager.getInstance(mContext).requestAsyn(ConstURL.WEIBO_GET, RequestManager.TYPE_GET, params, c);
    }

    public static void addWeibo(Context mContext, String entryId, Weibo weibo, RequestManager.ReqCallBack c) {
        HashMap<String, String> params = new HashMap<>();
        params.put("entryId", entryId);
        params.put("username", weibo.getUsername());
        params.put("uid", weibo.getUserId());
        params.put("headimg", weibo.getHeadimg());
        params.put("content", weibo.getContent());
        params.put("picCount", weibo.getContentImgs().urlList.size() + "");

        for (Map.Entry<Integer, String> keyvalue : weibo.getContentImgs().remoteUrlList.entrySet()) {
            params.put("pic" + keyvalue.getKey(), keyvalue.getValue());
            Log.i("WeiboNetter", keyvalue.getValue());
        }

        RequestManager.getInstance(mContext).requestAsyn(ConstURL.WEIBO_ADD, RequestManager.TYPE_GET, params, c);
    }

    public static void incCounter(Context mContext, String counter_type, String weibo_id, RequestManager.ReqCallBack c) {
        HashMap<String, String> params = new HashMap<>();
        params.put("wid", weibo_id);
        params.put("counter_type", counter_type);

        RequestManager.getInstance(mContext).requestAsyn(ConstURL.WEIBO_COUNTER, RequestManager.TYPE_GET, params, c);
    }
}
