package com.mediatek.game;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Surface;

public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                Surface surface = getWindow().getDecorView().getDisplay().getSurface();
                if (surface != null) {
                    surface.setFrameRate(120f, Surface.FRAME_RATE_COMPATIBILITY_DEFAULT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TextView v = new TextView(this);
        v.setText("Game Helio G100\n\nAndroid 16 / API 36\n120 FPS frame-rate");
        setContentView(v);
    }
}
