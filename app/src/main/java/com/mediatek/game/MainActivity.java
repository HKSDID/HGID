package com.mediatek.game;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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

        // Dark window background and status bar
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                getWindow().setStatusBarColor(Color.BLACK);
            } catch (Exception e) {
                Log.w(TAG, "Unable to set status bar color", e);
            }
        }

        // Layout container
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText("Game Helio G100\n\nAndroid 16 / API 36\n120 FPS frame-rate");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        layout.addView(title);

        // Switch to enable/disable gameplay-only 120Hz mode
        Switch gameModeSwitch = new Switch(this);
        gameModeSwitch.setText("Enable 120Hz Game Mode (gameplay only)");
        gameModeSwitch.setTextColor(Color.WHITE);
        gameModeSwitch.setChecked(false); // default off
        gameModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GameModeController.enableGameMode(MainActivity.this);
                } else {
                    GameModeController.disableGameMode(MainActivity.this);
                }
            }
        });
        layout.addView(gameModeSwitch);

        // Keep previous fallback: try set surface frame rate for API 33+
        if (Build.VERSION.SDK_INT >= 33) {
            try {
                Display display = getWindow().getDecorView().getDisplay();
                Method getSurfaceMethod = Display.class.getMethod("getSurface");
                Surface surface = (Surface) getSurfaceMethod.invoke(display);
                if (surface != null) {
                    surface.setFrameRate(120f, Surface.FRAME_RATE_COMPATIBILITY_DEFAULT);
                }
            } catch (Exception e) {
                Log.w(TAG, "Surface.setFrameRate fallback failed", e);
            }
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // keep default behavior: do not auto-enable game mode on resume
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Ensure mode is cleared when paused (safety)
        GameModeController.disableGameMode(this);
    }
}
