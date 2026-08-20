# ProGuard rules for HGID - Full Preservation Configuration
# Comprehensive rules for FPS Meter, Performance Monitoring, Network Monitoring, 
# Thermal/Temperature Monitoring, Runtime Permissions, and Real-time Updates

# ===== GLOBAL PRESERVATION - KEEP EVERYTHING BY DEFAULT =====
-dontshrink
-dontoptimize
-dontobfuscate
-dontwarn

# Verbose output for debugging
-verbose

# ===== KEEP ALL ATTRIBUTES =====
-keepattributes *
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions
-keepattributes Signature
-keepattributes Deprecated
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes PermittedSubclasses
-keepattributes Record
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations
-keepattributes Synthetic
-keepattributes Metadata
-keepattributes LineNumberTable
-renamesourcefileattribute SourceFile

# ===== KEEP ALL APPLICATION CLASSES =====
-keep class ** { *; }
-keepclassmembers class ** { *; }
-keepclasseswithmembers class ** { *; }

# ===== KEEP ALL INTERFACES AND IMPLEMENTATIONS =====
-keep interface ** { *; }
-keep interface * { *; }
-keepclassmembers interface ** { *; }
-keepclassandmembers class * implements ** { *; }

# ===== KEEP ALL ENUMS =====
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    **[] $VALUES;
    public <init>(...);
}

# ===== KEEP ALL ANNOTATIONS =====
-keep @interface * { *; }
-keepclassmembers @interface * { *; }

# ===== KEEP ALL VIEW CLASSES =====
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
    public void set*(...);
    public *** get*(...);
}

# ===== KEEP ALL ACTIVITY CLASSES =====
-keep public class * extends android.app.Activity {
    public <init>(android.content.Context);
    public void onCreate(android.os.Bundle);
    public void onResume();
    public void onPause();
    public void onDestroy();
    public void onStart();
    public void onStop();
    public void onRestart();
}

# ===== KEEP ALL FRAGMENT CLASSES =====
-keep public class * extends androidx.fragment.app.Fragment {
    public <init>();
    public void onCreate(android.os.Bundle);
    public void onViewCreated(android.view.View, android.os.Bundle);
    public void onStart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroyView();
}

# ===== KEEP ALL SERVICE CLASSES =====
-keep class * extends android.app.Service {
    public <init>();
    public void onCreate();
    public void onDestroy();
    public int onStartCommand(android.content.Intent, int, int);
    public android.os.IBinder onBind(android.content.Intent);
}

# ===== KEEP ALL BROADCAST RECEIVER CLASSES =====
-keep class * extends android.content.BroadcastReceiver {
    public <init>();
    public void onReceive(android.content.Context, android.content.Intent);
}

# ===== KEEP ALL APPLICATION CLASSES =====
-keep class * extends android.app.Application {
    public void onCreate();
}

# ===== KEEP ALL CONTENT PROVIDER CLASSES =====
-keep class * extends android.content.ContentProvider {
    public <init>();
    public boolean onCreate();
    public java.lang.String getType(android.net.Uri);
    public android.database.Cursor query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String);
    public android.net.Uri insert(android.net.Uri, android.content.ContentValues);
    public int update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[]);
    public int delete(android.net.Uri, java.lang.String, java.lang.String[]);
}

# ===== KEEP ALL CUSTOM MONITORING CLASSES =====
-keep class com.mediatek.game.** { *; }
-keep class **.fps.** { *; }
-keep class **.performance.** { *; }
-keep class **.monitor.** { *; }
-keep class **.meter.** { *; }
-keep class **.network.** { *; }
-keep class **.thermal.** { *; }
-keep class **.temperature.** { *; }
-keep class **.permission.** { *; }
-keep class **.runtime.** { *; }
-keep class **.update.** { *; }

# ===== KEEP ALL GRAPHICS AND RENDERING CLASSES =====
-keep class androidx.graphics.** { *; }
-keep class android.graphics.** { *; }
-keep class android.view.animation.** { *; }
-keep class android.animation.** { *; }
-keep class android.media.** { *; }

# ===== KEEP ALL LIFECYCLE CLASSES =====
-keep class androidx.lifecycle.** { *; }
-keep class android.arch.lifecycle.** { *; }

# ===== KEEP ALL RESOURCE CLASSES =====
-keepclasseswithmembernames class **.R$* { *; }
-keep class **.R { *; }
-keep class **.R$* { *; }

# ===== KEEP ALL DISPLAY AND UI CLASSES =====
-keep class android.view.Choreographer { *; }
-keep class android.view.Display { *; }
-keep class android.view.Display$Mode { *; }
-keep class android.view.WindowManager { *; }
-keep class android.util.DisplayMetrics { *; }
-keep class android.graphics.Paint { *; }
-keep class android.graphics.Canvas { *; }
-keep class android.graphics.Path { *; }
-keep class android.graphics.Rect { *; }
-keep class android.graphics.RectF { *; }

# ===== KEEP ALL NETWORK CLASSES =====
-keep class android.net.** { *; }
-keep class android.telephony.** { *; }
-keep class java.net.** { *; }
-keepclassmembers class android.net.TrafficStats {
    public static long getTotalRxBytes();
    public static long getTotalTxBytes();
    public static long getMobileRxBytes();
    public static long getMobileTxBytes();
    public static long getUidRxBytes(int);
    public static long getUidTxBytes(int);
}

# ===== KEEP ALL THERMAL AND BATTERY CLASSES =====
-keep class android.os.BatteryManager { *; }
-keep class android.hardware.** { *; }
-keep class android.thermal.** { *; }

# ===== KEEP ALL PERMISSION AND CONTEXT CLASSES =====
-keep class android.content.pm.** { *; }
-keep class android.content.Context { *; }
-keepclassmembers class android.content.Context {
    public int checkSelfPermission(java.lang.String);
    public boolean hasSystemFeature(java.lang.String);
    public java.lang.Object getSystemService(java.lang.String);
}

# ===== KEEP ALL ANDROIDX SUPPORT CLASSES =====
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keepclassmembers class androidx.** {
    <init>(...);
    public <methods>;
    public static <fields>;
}

# ===== KEEP ALL ANDROIDX CORE CLASSES =====
-keep class androidx.core.app.ActivityCompat { *; }
-keep class androidx.core.content.ContextCompat { *; }
-keep class androidx.core.** { *; }

# ===== KEEP ALL SYSTEM PROPERTY CLASSES =====
-keepclassmembers class android.os.SystemProperties {
    public static java.lang.String get(java.lang.String);
    public static java.lang.String get(java.lang.String, java.lang.String);
    public static int getInt(java.lang.String, int);
    public static long getLong(java.lang.String, long);
    public static boolean getBoolean(java.lang.String, boolean);
}

# ===== KEEP ALL HANDLER AND THREADING CLASSES =====
-keep class android.os.Handler { *; }
-keep class android.os.Looper { *; }
-keep class android.os.Message { *; }
-keep class java.lang.Thread { *; }
-keep class java.lang.Runnable { *; }

# ===== KEEP ALL PREFERENCES AND STORAGE CLASSES =====
-keep class android.content.SharedPreferences { *; }
-keep class android.content.SharedPreferences$Editor { *; }
-keep class androidx.preference.** { *; }

# ===== KEEP ALL NATIVE METHODS =====
-keepclasseswithmembernames class * {
    native <methods>;
}

# ===== KEEP ALL SERIALIZABLE CLASSES =====
-keep class * implements java.io.Serializable { *; }
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===== KEEP ALL PARCELABLE CLASSES =====
-keep class * implements android.os.Parcelable { *; }
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ===== KEEP ALL CALLBACK INTERFACES =====
-keep interface * { *; }
-keepclassmembers interface * { *; }

# ===== KEEP ALL KOTLIN METADATA =====
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keepclassmembers class ** {
    *** lambda*(...);
}

# ===== KEEP ALL COMMON LIBRARIES =====
-keep class com.google.** { *; }
-keep class com.squareup.** { *; }
-keep class com.jakewharton.** { *; }
-keep class io.reactivex.** { *; }
-keep class rx.** { *; }

# ===== SUPPRESS WARNINGS =====
-dontwarn android.**
-dontwarn androidx.**
-dontwarn com.google.**
-dontwarn java.**
-dontwarn javax.**
-dontwarn kotlin.**
-dontwarn kotlinx.**

# ===== OPTIMIZATION SETTINGS =====
-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively

# ===== FINAL SAFETY RULES =====
# Ensure nothing gets removed
-keep,allowoptimization class * {
    <init>(...);
    <fields>;
    <methods>;
}

-keepclassmembers,allowoptimization class * {
    *** *(...);
    public <methods>;
    public static <fields>;
}

# End of ProGuard configuration
