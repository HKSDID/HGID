# ProGuard rules for FPS Meter and Performance Monitoring

# Keep all View classes and their methods
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    public *** get*();
}

# Keep Activity classes
-keep public class * extends android.app.Activity {
    public <init>(android.content.Context);
    public void onCreate(android.os.Bundle);
    public void onResume();
    public void onPause();
}

# Keep custom performance monitoring classes
-keep class com.mediatek.game.** { *; }
-keepclassmembers class com.mediatek.game.** {
    <init>(...);
    *** get*(...);
    *** set*(...);
    public static final int *;
}

# Keep graphics and rendering classes
-keep class androidx.graphics.** { *; }
-keep class android.graphics.** { *; }
-keep class android.view.animation.** { *; }

# Keep lifecycle classes for performance monitoring
-keep class androidx.lifecycle.** { *; }
-keep class android.arch.lifecycle.** { *; }

# Keep drawable resources referenced in code
-keepclasseswithmembernames class **.R$drawable { <fields>; }
-keepclasseswithmembernames class **.R$id { <fields>; }
-keepclasseswithmembernames class **.R$layout { <fields>; }
-keepclasseswithmembernames class **.R$color { <fields>; }

# Preserve all View subclasses - critical for custom meter UI
-keepclasseswithmembers class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Keep callback interfaces
-keep interface * { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep source file names and line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Prevent inlining of performance-critical methods
-optimizationpasses 3
-allowaccessmodification

# Keep Kotlin metadata
-keepattributes *Annotation*
-keepattributes InnerClasses

# ===== NEW ADDITIONS FOR FPS, REFRESH RATE, NETWORK, TEMPERATURE =====

# Keep FPS meter and refresh rate monitoring classes
-keep class **.fps.** { *; }
-keep class **.performance.** { *; }
-keep class **.monitor.** { *; }
-keep class **.meter.** { *; }
-keepclassmembers class **.fps.** {
    <init>(...);
    public <methods>;
    public static <fields>;
}

# Keep Display and Choreographer classes for refresh rate
-keep class android.view.Choreographer { *; }
-keep class android.view.Display { 
    public int getRefreshRate();
    public android.view.Display$Mode getMode();
}
-keep class android.view.Display$Mode { *; }

# Keep WindowManager for display metrics
-keep class android.view.WindowManager { *; }
-keep class android.util.DisplayMetrics { *; }

# Keep network monitoring classes
-keep class **.network.** { *; }
-keep class android.net.** { *; }
-keep class android.telephony.** { *; }
-keepclassmembers class android.net.TrafficStats {
    public static long getTotalRxBytes();
    public static long getTotalTxBytes();
    public static long getMobileRxBytes();
    public static long getMobileTxBytes();
}

# Keep temperature/thermal monitoring classes
-keep class **.thermal.** { *; }
-keep class **.temperature.** { *; }
-keep class android.os.BatteryManager { *; }
-keep class android.hardware.** { *; }

# Keep permission-related classes
-keep class android.content.pm.** { *; }
-keepclassmembers class android.content.Context {
    public int checkSelfPermission(java.lang.String);
    public boolean hasSystemFeature(java.lang.String);
}

# Keep runtime permission classes
-keep class androidx.core.app.ActivityCompat { *; }
-keep class androidx.core.content.ContextCompat { *; }

# Keep system property access classes
-keepclassmembers class android.os.SystemProperties {
    public static java.lang.String get(java.lang.String);
    public static java.lang.String get(java.lang.String, java.lang.String);
    public static int getInt(java.lang.String, int);
}

# Keep handler and thread-related classes for real-time updates
-keep class android.os.Handler { *; }
-keep class android.os.Looper { *; }
-keep class java.lang.Thread { *; }

# Keep paint and canvas for drawing meter UI
-keep class android.graphics.Paint { *; }
-keep class android.graphics.Canvas { *; }
-keep class android.graphics.Path { *; }
-keep class android.graphics.Rect { *; }

# Keep SharedPreferences for storing settings
-keep class android.content.SharedPreferences { *; }
-keep class android.content.SharedPreferences$Editor { *; }

# Preserve all custom service classes
-keep class * extends android.app.Service { 
    public <init>();
    public void onCreate();
    public void onDestroy();
    public int onStartCommand(android.content.Intent, int, int);
}

# Keep BroadcastReceiver classes for system events
-keep class * extends android.content.BroadcastReceiver {
    public <init>();
    public void onReceive(android.content.Context, android.content.Intent);
}

# Keep Java enums and their methods
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep lambdas and functional interfaces
-keepclassmembers class * {
    *** lambda*(...);
}

# Verbose output for debugging
-verbose

# Keep line numbers for crash reporting
-keepattributes LineNumberTable
