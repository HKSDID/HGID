package com.mediatek.game;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.Gravity;
import android.view.View;
import android.view.Choreographer;
import android.content.Context;
import android.os.Handler;
import java.lang.ref.WeakReference;

public final class FpsOverlay {
    private static WeakReference<View> sOverlayRef = null;
    private static Choreographer.FrameCallback frameCallback = null;
    private static long lastTime = 0;
    private static int frames = 0;
    private static Handler uiHandler = new Handler();

    public static void show(Activity activity) {
        if (activity == null) return;
        try {
            if (sOverlayRef != null && sOverlayRef.get() != null) return; // already shown

            final TextView tv = new TextView(activity);
            tv.setText("FPS: --");
            tv.setTextSize(12);
            int pad = (int)(6 * activity.getResources().getDisplayMetrics().density);
            tv.setPadding(pad,pad,pad,pad);
            tv.setBackgroundColor(0x7F000000);
            tv.setTextColor(0xFFFFFFFF);

            ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            tv.setX(10);
            tv.setY(10);
            decor.addView(tv);

            sOverlayRef = new WeakReference<View>(tv);

            lastTime = System.nanoTime();
            frames = 0;
            frameCallback = new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long frameTimeNanos) {
                    frames++;
                    long now = System.nanoTime();
                    if (now - lastTime >= 1_000_000_000L) {
                        final int fps = frames;
                        frames = 0;
                        lastTime = now;
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    TextView v = (TextView) (sOverlayRef != null ? sOverlayRef.get() : null);
                                    if (v != null) v.setText("FPS: " + fps);
                                } catch (Exception ignored) {}
                            }
                        });
                    }
                    Choreographer.getInstance().postFrameCallback(this);
                }
            };
            Choreographer.getInstance().postFrameCallback(frameCallback);

        } catch (Exception ignored) {}
    }

    public static void hide(Activity activity) {
        try {
            View v = sOverlayRef != null ? sOverlayRef.get() : null;
            if (v != null) {
                ViewGroup parent = (ViewGroup) v.getParent();
                if (parent != null) parent.removeView(v);
                sOverlayRef = null;
            }
            if (frameCallback != null) {
                Choreographer.getInstance().removeFrameCallback(frameCallback);
                frameCallback = null;
            }
        } catch (Exception ignored) {}
    }
}
