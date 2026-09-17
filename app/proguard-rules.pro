# Add project specific ProGuard rules here.

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
# Retrofit and OkHttp ship their own consumer-proguard-rules.pro
# inside their AARs — no blanket -keep needed. Just silence
# warnings from optional/reflective code paths they reference.
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
# Gson / model classes
# Gson's core classes are used reflectively by Retrofit's converter,
# but Gson itself does NOT need a blanket keep. Only YOUR model
# classes (deserialization targets) need protection.
# ---------------------------------------------------------------
-keepattributes Signature
-dontwarn com.google.gson.**

-keep class saneforce.sanzen.**.EditModelClass { *; }
-keep class saneforce.sanzen.**.DCRLastVisitDetails { *; }
-keep class saneforce.sanzen.**.CustomSetupResponse { *; }
-keep class saneforce.sanzen.**.SetupResponse { *; }

# Safety net for any other model class using @SerializedName
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
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
# Glide 5.x ships correct consumer rules in its AAR automatically.
# Only keep your own AppGlideModule implementation if you have one.
# ---------------------------------------------------------------
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-dontwarn com.bumptech.glide.**

# ---------------------------------------------------------------
# Room
# AndroidX Room ships its own consumer proguard rules automatically.
# ---------------------------------------------------------------

# ---------------------------------------------------------------
# AWS SDK (S3 / mobile client)
# The AWS Android SDK's AAR does not reliably ship consumer rules
# for every module, and it does use reflection in several places
# (credentials providers, service clients). This one genuinely
# needs a broader keep — but scope -dontwarn instead of -keep
# where you're not hitting actual crashes.
# ---------------------------------------------------------------
-dontwarn com.amazonaws.**
-dontwarn org.apache.commons.logging.**
-keep class com.amazonaws.mobileconnectors.s3.transferutility.** { *; }
-keep class com.amazonaws.auth.** { *; }
-keep class com.amazonaws.services.s3.model.** { *; }

# ---------------------------------------------------------------
# RxJava3
# No blanket keep needed — RxJava ships its own consumer rules.
# ---------------------------------------------------------------
-dontwarn io.reactivex.**

# ---------------------------------------------------------------
# Firebase / Crashlytics
# Firebase SDKs ship consumer proguard rules automatically.
# ---------------------------------------------------------------
-dontwarn com.google.firebase.**

# ---------------------------------------------------------------
# Play Core / Play Services
# Ships consumer rules automatically.
# ---------------------------------------------------------------
-dontwarn com.google.android.play.core.**

# ---------------------------------------------------------------
# MPAndroidChart
# Older library, no bundled consumer rules — does use some
# reflection for axis/renderer classes, so keep scoped to the
# library's public API surface only.
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