package com.mediatek.game;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.view.Surface;
import android.view.Display;
import android.graphics.Color;
import android.util.Log;
import java.lang.reflect.Method;

/**
 * Enhanced MainActivity - Full monitoring dashboard
 * Displays FPS, Network, Thermal, Battery, and System metrics
 */
public class MainActivityEnhanced extends Activity {
    private static final String TAG = "MainActivityEnhanced";
    private LinearLayout metricsLayout;
    private TextView fpsView, networkView, thermalView, batteryView, systemView, statusView;
    
    private FpsOverlay fpsOverlay;
    private NetworkMonitor networkMonitor;
    private ThermalMonitor thermalMonitor;
    private BatteryMonitor batteryMonitor;
    private SystemMonitor systemMonitor;
    private UpdateManager updateManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Setup dark theme
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        
        // Create main layout
        ScrollView scrollView = new ScrollView(this);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(16, 16, 16, 16);
        mainLayout.setBackgroundColor(Color.BLACK);
        
        // Title
        TextView title = new TextView(this);
        title.setText("HGID - Performance Monitor\nAndroid 16 / API 36 / Helio G100\n120 FPS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(16f);
        mainLayout.addView(title);
        
        // Game Mode Switch
        Switch gameModeSwitch = new Switch(this);
        gameModeSwitch.setText("Enable 120Hz Game Mode");
        gameModeSwitch.setTextColor(Color.WHITE);
        gameModeSwitch.setChecked(PreferencesHelper.isHighRefreshEnabled(this));
        gameModeSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            PreferencesHelper.setHighRefreshEnabled(this, isChecked);
            if (isChecked) {
                GameModeController.enableGameMode(this);
                startAllMonitoring();
            } else {
                GameModeController.disableGameMode(this);
                stopAllMonitoring();
            }
        });
        mainLayout.addView(gameModeSwitch);
        
        // Metrics views
        metricsLayout = new LinearLayout(this);
        metricsLayout.setOrientation(LinearLayout.VERTICAL);
        metricsLayout.setPadding(8, 8, 8, 8);
        
        // FPS Metrics
        fpsView = createMetricView("FPS: Loading...");
        metricsLayout.addView(fpsView);
        
        // Network Metrics
        networkView = createMetricView("Network: Loading...");
        metricsLayout.addView(networkView);
        
        // Thermal Metrics
        thermalView = createMetricView("Thermal: Loading...");
        metricsLayout.addView(thermalView);
        
        // Battery Metrics
        batteryView = createMetricView("Battery: Loading...");
        metricsLayout.addView(batteryView);
        
        // System Metrics
        systemView = createMetricView("System: Loading...");
        metricsLayout.addView(systemView);
        
        // Status
        statusView = createMetricView("Status: Initializing...");
        statusView.setTextColor(Color.YELLOW);
        metricsLayout.addView(statusView);
        
        mainLayout.addView(metricsLayout);
        scrollView.addView(mainLayout);
        setContentView(scrollView);
        
        // Initialize monitors
        initializeMonitors();
        
        // Request permissions
        RuntimePermissionHelper.requestAllPermissions(this);
        
        Log.i(TAG, "Enhanced MainActivity created");
    }
    
    private TextView createMetricView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.GREEN);
        tv.setTextSize(12f);
        tv.setPadding(8, 4, 8, 4);
        tv.setBackgroundColor(0x1F333333);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 4, 0, 4);
        tv.setLayoutParams(lp);
        return tv;
    }
    
    private void initializeMonitors() {
        networkMonitor = NetworkMonitor.getInstance();
        networkMonitor.setUpdateCallback(stats ->
            networkView.setText("Network: " + stats.toString()));
        
        thermalMonitor = ThermalMonitor.getInstance();
        thermalMonitor.init(this);
        thermalMonitor.setUpdateCallback(stats ->
            thermalView.setText("Thermal: " + stats.toString()));
        
        batteryMonitor = BatteryMonitor.getInstance();
        batteryMonitor.init(this);
        batteryMonitor.setUpdateCallback(stats ->
            batteryView.setText("Battery: " + stats.toString()));
        
        systemMonitor = SystemMonitor.getInstance();
        systemMonitor.init(this);
        systemMonitor.setUpdateCallback(stats ->
            systemView.setText("System: " + stats.toString()));
        
        updateManager = UpdateManager.getInstance();
        updateManager.init(this);
        
        statusView.setText("Status: Ready (v" + updateManager.getCurrentVersion() + ")");
    }
    
    private void startAllMonitoring() {
        networkMonitor.startMonitoring();
        thermalMonitor.startMonitoring();
        batteryMonitor.startMonitoring();
        systemMonitor.startMonitoring();
        statusView.setText("Status: All monitors active");
        Log.i(TAG, "All monitoring started");
    }
    
    private void stopAllMonitoring() {
        networkMonitor.stopMonitoring();
        thermalMonitor.stopMonitoring();
        batteryMonitor.stopMonitoring();
        systemMonitor.stopMonitoring();
        statusView.setText("Status: Monitoring stopped");
        Log.i(TAG, "All monitoring stopped");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAllMonitoring();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RuntimePermissionHelper.handlePermissionResult(requestCode, permissions, grantResults);
    }
}
