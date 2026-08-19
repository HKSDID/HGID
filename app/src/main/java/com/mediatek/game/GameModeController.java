package com.mediatek.game;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import java.lang.reflect.Method;

public final class GameModeController {
    private static final String TAG = "GameModeController";

    // Enable aggressive high-refresh game mode (gameplay only)
    public static void enableGameMode(Activity activity) {
        if (activity == null) return;
        // Request the highest display mode (e.g., 120Hz)
        HighRefreshHelper.requestHighestRefreshRate(activity);

        // For API 33+, also set surface frame-rate if available
        if (Build.VERSION.SDK_INT >= 33) {
            try {
                Display display = activity.getWindow().getDecorView().getDisplay();
                Method getSurfaceMethod = Display.class.getMethod("getSurface");
                Surface surface = (Surface) getSurfaceMethod.invoke(display);
                if (surface != null) {
                    surface.setFrameRate(120f, Surface.FRAME_RATE_COMPATIBILITY_DEFAULT);
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to set surface frame rate", e);
            }
        }

        // Placeholder: vendor-specific performance toggles could be invoked here
        Log.i(TAG, "Game mode enabled (requested high refresh)");
    }

    // Disable/restore default behavior
    public static void disableGameMode(Activity activity) {
        if (activity == null) return;
        HighRefreshHelper.requestDefaultRefreshRate(activity);
        Log.i(TAG, "Game mode disabled (cleared preferred refresh)");
    }
}
