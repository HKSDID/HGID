package com.mediatek.game;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Surface;
import android.view.Display;
import java.lang.reflect.Method;
import android.graphics.Color;
import android.view.View;
import android.util.Log;

public final class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        // Ensure the window background and status bar are dark so UI isn't all white.
        // This keeps things simple without adding AppCompat.
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                getWindow().setStatusBarColor(Color.BLACK);
            } catch (Exception e) {
                Log.w(TAG, "Unable to set status bar color", e);
            }
        }

        // Existing Surface.setFrameRate on API 33+ (keeps current approach for surfaces)
        if (Build.VERSION.SDK_INT >= 33) {
            try {
                Display display = getWindow().getDecorView().getDisplay();
                Method getSurfaceMethod = Display.class.getMethod("getSurface");
                Surface surface = (Surface) getSurfaceMethod.invoke(display);
                if (surface != null) {
                    surface.setFrameRate(120f, Surface.FRAME_RATE_COMPATIBILITY_DEFAULT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TextView v = new TextView(this);
        v.setText("Game Helio G100\n\nAndroid 16 / API 36\n120 FPS frame-rate");
        v.setTextColor(Color.WHITE);
        v.setBackgroundColor(Color.BLACK);
        v.setTextSize(18f);
        v.setPadding(24, 24, 24, 24);
        setContentView(v);

        // Optional: keep decor view flags for full-screen/readable UI if desired
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Request the highest refresh rate when the app is in the foreground.
        HighRefreshHelper.requestHighestRefreshRate(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Clear the preference when the app is paused to be a good system citizen.
        HighRefreshHelper.requestDefaultRefreshRate(this);
    }
}
