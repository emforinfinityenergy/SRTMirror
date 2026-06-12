# SRT Mirror ProGuard Rules

# srtdroid - JNI native calls
-keep class io.github.thibaultbee.srtdroid.** { *; }
-dontwarn io.github.thibaultbee.srtdroid.**

# Keep native method references
-keepclasseswithmembernames class * {
    native <methods>;
}

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# kotlinx.coroutines
-dontwarn kotlinx.coroutines.**

# Compose
-dontwarn androidx.compose.**
