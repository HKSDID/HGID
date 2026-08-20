package com.mediatek.game;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Switch;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.view.View;

public class SettingsActivity extends Activity {
    private static final String PREFS = "game_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        final Switch swAllow = findViewById(R.id.switch_allow_background);
        final Switch swFps = findViewById(R.id.switch_show_fps);
        final Switch swHigh = findViewById(R.id.switch_high_refresh);
        final EditText etFps = findViewById(R.id.edit_target_fps);
        Button btnSave = findViewById(R.id.btn_save);

        swAllow.setChecked(prefs.getBoolean("pref_allow_background_running", false));
        swFps.setChecked(prefs.getBoolean("pref_show_fps_overlay", false));
        swHigh.setChecked(prefs.getBoolean("pref_enable_high_refresh", true));
        etFps.setText(String.valueOf(prefs.getInt("pref_target_fps", 60)));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor e = prefs.edit();
                e.putBoolean("pref_allow_background_running", swAllow.isChecked());
                e.putBoolean("pref_show_fps_overlay", swFps.isChecked());
                e.putBoolean("pref_enable_high_refresh", swHigh.isChecked());
                int target = 60;
                try { target = Integer.parseInt(etFps.getText().toString()); } catch (Exception ignored) {}
                e.putInt("pref_target_fps", target);
                e.apply();
                finish();
            }
        });
    }
}
