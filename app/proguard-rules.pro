# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ---------------------------------------------------------------
# WebView JS interfaces
# ---------------------------------------------------------------
-keepclassmembers class saneforce.sanzen.activity.reports.DynamicWebActivity$WebAppInterface {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class saneforce.sanzen.activity.reports.ReportWebActivity$* {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface

# ---------------------------------------------------------------
# Retrofit / OkHttp
# ---------------------------------------------------------------
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes Exceptions
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# ---------------------------------------------------------------
# Gson core
# ---------------------------------------------------------------
-keepattributes Signature
-dontwarn com.google.gson.**

# ---------------------------------------------------------------
# Gson deserialization targets — annotated fields
# ---------------------------------------------------------------
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ---------------------------------------------------------------
# Gson deserialization targets — NON-annotated fields
# Gson matches JSON keys to field names directly when there's no
# @SerializedName. If R8 renames these fields, deserialization
# silently returns null instead of throwing, causing NPEs far
# downstream (e.g. getSessionList().isEmpty() on a null list).
# Keyed by this project's naming convention so any current or
# future model class in ANY package is covered automatically.
# ---------------------------------------------------------------
-keepclassmembers class saneforce.sanzen.**.*ModelClass {
    <fields>;
}
-keepclassmembers class saneforce.sanzen.**.*ModelClass$* {
    <fields>;
}
-keepclassmembers class saneforce.sanzen.**.*Model {
    <fields>;
}
-keepclassmembers class saneforce.sanzen.**.*Response {
    <fields>;
}
-keepclassmembers class saneforce.sanzen.**.BrandModelClass {
    <fields>;
}
-keepclassmembers class saneforce.sanzen.**.BrandModelClass$* {
    <fields>;
}

# ---------------------------------------------------------------
# ButterKnife
# ---------------------------------------------------------------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# ---------------------------------------------------------------
# Glide
# ---------------------------------------------------------------
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-dontwarn com.bumptech.glide.**

# ---------------------------------------------------------------
# Room
# AndroidX Room ships its own consumer proguard rules automatically.
# ---------------------------------------------------------------

# ---------------------------------------------------------------
# AWS SDK (S3 / mobile client)
# ---------------------------------------------------------------
-dontwarn com.amazonaws.**
-dontwarn org.apache.commons.logging.**
-keep class com.amazonaws.mobileconnectors.s3.transferutility.** { *; }
-keep class com.amazonaws.auth.** { *; }
-keep class com.amazonaws.services.s3.model.** { *; }

# ---------------------------------------------------------------
# RxJava3
# ---------------------------------------------------------------
-dontwarn io.reactivex.**

# ---------------------------------------------------------------
# Firebase / Crashlytics
# ---------------------------------------------------------------
-dontwarn com.google.firebase.**

# ---------------------------------------------------------------
# Play Core / Play Services
# ---------------------------------------------------------------
-dontwarn com.google.android.play.core.**

# ---------------------------------------------------------------
# MPAndroidChart
# ---------------------------------------------------------------
-keep class com.github.mikephil.charting.charts.** { *; }
-keep class com.github.mikephil.charting.data.** { *; }
-dontwarn com.github.mikephil.charting.**

# ---------------------------------------------------------------
# graphview
# ---------------------------------------------------------------
-keep class com.jjoe64.graphview.** { *; }

# ---------------------------------------------------------------
# pdfium-android / android-pdf-viewer
# ---------------------------------------------------------------
-keep class io.github.oothp.** { *; }
-dontwarn io.github.oothp.**

# ---------------------------------------------------------------
# Readable crash stack traces (deobfuscated later via mapping.txt)
# ---------------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---------------------------------------------------------------
# General Android component / Parcelable / Serializable safety
# ---------------------------------------------------------------
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}