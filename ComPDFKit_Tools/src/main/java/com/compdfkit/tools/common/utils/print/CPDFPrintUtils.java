package com.compdfkit.tools.common.utils.print;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.print.PrintAttributes;
import android.print.PrintManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.print.PrintHelper;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.ui.reader.CPDFReaderView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CPDFPrintUtils {
    private static final String TAG = "CPDFPrintUtils";
    private static final ExecutorService PRINT_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    /**
     * @param ：[context, file, pageCounts, isEncrypted]
     * @return : void
     * @methodName ：printCurrentDocument 
     */
    public static void printCurrentDocument(@NotNull final Activity activity,CPDFDocument document) {
        try {
            if (PrintHelper.systemSupportsPrint()) {
                PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
                PrintAttributes attributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(new PrintAttributes.Resolution("id", Context.PRINT_SERVICE, 300, 300))
                        .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build();
                CPDFPrintAdapter adapter = getPDFPrintAdapter(activity, document);
                printManager.print(document.getFileName(), adapter, attributes);
            } else {
                CToastUtil.showToast(activity.getApplicationContext(), R.string.tools_print_systemversion_not_support);
            }
        } catch (Exception e) {
            CToastUtil.showToast(activity.getApplicationContext(), R.string.tools_print_not_working);
        }
    }

    /**
     * Saves the current document to a temporary PDF, flattens annotations for
     * printing, and lets the system print service render the resulting PDF.
     */
    public static void printCurrentDocumentWithAnnotations(@NonNull final Activity activity,
                                                           @NonNull final CPDFDocument document, @Nullable CPDFReaderView readerView) {
        final File printDirectory = new File(activity.getCacheDir(), "compdfkit_print");
        if (!printDirectory.exists() && !printDirectory.mkdirs()) {
            CToastUtil.showToast(activity.getApplicationContext(), R.string.tools_print_not_working);
            return;
        }
        final File printFile = new File(printDirectory,
                "print_" + System.currentTimeMillis() + ".pdf");
        PRINT_EXECUTOR.execute(() -> {
            CPDFDocument flattenedDocument = null;
            try {
                // Persist all in-memory edits before copying the source PDF.
                if (!document.save()) {
                    throw new IllegalStateException("Failed to save current PDF");
                }

                copyCurrentDocument(activity, document, printFile);

                flattenedDocument = new CPDFDocument(activity.getApplicationContext());
                if (flattenedDocument.open(printFile.getAbsolutePath())
                        != CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
                    throw new IllegalStateException("Failed to open print PDF");
                }
                if (!flattenedDocument.flattenAllPages(CPDFPage.PDFFlattenOption.FLAT_PRINT)
                        || !flattenedDocument.save()) {
                    throw new IllegalStateException("Failed to flatten annotations");
                }

                Uri printUri = CFileUtils.getUriBySystem(activity, printFile);
                MAIN_HANDLER.post(() -> CFileUtils.startPrint(
                        activity, printFile.getAbsolutePath(), printUri));
            } catch (Exception e) {
                Log.e(TAG, "Failed to prepare PDF for system printing", e);
                MAIN_HANDLER.post(() -> CToastUtil.showToast(
                        activity.getApplicationContext(), R.string.tools_print_not_working));
            } finally {
                if (document.shouleReloadDocument()){
                    document.reload();
                    if (readerView != null){
                        CThreadPoolUtils.getInstance().executeMain(readerView::reloadPages2);
                    }
                }
                if (flattenedDocument != null) {
                    flattenedDocument.close();
                }
            }
        });
    }

    private static void copyCurrentDocument(@NonNull Context context,
                                            @NonNull CPDFDocument document,
                                            @NonNull File destination) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (!TextUtils.isEmpty(document.getAbsolutePath())) {
                inputStream = new FileInputStream(document.getAbsolutePath());
            } else if (document.getUri() != null) {
                inputStream = context.getContentResolver().openInputStream(document.getUri());
            }
            if (inputStream == null) {
                throw new IOException("Current PDF has no readable source");
            }
            outputStream = new FileOutputStream(destination);
            byte[] buffer = new byte[16 * 1024];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    @NonNull
    private static CPDFPrintAdapter getPDFPrintAdapter(@NonNull Activity activity, CPDFDocument document) {
        CPDFPrintAdapter adapter = new CPDFPrintAdapter(activity, document.getPageCount(), document.getFileName(), new CPDFPrintAdapter.IPrintCallback() {
            @Override
            public Bitmap onWriteBitmap(int pageNum, int pageWidth, boolean isDrawAnnot) {
                return pdfToBitmap(document, pageNum, pageWidth, isDrawAnnot);
            }
            @Override
            public void onFinish() {

            }
        });
        adapter.setResolutionScale(CPDFPrintAdapter.FHQ);
        adapter.setDrawAnnot(true);
        return adapter;
    }

    private static Bitmap pdfToBitmap(CPDFDocument mCPDFDocument, int pageIndex, int pageWidth_, boolean isDrawAnnot) {
        Bitmap bitmap = null;
        if (mCPDFDocument != null) {
            CPDFPage page = mCPDFDocument.pageAtIndex(pageIndex);
            if (page != null) {
                int pageWidth = pageWidth_;
                if (pageWidth == -1) {
                    pageWidth = (int) page.getSize().width();
                }
                int pageHeight = (int) (page.getSize().height() / page.getSize().width() * pageWidth);
                float scale = (float) pageWidth / page.getSize().width();
                if (pageWidth != 0 && pageHeight != 0) {
                    bitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888);
                }

                if (!mCPDFDocument.isValid()) {
                    return null;
                }

                boolean rendered = mCPDFDocument.renderPageAtIndex(
                        bitmap,
                        pageIndex,
                        pageWidth,
                        pageHeight,
                        0,
                        0,
                        pageWidth,
                        pageHeight,
                        Color.WHITE,
                        255,
                        0,
                        isDrawAnnot,
                        true
                );
                if (!rendered) {
                    Log.e(TAG, "Failed to render page " + pageIndex
                            + ", drawAnnotations=" + isDrawAnnot + ", drawForms=true");
                    return null;
                }

                if (bitmap == null || bitmap.isRecycled()) {
                    if (pageWidth != 0 && pageHeight != 0) {
                        bitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888);
                    }
                }
            }
        }
        return bitmap;
    }
}
