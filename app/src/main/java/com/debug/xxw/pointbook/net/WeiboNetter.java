package com.debug.xxw.pointbook.net;

import android.content.Context;
import android.content.Entity;
import android.util.Log;

import com.debug.xxw.pointbook.activity.Activity_editor;
import com.debug.xxw.pointbook.model.Weibo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xxw on 2017/10/20.
 */

public class WeiboNetter {
    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext;

    private final String GETURL = "weibo/get";
    private final String ADDURL = "weibo/add";

    public WeiboNetter(Context c) { mContext  = c;}

    public void queryWeiboList(String markerId, RequestManager.ReqCallBack c){
        HashMap<String, String> params = new HashMap<>();
        params.put("entryId", markerId);

        RequestManager.getInstance(mContext).requestAsyn(GETURL,RequestManager.TYPE_GET, params, c);
    }

    public void addWeibo(String entryId, Weibo weibo, RequestManager.ReqCallBack c){
        HashMap<String, String> params = new HashMap<>();
        params.put("entryId", entryId);
        params.put("username", weibo.getUsername());
        params.put("headimgurl", "");
        params.put("content", weibo.getContent());
        params.put("fromwhere", weibo.getFrom());
        params.put("picCount", weibo.getContentImgs().urlList.size()+"");

        for(Map.Entry<Integer,String> keyvalue : weibo.getContentImgs().remoteUrlList.entrySet()){
            params.put("pic"+keyvalue.getKey(), keyvalue.getValue());
            Log.i(TAG, keyvalue.getValue());
        }

        RequestManager.getInstance(mContext).requestAsyn(ADDURL,RequestManager.TYPE_GET,params, c);
    }
}
