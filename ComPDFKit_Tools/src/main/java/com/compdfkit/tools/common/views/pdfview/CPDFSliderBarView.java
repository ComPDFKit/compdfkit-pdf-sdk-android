/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.compdfkit.tools.R;


/**
 *
 */
public class CPDFSliderBarView extends CPDFSlideBar implements CPDFSlideBar.OnScrollToPageListener {

    int currentScrollPageIndex = -1;

    private int sliderBarThumbnailWidth = 314;

    private int sliderBarThumbnailHeight = 444;

    private CPDFViewCtrl pdfView;

    public CPDFSliderBarView(Context context) {
        this(context, null);
    }

    public CPDFSliderBarView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CPDFSliderBarView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOnScrollToPageListener(this);
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
        if (pdfView.getCPdfReaderView().getPDFDocument() != null) {
            setPageCount(pdfView.getCPdfReaderView().getPDFDocument().getPageCount());
        }
    }

    public void initSliderBar(CPDFSlideBar.SlideBarPosition position, int thumbnailWidth, int thumbnailHeight) {
        sliderBarThumbnailWidth = thumbnailWidth;
        sliderBarThumbnailHeight = thumbnailHeight;
        showThumbnail(false);
        onlyShowPageIndex(true, 150);
        setSlideBarBitmap(R.drawable.tools_ic_pdf_slider_bar);
        setSlideBarPosition(position);
        setTextBackgroundColor(ContextCompat.getColor(getContext(), R.color.tools_page_indicator_bg_color));
    }

    @Override
    public void onScrollBegin(int pageIndex) {
        if (pdfView != null) {
            pdfView.getCPdfReaderView().removeAllAnnotFocus();
        }
    }

    @Override
    public void onScroll(int pageIndex) {
    }

    @Override
    public void onScrollEnd(int pageIndex) {
        currentScrollPageIndex = -1;
        pdfView.getCPdfReaderView().setDisplayPageIndex(pageIndex);
    }

    @Override
    public void onScrollToPage(int pageIndex) {
        refreshDocumentPageThumbnail(pageIndex);
    }

    private void refreshDocumentPageThumbnail(int pageIndex) {
//        if (currentScrollPageIndex != pageIndex) {
//            currentScrollPageIndex = pageIndex;
//            Glide.with(getContext())
//                    .asBitmap()
//                    .load(CPDFWrapper.fromDocument(pdfView.getCPdfReaderView().getPDFDocument(), currentScrollPageIndex))
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(new SimpleTarget<Bitmap>(sliderBarThumbnailWidth, sliderBarThumbnailHeight) {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            setThumbnailBitmap(resource);
//                        }
//                    });
//        }
    }
}
