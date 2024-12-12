/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;


/**
 * CPDFPageIndicatorView is a custom view that displays the current page index and the total number of pages in a PDF document.
 *
 *
 */
public class CPDFPageIndicatorView extends LinearLayout {

    /**
     *  the index of the current page, default is 1
     */
    private int currentPageIndex = 1;

    /**
     * the total number of pages in the PDF document
     */
    private int totalPage;

    /**
     * listener for page indicator click events
     */
    private OnPageIndicatorClickListener pageIndicatorClickListener;

    private AppCompatTextView tvPageIndex;

    private AppCompatTextView tvTotalPage;

    private boolean isRNMeasureLayout = false;

    public CPDFPageIndicatorView(Context context) {
        this(context, null);
    }

    public CPDFPageIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPDFPageIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    /**
     * Initializes the view by creating and configuring the text view and adding it to the layout.
     */
    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.tools_page_indicator_view, this);
        setGravity(Gravity.CENTER_HORIZONTAL);
        tvPageIndex = findViewById(R.id.tv_page_index);
        tvTotalPage = findViewById(R.id.tv_page_count);

        // set click listener for the layout
        setOnClickListener(v -> {
            if (pageIndicatorClickListener != null) {
                pageIndicatorClickListener.page(currentPageIndex);
            }
        });
    }

    /**

     Sets the total number of pages in the PDF document.
     @param total the total number of pages
     */
    public void setTotalPage(int total){
        this.totalPage = total;
        if (tvTotalPage != null) {
            tvTotalPage.setText(totalPage + "");
        }
    }
    /**

     Sets the index of the current page.
     @param pageIndex the index of the current page
     */
    public void setCurrentPageIndex(int pageIndex){
        this.currentPageIndex = pageIndex + 1;
        if (tvPageIndex != null) {
            tvPageIndex.setText(currentPageIndex + "");
        }
        if (isRNMeasureLayout){
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    }

    public void setRNMeasureLayout(boolean RNMeasureLayout) {
        isRNMeasureLayout = RNMeasureLayout;
        invalidate();
    }

    /**
     Sets the listener for page indicator click events.
     @param pageIndicatorClickListener the listener to be set
     */
    public void setPageIndicatorClickListener(OnPageIndicatorClickListener pageIndicatorClickListener) {
        this.pageIndicatorClickListener = pageIndicatorClickListener;
    }
    /**

     Listener interface for page indicator click events.
     */
    public interface OnPageIndicatorClickListener{
        void page(int pageIndex);
    }
}
