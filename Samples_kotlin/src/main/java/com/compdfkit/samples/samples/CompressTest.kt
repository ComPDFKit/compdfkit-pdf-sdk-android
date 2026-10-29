/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.samples

import com.compdfkit.core.common.CPDFDocumentException
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.document.CPDFDocument.CPDFCompressAlgorithm
import com.compdfkit.core.document.CPDFDocument.CPDFImageOptimizeOptions
import com.compdfkit.core.document.CPDFDocument.CPDFOptimizeOptions
import com.compdfkit.core.document.CPDFDocument.CPDFOptimizeResult
import com.compdfkit.core.document.CPDFDocument.OptimizeListener
import com.compdfkit.core.document.CPDFDocument.PDFDocumentCompressLevel
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File
import java.util.Locale

/**
 * Demonstrates the PDF compression / optimization API.
 *
 * Two compression entry points exist:
 *  - [CPDFDocument.optimizeSaveAsCopy], driven by [CPDFOptimizeOptions];
 *  - the legacy [CPDFDocument.saveAsCompressOptimize], driven by a single quality value.
 *
 * Three behaviours are worth keeping in mind while reading the code below:
 *  - a successful `optimizeSaveAsCopy` / `saveAsCompressOptimize` closes the source document,
 *    so every section reopens its own copy;
 *  - the output path may not be the path the document was opened from, otherwise
 *    `CANNOT_SAVEAS_CURRENTFILE` is thrown;
 *  - the legacy quality value must be inside `(0, 120]`.
 */
class CompressTest : PDFSamples() {

    /** Asset name to the cached copy every section opens. */
    private val assetPaths = HashMap<String, String>()

    init {
        setTitle(R.string.compress_test_title)
        setDescription(R.string.compress_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        compressWithQualityPresets()
        compressWithCustomFlags()
        compressWithImageAlgorithms()
        compressWithProgressAndCancel()
        compressWithLegacyLevels()
        printFooter()
    }

    // ------------------------------------------------------------------
    // Samples 1 : quality presets
    // ------------------------------------------------------------------

    /**
     * Compresses the same document three times with a low / standard / high image quality
     * preset. A preset is nothing more than a [CPDFImageOptimizeOptions] applied to the colour,
     * greyscale and monochrome image sets:
     *  - `upperPpi` — images above this resolution are down-sampled, the others are left alone;
     *  - `targetPpi` — the resolution they are down-sampled to;
     *  - `quality` — the lossy re-encoding quality, 0 ~ 100.
     */
    private fun compressWithQualityPresets() {
        outputListener?.println()
        outputListener?.println("Samples 1 : Compress with the low / standard / high quality presets")

        compressWithPreset(
            IMAGE_DOCUMENT, "Low", LOW_UPPER_PPI, LOW_TARGET_PPI, LOW_QUALITY,
            "Compress/CompressTest_Low.pdf"
        )
        compressWithPreset(
            IMAGE_DOCUMENT, "Standard", STANDARD_UPPER_PPI, STANDARD_TARGET_PPI, STANDARD_QUALITY,
            "Compress/CompressTest_Standard.pdf"
        )
        compressWithPreset(
            IMAGE_DOCUMENT, "High", HIGH_UPPER_PPI, HIGH_TARGET_PPI, HIGH_QUALITY,
            "Compress/CompressTest_High.pdf"
        )

        outputListener?.println()
        outputListener?.println("The same presets applied to a text only document :")
        compressWithPreset(
            TEXT_DOCUMENT, "Standard", STANDARD_UPPER_PPI, STANDARD_TARGET_PPI, STANDARD_QUALITY,
            "Compress/CompressTest_TextOnly.pdf"
        )

        printDividingLine()
    }

    private fun compressWithPreset(
        assetName: String, presetName: String, upperPpi: Int, targetPpi: Int,
        quality: Int, outputName: String
    ) {
        val document = openAsset(assetName) ?: return
        val sourceSize = assetSize(assetName)

        val options = CPDFOptimizeOptions()
        options.imageQuality = quality.toFloat()
        options.colorImageOptions = CPDFImageOptimizeOptions(
            upperPpi, targetPpi, quality, BLOCK_SIZE, CPDFCompressAlgorithm.kJPX
        )
        options.grayscaleImageOptions = CPDFImageOptimizeOptions(
            upperPpi, targetPpi, quality, BLOCK_SIZE, CPDFCompressAlgorithm.kJPX
        )
        options.monochromeImageOptions = CPDFImageOptimizeOptions(
            upperPpi, targetPpi, quality, BLOCK_SIZE, CPDFCompressAlgorithm.kJBIG2
        )

        val output = File(outputDir(), outputName)
        val label = "$presetName ($upperPpi -> $targetPpi ppi, quality $quality)"
        optimizeSaveAsCopy(document, options, output, label, sourceSize, null)
    }

    // ------------------------------------------------------------------
    // Samples 2 : custom optimize flags
    // ------------------------------------------------------------------

    /**
     * [CPDFOptimizeOptions.optimizeFlags] is a bit mask that selects which clean-up steps run.
     * [CPDFOptimizeOptions.DEFAULT_OPTIMIZE_FLAGS] is a safe starting point; flags are then
     * added with `or` and removed with `and inv()`.
     */
    private fun compressWithCustomFlags() {
        outputListener?.println()
        outputListener?.println("Samples 2 : Compress with a custom set of optimize flags")

        val document = openAsset(IMAGE_DOCUMENT)
        if (document == null) {
            printDividingLine()
            return
        }
        val sourceSize = assetSize(IMAGE_DOCUMENT)

        val options = CPDFOptimizeOptions()
        outputListener?.println("Default flags : 0x${Integer.toHexString(options.optimizeFlags)}")

        // Drop everything the reader does not strictly need to render the pages.
        options.optimizeFlags = options.optimizeFlags or CPDFOptimizeOptions.REMOVE_BOOKMARKS
        options.optimizeFlags = options.optimizeFlags or CPDFOptimizeOptions.REMOVE_ANNOTATIONS
        options.optimizeFlags = options.optimizeFlags or CPDFOptimizeOptions.REMOVE_METADATA
        options.optimizeFlags = options.optimizeFlags or CPDFOptimizeOptions.REMOVE_DOCUMENT_INFO
        options.optimizeFlags = options.optimizeFlags or CPDFOptimizeOptions.REMOVE_JAVASCRIPT_ACTIONS
        options.optimizeFlags = options.optimizeFlags or CPDFOptimizeOptions.REMOVE_PAGE_THUMBNAILS
        // Linearize the result so a viewer can render page 1 before the whole file is downloaded.
        options.optimizeFlags = options.optimizeFlags or CPDFOptimizeOptions.FAST_WEB_VIEW
        // Keep the embedded fonts, dropping them would break the rendering on devices
        // that do not have a matching system font.
        options.optimizeFlags = options.optimizeFlags and CPDFOptimizeOptions.REMOVE_EMBEDDED_FONTS.inv()

        outputListener?.println("Custom flags  : 0x${Integer.toHexString(options.optimizeFlags)}")
        outputListener?.println(
            "Removing bookmarks, annotations, metadata, document info, " +
                    "JavaScript actions and page thumbnails, and enabling fast web view."
        )

        outputListener?.println("Annotations in the source : ${annotationCount(document)}")

        val output = File(outputDir(), "Compress/CompressTest_CustomFlags.pdf")
        optimizeSaveAsCopy(document, options, output, "Custom flags", sourceSize, null)

        // Reopen the result to prove the content really is gone.
        val result = CPDFDocument(context())
        result.open(output.absolutePath)
        if (result.isValid) {
            outputListener?.println("Annotations in the result : ${annotationCount(result)}")
        }
        result.close()

        printDividingLine()
    }

    // ------------------------------------------------------------------
    // Samples 3 : image compression algorithms
    // ------------------------------------------------------------------

    /**
     * Each image set can use its own codec. The three that matter in practice :
     *  - [CPDFCompressAlgorithm.kJPX] — JPEG 2000, the best ratio for photographic colour
     *    images, and the default for colour and greyscale;
     *  - [CPDFCompressAlgorithm.kDCT] — baseline JPEG, faster and far more widely supported
     *    by old readers;
     *  - [CPDFCompressAlgorithm.kJBIG2] — bi-level codec, the only sensible choice for scanned
     *    black and white pages, and the default for monochrome.
     *
     * [CPDFCompressAlgorithm.kFlate] is lossless and is the fallback for artwork that must not
     * be degraded.
     */
    private fun compressWithImageAlgorithms() {
        outputListener?.println()
        outputListener?.println("Samples 3 : Choose the image compression algorithm per image type")

        val document = openAsset(IMAGE_DOCUMENT)
        if (document == null) {
            printDividingLine()
            return
        }
        val sourceSize = assetSize(IMAGE_DOCUMENT)

        val options = CPDFOptimizeOptions()
        options.optimizeFlags = options.optimizeFlags or CPDFOptimizeOptions.COMPRESS_IMAGES
        // Photographic content : JPEG, for maximum reader compatibility.
        options.colorImageOptions = CPDFImageOptimizeOptions(
            STANDARD_UPPER_PPI, STANDARD_TARGET_PPI, STANDARD_QUALITY, BLOCK_SIZE,
            CPDFCompressAlgorithm.kDCT
        )
        // Greyscale : JPEG 2000, better ratio at the same perceived quality.
        options.grayscaleImageOptions = CPDFImageOptimizeOptions(
            STANDARD_UPPER_PPI, STANDARD_TARGET_PPI, STANDARD_QUALITY, BLOCK_SIZE,
            CPDFCompressAlgorithm.kJPX
        )
        // Scanned bi-level pages : JBIG2.
        options.monochromeImageOptions = CPDFImageOptimizeOptions(
            HIGH_UPPER_PPI * 2, STANDARD_TARGET_PPI * 2, STANDARD_QUALITY, BLOCK_SIZE,
            CPDFCompressAlgorithm.kJBIG2
        )
        // Streams that are still uncompressed, or use the obsolete LZW filter, are re-deflated.
        options.optimizeFlags =
            options.optimizeFlags or CPDFOptimizeOptions.USE_FLATE_FOR_UNCOMPRESSED_STREAMS
        options.optimizeFlags =
            options.optimizeFlags or CPDFOptimizeOptions.REPLACE_LZW_WITH_FLATE

        outputListener?.println("Color     : ${options.colorImageOptions.compressAlgorithm}")
        outputListener?.println("Grayscale : ${options.grayscaleImageOptions.compressAlgorithm}")
        outputListener?.println("Monochrome: ${options.monochromeImageOptions.compressAlgorithm}")

        val output = File(outputDir(), "Compress/CompressTest_ImageAlgorithm.pdf")
        optimizeSaveAsCopy(document, options, output, "Per type algorithm", sourceSize, null)

        printDividingLine()
    }

    // ------------------------------------------------------------------
    // Samples 4 : progress and cancellation
    // ------------------------------------------------------------------

    /**
     * [OptimizeListener] is polled by the native optimizer : `onUpdate` reports the page being
     * processed and `isCancelled` lets the caller stop the run. A cancelled run reports
     * `cancelled == true` and leaves no usable output file, so the caller is expected to delete it.
     *
     * [CPDFOptimizeResult.stepsUs] and [CPDFOptimizeResult.saveUs] split the elapsed time between
     * the optimization steps and the final serialization.
     */
    private fun compressWithProgressAndCancel() {
        outputListener?.println()
        outputListener?.println("Samples 4 : Report the progress, then cancel a running compression")

        val document = openAsset(IMAGE_DOCUMENT)
        if (document == null) {
            printDividingLine()
            return
        }
        val sourceSize = assetSize(IMAGE_DOCUMENT)
        val pageCount = document.pageCount

        val output = File(outputDir(), "Compress/CompressTest_Progress.pdf")
        val result = optimizeSaveAsCopy(
            document, CPDFOptimizeOptions(), output, "With progress", sourceSize,
            object : OptimizeListener {
                override fun isCancelled(): Boolean = false

                override fun onUpdate(pageIndex: Int) {
                    outputListener?.println("  progress : page ${pageIndex + 1} / $pageCount")
                }
            })
        if (result != null) {
            outputListener?.println("Pages processed : ${result.pagesProcessed}")
            outputListener?.println(
                String.format(Locale.US, "Optimize steps  : %.2f ms", result.stepsUs / 1000F)
            )
            outputListener?.println(
                String.format(Locale.US, "Serialization   : %.2f ms", result.saveUs / 1000F)
            )
        }

        // Second run, cancelled as soon as the first page has been handed to the listener.
        outputListener?.println()
        outputListener?.println("Running again and cancelling after the first page :")
        val cancelDocument = openAsset(IMAGE_DOCUMENT)
        if (cancelDocument == null) {
            printDividingLine()
            return
        }
        val cancelOutput = File(outputDir(), "Compress/CompressTest_Cancelled.pdf")
        cancelOutput.parentFile?.mkdirs()
        try {
            val cancelResult = cancelDocument.optimizeSaveAsCopy(
                cancelOutput.absolutePath, CPDFOptimizeOptions(),
                object : OptimizeListener {

                    private var cancelRequested = false

                    override fun isCancelled(): Boolean = cancelRequested

                    override fun onUpdate(pageIndex: Int) {
                        cancelRequested = true
                    }
                })
            outputListener?.println("Success   : ${cancelResult.isSuccess}")
            outputListener?.println("Cancelled : ${cancelResult.isCancelled}")
            outputListener?.println("Pages processed before stopping : ${cancelResult.pagesProcessed}")
            if (!cancelResult.isSuccess) {
                // The partial file is not a valid PDF, drop it.
                FileUtils.deleteFile(cancelOutput)
            }
        } catch (e: CPDFDocumentException) {
            outputListener?.println("Compression failed : ${e.message}")
        } finally {
            cancelDocument.close()
        }

        printDividingLine()
    }

    // ------------------------------------------------------------------
    // Samples 5 : legacy compress levels
    // ------------------------------------------------------------------

    /**
     * The legacy [CPDFDocument.saveAsCompressOptimize] exposes a single quality knob through
     * [PDFDocumentCompressLevel], from `MICRO` (30) to `HIGH` (120). It is kept for
     * compatibility; new code should prefer [CPDFDocument.optimizeSaveAsCopy].
     */
    private fun compressWithLegacyLevels() {
        outputListener?.println()
        outputListener?.println("Samples 5 : Compress with the legacy quality levels")

        for (level in PDFDocumentCompressLevel.values()) {
            legacyCompress(level)
        }

        // Rejected before reaching the native layer : the quality has to be inside (0, 120].
        val document = openAsset(IMAGE_DOCUMENT)
        if (document != null) {
            val output = File(outputDir(), "Compress/CompressTest_Invalid.pdf")
            try {
                document.saveAsCompressOptimize(output.absolutePath, 200)
            } catch (e: CPDFDocumentException) {
                outputListener?.println("Quality 200 rejected : ${e.message}")
            }
            document.close()
        }

        printDividingLine()
    }

    private fun legacyCompress(level: PDFDocumentCompressLevel) {
        val document = openAsset(IMAGE_DOCUMENT) ?: return
        val sourceSize = assetSize(IMAGE_DOCUMENT)

        val output = File(outputDir(), "Compress/CompressTest_Legacy_${level.name}.pdf")
        output.parentFile?.mkdirs()
        try {
            // A successful save closes the document, exactly like optimizeSaveAsCopy does.
            val success = document.saveAsCompressOptimize(output.absolutePath, level) { }
            if (success) {
                addFileList(output.absolutePath)
                printSizeCompare("${level.name} (quality ${level.quality})", sourceSize, output)
            } else {
                outputListener?.println("${level.name} : compression failed")
            }
        } catch (e: CPDFDocumentException) {
            outputListener?.println("${level.name} : compression failed, ${e.message}")
        } finally {
            document.close()
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Runs [CPDFDocument.optimizeSaveAsCopy] and prints the size comparison. The document is
     * closed on the way out : the SDK already closes it when the save succeeds, and closing
     * twice is harmless.
     *
     * @return The optimization result, or null when the save threw.
     */
    private fun optimizeSaveAsCopy(
        document: CPDFDocument, options: CPDFOptimizeOptions, output: File, label: String,
        sourceSize: Long, listener: OptimizeListener?
    ): CPDFOptimizeResult? {
        output.parentFile?.mkdirs()
        return try {
            val result = document.optimizeSaveAsCopy(output.absolutePath, options, listener)
            if (result.isSuccess) {
                addFileList(output.absolutePath)
                printSizeCompare(label, sourceSize, output)
            } else {
                outputListener?.println("$label : compression failed")
            }
            result
        } catch (e: CPDFDocumentException) {
            outputListener?.println("$label : compression failed, ${e.message}")
            null
        } finally {
            document.close()
        }
    }

    /**
     * Opens a copy of an asset in the cache directory. Compression always writes to a new path,
     * so the source is never modified.
     */
    private fun openAsset(assetName: String): CPDFDocument? {
        val document = CPDFDocument(context())
        document.open(assetPath(assetName))
        if (!document.isValid) {
            outputListener?.println("Failed to open $assetName")
            document.close()
            return null
        }
        return document
    }

    private fun annotationCount(document: CPDFDocument): Int {
        var count = 0
        for (i in 0 until document.pageCount) {
            count += document.pageAtIndex(i).annotCount
        }
        return count
    }

    private fun assetSize(assetName: String): Long = File(assetPath(assetName)).length()

    /**
     * Copies an asset to the cache directory once and reuses that copy afterwards.
     * `getAssetsTempFile` recreates the file on every call, which would pull the file out
     * from under a document that is still open.
     */
    private fun assetPath(assetName: String): String =
        assetPaths.getOrPut(assetName) { getAssetsTempFile(context(), assetName) ?: "" }

    private fun printSizeCompare(label: String, sourceSize: Long, output: File) {
        val compressedSize = output.length()
        if (sourceSize <= 0 || compressedSize <= 0) {
            outputListener?.println("$label : ${formatSize(compressedSize)}")
            return
        }
        val saved = 100F * (sourceSize - compressedSize) / sourceSize
        outputListener?.println(
            String.format(
                Locale.US, "%s : %s -> %s (%.1f%% saved), %s",
                label, formatSize(sourceSize), formatSize(compressedSize), saved, output.name
            )
        )
    }

    private fun formatSize(bytes: Long): String {
        if (bytes < 1024) {
            return "$bytes B"
        }
        return String.format(Locale.US, "%.1f KB", bytes / 1024F)
    }

    companion object {
        /**
         * A 1.4 MB document carrying images and annotations, the kind of file that actually
         * benefits from image down-sampling and from the content removal flags.
         */
        private const val IMAGE_DOCUMENT = "ComPDFKit_Annotations_Sample_File.pdf"

        /** Mostly text document, used as a reference to show what image compression cannot do. */
        private const val TEXT_DOCUMENT = "CommonFivePage.pdf"

        /** Quality presets, mirroring the ones used by the compression dialog in ComPDFKit_Tools. */
        private const val LOW_UPPER_PPI = 120
        private const val LOW_TARGET_PPI = 96
        private const val LOW_QUALITY = 30
        private const val STANDARD_UPPER_PPI = 180
        private const val STANDARD_TARGET_PPI = 150
        private const val STANDARD_QUALITY = 45
        private const val HIGH_UPPER_PPI = 220
        private const val HIGH_TARGET_PPI = 150
        private const val HIGH_QUALITY = 75

        /** Tile size used when the image is re-encoded, in pixels. */
        private const val BLOCK_SIZE = 256
    }
}
