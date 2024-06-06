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
# 指定代码的压缩级别 0 - 7(指定代码进行迭代优化的次数，在Android里面默认是5，这条指令也只有在可以优化时起作用。)
-optimizationpasses 5
# 混淆时不会产生形形色色的类名(混淆时不使用大小写混合类名)
#-dontusemixedcaseclassnames
# 指定不去忽略非公共的库类(不跳过library中的非public的类)
-dontskipnonpubliclibraryclasses
# 指定不去忽略包可见的库类的成员
#-dontskipnonpubliclibraryclassmembers
#不进行优化，建议使用此选项，
-dontoptimize
 # 不进行预校验,Android不需要,可加快混淆速度。
-dontpreverify
# 屏蔽警告
-ignorewarnings
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆
-keepattributes *Annotation*
# 避免混淆泛型, 这在JSON实体映射时非常重要
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
 #优化时允许访问并修改有修饰符的类和类的成员，这可以提高优化步骤的结果。
# 比如，当内联一个公共的getter方法时，这也可能需要外地公共访问。
# 虽然java二进制规范不需要这个，要不然有的虚拟机处理这些代码会有问题。当有优化和使用-repackageclasses时才适用。
#指示语：不能用这个指令处理库中的代码，因为有的类和类成员没有设计成public ,而在api中可能变成public
#-allowaccessmodification
#当有优化和使用-repackageclasses时才适用。
-repackageclasses ''
 # 混淆时记录日志(打印混淆的详细信息)
 # 这句话能够使我们的项目混淆后产生映射文件
 # 包含有类名->混淆后类名的映射关系
-verbose

-keepattributes InnerClasses

# 枚举类不能被混淆
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}


-keepattributes Signature #泛型
#native方法不混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#v4包不混淆
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

#compdfkit库
-keep class com.compdfkit.core.**{*;}
-keep enum com.compdfkit.core.**{*;}
-keep interface com.compdfkit.core.**{*;}

#compdfkit-ui库
-keep class com.compdfkit.ui.attribute.**{*;}
-keep enum com.compdfkit.ui.attribute.**{*;}
-keep interface com.compdfkit.ui.attribute.**{*;}

-keep class com.compdfkit.ui.contextmenu.**{*;}
-keep enum com.compdfkit.ui.contextmenu.**{*;}
-keep interface com.compdfkit.ui.contextmenu.**{*;}

-keep class com.compdfkit.ui.proxy.**{*;}
-keep enum com.compdfkit.ui.proxy.**{*;}
-keep interface com.compdfkit.ui.proxy.**{*;}

#不能除去混淆，所有内部逻辑全在里面，防止外部浏览后能知道我们的逻辑和做法
#-keep class com.compdfkit.ui.internal.**{*;}
#-keep enum com.compdfkit.ui.internal.**{*;}
#-keep interface com.compdfkit.ui.internal.**{*;}

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
#-allowaccessmodification //不允许改变方法声明

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