# ComPDFKit SDK Tools

## Overview
The `ComPDFKit_Tools` module is an integrated part of the `ComPDFKit SDK`, offering a complete set of common PDF functionalities for quick PDF viewing and editing. If the interface provided by the `ComPDFKit_Tools` module does not meet your product requirements, you can also integrate the `ComPDFKit SDK` yourself to achieve the desired UI. Please note that the functionalities presented by this module do not represent all the capabilities of the `ComPDFKit SDK`. If you cannot find the feature you need, please contact our sales team.

<img src="screenshots/img_1.jpg" style="zoom: 15%;" />

## Add the ComPDFKit PDF SDK Package

In addition to the `ComPDFKit_Tools` module source code, we also provide a Gradle-based integration option. To ensure compatibility, please make sure the tools module version matches the `ComPDFKit SDK` version.

```diff
dependencies {
  implementation 'com.compdf:compdfkit:2.1.3'
  implementation 'com.compdf:compdfkit-ui:2.1.3'
+  implementation 'com.compdf:compdfkit-tools:2.1.3'
}
```

## Usage

The ComPDFKit_Tools module provides two main components: [CPDFDocumentActivity](./src/main/java/com/compdfkit/tools/common/pdf/CPDFDocumentActivity.java) and [CPDFDocumentFragment.java](./src/main/java/com/compdfkit/tools/common/pdf/CPDFDocumentFragment.java). These components enable you to easily implement PDF functionalities.

* **CPDFDocumentActivity.java**

Use CPDFDocumentActivity to display a PDF document:
```java
CPDFConfiguration configuration = CPDFConfigurationUtils.normalConfig(fragment.getContext(), "tools_default_configuration.json");

// Example 1:
CPDFDocumentActivity.startActivity(context, uri, password, configuration);

// Example 2:
CPDFDocumentActivity.startActivity(context, filePath, password, configuration);
```

* **CPDFDocumentFragment.java**

Use CPDFDocumentFragment to load a PDF document into a Fragment:
```java
CPDFConfiguration configuration = CPDFConfigurationUtils.normalConfig(fragment.getContext(), "tools_default_configuration.json");

// Example 1:
CPDFDocumentFragment documentFragment = CPDFDocumentFragment.newInstance(
  filePath,
  password,
  configuration);

// Example 2:
CPDFDocumentFragment documentFragment = CPDFDocumentFragment.newInstance(
  uri,
  password,
  configuration);
```



