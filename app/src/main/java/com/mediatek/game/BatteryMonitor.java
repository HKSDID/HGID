package com.mediatek.game;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.Locale;

/**
 * Battery Monitor - Real-time battery status and health tracking
 * Monitors battery percentage, temperature, voltage, and health
 */
public class BatteryMonitor {
    private static final String TAG = "BatteryMonitor";
    private static BatteryMonitor instance;
    private Context context;
    private BatteryBroadcastReceiver batteryReceiver;
    private BatteryUpdateCallback updateCallback;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isMonitoring = false;
    
    public interface BatteryUpdateCallback {
        void onBatteryStatusUpdated(BatteryStats stats);
    }
    
    public static class BatteryStats {
        public int level;                    // 0-100
        public int scale;                    // Usually 100
        public int temperature;              // Celsius
        public int voltage;                  // mV
        public int health;                   // BatteryManager.BATTERY_HEALTH_*
        public int status;                   // BatteryManager.BATTERY_STATUS_*
        public int plugged;                  // BatteryManager.BATTERY_PLUGGED_*
        public String technology;            // Battery technology (Li-ion, etc)
        public boolean isPresent;            // Whether battery is present
        public long timestamp;
        
        @Override
        public String toString() {
            return String.format(Locale.US,
                "Battery: %d%% | Temp: %d°C | Voltage: %dmV | Health: %d | Status: %d",
                level, temperature, voltage, health, status);
        }
    }
    
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                BatteryStats stats = parseBatteryIntent(intent);
                if (updateCallback != null) {
                    mainHandler.post(() -> updateCallback.onBatteryStatusUpdated(stats));
                }
            }
        }
    }
    
    public static synchronized BatteryMonitor getInstance() {
        if (instance == null) {
            instance = new BatteryMonitor();
        }
        return instance;
    }
    
    public void init(Context context) {
        this.context = context.getApplicationContext();
        Log.i(TAG, "BatteryMonitor initialized");
    }
    
    public void setUpdateCallback(BatteryUpdateCallback callback) {
        this.updateCallback = callback;
    }
    
    public void startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "Battery monitoring already started");
            return;
        }
        
        isMonitoring = true;
        batteryReceiver = new BatteryBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryReceiver, filter);
        Log.i(TAG, "Battery monitoring started");
    }
    
    public void stopMonitoring() {
        if (!isMonitoring || batteryReceiver == null) {
            return;
        }
        
        isMonitoring = false;
        try {
            context.unregisterReceiver(batteryReceiver);
        } catch (Exception e) {
            Log.w(TAG, "Failed to unregister receiver", e);
        }
        Log.i(TAG, "Battery monitoring stopped");
    }
    
    public BatteryStats getCurrentStats() {
        try {
            Intent batteryStatus = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (batteryStatus != null) {
                return parseBatteryIntent(batteryStatus);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to get battery stats", e);
        }
        return new BatteryStats();
    }
    
    private BatteryStats parseBatteryIntent(Intent intent) {
        BatteryStats stats = new BatteryStats();
        stats.level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        stats.scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        stats.temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        stats.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        stats.health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,
            BatteryManager.BATTERY_HEALTH_UNKNOWN);
        stats.status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
            BatteryManager.BATTERY_STATUS_UNKNOWN);
        stats.plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        stats.technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        stats.isPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, true);
        stats.timestamp = System.currentTimeMillis();
        return stats;
    }
}
