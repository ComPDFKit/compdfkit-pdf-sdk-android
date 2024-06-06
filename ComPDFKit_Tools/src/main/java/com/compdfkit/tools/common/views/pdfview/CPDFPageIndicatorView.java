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
import android.graphics.Color;
import android.util.AttributeSet;
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

    private AppCompatTextView tvPageIndicator;

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
        // create the text view for displaying the page indicator
        tvPageIndicator = new AppCompatTextView(getContext());
        // set layout params for the text view
        LinearLayout.LayoutParams currentPageLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int horizontalMargin = CDimensUtils.dp2px(getContext(), 8);
        int verticalMargin = CDimensUtils.dp2px(getContext(), 4);
        currentPageLayoutParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
        tvPageIndicator.setLayoutParams(currentPageLayoutParams);
        // set text color to white
        tvPageIndicator.setTextColor(Color.WHITE);
        // add the text view to the layout
        addView(tvPageIndicator);
        // set background resource for the layout
        setBackgroundResource(R.drawable.tools_pdf_page_indactor_bg);
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
        updatePageIndicator();
    }
    /**

     Sets the index of the current page.
     @param pageIndex the index of the current page
     */
    public void setCurrentPageIndex(int pageIndex){
        this.currentPageIndex = pageIndex + 1;
        updatePageIndicator();
    }
    /**

     Updates the text view to display the current page index and the total number of pages.
     */
    private void updatePageIndicator(){
        tvPageIndicator.setText(currentPageIndex + "/" + totalPage);
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
