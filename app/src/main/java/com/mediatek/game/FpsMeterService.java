package com.mediatek.game;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Choreographer;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import java.lang.ref.WeakReference;

/**
 * FPS Meter Service - Real-time performance monitoring
 * Displays FPS, frame time, and performance metrics
 */
public class FpsMeterService extends Service {
    private static final String TAG = "FpsMeterService";
    private static final int NOTIFICATION_ID = 1002;
    private static final int UPDATE_INTERVAL_MS = 1000; // Update every 1 second
    
    private static WeakReference<Activity> sActivityRef = null;
    private static WeakReference<ViewGroup> sMetricsViewRef = null;
    private static Choreographer.FrameCallback sFrameCallback = null;
    
    private static long lastUpdateTime = 0;
    private static int frameCount = 0;
    private static long lastFrameTime = 0;
    private static float maxFrameTime = 0;
    private static float minFrameTime = Float.MAX_VALUE;
    private static float avgFrameTime = 0;
    private static int droppedFrames = 0;
    private static final float TARGET_FRAME_TIME_60FPS = 16.67f; // milliseconds
    private static final float TARGET_FRAME_TIME_120FPS = 8.33f; // milliseconds
    
    private static Handler uiHandler;
    private int performanceMode = PERFORMANCE_MODE_BALANCED; // Default mode
    
    public static final int PERFORMANCE_MODE_BALANCED = 0;
    public static final int PERFORMANCE_MODE_HIGH = 1;
    public static final int PERFORMANCE_MODE_ULTRA = 2;
    public static final int PERFORMANCE_MODE_BATTERY_SAVER = 3;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FpsMeterService onCreate");
        if (uiHandler == null) {
            uiHandler = new Handler(Looper.getMainLooper());
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "FpsMeterService onStartCommand");
        
        if (intent != null) {
            performanceMode = intent.getIntExtra("performance_mode", PERFORMANCE_MODE_BALANCED);
            Log.d(TAG, "Performance Mode: " + performanceMode);
        }
        
        // Create foreground notification (required for background service)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification().build());
        }
        
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "FpsMeterService onDestroy");
        stopMetricsTracking();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
    }
    
    /**
     * Start metrics tracking with overlay
     */
    public static void startMetricsTracking(Activity activity, int mode) {
        if (activity == null) return;
        
        try {
            if (sMetricsViewRef != null && sMetricsViewRef.get() != null) {
                Log.d(TAG, "Metrics tracking already active");
                return;
            }
            
            sActivityRef = new WeakReference<>(activity);
            
            // Create metrics display container
            LinearLayout metricsContainer = new LinearLayout(activity);
            metricsContainer.setOrientation(LinearLayout.VERTICAL);
            metricsContainer.setBackgroundColor(0xAA000000); // Semi-transparent black
            metricsContainer.setPadding(12, 8, 12, 8);
            metricsContainer.setX(10);
            metricsContainer.setY(100);
            
            // FPS display
            final TextView fpsText = new TextView(activity);
            fpsText.setText("FPS: 0");
            fpsText.setTextColor(0xFF00FF00); // Green
            fpsText.setTextSize(11f);
            metricsContainer.addView(fpsText);
            
            // Frame time display
            final TextView frameTimeText = new TextView(activity);
            frameTimeText.setText("Frame: 0.0ms");
            frameTimeText.setTextColor(0xFF00FF00);
            frameTimeText.setTextSize(11f);
            metricsContainer.addView(frameTimeText);
            
            // Min/Max/Avg display
            final TextView statsText = new TextView(activity);
            statsText.setText("Min/Avg/Max: 0/0/0ms");
            statsText.setTextColor(0xFF00FF00);
            statsText.setTextSize(11f);
            metricsContainer.addView(statsText);
            
            // Dropped frames display
            final TextView droppedText = new TextView(activity);
            droppedText.setText("Dropped: 0");
            droppedText.setTextColor(0xFFFFFF00); // Yellow for warnings
            droppedText.setTextSize(11f);
            metricsContainer.addView(droppedText);
            
            // Performance mode display
            final TextView modeText = new TextView(activity);
            modeText.setText("Mode: " + getModeString(mode));
            modeText.setTextColor(0xFF00CCFF); // Cyan
            modeText.setTextSize(10f);
            metricsContainer.addView(modeText);
            
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, 
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            metricsContainer.setLayoutParams(lp);
            decorView.addView(metricsContainer);
            
            sMetricsViewRef = new WeakReference<>(metricsContainer);
            
            // Initialize tracking variables
            lastUpdateTime = System.currentTimeMillis();
            lastFrameTime = System.nanoTime();
            frameCount = 0;
            droppedFrames = 0;
            maxFrameTime = 0;
            minFrameTime = Float.MAX_VALUE;
            avgFrameTime = 0;
            
            // Post frame callback
            sFrameCallback = new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long frameTimeNanos) {
                    frameCount++;
                    
                    long now = System.nanoTime();
                    float currentFrameTime = (now - lastFrameTime) / 1_000_000.0f; // Convert to ms
                    lastFrameTime = now;
                    
                    // Update frame time stats
                    maxFrameTime = Math.max(maxFrameTime, currentFrameTime);
                    minFrameTime = Math.min(minFrameTime, currentFrameTime);
                    avgFrameTime = (avgFrameTime * (frameCount - 1) + currentFrameTime) / frameCount;
                    
                    // Detect dropped frames (frame time > 16.67ms for 60FPS target)
                    float targetFrameTime = (mode == PERFORMANCE_MODE_ULTRA) ? TARGET_FRAME_TIME_120FPS : TARGET_FRAME_TIME_60FPS;
                    if (currentFrameTime > targetFrameTime * 1.5f) {
                        droppedFrames++;
                    }
                    
                    // Update UI every UPDATE_INTERVAL_MS
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTime >= UPDATE_INTERVAL_MS) {
                        final int fps = frameCount;
                        final float frameTime = currentFrameTime;
                        final float minFt = minFrameTime;
                        final float avgFt = avgFrameTime;
                        final float maxFt = maxFrameTime;
                        final int dropped = droppedFrames;
                        
                        lastUpdateTime = currentTime;
                        frameCount = 0;
                        droppedFrames = 0;
                        maxFrameTime = 0;
                        minFrameTime = Float.MAX_VALUE;
                        avgFrameTime = 0;
                        
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ViewGroup container = sMetricsViewRef != null ? sMetricsViewRef.get() : null;
                                    if (container != null && container.getChildCount() >= 5) {
                                        TextView fpsView = (TextView) container.getChildAt(0);
                                        TextView frameView = (TextView) container.getChildAt(1);
                                        TextView statsView = (TextView) container.getChildAt(2);
                                        TextView droppedView = (TextView) container.getChildAt(3);
                                        
                                        fpsView.setText(String.format("FPS: %d", fps));
                                        frameView.setText(String.format("Frame: %.1fms", frameTime));
                                        statsView.setText(String.format("Min/Avg/Max: %.1f/%.1f/%.1f ms", minFt, avgFt, maxFt));
                                        
                                        if (dropped > 0) {
                                            droppedView.setTextColor(0xFFFF0000); // Red if drops exist
                                            droppedView.setText(String.format("Dropped: %d", dropped));
                                        } else {
                                            droppedView.setTextColor(0xFFFFFF00);
                                            droppedView.setText("Dropped: 0");
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.w(TAG, "Error updating metrics UI: " + e.getMessage());
                                }
                            }
                        });
                    }
                    
                    Choreographer.getInstance().postFrameCallback(this);
                }
            };
            
            Choreographer.getInstance().postFrameCallback(sFrameCallback);
            Log.i(TAG, "Metrics tracking started");
            
        } catch (Exception e) {
            Log.e(TAG, "Error starting metrics tracking: " + e.getMessage(), e);
        }
    }
    
    /**
     * Stop metrics tracking
     */
    public static void stopMetricsTracking() {
        try {
            if (sMetricsViewRef != null && sMetricsViewRef.get() != null) {
                ViewGroup container = sMetricsViewRef.get();
                ViewGroup parent = (ViewGroup) container.getParent();
                if (parent != null) {
                    parent.removeView(container);
                }
                sMetricsViewRef = null;
            }
            
            if (sFrameCallback != null) {
                Choreographer.getInstance().removeFrameCallback(sFrameCallback);
                sFrameCallback = null;
            }
            
            sActivityRef = null;
            Log.i(TAG, "Metrics tracking stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping metrics tracking: " + e.getMessage(), e);
        }
    }
    
    /**
     * Apply performance mode settings
     */
    public static void applyPerformanceMode(Activity activity, int mode) {
        if (activity == null) return;
        
        Log.d(TAG, "Applying performance mode: " + getModeString(mode));
        
        switch (mode) {
            case PERFORMANCE_MODE_HIGH:
                // High performance: 120Hz, aggressive optimization
                GameModeController.enableGameMode(activity);
                PreferencesHelper.setHighRefreshEnabled(activity, true);
                break;
                
            case PERFORMANCE_MODE_ULTRA:
                // Ultra performance: Max refresh rate, all optimizations
                GameModeController.enableGameMode(activity);
                HighRefreshHelper.requestHighestRefreshRate(activity);
                break;
                
            case PERFORMANCE_MODE_BATTERY_SAVER:
                // Battery saver: Reduced refresh rate, power management
                GameModeController.disableGameMode(activity);
                HighRefreshHelper.requestDefaultRefreshRate(activity);
                break;
                
            case PERFORMANCE_MODE_BALANCED:
            default:
                // Balanced: Normal operation
                PreferencesHelper.setHighRefreshEnabled(activity, false);
                break;
        }
    }
    
    /**
     * Get performance mode string
     */
    private static String getModeString(int mode) {
        switch (mode) {
            case PERFORMANCE_MODE_HIGH: return "High";
            case PERFORMANCE_MODE_ULTRA: return "Ultra";
            case PERFORMANCE_MODE_BATTERY_SAVER: return "Battery";
            case PERFORMANCE_MODE_BALANCED: return "Balanced";
            default: return "Unknown";
        }
    }
    
    /**
     * Create foreground notification
     */
    private NotificationCompat.Builder createNotification() {
        return new NotificationCompat.Builder(this, "fps_meter")
                .setContentTitle("FPS Meter Service")
                .setContentText("Real-time performance monitoring active")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);
    }
}
