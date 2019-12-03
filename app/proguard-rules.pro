# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-dontobfuscate
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

# support crashlytic
# https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports#android
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# Log.d()/v() are deleted automatically
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}

-keep class com.github.bumptech.glide.** { *; }
-keep class com.google.code.gson.** { *; }
-keep class com.mq.myvtg.model.** { *; }
-keepattributes Signature
