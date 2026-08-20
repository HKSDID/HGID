package com.mediatek.game;

import android.app.Application;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class GameApplication extends Application implements ActivityLifecycleCallbacks {
    private static final String TAG = "GameApplication";
    private String gameplayActivityName;

    // Track foreground/background
    private int startedActivityCount = 0;
    private boolean isChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                gameplayActivityName = ai.metaData.getString("com.mediatek.game.GAMEPLAY_ACTIVITY", null);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to read meta-data", e);
        }
        if (gameplayActivityName != null && gameplayActivityName.startsWith(".")) {
            gameplayActivityName = getPackageName() + gameplayActivityName;
        }
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (gameplayActivityName != null && activity.getClass().getName().equals(gameplayActivityName)) {
            GameModeController.enableGameMode(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // Keep this for quick pause handling; major cleanup happens on background transition
        if (gameplayActivityName != null && activity.getClass().getName().equals(gameplayActivityName)) {
            GameModeController.disableGameMode(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        startedActivityCount++;
        isChangingConfigurations = activity.isChangingConfigurations();
        if (startedActivityCount == 1) {
            // App moved to foreground
            Log.d(TAG, "App entered foreground");
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        startedActivityCount = Math.max(0, startedActivityCount - 1);
        isChangingConfigurations = activity.isChangingConfigurations();
        if (startedActivityCount == 0 && !isChangingConfigurations) {
            // App moved to background — perform full cleanup
            Log.d(TAG, "App entered background — running full game-mode cleanup");
            GameModeController.onAppBackgrounded(getApplicationContext());
        }
    }

    // Other ActivityLifecycleCallbacks methods
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
