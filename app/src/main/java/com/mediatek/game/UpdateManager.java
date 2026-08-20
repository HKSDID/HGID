package com.mediatek.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Update Manager - Handles real-time updates and feature state management
 * Manages app version, feature updates, and live metric updates
 */
public class UpdateManager {
    private static final String TAG = "UpdateManager";
    private static UpdateManager instance;
    private static final String PREFS_KEY = "update_manager_prefs";
    private static final String KEY_APP_VERSION = "app_version";
    private static final String KEY_LAST_UPDATE = "last_update_time";
    private static final String KEY_FEATURES_ENABLED = "features_enabled";
    
    private Context context;
    private SharedPreferences prefs;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<UpdateListener> updateListeners = new ArrayList<>();
    
    public interface UpdateListener {
        void onUpdateAvailable(UpdateInfo info);
        void onUpdateInstalled(String version);
        void onFeatureToggled(String feature, boolean enabled);
    }
    
    public static class UpdateInfo {
        public String version;
        public String description;
        public long releaseTime;
        public boolean isForced;
        public String downloadUrl;
        public int buildNumber;
        
        @Override
        public String toString() {
            return "Update v" + version + " (build " + buildNumber + "): " + description;
        }
    }
    
    public static synchronized UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }
    
    public void init(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        Log.i(TAG, "UpdateManager initialized");
    }
    
    public void addUpdateListener(UpdateListener listener) {
        if (!updateListeners.contains(listener)) {
            updateListeners.add(listener);
        }
    }
    
    public void removeUpdateListener(UpdateListener listener) {
        updateListeners.remove(listener);
    }
    
    /**
     * Get current app version
     */
    public String getCurrentVersion() {
        try {
            return context.getPackageManager().getPackageInfo(
                context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            Log.w(TAG, "Failed to get version", e);
            return "Unknown";
        }
    }
    
    /**
     * Get current build number
     */
    public int getCurrentBuildNumber() {
        try {
            return context.getPackageManager().getPackageInfo(
                context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            Log.w(TAG, "Failed to get build number", e);
            return 0;
        }
    }
    
    /**
     * Check for updates (would typically call a remote server)
     */
    public void checkForUpdates() {
        new Thread(() -> {
            try {
                // Simulate update check
                String currentVersion = getCurrentVersion();
                long lastUpdate = prefs.getLong(KEY_LAST_UPDATE, 0);
                long now = System.currentTimeMillis();
                
                // Only check if last check was > 1 hour ago
                if (now - lastUpdate > 3600000) {
                    Log.i(TAG, "Checking for updates...");
                    prefs.edit().putLong(KEY_LAST_UPDATE, now).apply();
                    
                    // Here you would typically:
                    // 1. Connect to update server
                    // 2. Parse update metadata
                    // 3. Notify listeners
                    notifyUpdateAvailable(new UpdateInfo());
                }
            } catch (Exception e) {
                Log.w(TAG, "Update check failed", e);
            }
        }).start();
    }
    
    /**
     * Toggle feature state and notify listeners
     */
    public void toggleFeature(String featureName, boolean enabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("feature_" + featureName, enabled);
        editor.apply();
        Log.i(TAG, "Feature " + featureName + " toggled: " + enabled);
        
        for (UpdateListener listener : updateListeners) {
            mainHandler.post(() -> listener.onFeatureToggled(featureName, enabled));
        }
    }
    
    /**
     * Check if feature is enabled
     */
    public boolean isFeatureEnabled(String featureName) {
        return prefs.getBoolean("feature_" + featureName, false);
    }
    
    /**
     * Get last update time
     */
    public long getLastUpdateTime() {
        return prefs.getLong(KEY_LAST_UPDATE, 0);
    }
    
    /**
     * Notify all listeners of available update
     */
    private void notifyUpdateAvailable(UpdateInfo info) {
        for (UpdateListener listener : updateListeners) {
            mainHandler.post(() -> listener.onUpdateAvailable(info));
        }
    }
}
