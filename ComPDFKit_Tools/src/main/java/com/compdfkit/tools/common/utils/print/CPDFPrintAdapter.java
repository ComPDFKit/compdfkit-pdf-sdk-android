package com.compdfkit.tools.common.utils.print;

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
import android.util.Log;

import androidx.print.PrintHelper;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CPDFPrintAdapter extends PrintDocumentAdapter {
    private static final String TAG = "CPDFPrintAdapter";
    private static final int MILS_PER_INCH = 1000;

    public static final float SD = 1.5f;
    public static final float HQ = 2.3f;
    public static final float FHQ = 3.0f;
    private float resolutionScale = SD;
    /****** draw annot ******/
    private boolean isDrawAnnot = true;
    private Context context;
    private String jobName;
    private int totalpages;
    private PrintAttributes currentAttributes;
    private IPrintCallback iPrintCallback;
    private AsyncTask<Void, Void, Throwable> activeTask;

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
        
            PrintDocumentInfo info = new PrintDocumentInfo
                    .Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages)
                    .build();
            // Content layout reflow is complete
            callback.onLayoutFinished(info, !newAttributes.equals(oldAttributes));
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed.");
        }
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        final AsyncTask<Void, Void, Throwable> asyncTask = new AsyncTask<Void, Void, Throwable>() {
            final SparseIntArray writtenPages = new SparseIntArray();
            final long startTime = System.currentTimeMillis();

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
                    long renderStart = System.currentTimeMillis();

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

                    Log.d(TAG, "Rendered " + writtenPages.size() + " pages in "
                            + (System.currentTimeMillis() - renderStart) + " ms");
                    long writeStart = System.currentTimeMillis();
                    mPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                    Log.d(TAG, "Wrote PDF in " + (System.currentTimeMillis() - writeStart)
                            + " ms; total " + (System.currentTimeMillis() - startTime) + " ms");
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

            @Override
            protected void onCancelled(Throwable throwable) {
                closeDestination(destination);
                callback.onWriteCancelled();
            }
        };
        activeTask = asyncTask;
        asyncTask.execute();
    }

    @Override
    public void onFinish() {
        try {
            if (activeTask != null && !activeTask.isCancelled()) {
                activeTask.cancel(true);
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

    private void drawPage(PdfDocument.Page page, int pagenumber) throws IOException {
        Canvas canvas = page.getCanvas();
        Bitmap bitmap = null;
        try {
            canvas.drawColor(Color.WHITE);
            int pageWidth = (int) (72 * (float) currentAttributes.getMediaSize().getWidthMils() / MILS_PER_INCH);
            int pageHeight = (int) (72 * (float) currentAttributes.getMediaSize().getHeightMils() / MILS_PER_INCH);

            if (null != iPrintCallback) {
                bitmap = iPrintCallback.onWriteBitmap(pagenumber, (int) (pageWidth * resolutionScale), isDrawAnnot);
            }

            if (bitmap == null || bitmap.isRecycled()) {
                throw new IOException("Failed to render page " + pagenumber);
            }

            if (bitmap != null) {
                
                float scale = Math.min((float) pageWidth / bitmap.getWidth(), (float) pageHeight / bitmap.getHeight());
                if (scale > 1) {
                    scale = 1;
                }

                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                matrix.postTranslate((pageWidth - bitmap.getWidth() * scale) / 2f,
                        (pageHeight - bitmap.getHeight() * scale) / 2f);


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
        } finally {
            if ((null != bitmap) && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    /**
     * @param ：[writtenPages] 。
     * @return : android.print.PageRange[] 
     * @methodName ：computeWrittenPageRanges created by luozhipeng on 2019-10-11 21:04.
     * @description ：
     */
    private PageRange[] computeWrittenPageRanges(SparseIntArray writtenPages) {
        final List<PageRange> pageRanges = new ArrayList<>();

        if (writtenPages.size() == 0) return new PageRange[0];
        int start = writtenPages.valueAt(0);
        int end = start;
        final int writtenPageCount = writtenPages.size();
        for (int i = 1; i < writtenPageCount; i++) {
            int page = writtenPages.valueAt(i);
            if (page == end + 1) {
                end = page;
            } else {
                pageRanges.add(new PageRange(start, end));
                start = end = page;
            }
        }
        pageRanges.add(new PageRange(start, end));

        PageRange[] pageRangesArray = new PageRange[pageRanges.size()];
        pageRanges.toArray(pageRangesArray);
        return pageRangesArray;
    }

    private void closeDestination(ParcelFileDescriptor destination) {
        try { destination.close(); } catch (IOException ignored) { }
    }

    public interface IPrintCallback extends PrintHelper.OnPrintFinishCallback {
        Bitmap onWriteBitmap(int pageNum, int pageWidth, boolean isDrawAnnot);
    }
}
