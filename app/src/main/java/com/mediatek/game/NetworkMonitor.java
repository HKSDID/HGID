package com.mediatek.game;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.Locale;

/**
 * Network Monitoring System - Real-time network usage tracking
 * Monitors bandwidth usage, connection type, and data statistics
 */
public class NetworkMonitor {
    private static final String TAG = "NetworkMonitor";
    private static NetworkMonitor instance;
    private static long lastRxBytes = 0;
    private static long lastTxBytes = 0;
    private static long mobileRxBytes = 0;
    private static long mobileTxBytes = 0;
    
    // Callbacks
    private NetworkUpdateCallback updateCallback;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    
    // Monitoring
    private volatile boolean isMonitoring = false;
    private Thread monitoringThread;
    private static final int MONITOR_INTERVAL_MS = 1000;
    
    public interface NetworkUpdateCallback {
        void onNetworkStatsUpdated(NetworkStats stats);
    }
    
    public static class NetworkStats {
        public long totalRxBytes;
        public long totalTxBytes;
        public long mobileRxBytes;
        public long mobileTxBytes;
        public float rxBytesPerSec;
        public float txBytesPerSec;
        public String connectionType;
        public long timestamp;
        
        @Override
        public String toString() {
            return String.format(Locale.US,
                "RX: %.2f KB/s | TX: %.2f KB/s | Total RX: %d KB | Total TX: %d KB",
                rxBytesPerSec / 1024, txBytesPerSec / 1024,
                totalRxBytes / 1024, totalTxBytes / 1024);
        }
    }
    
    public static synchronized NetworkMonitor getInstance() {
        if (instance == null) {
            instance = new NetworkMonitor();
        }
        return instance;
    }
    
    public void setUpdateCallback(NetworkUpdateCallback callback) {
        this.updateCallback = callback;
    }
    
    public void startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "Monitoring already started");
            return;
        }
        
        isMonitoring = true;
        lastRxBytes = TrafficStats.getTotalRxBytes();
        lastTxBytes = TrafficStats.getTotalTxBytes();
        mobileRxBytes = TrafficStats.getMobileRxBytes();
        mobileTxBytes = TrafficStats.getMobileTxBytes();
        
        monitoringThread = new Thread(() -> {
            while (isMonitoring) {
                try {
                    NetworkStats stats = collectNetworkStats();
                    if (updateCallback != null) {
                        mainHandler.post(() -> updateCallback.onNetworkStatsUpdated(stats));
                    }
                    Thread.sleep(MONITOR_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Log.d(TAG, "Monitoring thread interrupted");
                    break;
                }
            }
        }, "NetworkMonitor");
        monitoringThread.setDaemon(true);
        monitoringThread.start();
        Log.i(TAG, "Network monitoring started");
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
        Log.i(TAG, "Network monitoring stopped");
    }
    
    private NetworkStats collectNetworkStats() {
        NetworkStats stats = new NetworkStats();
        stats.totalRxBytes = TrafficStats.getTotalRxBytes();
        stats.totalTxBytes = TrafficStats.getTotalTxBytes();
        stats.mobileRxBytes = TrafficStats.getMobileRxBytes();
        stats.mobileTxBytes = TrafficStats.getMobileTxBytes();
        
        // Calculate rates per second
        long rxDelta = stats.totalRxBytes - lastRxBytes;
        long txDelta = stats.totalTxBytes - lastTxBytes;
        
        stats.rxBytesPerSec = rxDelta > 0 ? rxDelta : 0;
        stats.txBytesPerSec = txDelta > 0 ? txDelta : 0;
        stats.timestamp = System.currentTimeMillis();
        stats.connectionType = getConnectionType();
        
        lastRxBytes = stats.totalRxBytes;
        lastTxBytes = stats.totalTxBytes;
        
        return stats;
    }
    
    public NetworkStats getCurrentStats() {
        return collectNetworkStats();
    }
    
    private String getConnectionType() {
        try {
            if (mobileRxBytes > 0 || mobileTxBytes > 0) {
                return "Mobile";
            }
            return "Wi-Fi/Other";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
