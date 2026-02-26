# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes InnerClasses
-optimizationpasses 5
-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers
# -dontoptimize
-dontpreverify
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#-allowaccessmodification
-repackageclasses ''
-verbose

-keepattributes InnerClasses
-keepparameternames

-keepattributes Signature 
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#compdfkit lib
-keep class com.compdfkit.core.**{*;}
-keep enum com.compdfkit.core.**{*;}
-keep interface com.compdfkit.core.**{*;}

#compdfkit-ui lib
-keep class com.compdfkit.ui.attribute.**{*;}
-keep enum com.compdfkit.ui.attribute.**{*;}
-keep interface com.compdfkit.ui.attribute.**{*;}

-keep class com.compdfkit.ui.contextmenu.**{*;}
-keep enum com.compdfkit.ui.contextmenu.**{*;}
-keep interface com.compdfkit.ui.contextmenu.**{*;}

-keep class com.compdfkit.ui.proxy.**{*;}
-keep enum com.compdfkit.ui.proxy.**{*;}
-keep interface com.compdfkit.ui.proxy.**{*;}

-keep class com.compdfkit.ui.edit.**{*;}
-keep enum com.compdfkit.ui.edit.**{*;}
-keep interface com.compdfkit.ui.edit.**{*;}

-keep interface com.compdfkit.ui.reader.CPDFAddAnnotCallback{*;}
-keep interface com.compdfkit.ui.reader.IPDFErrorMessageCallback{*;}
-keep enum com.compdfkit.ui.reader.IPDFErrorMessageCallback$ErrorId{*;}
-keep interface com.compdfkit.ui.reader.IReaderViewCallback{*;}
-keep interface com.compdfkit.ui.reader.InkUndoRedoCallback{*;}
-keep interface com.compdfkit.ui.reader.OnFocusedTypeChangedListener{*;}
-keep interface com.compdfkit.ui.reader.OnTouchModeChangedListener{*;}
-keep interface com.compdfkit.ui.reader.IDocumentStatusCallback{*;}
-keep class com.compdfkit.ui.reader.CPDFPageView {
public *;
}
-keep class com.compdfkit.ui.reader.PageView {
public *;
}
-keep class com.compdfkit.ui.reader.CPDFReaderView {
public *;
}
-keep class com.compdfkit.ui.reader.ReaderView {
public *;
}
-keep enum com.compdfkit.ui.reader.CPDFPageView$*{*;}
-keep interface com.compdfkit.ui.reader.CPDFPageView$*{*;}
-keep enum com.compdfkit.ui.reader.PageView$*{*;}
-keep interface com.compdfkit.ui.reader.PageView$*{*;}
-keep enum com.compdfkit.ui.reader.CPDFReaderView$*{*;}
-keep interface com.compdfkit.ui.reader.CPDFReaderView$*{*;}
-keep class com.compdfkit.ui.reader.CPDFReaderView$*{*;}
-keep enum com.compdfkit.ui.reader.ReaderView$*{*;}
-keep interface com.compdfkit.ui.reader.ReaderView$*{*;}


-keep class com.compdfkit.ui.textsearch.**{*;}
-keep enum com.compdfkit.ui.textsearch.**{*;}
-keep interface com.compdfkit.ui.textsearch.**{*;}

-keep class com.compdfkit.ui.utils.**{*;}
-keep enum com.compdfkit.ui.utils.**{*;}
-keep interface com.compdfkit.ui.utils.**{*;}

-keep class com.compdfkit.ui.widget.**{*;}
-keep enum com.compdfkit.ui.widget.**{*;}
-keep interface com.compdfkit.ui.widget.**{*;}

-keep class com.compdfkit.tools.**{*;}
-keep enum com.compdfkit.tools.**{*;}
-keep interface com.compdfkit.tools.**{*;}


#############optimize##############
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
#-allowaccessmodification 

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Preserve some attributes that may be required for reflection.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod


# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick.
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontnote androidx.**
-dontwarn android.support.**
-dontwarn androidx.**

# This class is deprecated, but remains for backward compatibility.
-dontwarn android.util.FloatMath

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep
-keep class androidx.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}
-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# These classes are duplicated between android.jar and org.apache.http.legacy.jar.
-dontnote org.apache.http.**
-dontnote android.net.http.**

# These classes are duplicated between android.jar and core-lambda-stubs.jar.
-dontnote java.lang.invoke.**

-dontwarn com.google.android.material.R$id