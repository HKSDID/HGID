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
