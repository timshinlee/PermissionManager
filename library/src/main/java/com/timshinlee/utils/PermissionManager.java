package com.timshinlee.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private static final String TAG = "PermissionManager";
    private static PermissionManager instance;
    private PermissionCallback callback;
    private boolean debugMode;

    private PermissionManager() {
    }

    public static PermissionManager getInstance(PermissionCallback callback) {
        synchronized (PermissionManager.class) {
            if (instance == null) {
                instance = new PermissionManager();
            }
            instance.callback = callback;
        }
        return instance;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * @return whether need to request the permission or not.
     */
    public boolean requestPermissionIfNeeded(Activity activity, String permission, int requestCode) {
        return requestPermissionIfNeeded(activity, new String[]{permission}, requestCode);
    }

    /**
     * @return whether need to request permissions or not.
     */
    public boolean requestPermissionIfNeeded(Activity activity, String[] permissions, int requestCode) {
        List<String> needToPermits = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                logE("permission need to request : " + permission);
                needToPermits.add(permission);
            }
        }
        if (needToPermits.size() <= 0) {
            if (callback != null) {
                callback.onAllGranted();
            }
            return false;
        }
        ActivityCompat.requestPermissions(activity, needToPermits.toArray(new String[]{}), requestCode);
        return true;
    }

    public interface PermissionCallback {
        void onAllGranted();

        void onDenied(String[] deniedPermissions);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length <= 0) {
            logE("request has been cancelled");
            return;
        }
        if (callback == null) {
            logE("onRequestPermissionsResult: no callback specified");
            return;
        }
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            logE("onRequestPermissionsResult: permissions = " + permissions[i]);
            logE("onRequestPermissionsResult: grantResult = " + (grantResults[i] == PackageManager.PERMISSION_GRANTED));

            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        if (deniedPermissions.size() > 0) {
            callback.onDenied(deniedPermissions.toArray(new String[]{}));
        } else {
            callback.onAllGranted();
        }
    }

    private void logE(String content) {
        if (debugMode) {
            Log.e(TAG, content);
        }
    }
}
