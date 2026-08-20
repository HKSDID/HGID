package com.mediatek.game;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Locale;

/**
 * System Monitor - CPU, Memory, and system resource monitoring
 * Tracks CPU usage, memory consumption, and system health
 */
public class SystemMonitor {
    private static final String TAG = "SystemMonitor";
    private static SystemMonitor instance;
    private Context context;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private SystemUpdateCallback updateCallback;
    private volatile boolean isMonitoring = false;
    private Thread monitoringThread;
    
    public interface SystemUpdateCallback {
        void onSystemStatsUpdated(SystemStats stats);
    }
    
    public static class SystemStats {
        public float cpuUsagePercent = 0f;       // 0-100
        public long totalMemory = 0;            // Bytes
        public long availableMemory = 0;        // Bytes
        public long usedMemory = 0;             // Bytes
        public float memoryUsagePercent = 0f;   // 0-100
        public long nativeMemory = 0;           // Bytes
        public long javaMemory = 0;             // Bytes
        public int openFileDescriptors = 0;
        public int runningProcesses = 0;
        public long uptime = 0;                 // milliseconds
        public long timestamp;
        
        @Override
        public String toString() {
            return String.format(Locale.US,
                "CPU: %.1f%% | Memory: %.1f%% (%d MB / %d MB) | Processes: %d",
                cpuUsagePercent, memoryUsagePercent,
                usedMemory / (1024 * 1024), totalMemory / (1024 * 1024),
                runningProcesses);
        }
    }
    
    public static synchronized SystemMonitor getInstance() {
        if (instance == null) {
            instance = new SystemMonitor();
        }
        return instance;
    }
    
    public void init(Context context) {
        this.context = context.getApplicationContext();
        Log.i(TAG, "SystemMonitor initialized");
    }
    
    public void setUpdateCallback(SystemUpdateCallback callback) {
        this.updateCallback = callback;
    }
    
    public void startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "System monitoring already started");
            return;
        }
        
        isMonitoring = true;
        monitoringThread = new Thread(() -> {
            while (isMonitoring) {
                try {
                    SystemStats stats = collectSystemStats();
                    if (updateCallback != null) {
                        mainHandler.post(() -> updateCallback.onSystemStatsUpdated(stats));
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "SystemMonitor");
        monitoringThread.setDaemon(true);
        monitoringThread.start();
        Log.i(TAG, "System monitoring started");
    }
    
    public void stopMonitoring() {
        isMonitoring = false;
        if (monitoringThread != null) {
            try {
                monitoringThread.join(2000);
            } catch (InterruptedException e) {
                Log.w(TAG, "Thread join interrupted");
            }
        }
        Log.i(TAG, "System monitoring stopped");
    }
    
    private SystemStats collectSystemStats() {
        SystemStats stats = new SystemStats();
        stats.timestamp = System.currentTimeMillis();
        
        // CPU usage
        stats.cpuUsagePercent = readCpuUsage();
        
        // Memory stats
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                am.getMemoryInfo(memInfo);
                stats.totalMemory = memInfo.totalMem;
                stats.availableMemory = memInfo.availMem;
                stats.usedMemory = memInfo.totalMem - memInfo.availMem;
                stats.memoryUsagePercent = (float) (stats.usedMemory * 100.0 / stats.totalMemory);
                stats.runningProcesses = am.getRunningAppProcesses().size();
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to read memory stats", e);
        }
        
        // Native and Java memory
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // For Android 12+, use Runtime memory as Debug.getNativeHeap() was removed
                Runtime runtime = Runtime.getRuntime();
                stats.nativeMemory = runtime.totalMemory() - runtime.freeMemory();
            } else {
                // For Android 11 and below, use Debug.getNativeHeap()
                stats.nativeMemory = Debug.getNativeHeap().totalMemory;
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to read native memory, using fallback");
            // Fallback to runtime memory
            Runtime runtime = Runtime.getRuntime();
            stats.nativeMemory = runtime.totalMemory() - runtime.freeMemory();
        }
        stats.javaMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Uptime
        stats.uptime = System.currentTimeMillis() / 1000;
        
        return stats;
    }
    
    public SystemStats getCurrentStats() {
        return collectSystemStats();
    }
    
    private float readCpuUsage() {
        try {
            // Read /proc/stat
            BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"));
            String line = reader.readLine();
            reader.close();
            
            if (line != null) {
                String[] parts = line.split(" +");
                if (parts.length >= 5) {
                    // Simple CPU calculation (in real app, track delta)
                    long user = Long.parseLong(parts[1]);
                    long system = Long.parseLong(parts[3]);
                    long idle = Long.parseLong(parts[4]);
                    long total = user + system + idle;
                    return (float) ((user + system) * 100.0 / total);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to read CPU usage");
        }
        return 0f;
    }
}
