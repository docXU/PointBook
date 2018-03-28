package com.debug.xxw.pointbook.net;

import android.util.Log;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.File;
import java.net.URI;

/**
 * Created by xxw on 2017/12/18.
 */

public class QiniuUtils {

    private static String token = "";
    private int uploadComItemCount = 0;

    public synchronized void UploadCurItemComplete() {
        uploadComItemCount++;
    }

    public synchronized int getUploadComItemCount() {
        return uploadComItemCount;
    }

    public synchronized void UploadTaskComplete() {
        uploadComItemCount = 0;
    }

    public void uploadFile(String path, UpCompletionHandler upCompletionHandler) {
        if (token.equals(""))
            queryToken();

        Configuration config = new Configuration.Builder()
                .zone(FixedZone.zone2)
                .build();
        UploadManager uploadManager = new UploadManager(config);
        File data = new File(URI.create("file://" + path));
        uploadManager.put(data, null, token, upCompletionHandler, null);
    }

    public void queryToken() {
        String accessKey = "7CP2LqebbmZo8LkFkV51hypP3dLPa6-_jpkTgZwt";
        String secretKey = "k4f_rL4L4rwQu6bnvcoJxg70AXL2CnRpA3fwK9zW";
        String bucket = "pointbook1";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        token = upToken;
        Log.i("Qiniu", upToken);
    }

}
