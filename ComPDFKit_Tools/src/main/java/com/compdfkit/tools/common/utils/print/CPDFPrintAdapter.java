package com.compdfkit.tools.common.utils.print;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.util.SparseIntArray;

import androidx.print.PrintHelper;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CPDFPrintAdapter extends PrintDocumentAdapter {
    private static final int MILS_PER_INCH = 1000;

    /****** 打印清晰度： 1.5：标清；2.3：高清；3：超清 ******/
    public static final float SD = 1.5f;
    public static final float HQ = 2.3f;
    public static final float FHQ = 3.0f;
    private float resolutionScale = SD;
    /****** 是否绘制注释 ******/
    private boolean isDrawAnnot = true;
    private Context context;
    private String jobName;
    private int totalpages;
    private PrintAttributes currentAttributes;
    private IPrintCallback iPrintCallback;
    private final Stack<AsyncTask> mStack = new Stack<>();

    public CPDFPrintAdapter(Context context, int totalpages, String jobName, IPrintCallback iPrintCallback) {
        this.context = context.getApplicationContext();
        this.totalpages = totalpages;
        this.jobName = jobName;
        this.iPrintCallback = iPrintCallback;
    }

    public CPDFPrintAdapter(Context context, int totalPages, String jobName, float resolutionScale, boolean isDrawAnnot, IPrintCallback iPrintCallback) {
        this.resolutionScale = resolutionScale;
        this.isDrawAnnot = isDrawAnnot;
        this.context = context.getApplicationContext();
        this.totalpages = totalPages;
        this.jobName = jobName;
        this.iPrintCallback = iPrintCallback;
    }

    public void setResolutionScale(float resolutionScale) {
        this.resolutionScale = resolutionScale;
    }

    public void setDrawAnnot(boolean drawAnnot) {
        isDrawAnnot = drawAnnot;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        currentAttributes = newAttributes;

        // Respond to cancellation request
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalpages > 0) {
            //构建文档配置信息
            PrintDocumentInfo info = new PrintDocumentInfo
                    .Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages)
                    .build();
            // Content layout reflow is complete
            callback.onLayoutFinished(info, true);
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed.");
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        final AsyncTask<Void, Void, Throwable> asyncTask = new AsyncTask<Void, Void, Throwable>() {
            final SparseIntArray writtenPages = new SparseIntArray();

            @Override
            protected Throwable doInBackground(Void... voids) {
                PrintedPdfDocument mPdfDocument = null;
                try {
                    // check for cancellation
                    if (isCancelled() || cancellationSignal.isCanceled()) {
                        return null;
                    }

                    // Create a new PdfDocument with the requested page attributes
                    mPdfDocument = new PrintedPdfDocument(context, currentAttributes);

                    for (int i = 0; i < totalpages; i++) {
                        // Check to see if this page is in the output range.
                        if (containsPage(pageRanges, i)) {
                            // check for cancellation
                            if (isCancelled() || cancellationSignal.isCanceled()) {
                                return null;
                            }

                            final PdfDocument.Page page = mPdfDocument.startPage(i);

                            // Draw page content for printing
                            drawPage(page, i);

                            // Rendering is complete, so page can be finalized.
                            mPdfDocument.finishPage(page);

                            writtenPages.append(writtenPages.size(), i);
                        }
                    }

                    // check for cancellation
                    if (isCancelled() || cancellationSignal.isCanceled()) {
                        return null;
                    }

                    // Write the document.
                    mPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                    return null;
                } catch (Exception e) {
                    return e;
                } finally {
                    if (null != mPdfDocument) {
                        mPdfDocument.close();
                    }
                }
            }

            @Override
            protected void onPostExecute(Throwable throwable) {
                super.onPostExecute(throwable);
                try {
                    if (cancellationSignal.isCanceled()) {
                        // Cancelled.
                        callback.onWriteCancelled();
                    } else if (null == throwable) {
                        PageRange[] writtenPageRange = computeWrittenPageRanges(writtenPages);
                        // Signal the print framework the document is complete
                        callback.onWriteFinished(writtenPageRange);
                    } else {
                        // Failed.
                        callback.onWriteFailed(throwable.getMessage());
                    }
                } finally {
                    writtenPages.clear();
                    try {
                        destination.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        };
        mStack.push(asyncTask);
        asyncTask.execute();
    }

    @Override
    public void onFinish() {
        try {
            while (!mStack.empty()) {
                try {
                    AsyncTask asyncTask = mStack.pop();
                    if ((null != asyncTask) && !asyncTask.isCancelled()) {
                        asyncTask.cancel(true);
                    }
                } catch (Exception ignored) {
                }
            }
        } finally {
            if (null != iPrintCallback) {
                iPrintCallback.onFinish();
            }
            super.onFinish();
        }
    }


    private boolean containsPage(PageRange[] pageRanges, int numPage) {
        for (PageRange pr : pageRanges) {
            if ((numPage >= pr.getStart()) && (numPage <= pr.getEnd())) {
                return true;
            }
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page, int pagenumber) {
        Canvas canvas = page.getCanvas();
        Bitmap bitmap = null;
        try {
            canvas.drawColor(Color.WHITE);
            int pageWidth = (int) (72 * (float) currentAttributes.getMediaSize().getWidthMils() / MILS_PER_INCH);
            int pageHeight = (int) (72 * (float) currentAttributes.getMediaSize().getHeightMils() / MILS_PER_INCH);

            if (null != iPrintCallback) {
                bitmap = iPrintCallback.onWriteBitmap(pagenumber, (int) (pageWidth * resolutionScale), isDrawAnnot);
            }

            if (null != bitmap) {
                // 计算页码的缩放比例
                float scale = Math.min((float) pageWidth / bitmap.getWidth(), (float) pageHeight / bitmap.getHeight());
                if (scale > 1) {
                    scale = 1;
                }
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                // 页码居中
//                int translateX = Math.abs((int) (bitmap.getWidth() * scale) - pageWidth);
//                int translateY = Math.abs((int) (bitmap.getHeight() * scale) - pageHeight);
//                matrix.postTranslate(translateX / 2f, translateY / 2f);

                canvas.drawBitmap(bitmap, matrix, null);
            }

//            if (isShowTestPage) {
//                // units are in points (1/72 of an inch)
//                int titleBaseLine = 72;
//                int leftMargin = 54;
//                Paint paint = new Paint();
//                paint.setColor(Color.RED);
//                paint.setTextSize(32);
//                canvas.drawText(String.format(Locale.US, "PageNum: %s;PageWidth: %s", (pagenumber + 1), (int) (pageWidth * resolutionScale)), leftMargin, titleBaseLine, paint);
//            }
        } catch (Exception e) {
            canvas.drawColor(Color.WHITE);
        } finally {
            if ((null != bitmap) && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    /**
     * @param ：[writtenPages] 一个SparseIntArray，其中包含必须写入的页面。
     * @return : android.print.PageRange[] 一个包含结果范围的PageRange数组。
     * @methodName ：computeWrittenPageRanges created by luozhipeng on 2019-10-11 21:04.
     * @description ：将所选页面转换为以PageRange数组形式写入的函数。
     */
    private PageRange[] computeWrittenPageRanges(SparseIntArray writtenPages) {
        final List<PageRange> pageRanges = new ArrayList<>();

        int start = -1;
        int end;
        final int writtenPageCount = writtenPages.size();
        for (int i = 0; i < writtenPageCount; i++) {
            if (start < 0) {
                start = writtenPages.valueAt(i);
            }
            int oldEnd = end = start;
            while (i < writtenPageCount && (end - oldEnd) <= 1) {
                oldEnd = end;
                end = writtenPages.valueAt(i);
                i++;
            }
            @SuppressLint("Range") PageRange pageRange = new PageRange(start, end);
            pageRanges.add(pageRange);
            start = -1;
        }

        PageRange[] pageRangesArray = new PageRange[pageRanges.size()];
        pageRanges.toArray(pageRangesArray);
        return pageRangesArray;
    }

    public interface IPrintCallback extends PrintHelper.OnPrintFinishCallback {
        Bitmap onWriteBitmap(int pageNum, int pageWidth, boolean isDrawAnnot);
    }
}
