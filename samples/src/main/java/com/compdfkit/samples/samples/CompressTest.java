/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFDocument.CPDFCompressAlgorithm;
import com.compdfkit.core.document.CPDFDocument.CPDFImageOptimizeOptions;
import com.compdfkit.core.document.CPDFDocument.CPDFOptimizeOptions;
import com.compdfkit.core.document.CPDFDocument.CPDFOptimizeResult;
import com.compdfkit.core.document.CPDFDocument.CompressListener;
import com.compdfkit.core.document.CPDFDocument.OptimizeListener;
import com.compdfkit.core.document.CPDFDocument.PDFDocumentCompressLevel;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Demonstrates the PDF compression / optimization API.
 *
 * <p>Two compression entry points exist:
 * <ul>
 *     <li>{@link CPDFDocument#optimizeSaveAsCopy(String, CPDFOptimizeOptions, OptimizeListener)},
 *     driven by {@link CPDFOptimizeOptions};</li>
 *     <li>the legacy {@link CPDFDocument#saveAsCompressOptimize(String, PDFDocumentCompressLevel, CompressListener)},
 *     driven by a single quality value.</li>
 * </ul>
 *
 * <p>Three behaviours are worth keeping in mind while reading the code below:
 * <ul>
 *     <li>a successful {@code optimizeSaveAsCopy} / {@code saveAsCompressOptimize} closes the source
 *     document, so every section reopens its own copy;</li>
 *     <li>the output path may not be the path the document was opened from, otherwise
 *     {@link CPDFDocumentException.ErrType#CANNOT_SAVEAS_CURRENTFILE} is thrown;</li>
 *     <li>the legacy quality value must be inside {@code (0, 120]}.</li>
 * </ul>
 */
public class CompressTest extends PDFSamples {

    /**
     * A 1.4 MB document carrying images and annotations, the kind of file that actually
     * benefits from image down-sampling and from the content removal flags.
     */
    private static final String IMAGE_DOCUMENT = "ComPDFKit_Annotations_Sample_File.pdf";

    /** Mostly text document, used as a reference to show what image compression cannot do. */
    private static final String TEXT_DOCUMENT = "CommonFivePage.pdf";

    /** Quality presets, mirroring the ones used by the compression dialog in ComPDFKit_Tools. */
    private static final int LOW_UPPER_PPI = 120;
    private static final int LOW_TARGET_PPI = 96;
    private static final int LOW_QUALITY = 30;
    private static final int STANDARD_UPPER_PPI = 180;
    private static final int STANDARD_TARGET_PPI = 150;
    private static final int STANDARD_QUALITY = 45;
    private static final int HIGH_UPPER_PPI = 220;
    private static final int HIGH_TARGET_PPI = 150;
    private static final int HIGH_QUALITY = 75;

    /** Tile size used when the image is re-encoded, in pixels. */
    private static final int BLOCK_SIZE = 256;

    /** Asset name to the cached copy every section opens. */
    private final Map<String, String> assetPaths = new HashMap<>();

    public CompressTest() {
        setTitle(R.string.compress_test_title);
        setDescription(R.string.compress_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        compressWithQualityPresets();
        compressWithCustomFlags();
        compressWithImageAlgorithms();
        compressWithProgressAndCancel();
        compressWithLegacyLevels();
        printFooter();
    }

    // ------------------------------------------------------------------
    // Samples 1 : quality presets
    // ------------------------------------------------------------------

    /**
     * Compresses the same document three times with a low / standard / high image
     * quality preset. A preset is nothing more than an {@link CPDFImageOptimizeOptions}
     * applied to the colour, greyscale and monochrome image sets:
     * <ul>
     *     <li>{@code upperPpi} — images above this resolution are down-sampled, the others are left alone;</li>
     *     <li>{@code targetPpi} — the resolution they are down-sampled to;</li>
     *     <li>{@code quality} — the lossy re-encoding quality, 0 ~ 100.</li>
     * </ul>
     */
    private void compressWithQualityPresets() {
        outputListener.println();
        outputListener.println("Samples 1 : Compress with the low / standard / high quality presets");

        compressWithPreset("Low", LOW_UPPER_PPI, LOW_TARGET_PPI, LOW_QUALITY,
                "Compress/CompressTest_Low.pdf");
        compressWithPreset("Standard", STANDARD_UPPER_PPI, STANDARD_TARGET_PPI, STANDARD_QUALITY,
                "Compress/CompressTest_Standard.pdf");
        compressWithPreset("High", HIGH_UPPER_PPI, HIGH_TARGET_PPI, HIGH_QUALITY,
                "Compress/CompressTest_High.pdf");

        outputListener.println();
        outputListener.println("The same presets applied to a text only document :");
        compressWithPreset(TEXT_DOCUMENT, "Standard", STANDARD_UPPER_PPI, STANDARD_TARGET_PPI,
                STANDARD_QUALITY, "Compress/CompressTest_TextOnly.pdf");

        printDividingLine();
    }

    private void compressWithPreset(String presetName, int upperPpi, int targetPpi, int quality,
                                    String outputName) {
        compressWithPreset(IMAGE_DOCUMENT, presetName, upperPpi, targetPpi, quality, outputName);
    }

    private void compressWithPreset(String assetName, String presetName, int upperPpi, int targetPpi,
                                    int quality, String outputName) {
        CPDFDocument document = openAsset(assetName);
        if (document == null) {
            return;
        }
        long sourceSize = assetSize(assetName);

        CPDFOptimizeOptions options = new CPDFOptimizeOptions();
        options.imageQuality = quality;
        options.colorImageOptions = new CPDFImageOptimizeOptions(upperPpi, targetPpi, quality,
                BLOCK_SIZE, CPDFCompressAlgorithm.kJPX);
        options.grayscaleImageOptions = new CPDFImageOptimizeOptions(upperPpi, targetPpi, quality,
                BLOCK_SIZE, CPDFCompressAlgorithm.kJPX);
        options.monochromeImageOptions = new CPDFImageOptimizeOptions(upperPpi, targetPpi, quality,
                BLOCK_SIZE, CPDFCompressAlgorithm.kJBIG2);

        File output = new File(outputDir(), outputName);
        String label = presetName + " (" + upperPpi + " -> " + targetPpi + " ppi, quality " + quality + ")";
        optimizeSaveAsCopy(document, options, output, label, sourceSize, null);
    }

    // ------------------------------------------------------------------
    // Samples 2 : custom optimize flags
    // ------------------------------------------------------------------

    /**
     * {@link CPDFOptimizeOptions#optimizeFlags} is a bit mask that selects which
     * clean-up steps run. {@link CPDFOptimizeOptions#DEFAULT_OPTIMIZE_FLAGS} is a
     * safe starting point; flags are then added with {@code |} and removed with
     * {@code & ~}.
     */
    private void compressWithCustomFlags() {
        outputListener.println();
        outputListener.println("Samples 2 : Compress with a custom set of optimize flags");

        CPDFDocument document = openAsset(IMAGE_DOCUMENT);
        if (document == null) {
            printDividingLine();
            return;
        }
        long sourceSize = assetSize(IMAGE_DOCUMENT);

        CPDFOptimizeOptions options = new CPDFOptimizeOptions();
        outputListener.println("Default flags : 0x" + Integer.toHexString(options.optimizeFlags));

        // Drop everything the reader does not strictly need to render the pages.
        options.optimizeFlags |= CPDFOptimizeOptions.REMOVE_BOOKMARKS;
        options.optimizeFlags |= CPDFOptimizeOptions.REMOVE_ANNOTATIONS;
        options.optimizeFlags |= CPDFOptimizeOptions.REMOVE_METADATA;
        options.optimizeFlags |= CPDFOptimizeOptions.REMOVE_DOCUMENT_INFO;
        options.optimizeFlags |= CPDFOptimizeOptions.REMOVE_JAVASCRIPT_ACTIONS;
        options.optimizeFlags |= CPDFOptimizeOptions.REMOVE_PAGE_THUMBNAILS;
        // Linearize the result so a viewer can render page 1 before the whole file is downloaded.
        options.optimizeFlags |= CPDFOptimizeOptions.FAST_WEB_VIEW;
        // Keep the embedded fonts, dropping them would break the rendering on devices
        // that do not have a matching system font.
        options.optimizeFlags &= ~CPDFOptimizeOptions.REMOVE_EMBEDDED_FONTS;

        outputListener.println("Custom flags  : 0x" + Integer.toHexString(options.optimizeFlags));
        outputListener.println("Removing bookmarks, annotations, metadata, document info, "
                + "JavaScript actions and page thumbnails, and enabling fast web view.");
        outputListener.println("Annotations in the source : " + annotationCount(document));

        File output = new File(outputDir(), "Compress/CompressTest_CustomFlags.pdf");
        optimizeSaveAsCopy(document, options, output, "Custom flags", sourceSize, null);

        // Reopen the result to prove the content really is gone.
        CPDFDocument result = new CPDFDocument(context);
        result.open(output.getAbsolutePath());
        if (result.isValid()) {
            outputListener.println("Annotations in the result : " + annotationCount(result));
        }
        result.close();

        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 3 : image compression algorithms
    // ------------------------------------------------------------------

    /**
     * Each image set can use its own codec. The three that matter in practice :
     * <ul>
     *     <li>{@link CPDFCompressAlgorithm#kJPX} — JPEG 2000, the best ratio for photographic
     *     colour images, and the default for colour and greyscale;</li>
     *     <li>{@link CPDFCompressAlgorithm#kDCT} — baseline JPEG, faster and far more widely
     *     supported by old readers;</li>
     *     <li>{@link CPDFCompressAlgorithm#kJBIG2} — bi-level codec, the only sensible choice for
     *     scanned black and white pages, and the default for monochrome.</li>
     * </ul>
     * {@link CPDFCompressAlgorithm#kFlate} is lossless and is the fallback for artwork that must
     * not be degraded.
     */
    private void compressWithImageAlgorithms() {
        outputListener.println();
        outputListener.println("Samples 3 : Choose the image compression algorithm per image type");

        CPDFDocument document = openAsset(IMAGE_DOCUMENT);
        if (document == null) {
            printDividingLine();
            return;
        }
        long sourceSize = assetSize(IMAGE_DOCUMENT);

        CPDFOptimizeOptions options = new CPDFOptimizeOptions();
        options.optimizeFlags |= CPDFOptimizeOptions.COMPRESS_IMAGES;
        // Photographic content : JPEG, for maximum reader compatibility.
        options.colorImageOptions = new CPDFImageOptimizeOptions(STANDARD_UPPER_PPI,
                STANDARD_TARGET_PPI, STANDARD_QUALITY, BLOCK_SIZE, CPDFCompressAlgorithm.kDCT);
        // Greyscale : JPEG 2000, better ratio at the same perceived quality.
        options.grayscaleImageOptions = new CPDFImageOptimizeOptions(STANDARD_UPPER_PPI,
                STANDARD_TARGET_PPI, STANDARD_QUALITY, BLOCK_SIZE, CPDFCompressAlgorithm.kJPX);
        // Scanned bi-level pages : JBIG2.
        options.monochromeImageOptions = new CPDFImageOptimizeOptions(HIGH_UPPER_PPI * 2,
                STANDARD_TARGET_PPI * 2, STANDARD_QUALITY, BLOCK_SIZE, CPDFCompressAlgorithm.kJBIG2);
        // Streams that are still uncompressed, or use the obsolete LZW filter, are re-deflated.
        options.optimizeFlags |= CPDFOptimizeOptions.USE_FLATE_FOR_UNCOMPRESSED_STREAMS;
        options.optimizeFlags |= CPDFOptimizeOptions.REPLACE_LZW_WITH_FLATE;

        outputListener.println("Color     : " + options.colorImageOptions.compressAlgorithm);
        outputListener.println("Grayscale : " + options.grayscaleImageOptions.compressAlgorithm);
        outputListener.println("Monochrome: " + options.monochromeImageOptions.compressAlgorithm);

        File output = new File(outputDir(), "Compress/CompressTest_ImageAlgorithm.pdf");
        optimizeSaveAsCopy(document, options, output, "Per type algorithm", sourceSize, null);

        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 4 : progress and cancellation
    // ------------------------------------------------------------------

    /**
     * {@link OptimizeListener} is polled by the native optimizer : {@code onUpdate}
     * reports the page being processed and {@code isCancelled} lets the caller stop
     * the run. A cancelled run reports {@code cancelled == true} and leaves no usable
     * output file, so the caller is expected to delete it.
     *
     * <p>{@link CPDFOptimizeResult#stepsUs} and {@link CPDFOptimizeResult#saveUs} split the
     * elapsed time between the optimization steps and the final serialization.
     */
    private void compressWithProgressAndCancel() {
        outputListener.println();
        outputListener.println("Samples 4 : Report the progress, then cancel a running compression");

        CPDFDocument document = openAsset(IMAGE_DOCUMENT);
        if (document == null) {
            printDividingLine();
            return;
        }
        long sourceSize = assetSize(IMAGE_DOCUMENT);
        int pageCount = document.getPageCount();

        File output = new File(outputDir(), "Compress/CompressTest_Progress.pdf");
        CPDFOptimizeResult result = optimizeSaveAsCopy(document, new CPDFOptimizeOptions(), output,
                "With progress", sourceSize, new OptimizeListener() {
                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public void onUpdate(int pageIndex) {
                        outputListener.println("  progress : page " + (pageIndex + 1) + " / " + pageCount);
                    }
                });
        if (result != null) {
            outputListener.println("Pages processed : " + result.pagesProcessed);
            outputListener.println(String.format(Locale.US, "Optimize steps  : %.2f ms",
                    result.stepsUs / 1000F));
            outputListener.println(String.format(Locale.US, "Serialization   : %.2f ms",
                    result.saveUs / 1000F));
        }

        // Second run, cancelled as soon as the first page has been handed to the listener.
        outputListener.println();
        outputListener.println("Running again and cancelling after the first page :");
        CPDFDocument cancelDocument = openAsset(IMAGE_DOCUMENT);
        if (cancelDocument == null) {
            printDividingLine();
            return;
        }
        File cancelOutput = new File(outputDir(), "Compress/CompressTest_Cancelled.pdf");
        cancelOutput.getParentFile().mkdirs();
        try {
            CPDFOptimizeResult cancelResult = cancelDocument.optimizeSaveAsCopy(
                    cancelOutput.getAbsolutePath(), new CPDFOptimizeOptions(), new OptimizeListener() {

                        private boolean cancelRequested = false;

                        @Override
                        public boolean isCancelled() {
                            return cancelRequested;
                        }

                        @Override
                        public void onUpdate(int pageIndex) {
                            cancelRequested = true;
                        }
                    });
            outputListener.println("Success   : " + cancelResult.isSuccess());
            outputListener.println("Cancelled : " + cancelResult.isCancelled());
            outputListener.println("Pages processed before stopping : " + cancelResult.pagesProcessed);
            if (!cancelResult.isSuccess()) {
                // The partial file is not a valid PDF, drop it.
                FileUtils.deleteFile(cancelOutput);
            }
        } catch (CPDFDocumentException e) {
            outputListener.println("Compression failed : " + e.getMessage());
        } finally {
            cancelDocument.close();
        }

        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 5 : legacy compress levels
    // ------------------------------------------------------------------

    /**
     * The legacy {@link CPDFDocument#saveAsCompressOptimize(String, PDFDocumentCompressLevel, CompressListener)}
     * exposes a single quality knob through {@link PDFDocumentCompressLevel}, from
     * {@code MICRO} (30) to {@code HIGH} (120). It is kept for compatibility; new code should
     * prefer {@link CPDFDocument#optimizeSaveAsCopy(String, CPDFOptimizeOptions)}.
     */
    private void compressWithLegacyLevels() {
        outputListener.println();
        outputListener.println("Samples 5 : Compress with the legacy quality levels");

        for (PDFDocumentCompressLevel level : PDFDocumentCompressLevel.values()) {
            legacyCompress(level);
        }

        // Rejected before reaching the native layer : the quality has to be inside (0, 120].
        CPDFDocument document = openAsset(IMAGE_DOCUMENT);
        if (document != null) {
            File output = new File(outputDir(), "Compress/CompressTest_Invalid.pdf");
            try {
                document.saveAsCompressOptimize(output.getAbsolutePath(), 200);
            } catch (CPDFDocumentException e) {
                outputListener.println("Quality 200 rejected : " + e.getMessage());
            }
            document.close();
        }

        printDividingLine();
    }

    private void legacyCompress(PDFDocumentCompressLevel level) {
        CPDFDocument document = openAsset(IMAGE_DOCUMENT);
        if (document == null) {
            return;
        }
        long sourceSize = assetSize(IMAGE_DOCUMENT);

        File output = new File(outputDir(), "Compress/CompressTest_Legacy_" + level.name() + ".pdf");
        output.getParentFile().mkdirs();
        try {
            // A successful save closes the document, exactly like optimizeSaveAsCopy does.
            boolean success = document.saveAsCompressOptimize(output.getAbsolutePath(), level, pageIndex -> {
            });
            if (success) {
                addFileList(output.getAbsolutePath());
                printSizeCompare(level.name() + " (quality " + level.quality + ")", sourceSize, output);
            } else {
                outputListener.println(level.name() + " : compression failed");
            }
        } catch (CPDFDocumentException e) {
            outputListener.println(level.name() + " : compression failed, " + e.getMessage());
        } finally {
            document.close();
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Runs {@link CPDFDocument#optimizeSaveAsCopy(String, CPDFOptimizeOptions, OptimizeListener)}
     * and prints the size comparison. The document is closed on the way out : the SDK already
     * closes it when the save succeeds, and closing twice is harmless.
     *
     * @return The optimization result, or null when the save threw.
     */
    private CPDFOptimizeResult optimizeSaveAsCopy(CPDFDocument document, CPDFOptimizeOptions options,
                                                  File output, String label, long sourceSize,
                                                  OptimizeListener listener) {
        output.getParentFile().mkdirs();
        try {
            CPDFOptimizeResult result = document.optimizeSaveAsCopy(output.getAbsolutePath(),
                    options, listener);
            if (result.isSuccess()) {
                addFileList(output.getAbsolutePath());
                printSizeCompare(label, sourceSize, output);
            } else {
                outputListener.println(label + " : compression failed");
            }
            return result;
        } catch (CPDFDocumentException e) {
            outputListener.println(label + " : compression failed, " + e.getMessage());
            return null;
        } finally {
            document.close();
        }
    }

    /**
     * Opens a copy of an asset in the cache directory. Compression always writes to a new
     * path, so the source is never modified.
     */
    private CPDFDocument openAsset(String assetName) {
        CPDFDocument document = new CPDFDocument(context);
        document.open(assetPath(assetName));
        if (!document.isValid()) {
            outputListener.println("Failed to open " + assetName);
            document.close();
            return null;
        }
        return document;
    }

    private int annotationCount(CPDFDocument document) {
        int count = 0;
        for (int i = 0; i < document.getPageCount(); i++) {
            count += document.pageAtIndex(i).getAnnotCount();
        }
        return count;
    }

    private long assetSize(String assetName) {
        return new File(assetPath(assetName)).length();
    }

    /**
     * Copies an asset to the cache directory once and reuses that copy afterwards.
     * {@code getAssetsTempFile} recreates the file on every call, which would pull the
     * file out from under a document that is still open.
     */
    private String assetPath(String assetName) {
        String path = assetPaths.get(assetName);
        if (path == null) {
            path = FileUtils.getAssetsTempFile(context, assetName);
            assetPaths.put(assetName, path);
        }
        return path;
    }

    private void printSizeCompare(String label, long sourceSize, File output) {
        long compressedSize = output.length();
        if (sourceSize <= 0 || compressedSize <= 0) {
            outputListener.println(label + " : " + formatSize(compressedSize));
            return;
        }
        float saved = 100F * (sourceSize - compressedSize) / sourceSize;
        outputListener.println(String.format(Locale.US, "%s : %s -> %s (%.1f%% saved), %s",
                label, formatSize(sourceSize), formatSize(compressedSize), saved, output.getName()));
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        return String.format(Locale.US, "%.1f KB", bytes / 1024F);
    }
}
