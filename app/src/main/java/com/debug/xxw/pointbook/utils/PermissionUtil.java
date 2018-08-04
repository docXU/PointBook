package com.debug.xxw.pointbook.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.model.viewmodel.HintDialogFragment;

/**
 * Created by xxw on 2018/3/23.
 *
 * @author xxw
 * @date 2018/3/23.
 */

public class PermissionUtil {
    public static final int LOCATION_PERMISSION_CODE = 100;
    public static final int STORAGE_PERMISSION_CODE = 101;
    private static final String TAG = PermissionUtil.class.getName();

    public static boolean checkLocationPermission(Activity activity) {
        // 检查是否有定位权限
        // 检查权限的方法: ContextCompat.checkSelfPermission()两个参数分别是Context和权限名.
        // 返回PERMISSION_GRANTED是有权限，PERMISSION_DENIED没有权限
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //没有权限，向系统申请该权限。
            Log.i(TAG, "没有权限");
            requestPermission(activity, LOCATION_PERMISSION_CODE);
            return false;
        }

        return true;
    }

    public static void checkStoragePermission(Activity activity) {
        // 检查是否有存储的读写权限
        // 检查权限的方法: ContextCompat.checkSelfPermission()两个参数分别是Context和权限名.
        // 返回PERMISSION_GRANTED是有权限，PERMISSION_DENIED没有权限
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有权限，向系统申请该权限。
            Log.i(TAG, "没有权限");
            requestPermission(activity, STORAGE_PERMISSION_CODE);
        }
    }

    private static void requestPermission(Activity activity, int permissioncode) {
        String permission = getPermissionString(permissioncode);
        if (!isEmptyOrNullString(permission)) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                if (permissioncode == LOCATION_PERMISSION_CODE) {
                    DialogFragment newFragment = HintDialogFragment.newInstance(R.string.location_description_title,
                            R.string.location_description_why_we_need_the_permission,
                            permissioncode);
                    newFragment.show(activity.getFragmentManager(), HintDialogFragment.class.getSimpleName());
                } else if (permissioncode == STORAGE_PERMISSION_CODE) {
                    DialogFragment newFragment = HintDialogFragment.newInstance(R.string.storage_description_title,
                            R.string.storage_description_why_we_need_the_permission,
                            permissioncode);
                    newFragment.show(activity.getFragmentManager(), HintDialogFragment.class.getSimpleName());
                }


            } else {
                Log.i(TAG, "返回false 不需要解释为啥要权限，可能是第一次请求，也可能是勾选了不再询问");
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission}, permissioncode);
            }
        }
    }

    public static String getPermissionString(int requestCode) {
        String permission = "";
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
            case STORAGE_PERMISSION_CODE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            default:
                break;
        }
        return permission;
    }


    public static boolean isEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }
}
