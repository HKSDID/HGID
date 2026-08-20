package com.mediatek.game;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime Permission Helper - Manages Android runtime permissions
 * Requests and checks for required permissions for monitoring features
 */
public class RuntimePermissionHelper {
    private static final String TAG = "RuntimePermissionHelper";
    private static final int PERMISSION_REQUEST_CODE = 9001;
    
    // Required permissions for monitoring features
    private static final String[] REQUIRED_PERMISSIONS = {
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.BATTERY_STATS,
    };
    
    public interface PermissionCallback {
        void onPermissionsGranted(String[] permissions);
        void onPermissionsDenied(String[] permissions);
    }
    
    private static PermissionCallback callback;
    
    public static void setPermissionCallback(PermissionCallback cb) {
        callback = cb;
    }
    
    /**
     * Request all required permissions
     */
    public static void requestAllPermissions(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "Activity is null");
            return;
        }
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG, "Runtime permissions not required (API < 23)");
            if (callback != null) {
                callback.onPermissionsGranted(new String[0]);
            }
            return;
        }
        
        List<String> permissionsToRequest = new ArrayList<>();
        for (String perm : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(perm);
            }
        }
        
        if (!permissionsToRequest.isEmpty()) {
            Log.i(TAG, "Requesting " + permissionsToRequest.size() + " permissions");
            ActivityCompat.requestPermissions(activity,
                permissionsToRequest.toArray(new String[0]),
                PERMISSION_REQUEST_CODE);
        } else {
            Log.i(TAG, "All permissions already granted");
            if (callback != null) {
                callback.onPermissionsGranted(REQUIRED_PERMISSIONS);
            }
        }
    }
    
    /**
     * Check single permission
     */
    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Check if all required permissions are granted
     */
    public static boolean hasAllPermissions(Context context) {
        for (String perm : REQUIRED_PERMISSIONS) {
            if (!hasPermission(context, perm)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Handle permission result (call from Activity.onRequestPermissionsResult)
     */
    public static void handlePermissionResult(int requestCode, String[] permissions,
            int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CODE) {
            return;
        }
        
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(permissions[i]);
                Log.i(TAG, "Permission granted: " + permissions[i]);
            } else {
                denied.add(permissions[i]);
                Log.w(TAG, "Permission denied: " + permissions[i]);
            }
        }
        
        if (callback != null) {
            if (!granted.isEmpty()) {
                callback.onPermissionsGranted(granted.toArray(new String[0]));
            }
            if (!denied.isEmpty()) {
                callback.onPermissionsDenied(denied.toArray(new String[0]));
            }
        }
    }
}
