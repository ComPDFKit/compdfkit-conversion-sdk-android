# ComPDF Conversion SDK for Android

As part of the KDAN ecosystem, ComPDF Conversion SDK empowers your Android applications to convert PDF files into editable Office documents, images, HTML, and more — through a straightforward Kotlin API. Whether you're building a document viewer, a file management app, or an enterprise productivity tool, you can integrate high-fidelity PDF conversion capabilities directly into your mobile app, preserving layout, tables, and formatting throughout the conversion process.

> If you find this library helpful, please consider giving us a ⭐ **Star** on GitHub! Have feedback or questions? Join the conversation in our [Discussions](https://github.com/ComPDFKit/compdfkit-conversion-sdk-android/discussions).

**Supported Features**

* Convert PDF to Word (.docx)

* Convert PDF to Excel (.xlsx)

* Convert PDF to PowerPoint (.pptx)

* Convert PDF to HTML (.html)

* Convert PDF to CSV (.csv)

* Convert PDF to Image (.png, .jpg, .jpeg, .jpeg2000, .bmp, .tiff, .tga, .gif, .webp)

* Convert PDF to Plain Text (.txt)

* Convert PDF to Rich Text Format (.rtf)

* Convert PDF to Searchable PDF (.pdf)

* Convert PDF to Structured Data (.json)

* Convert PDF to Markdown (.md)

* Convert PDF to OFD (.ofd)

* Optical Character Recognition (OCR)

* Layout Analysis

* Table Recognition

## Table of Contents

* [Why ComPDF Conversion SDK](#why-compdf-conversion-sdk)

* [Conversion Quality Preview](#conversion-quality-preview)

* [Requirements](#requirements)

* [How to Run a Demo](#how-to-run-a-demo)

* [How to Make an Android Program in Kotlin with ComPDF Conversion SDK](#how-to-make-an-android-program-in-kotlin-with-compdf-conversion-sdk)

* [License and Free Trial](#license-and-free-trial)

* [Developer Guides for All Features](#developer-guides-for-all-features)

* [FAQ](#faq)

* [Changelog](#changelog)

* [Technical Support](#technical-support)

## **Why ComPDF Conversion SDK**

- **Mature Technology:** With years of technology accumulation, we have established a complete mechanism of product iteration to offer a continuous guarantee for product competitiveness.  

- **High-Fidelity Conversion:** Designed to preserve layout, tables, text structure, and formatting in the output files.  

- **AI-Enhanced Conversion:** Continuously improved for complex layout analysis, table recognition, OCR, and editable output.

- **Independent Intellectual Property Rights:** Our technology is independent and compliant with ISO, helping enterprises conduct international business without considering copyright risks.  

- **High-quality Service:** We provide 24/7 professional one-to-one technical support, including onsite service and remote assistance via phone and email.

## Conversion Quality Preview

With **AI table recognition, AI layout analysis, and OCR capabilities**, ComPDF Conversion SDK delivers accurate PDF conversion results. Simply select the appropriate conversion parameters based on your document type to achieve accurate document reconstruction. Below, we demonstrate the power of the ComPDF Conversion SDK by converting PDFs to Word, Excel, or searchable PDF files. Contact us to get a demo and experience our product's performance firsthand.

![conversion sdk performance GIF](./Images/conversion%20sdk%20performance%20GIF.gif)

### Convert PDF to Word

ComPDF Conversion SDK accurately restores all content and images, complex structures, paragraph aggregation, natural reading order, text styles, bold formatting, fonts, multi‑column layouts, image-text positioning, ordered lists, and more. Get a fully editable Word document that looks exactly like the original PDF.

![conversion sdk performance GIF](./Images/Performance%20-%20PDF%20to%20Word.png)

### Convert PDF to Excel

Extract standard tables and borderless tables with precision, preserve the content and images inside table cells, and retain text styles such as bold and color. ComPDF Conversion SDK turns PDF tables into fully functional Excel spreadsheets without losing structure or formatting.

![Conversion SDK: PDF to Excel](./Images/Performance%20-%20PDF%20to%20Excel.png)

### Convert Scanned PDF to editable text

Convert scanned documents and scanned PDFs into searchable PDFs while perfectly preserving the original layout and formatting. Every word becomes searchable, locatable, and selectable – ready for copying just like native digital text.

![conversion sdk performance GIF](./Images/Performance%20-%20PDF%20to%20searchable%20PDF.png)

## Requirements

**Download Conversion SDK:**

Download the ComPDF Android Conversion SDK from [GitHub](https://github.com/ComPDFKit/compdfkit-conversion-sdk-android?utm_source=github&utm_medium=compdfkit-conversion-sdk-android&utm_campaign=compdfkit_conversion_sdk_android_repo&ref_platform_id=github_compdfkit).

**System Requirements:**

| Platform | System Requirements                   | Development Environment | Notes                                    |
| -------- | ------------------------------------- | ----------------------- | ---------------------------------------- |
| Android  | Android 7.0 (API Level 24) or higher. | Android Studio          | Only supports arm64-v8a and armeabi-v7a. |

## How to Run a Demo

ComPDF Conversion SDK for Android provides a Kotlin demo sample. You can find this demo in the **"samples"** folder. Follow these steps to run it in Android Studio:

1. Import the **"ComPDFKit_Conversion_Demo"** project into Android Studio.
2. In the toolbar, select **"ComPDFKit_Conversion_Demo"** from the run configurations drop-down menu.
3. From the target device drop-down menu, select the device that you want to run the demo on.
4. Click **Run**.

If you don't have any devices configured, you need to either connect a device via USB or create an AVD to use the Android Emulator.

## How to Make an Android Program in Kotlin with ComPDF Conversion SDK

### Create a New Android Project

Open Android Studio, then click **File** -> **New** -> **New Project**. As shown in Figure 2-1, select **Empty Views Activity**.

Choose the project name, package name, desired location, and the corresponding minimum SDK version, then **click Finish**.



### Add ComPDF Conversion SDK Package

Copy all files from the **"lib" folder** to your project's **"app/main"** directory.  

Add the following line to the `dependencies` block in **"app/build.gradle.kts"**:

```groovy
...

dependencies {
    ...
    implementation(files("lib/ComPDFKitConversion-3.1.0.aar"))

    // For online verification
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("androidx.security:security-crypto:1.1.0-beta01")
    ...
}
```

Sync the Gradle configuration to ensure the ComPDFKit Conversion SDK is added to the project.

## License and Free Trial

### Get Free Trial License

[Contact our sales team](https://www.compdf.com/contact-sales?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android) and we'll send you a 30-day free trial license for ComPDF Conversion SDK.

### Get Commercial License

ComPDF Conversion SDK is a commercial SDK that requires a license for application release. Any documents, sample code, or source code distribution from the released package of ComPDF to any third party is prohibited. To get commercial license for ComPDF Conversion SDK, feel free to [contact our sales team](https://www.compdf.com/contact-sales?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android).

For Android Conversion SDK, the formal license must be bound to your ApplicationId, and each license is only valid for one device ID in development mode.

### Apply the License Key

If you haven't get a license key, please check out [how to obtain a license key](/guides/conversion-sdk/android/requirements). ComPDF Conversion SDK currently supports offline authentication to verify license keys.

*Learn more:* [*What is the authentication mechanism of ComPDF's license?*](https://www.compdf.com/faq/authentication-mechanism-of-compdfkit-license?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android)

**Copy the License Key:**

Accurately obtaining the license key is crucial for the application of the license.

1. In the email you received, locate the XML file containing the license key.
2. Open the XML file, and determine the license type based on the `<type>` field. If `<type>online</type>` is present, it indicates an online license. If `<type>offline</type>` is present or if the field is absent, it indicates an offline license.
- **Online License:**
  
      <?xml version="1.0" encoding="UTF-8" standalone="no"?>
      <license version="1">
          <platform>android</platform>
          <starttime>xxxxxxxx</starttime>
          <endtime>xxxxxxxx</endtime>
          <type>online</type>
          <key>LICENSE_KEY</key>
      </license>

- **Offline License**:
  
  ```xml
  <?xml version="1.0" encoding="UTF-8" standalone="no"?>
  <license version="1">
      <platform>android</platform>
      <starttime>xxxxxxxx</starttime>
      <endtime>xxxxxxxx</endtime>
      <key>LICENSE_KEY</key>
  </license>
  ```
3. Copy the value located at the LICENSE_KEY position within the `<key>LICENSE_KEY</key>` field. This is your license key.

**Apply the License Key**

You can perform offline authentication using the following method:

Before using the classes and methods of the ComPDF Conversion SDK in your project, you need to initialize the SDK with a valid license key. If you don't have a license key, feel free to contact the [ComPDF team](https://www.compdf.com/contact-sales?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android) to obtain one.

```kotlin
// Place the license.xml file in the assets directory of your app.
// The SDK will automatically verify the license when loaded.

// Initialize SDK resources.
ConverterManager.initialize()
```

## Developer Guides for All Features

ComPDF Conversion SDK provides a wide range of customizable conversion options, such as whether to include images or annotations in PDF documents during file conversion, enable OCR, perform layout analysis, and more. Explore the [guides](https://www.compdf.com/guides/conversion-sdk/android/overview?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android) for sample code and learn how to customize conversions for the following features.

- Initialize Library Resources

- Set DocumentAI Model

- Get Conversion Progress

- Cancel Conversion Task

- Select Page Range for Conversion

- Conversion Options: Contain Image & Annotation

- Page Layout Mode

- OCR

- Layout Analysis

- Table Recognition

- Output Font Option

- Convert PDF to Word

- Convert PDF to Excel

- Convert PDF to PowerPoint

- Convert PDF to HTML

- Convert PDF to CSV

- Convert PDF to Image

- Convert PDF to RTF

- Convert PDF to TXT

- Convert PDF to Searchable PDF

- Convert PDF to OFD

- Release library resources

- Extract PDF to JSON

- Extract PDF to Markdown

### FAQ

* Does OCR work on x86 architecture?
  Currently, OCR only works on x64 architecture.
- [More FAQ](https://www.compdf.com/faq?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android)

## Changelog

Go to our [changelog](https://www.compdf.com/conversion-sdk/changelog-android?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android) to keep up with the latest updates, improvements, and bug fixes.

## Technical Support

Thanks for your interest in ComPDF Conversion SDK, the easy-to-use and powerful development solution. If you encounter technical questions or bug issues when using ComPDF Conversion SDK, please submit the problem report to the [ComPDF team](mailto:support@compdf.com). More information as follows would help us to solve your problem:

- ComPDF Conversion SDK product and version.
- Your operating system and IDE version.
- Detailed descriptions of the problem.
- Any other related information, such as an error screenshot.

Home link: [https://www.compdf.com](https://www.compdf.com/?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android)

Technical Support: [https://www.compdf.com/support](https://www.compdf.com/support?utm_source=github_readme_conversionsdk_android&utm_medium=referral&utm_campaign=github_readme_conversionsdk_android)

Email: [support@compdf.com](mailto:support@compdf.com)

