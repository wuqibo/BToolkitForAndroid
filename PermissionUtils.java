package com.wuqibo.bppcallbackservice.btoolkit;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    public interface Callback {
        void permissionGranted();
    }

    public static String[] Permissions = null;

    /**
     * 如果没有获得权限，则执行权限申请，授权结果需要重写Activity的onRequestPermissionsResult()方法监听
     * 如果已经通过Manifest.xml文件或者前一次已授权的，permissionGrantedCallback回调将被调用，Activity不再回调onRequestPermissionsResult()方法
     * requestCode可自定义，当Activity的onRequestPermissionsResult()回调时，用于判断匹配
     */
    public static void requestPermission(Activity activity, int requestCode, Callback permissionGrantedCallback) {

        if (Permissions == null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                Permissions = new String[]{
                        Manifest.permission.FOREGROUND_SERVICE, //FOREGROUND_SERVICE Added in API level 28 Android 9.0
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_SMS
                };
            } else {
                Permissions = new String[]{
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_SMS
                };
            }
        }

        if (checkPermissions(activity, Permissions)) {
            if (permissionGrantedCallback != null) {
                permissionGrantedCallback.permissionGranted();
            }
        } else {
            List<String> needPermissions = getDeniedPermissions(activity, Permissions);
            ActivityCompat.requestPermissions(activity, needPermissions.toArray(new String[needPermissions.size()]), requestCode);
        }
    }

    private static boolean checkPermissions(Activity activity, String[] permissions) {
        //SDK低于23 ，在Manifest上注册有效，大于23的(android6.0以后的)，读取手机的隐私需要在代码动态申请
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static List<String> getDeniedPermissions(Activity activity, String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            // 检查权限,如果没有授权就添加
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                needRequestPermissionList.add(permission);
            }
        }
        return needRequestPermissionList;
    }

    public static boolean checkPermissionGranted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }
}
