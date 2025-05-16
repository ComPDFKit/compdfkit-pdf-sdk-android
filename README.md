# Introduction

ComPDFKit PDF SDK for Android is developed and maintained by [ComPDF](https://www.compdf.com/), enabling developers to quickly and seamlessly integrate advanced PDF functionalities—such as viewing, editing, annotating, and signing—into any Android application.
The ComPDFKit Android PDF Library provides an easy-to-use Java API that allows direct access to a wide range of PDF features without the need for complex configurations. By [registering for a free ComPDF API account](https://api.compdf.com/signup), developers can process up to 1,000 PDF files monthly.
More Information can be found at: [https://www.compdf.com/guides/pdf-sdk/android/overview](https://www.compdf.com/guides/pdf-sdk/android/overview)

# Table of Content

- [Related](#related)
- [Requirements](#requirements)
- [How to Make an Android PDF Viewer in Java](#how-to-make-an-android-pdf-viewer-in-java)
- [Supports](#support)
- [License](#license) 


# Related

- [ComPDFKit PDF SDK for Android Documentation Guide](https://www.compdf.com/guides/pdf-sdk/android/overview)
- [ComPDFKit PDF Library for iOS](https://github.com/ComPDFKit/compdfkit-pdf-sdk-ios-swift)
- [How to Build an Android PDF Viewer or Editor in Java](https://www.compdf.com/blog/build-an-android-pdf-viewer-or-editor-in-java)
- [Code Samples for Android](https://www.compdf.com/guides/pdf-sdk/android/examples) 
- [ComPDF API Reference](https://api.compdf.com/api-reference/overview)


# Requirements

[ComPDFKit Android PDF SDK](https://www.compdf.com/guides/pdf-sdk/android/overview) supports Android devices running API level 19 or newer and targets the latest stable Android 4.4 or later. In addition, it requires applications to be built with Java 8 language features enabled.

- Android Studio 3.2 or newer (support AndroidX).
- Project specifications.
  - A `minSdkVersion` of `19` or higher.  
  - A `compileSdkVersion` of `30` or higher.  
  - A `targetSdkVersion` of `34` or higher.  
  - Android ABI(s): x86, x86_64, armeabi-v7a, arm64-v8a.

# How to Make an Android PDF Viewer in Java

This section will help you quickly get started with ComPDFKit PDF SDK to make an Android app in Java with step-by-step instructions. Through the following steps, you will get a simple application that can display the contents of a specified PDF file.

## Video Guide:Build an Android PDF Editor in Java

[![image-youtube-20250515](image-android/image-youtube-20250515.png)](https://youtu.be/SgBidb_eYjA?si=_UX7oECMc7NvC_nv)

## Create a New Project

1. Use Android Studio to create a Phone & Tablet project. Here we create a **No Activity** project.

<img src="./image-android/create_project.png" alt="create_project" width="60%" height="60%" />


## Installation

### Integrate With Gradle

1. Open the `settings.gradle` file located in your project's root directory and add the `mavenCentral` repository:

```diff
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
+       mavenCentral()
    }
}
```

2. Open the `build.gradle` file in the application module directory:

<img src="./image-android/2.4.2.1-1.png" alt="2.4.2.1-1" width="40%" height="40%" />

Edit it and add the complete `ComPDFKit SDK` dependency:

```groovy
dependencies {
  implementation 'com.compdf:compdfkit:2.4.0'
  implementation 'com.compdf:compdfkit-ui:2.4.0'
}
```

3. Apply for read and write permissions in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

**Note:** *On your apps that target Android 6.0 or higher, make sure to check for and request read and write permissions to external storage at runtime.*

4. If you use an online license, please add network access permissions in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```


### Integrate Manually

1. Copy ***"ComPDFKit.aar"*** and ***"ComPDFKit-UI.aar"*** to the ***"libs"*** directory of the **app**.

![libs.png](./image-android/libs.png)

2. Add the following code into the **app** dictionary's ***"build.gradle"*** file:

```groovy
...
dependencies {
    /*ComPDFKit SDK*/
    implementation(fileTree('libs'))
    ...
}
...
```

3. Add [ComPDFKit PDF SDK for Android](https://www.compdf.com/blog/compdfkit-for-android) as a dependency to the project. Inside the **app** dictionary's ***"build.gradle"***, add ***"ComPDFKit.aar"***, ***"ComPDFKit-UI.aar"***, and the related support libraries to the `dependencies`. For simplicity, update the dependencies as follows:


```groovy
dependencies {
    ...
    //glide
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'

    implementation 'androidx.documentfile:documentfile:1.0.1'
}
```

4. Apply for read and write permissions in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

**Note:** *On your apps that target Android 6.0 or higher, make sure to check for and request read and write permissions to external storage at runtime.*

5. If you use an online license, please add network access permissions in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```



## Apply the License Key


Add this license in the **AndroidManifest.xml** of the main module. In version **1.13.0**, we introduced a brand-new online authentication license scheme for ComPDFKit SDK. By default, the SDK performs online authentication. If you are using a version prior to **1.13.0**, please refer to the following example to configure the SDK for offline authentication mode:<br/>

* **Online license**

```xml
<!-- Each ComPDFKit license is bound to a specific applicationId -->
<!-- For example: com.compdfkit.pdfviewer -->
<meta-data
    android:name="compdfkit_key_online"
    android:value="Your ComPDFKit Key" />
```

You can also initialize ComPDFKit SDK in code using:

```java
CPDFSdk.init(context, "your compdfkit license", false);
```

* **Offline license**

```xml
<!-- Each ComPDFKit license is bound to a specific applicationId -->
<!-- For example: com.compdfkit.pdfviewer -->
<meta-data
    android:name="compdfkit_key"
    android:value="Your ComPDFKit Key" />
```

You can also initialize ComPDFKit SDK in code using:

```java
CPDFSdk.init(context, "your compdfkit license");
```



## Add Proguard Rules

In the `proguard-rules.pro` file, please add the obfuscation configuration information for `compdfkit` as follows:

```
-keep class com.compdfkit.ui.** {*;}
-keep class com.compdfkit.core.** {*;}
```



## Display a PDF Document

1. Copy a PDF document into the **assets** directory of your Android project. For example, import the file ***"Quick Start Guide.pdf"*** to the path **src/main/assets**.


![structure.png](./image-android/structure.png)

2. Create a new **Empty Activity** under your package, and set the activity name to **MainActivity**.


<img src="./image-android/new_activity.png" alt="new_activity" width="40%" height="40%" />

Android Studio will automatically generate a source file called ***"MainActivity.java"*** and a layout file called ***"activity_main.xml"***.

The source file:

![activity_java.png](./image-android/activity_java.png)

The layout file:

![layout.png](./image-android/layout.png)

3. Create a `CPDFReaderView` in your ***"activity_main.xml"*** to display the contents of the PDF document:

```xml
<!-- Your activity_main.xml file -->

<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!-- Create a CPDFReaderView -->
    <com.compdfkit.ui.reader.CPDFReaderView
        android:id="@+id/readerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

Get the `CPDFReaderView` from the layout or create a `CPDFReaderView` directly in the code in the corresponding ***MainActivity.java*** file:

```Java
// Your MainActivity.java file

package com.compdfkit.pdfviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.compdfkit.ui.reader.CPDFReaderView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get CPDFReaderView from xml.
        CPDFReaderView readerView = findViewById(R.id.readerview);
        // Code to create CPDFReaderView.
        // CPDFDocument readerView = new CPDFReaderView(content);
    }
}
```
4. Open the document. This is a time-consuming process, so it needs to be executed in a **sub-thread**. After the document is opened successfully, the UI that renders the PDF is initiated:

```Java
// Your MainActivity.java file

... //imports

public class MainActivity extends AppCompatActivity {

    // Copy the PDF file from the assets folder to the cache folder.
    private void copyPdfFromAssetsToCache(String fileName) {
        try {
            InputStream inputStream = getAssets().open(fileName);
            File outputFile = new File(getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CPDFReaderView readerView = findViewById(R.id.readerview);
        // Code to create CPDFReaderView.
        // CPDFDocument readerView = new CPDFReaderView(content);

        //Create a document object.
        CPDFDocument document = new CPDFDocument(this);
        

        new Thread(() -> {
            String fileName = "Quick Start Guide.pdf";
            copyPdfFromAssetsToCache(fileName);

            File file = new File(getCacheDir(), fileName);
            String filePath = file.getAbsolutePath();

            //Open document.
            CPDFDocument.PDFDocumentError error = document.open(filePath);
            if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorPassword) {
                //The document is encrypted and requires a password to open.
                error = document.open(filePath, "password");
            }

            if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
                //The document is opened successfully and data can be parsed and manipulated.
            } else {
                //The PDF file failed to open. You can refer to the API file for specific error
            }
        }).start();
    }
}
```

5. Set the basic properties of `CPDFReaderView` :

```Java
// Your MainActivity.java file

... // imports

public class MainActivity extends AppCompatActivity {
    // Create a handler to run the code on the main thread.
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
...
    if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
        // The document is opened successfully and data can be parsed and manipulated.
        mainThreadHandler.post(() -> {
            // Set the document content for UI.
            readerView.setPDFDocument(document);
        });
    } else {
        // The PDF file failed to open. You can refer to the API file for specific error
    }
...
}
```

6. Your code may resemble the following at this stage: 

```Java
// Your MainActivity.java file

... // imports

public class MainActivity extends AppCompatActivity {
    // Create a handler to run the code on the main thread.
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    // Copy the PDF file from the assets folder to the cache folder.
    private void copyPdfFromAssetsToCache(String fileName) {
        try {
            InputStream inputStream = getAssets().open(fileName);
            File outputFile = new File(getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CPDFReaderView readerView = findViewById(R.id.readerview);

        //Create a document object.
        CPDFDocument document = new CPDFDocument(this);

        new Thread(() -> {
            String fileName = "Quick Start Guide.pdf";
            copyPdfFromAssetsToCache(fileName);

            File file = new File(getCacheDir(), fileName);
            String filePath = file.getAbsolutePath();

            //Open document.
            CPDFDocument.PDFDocumentError error = document.open(filePath);
            if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorPassword) {
                //The document is encrypted and requires a password to open.
                error = document.open(filePath, "password");
            }

            if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
                //The document is opened successfully and data can be parsed and manipulated.
                mainThreadHandler.post(() -> {
                    //Set the document to the reader view.
                    readerView.setPDFDocument(document);
                });
            } else {
                //The PDF file failed to open. You can refer to the API file for specific error
            }
        }).start();
    }
}
```

```xml
<!-- Your activity_main.xml file -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.compdfkit.ui.reader.CPDFReaderView
        android:id="@+id/readerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

7. Run the application.

<img src="./image-android/screen_shot.png" alt="screen_shot.png" width="25%" height="25%" />

Now, with the help of ComPDFKit, you can get a simple application to display a PDF file.

# Support

ComPDF offers professional technical support and 5*24 responsive service.

- For detailed information, please visit our [Guides](https://www.compdf.com/guides/pdf-sdk/android/overview) page.

- Stay updated with the latest improvements through our [Changelog](https://www.compdf.com/pdf-sdk/changelog-android).

- For technical assistance, please reach out to our [Technical Support](https://www.compdf.com/support).

- To get more details and an accurate quote, please contact our [Sales Team](https://compdf.com/contact-us) or [Send an Email](mailto:support@compdf.com) to us.




# License

ComPDF offers developers a [30-day free trial license](https://www.compdf.com/pricing) for free testing your Android projects. Additionally, you'll have access to a fully-featured product with no limitations on file or user count. 
