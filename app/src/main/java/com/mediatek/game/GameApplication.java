package com.mediatek.game;

import android.app.Application;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class GameApplication extends Application implements ActivityLifecycleCallbacks {
    private static final String TAG = "GameApplication";
    private String gameplayActivityName;

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
        if (gameplayActivityName != null && activity.getClass().getName().equals(gameplayActivityName)) {
            GameModeController.disableGameMode(activity);
        }
    }

    // Other ActivityLifecycleCallbacks methods (no-op)
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivityStarted(Activity activity) {}
    @Override public void onActivityStopped(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
