package com.mediatek.game;

import android.content.Context;
import android.content.SharedPreferences;

public final class PreferencesHelper {
    private static final String PREFS = "game_prefs";
    private static final String KEY_ALLOW_BACKGROUND = "pref_allow_background_running";
    private static final String KEY_SHOW_FPS = "pref_show_fps_overlay";
    private static final String KEY_ENABLE_HIGH_REFRESH = "pref_enable_high_refresh";
    private static final String KEY_GAME_MODE_ACTIVE = "game_mode_active";

    private PreferencesHelper() {}

    public static void setAllowBackgroundRunning(Context ctx, boolean allowed) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ALLOW_BACKGROUND, allowed)
            .apply();
    }

    public static boolean isAllowBackgroundRunning(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ALLOW_BACKGROUND, false);
    }

    public static void setShowFpsOverlay(Context ctx, boolean show) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SHOW_FPS, show)
            .apply();
    }

    public static boolean isShowFpsOverlay(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_SHOW_FPS, false);
    }

    public static void setHighRefreshEnabled(Context ctx, boolean enabled) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLE_HIGH_REFRESH, enabled)
            .apply();
    }

    public static boolean isHighRefreshEnabled(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLE_HIGH_REFRESH, true);
    }

    public static void setGameModeActive(Context ctx, boolean active) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_GAME_MODE_ACTIVE, active)
            .apply();
    }

    public static boolean isGameModeActive(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_GAME_MODE_ACTIVE, false);
    }
}
