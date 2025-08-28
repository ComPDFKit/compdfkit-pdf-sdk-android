package com.compdfkit.tools.common.utils.print;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.print.PrintAttributes;
import android.print.PrintManager;

import androidx.annotation.NonNull;
import androidx.print.PrintHelper;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;

import org.jetbrains.annotations.NotNull;

public class CPDFPrintUtils {

    /**
     * @param ：[context, file, pageCounts, isEncrypted]
     * @return : void
     * @methodName ：printCurrentDocument created by liujiyuan on 2018/9/13 下午5:31.
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
                CPDFPrintAdpater adapter = getPDFPrintAdapter(activity, document);
                printManager.print(document.getFileName(), adapter, attributes);
            } else {
                CToastUtil.showToast(activity.getApplicationContext(), R.string.tools_print_systemversion_not_support);
            }
        } catch (Exception e) {
            CToastUtil.showToast(activity.getApplicationContext(), R.string.tools_print_not_working);
        }
    }

    @NonNull
    private static CPDFPrintAdpater getPDFPrintAdapter(@NonNull Activity activity, CPDFDocument document) {
        CPDFPrintAdpater adapter = new CPDFPrintAdpater(activity, document.getPageCount(), document.getFileName(), new CPDFPrintAdpater.IPrintCallback() {
            @Override
            public Bitmap onWriteBitmap(int pageNum, int pageWidth, boolean isDrawAnnot) {
                return pdfToBitmap(document, pageNum, pageWidth, isDrawAnnot);
            }
            @Override
            public void onFinish() {

            }
        });
        adapter.setResolutionScale(CPDFPrintAdpater.FHQ);
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

                if (mCPDFDocument.isValid()) {
                    mCPDFDocument.renderPageAtIndex(
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
