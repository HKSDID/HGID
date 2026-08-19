package com.mediatek.game;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class HighRefreshHelper {
    private static final String TAG = "HighRefreshHelper";

    // Request the highest refresh-rate mode supported by the display.
    public static void requestHighestRefreshRate(Activity activity) {
        if (activity == null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG, "Display mode selection requires API >= M");
            return;
        }

        try {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Display.Mode[] modes = display.getSupportedModes();
            if (modes == null || modes.length == 0) {
                Log.i(TAG, "No supported display modes found");
                return;
            }

            Display.Mode best = modes[0];
            for (Display.Mode m : modes) {
                if (m.getRefreshRate() > best.getRefreshRate()) {
                    best = m;
                }
            }

            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.preferredDisplayModeId = best.getModeId();
            window.setAttributes(lp);

            Log.i(TAG, "Requested display mode id=" + best.getModeId() +
                    " refresh=" + best.getRefreshRate());
        } catch (Exception e) {
            Log.w(TAG, "Failed to request display mode", e);
        }
    }

    // Clear preference to let the system choose the default display mode.
    public static void requestDefaultRefreshRate(Activity activity) {
        if (activity == null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.preferredDisplayModeId = 0; // let system choose default
            window.setAttributes(lp);
            Log.i(TAG, "Cleared preferred display mode");
        } catch (Exception e) {
            Log.w(TAG, "Failed to clear display mode", e);
        }
    }
}
