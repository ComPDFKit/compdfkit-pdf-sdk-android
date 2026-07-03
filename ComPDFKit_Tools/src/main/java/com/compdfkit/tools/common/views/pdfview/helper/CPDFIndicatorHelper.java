/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.views.pdfview.helper;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.dialog.CGotoPageDialog;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFPageIndicatorView;
import com.compdfkit.ui.reader.CPDFReaderView;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

/**
 * Manages the page indicator view: creation, show/hide animation, and
 * the "go to page" dialog when the indicator is tapped.
 *
 * <p>Extracted from {@code CPDFViewCtrl}'s page indicator methods and fields.
 */
public class CPDFIndicatorHelper {

    // Context obtained via readerView.getContext() to avoid leaking an Activity reference.
    private final CPDFReaderView readerView;
    private CPDFPageIndicatorView indicatorView;
    private final int pageIndicatorMarginBottom;
    private final View rootView;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private ObjectAnimator pageIndicatorAnimator = null;
    private boolean isHiding = false;
    private boolean isScrolling = false;

    private final Runnable hideIndicatorRunnable = () -> {
        if (pageIndicatorAnimator != null) {
            isScrolling = false;
            pageIndicatorAnimator.reverse();
        }
    };

    public CPDFIndicatorHelper(CPDFReaderView readerView,
                              CPDFPageIndicatorView indicatorView,
                              int pageIndicatorMarginBottom, View rootView) {

        this.readerView = readerView;
        this.indicatorView = indicatorView;
        this.pageIndicatorMarginBottom = pageIndicatorMarginBottom;
        this.rootView = rootView;
    }

    /**
     * Update the indicator view reference (e.g. when recreated via enablePageIndicator).
     */
    public void setIndicatorView(CPDFPageIndicatorView indicatorView) {
        this.indicatorView = indicatorView;
    }

    /**
     * Enable or disable the page indicator.
     * When enabled, adds the indicator view; when disabled, removes it.
     */
    public void enablePageIndicator(boolean enable, CPDFPageIndicatorView indicatorView) {
        if (enable) {
            if (indicatorView != null) {
                this.indicatorView = indicatorView;
            }
            addPageIndicator();
        } else {
            if (this.indicatorView != null && rootView instanceof android.view.ViewGroup) {
                ((android.view.ViewGroup) rootView).removeView(this.indicatorView);
            }
            pageIndicatorAnimator = null;
        }
    }

    /**
     * Add (or re-add) the page indicator view to the root layout,
     * configure its click listener, and start the show/hide animator.
     */
    public void addPageIndicator() {
        if (indicatorView == null) {
            return;
        }
        if (rootView instanceof android.view.ViewGroup) {
            ((android.view.ViewGroup) rootView).removeView(indicatorView);
        }
        // CPDFViewCtrl extends ConstraintLayout, so use ConstraintLayout.LayoutParams directly.
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams clParams =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        clParams.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        clParams.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        clParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        int margin = CDimensUtils.dp2px(readerView.getContext(), 16);
        clParams.setMargins(margin, 0, margin, pageIndicatorMarginBottom);
        indicatorView.setLayoutParams(clParams);
        indicatorView.setAlpha(0F);
        if (rootView instanceof android.view.ViewGroup) {
            ((android.view.ViewGroup) rootView).addView(indicatorView);
        }
        if (readerView.getPDFDocument() == null) {
            return;
        }
        int totalPageCount = readerView.getPDFDocument().getPageCount();
        indicatorView.setTotalPage(totalPageCount);
        indicatorView.setCurrentPageIndex(0);
        indicatorView.setPageIndicatorClickListener(pageIndex -> {
            CGotoPageDialog dialog = CGotoPageDialog.newInstance(
                    (readerView.getContext().getString(R.string.tools_page) + String.format(" (%d/%d)", 1,
                            readerView.getPDFDocument().getPageCount())));
            dialog.setPageCount(readerView.getPDFDocument().getPageCount());
            dialog.setOnPDFDisplayPageIndexListener(page -> {
                if (page <= readerView.getPDFDocument().getPageCount() && page > 0) {
                    readerView.setDisplayPageIndex(page - 1, true);
                    showPageIndicator();
                }
            });
            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
            if (fragmentActivity != null) {
                dialog.show(fragmentActivity.getSupportFragmentManager(), "gotoPageDialog");
            }
        });
        pageIndicatorAnimator = ObjectAnimator.ofFloat(indicatorView, "alpha", 0F, 1F);
        pageIndicatorAnimator.setDuration(100);
        pageIndicatorAnimator.setInterpolator(new FastOutLinearInInterpolator());
        showPageIndicator();
        rootView.postDelayed(this::hidePageIndicator, 110);
    }

    /**
     * Start (or schedule) hiding the page indicator after a delay.
     */
    public void hidePageIndicator() {
        if (!isHiding) {
            isHiding = true;
            handler.postDelayed(hideIndicatorRunnable, 3000);
        }
    }

    /**
     * Show the page indicator with fade-in animation if not already visible.
     */
    public void showPageIndicator() {
        if (pageIndicatorAnimator != null && indicatorView != null && indicatorView.getAlpha() != 1.0F) {
            isHiding = false;
            pageIndicatorAnimator.start();
        }
    }

    /**
     * Called on scroll start. Shows the indicator and cancels any pending hide.
     */
    public void onScrolling() {
        isHiding = false;
        handler.removeCallbacks(hideIndicatorRunnable);
        if (!isScrolling) {
            isScrolling = true;
            showPageIndicator();
        }
    }

    /**
     * Update the current page index on the indicator view.
     */
    public void setCurrentPageIndex(int pageIndex) {
        if (indicatorView != null) {
            indicatorView.setCurrentPageIndex(pageIndex);
        }
    }
}
