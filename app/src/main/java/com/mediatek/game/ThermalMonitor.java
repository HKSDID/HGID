package com.mediatek.game;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Thermal Monitor - Real-time temperature and thermal monitoring
 * Tracks device temperature, thermal throttling, and battery health
 */
public class ThermalMonitor {
    private static final String TAG = "ThermalMonitor";
    private static ThermalMonitor instance;
    
    // Common thermal zone paths (varies by device/SoC)
    private static final String[] THERMAL_PATHS = {
        "/sys/class/thermal/thermal_zone0/temp",
        "/sys/class/thermal/thermal_zone1/temp",
        "/sys/class/thermal/thermal_zone2/temp",
        "/sys/class/thermal/cooling_device0/cur_state",
        "/sys/class/thermometer/temperature"
    };
    
    private ThermalUpdateCallback updateCallback;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Timer monitoringTimer;
    private BatteryManager batteryManager;
    private volatile boolean isMonitoring = false;
    private Context context;
    
    public interface ThermalUpdateCallback {
        void onThermalStatsUpdated(ThermalStats stats);
    }
    
    public static class ThermalStats {
        public float cpuTemp = 0f;           // °C
        public float gpuTemp = 0f;           // °C
        public float batteryTemp = 0f;       // °C
        public int throttlingLevel = 0;      // 0=none, 1-3=throttled, 4=critical
        public int batteryHealth = -1;       // BatteryManager constants
        public int batteryStatus = -1;       // BatteryManager constants
        public float batteryVoltage = 0f;    // mV
        public long timestamp;
        public String thermalState = "Normal";
        
        @Override
        public String toString() {
            return String.format(Locale.US,
                "CPU: %.1f°C | GPU: %.1f°C | Battery: %.1f°C | Throttle: %d | State: %s",
                cpuTemp, gpuTemp, batteryTemp, throttlingLevel, thermalState);
        }
    }
    
    public static synchronized ThermalMonitor getInstance() {
        if (instance == null) {
            instance = new ThermalMonitor();
        }
        return instance;
    }
    
    public void init(Context context) {
        this.context = context.getApplicationContext();
        try {
            this.batteryManager = (BatteryManager) this.context.getSystemService(Context.BATTERY_SERVICE);
        } catch (Exception e) {
            Log.w(TAG, "Failed to get BatteryManager", e);
        }
    }
    
    public void setUpdateCallback(ThermalUpdateCallback callback) {
        this.updateCallback = callback;
    }
    
    public void startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "Thermal monitoring already started");
            return;
        }
        
        isMonitoring = true;
        monitoringTimer = new Timer("ThermalMonitor", true);
        monitoringTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ThermalStats stats = collectThermalStats();
                if (updateCallback != null) {
                    mainHandler.post(() -> updateCallback.onThermalStatsUpdated(stats));
                }
            }
        }, 0, 2000);
        Log.i(TAG, "Thermal monitoring started");
    }
    
    public void stopMonitoring() {
        isMonitoring = false;
        if (monitoringTimer != null) {
            monitoringTimer.cancel();
            monitoringTimer = null;
        }
        Log.i(TAG, "Thermal monitoring stopped");
    }
    
    private ThermalStats collectThermalStats() {
        ThermalStats stats = new ThermalStats();
        stats.timestamp = System.currentTimeMillis();
        
        // Read thermal zones
        stats.cpuTemp = readThermalZone("/sys/class/thermal/thermal_zone0/temp");
        stats.gpuTemp = readThermalZone("/sys/class/thermal/thermal_zone1/temp");
        stats.batteryTemp = readThermalZone("/sys/class/thermal/thermal_zone2/temp");
        
        // Determine thermal state
        if (stats.cpuTemp > 80) {
            stats.throttlingLevel = 4;
            stats.thermalState = "CRITICAL";
        } else if (stats.cpuTemp > 65) {
            stats.throttlingLevel = 3;
            stats.thermalState = "SEVERE";
        } else if (stats.cpuTemp > 50) {
            stats.throttlingLevel = 2;
            stats.thermalState = "HIGH";
        } else if (stats.cpuTemp > 40) {
            stats.throttlingLevel = 1;
            stats.thermalState = "MODERATE";
        } else {
            stats.throttlingLevel = 0;
            stats.thermalState = "NORMAL";
        }
        
        // Battery stats (requires context)
        if (context != null && batteryManager != null) {
            try {
                stats.batteryHealth = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_HEALTH);
                stats.batteryStatus = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
                stats.batteryVoltage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_VOLTAGE);
            } catch (Exception e) {
                Log.w(TAG, "Failed to read battery manager", e);
            }
        }
        
        return stats;
    }
    
    public ThermalStats getCurrentStats() {
        return collectThermalStats();
    }
    
    private float readThermalZone(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) return 0f;
            
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            br.close();
            
            if (line != null && !line.isEmpty()) {
                long tempMillis = Long.parseLong(line.trim());
                return tempMillis / 1000f; // Convert millidegrees to degrees
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to read " + path);
        }
        return 0f;
    }
}
