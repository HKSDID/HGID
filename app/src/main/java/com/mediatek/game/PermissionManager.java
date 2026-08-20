package com.mediatek.game;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * PermissionManager - Runtime permissions handling
 * Manages requesting and checking permissions required for monitoring features
 */
public class PermissionManager {
    private static final String TAG = "PermissionManager";
    
    /**
     * Permissions required for full functionality
     */
    public static final String[] REQUIRED_PERMISSIONS = {
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.INTERNET,
    };
    
    /**
     * Optional permissions for enhanced monitoring
     */
    public static final String[] OPTIONAL_PERMISSIONS = {
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_WIFI_STATE,
    };
    
    /**
     * Request code for permission requests
     */
    public static final int PERMISSION_REQUEST_CODE = 100;
    
    /**
     * Check if all required permissions are granted
     */
    public static boolean hasAllRequiredPermissions(Context context) {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Missing permission: " + permission);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check specific permission
     */
    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Request required permissions
     */
    public static void requestRequiredPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] missingPermissions = getMissingPermissions(activity, REQUIRED_PERMISSIONS);
            
            if (missingPermissions.length > 0) {
                Log.i(TAG, "Requesting " + missingPermissions.length + " required permissions");
                ActivityCompat.requestPermissions(
                    activity,
                    missingPermissions,
                    PERMISSION_REQUEST_CODE
                );
            } else {
                Log.d(TAG, "All required permissions already granted");
            }
        }
    }
    
    /**
     * Request optional permissions
     */
    public static void requestOptionalPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] missingPermissions = getMissingPermissions(activity, OPTIONAL_PERMISSIONS);
            
            if (missingPermissions.length > 0) {
                Log.i(TAG, "Requesting " + missingPermissions.length + " optional permissions");
                ActivityCompat.requestPermissions(
                    activity,
                    missingPermissions,
                    PERMISSION_REQUEST_CODE + 1
                );
            }
        }
    }
    
    /**
     * Get array of missing permissions
     */
    private static String[] getMissingPermissions(Context context, String[] permissions) {
        java.util.List<String> missingList = new java.util.ArrayList<>();
        
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingList.add(permission);
            }
        }
        
        return missingList.toArray(new String[0]);
    }
    
    /**
     * Handle permission request results
     */
    public static void handlePermissionResult(
            int requestCode,
            String[] permissions,
            int[] grantResults,
            PermissionResultListener listener) {
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                Log.i(TAG, "All required permissions granted");
                if (listener != null) {
                    listener.onPermissionsGranted(true);
                }
            } else {
                Log.w(TAG, "Some required permissions denied");
                if (listener != null) {
                    listener.onPermissionsGranted(false);
                }
            }
        }
    }
    
    /**
     * Permission result callback interface
     */
    public interface PermissionResultListener {
        void onPermissionsGranted(boolean granted);
    }
    
    /**
     * Get human-readable permission name
     */
    public static String getPermissionName(String permission) {
        switch (permission) {
            case android.Manifest.permission.ACCESS_NETWORK_STATE:
                return "Network State Access";
            case android.Manifest.permission.INTERNET:
                return "Internet Access";
            case android.Manifest.permission.READ_PHONE_STATE:
                return "Phone State Access";
            case android.Manifest.permission.ACCESS_WIFI_STATE:
                return "WiFi State Access";
            default:
                return permission;
        }
    }
}
