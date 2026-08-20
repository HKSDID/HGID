package com.mediatek.game;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import java.lang.reflect.Method;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.app.NotificationManager;
import android.content.SharedPreferences;

public final class GameModeController {
    private static final String TAG = "GameModeController";
    private static PowerManager.WakeLock sWakeLock = null;
    private static final int GAME_NOTIFICATION_ID = 1001; // if used elsewhere

    // Enable aggressive high-refresh game mode (gameplay only)
    public static void enableGameMode(Activity activity) {
        if (activity == null) return;

        // Respect user preference for background running
        SharedPreferences prefs = activity.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        boolean allowBackground = prefs.getBoolean("pref_allow_background_running", false);

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

        // Acquire a temporary wake lock while in foreground if needed
        acquireWakeLockIfNeeded(activity.getApplicationContext());

        // Only start long-running background services if user allowed it
        if (allowBackground) {
            Log.i(TAG, "Allow background running is enabled — background services may be started if needed");
            // If you have a GameBackgroundService, start it here. Example (uncomment and replace):
            // Intent svc = new Intent(activity, GameBackgroundService.class);
            // activity.startForegroundService(svc);
        } else {
            Log.i(TAG, "Background running is disabled by preference — will not start background services");
        }

        // Persist that game mode is active
        prefs.edit().putBoolean("game_mode_active", true).apply();

        // Placeholder: vendor-specific performance toggles could be invoked here
        Log.i(TAG, "Game mode enabled (requested high refresh)");
    }

    // Disable/restore default behavior
    public static void disableGameMode(Activity activity) {
        if (activity == null) return;
        HighRefreshHelper.requestDefaultRefreshRate(activity);
        releaseWakeLock();
        persistGameEndedState(activity);
        Log.i(TAG, "Game mode disabled (cleared preferred refresh)");
    }

    // Called when whole app enters background
    public static void onAppBackgrounded(Context context) {
        Log.i(TAG, "onAppBackgrounded: performing full cleanup");
        releaseWakeLock();
        stopBackgroundServices(context);
        cancelGameNotifications(context);
        stopScheduledTasks();
        persistGameEndedState(context);
    }

    private static void acquireWakeLockIfNeeded(Context context) {
        try {
            if (sWakeLock == null) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (pm != null) {
                    sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG + ":wake");
                    sWakeLock.setReferenceCounted(false);
                    sWakeLock.acquire(10 * 60 * 1000L /*10 minutes timeout*/);
                    Log.d(TAG, "WakeLock acquired with timeout");
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to acquire wake lock", e);
        }
    }

    private static void releaseWakeLock() {
        try {
            if (sWakeLock != null && sWakeLock.isHeld()) {
                sWakeLock.release();
                sWakeLock = null;
                Log.d(TAG, "WakeLock released");
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to release wake lock", e);
        }
    }

    private static void stopBackgroundServices(Context context) {
        try {
            // If your app has a specific long-running service, stop it here.
            // Since there's no explicit service in the manifest, this is a no-op fallback.
            // Example (uncomment and replace if you add a service):
            // Intent svc = new Intent(context, GameBackgroundService.class);
            // context.stopService(svc);
            Log.d(TAG, "stopBackgroundServices: no explicit services to stop (no-op)");
        } catch (Exception e) {
            Log.w(TAG, "Failed to stop background services", e);
        }
    }

    private static void cancelGameNotifications(Context context) {
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                try {
                    nm.cancel(GAME_NOTIFICATION_ID);
                } catch (Exception ignored) {}
                // As a last resort, cancel all app notifications relating to game (use with caution)
                // nm.cancelAll();
                Log.d(TAG, "Canceled game notifications (if any)");
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to cancel game notifications", e);
        }
    }

    private static void stopScheduledTasks() {
        try {
            // If your app uses Executors or Timers for periodic work, shut them down here.
            Log.d(TAG, "stopScheduledTasks: no scheduled tasks known (no-op)");
        } catch (Exception e) {
            Log.w(TAG, "Failed to stop scheduled tasks", e);
        }
    }

    private static void persistGameEndedState(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("game_mode_active", false).apply();
        } catch (Exception e) {
            Log.w(TAG, "Failed to persist game ended state", e);
        }
    }
}
