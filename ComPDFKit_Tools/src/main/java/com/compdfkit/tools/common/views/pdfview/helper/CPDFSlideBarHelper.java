/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.views.pdfview.helper;

import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFPageNumberPreviewRenderer;
import com.compdfkit.tools.common.views.pdfview.CPDFSlideBarController;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.widget.CPDFPageNavigator;

/**
 * Manages slide bar synchronization logic: attaching/detaching the slide bar,
 * applying appearance, syncing document state, and handling vertical mode changes.
 *
 * <p>Extracted from {@code CPDFViewCtrl}'s private slide bar methods.
 */
public class CPDFSlideBarHelper {

    private final CPDFReaderView readerView;
    private final CPDFSlideBarController slideBarController;

    private boolean enableSliderBar = true;
    private CPDFPageNavigator.NavigatorPosition slideBarPosition = CPDFPageNavigator.NavigatorPosition.RIGHT;
    private @DrawableRes int sliderBarIconResId = R.drawable.tools_ic_pdf_slider_bar;
    private CPDFConfiguration cpdfConfiguration;

    public CPDFSlideBarHelper(CPDFReaderView readerView,
                              CPDFSlideBarController slideBarController) {
        this.readerView = readerView;
        this.slideBarController = slideBarController;
    }

    public void setEnableSliderBar(boolean enable) {
        this.enableSliderBar = enable;
    }

    public boolean isEnableSliderBar() {
        return enableSliderBar;
    }

    public void setSlideBarPosition(CPDFPageNavigator.NavigatorPosition position) {
        this.slideBarPosition = position;
        slideBarController.setPosition(slideBarPosition);
    }

    public CPDFPageNavigator.NavigatorPosition getSlideBarPosition() {
        return slideBarPosition;
    }

    public void setSliderBarIconResId(@DrawableRes int resId) {
        this.sliderBarIconResId = resId;
    }

    public void setCPDFConfiguration(CPDFConfiguration configuration) {
        this.cpdfConfiguration = configuration;
    }

    public void syncSlideBarPositionWithVerticalMode(boolean verticalMode) {
        CPDFPageNavigator.NavigatorPosition targetPosition = verticalMode
                ? CPDFPageNavigator.NavigatorPosition.RIGHT
                : CPDFPageNavigator.NavigatorPosition.BOTTOM;
        if (slideBarPosition == targetPosition) {
            return;
        }
        slideBarPosition = targetPosition;
      slideBarController.setPosition(slideBarPosition);
       if (readerView != null && readerView.getPDFDocument() != null) {
          // Use the reader view's current page so the slider syncs to the
          // right page position on orientation/vertical-mode change,
          // not page 0 (previously hard-coded).
          syncSlideBar(readerView.getPageNum());
       }
   }

    public void syncSlideBar(int currentPageIndex) {
        CPDFDocument document = readerView.getPDFDocument();
        enableSliderBar = shouldEnableSlideBar(document);
        if (!enableSliderBar || document == null) {
            detachSlideBar();
            return;
        }
        ensureSlideBarAttached();
        applySlideBarAppearance(document);
        syncSlideBarDocumentState(document, currentPageIndex);
    }

    private boolean shouldEnableSlideBar(@Nullable CPDFDocument document) {
        if (document != null && document.getPageCount() <= 1) {
            return false;
        }
        return cpdfConfiguration == null || cpdfConfiguration.readerViewConfig.enableSliderBar;
    }

    private void ensureSlideBarAttached() {
        slideBarController.setPosition(slideBarPosition);
        slideBarController.attachToParent();
    }

    private void applySlideBarAppearance(@NonNull CPDFDocument document) {
        int labelTextSize = CPDFPageNumberPreviewRenderer.getDefaultTextSizePx(readerView.getContext());
        int labelWidth = CPDFPageNumberPreviewRenderer.calculatePreviewWidth(readerView.getContext(), document, labelTextSize);
        slideBarController.configureAppearance(
                readerView,
                sliderBarIconResId,
                labelWidth,
                labelTextSize * 3 / 2,
                new CPDFPageNumberPreviewRenderer(
                        ContextCompat.getColor(readerView.getContext(), R.color.tools_page_indicator_bg_color),
                        labelTextSize,
                        CDimensUtils.dp2px(readerView.getContext(), 3)),
                new CPDFPageNavigator.OnDragListener() {
                    @Override
                    public void onDragBegin(int pageIndex) {
                        if (readerView != null) {
                            readerView.removeAllAnnotFocus();
                        }
                    }
                });
    }

    private void syncSlideBarDocumentState(@NonNull CPDFDocument document, int currentPageIndex) {
        slideBarController.syncDocumentState(document.getPageCount(), currentPageIndex);
    }

    public void syncSlideBarPage(int pageIndex, int duration) {
        if (!enableSliderBar || !slideBarController.hasNavigator()) {
            return;
        }
        slideBarController.animateToPage(pageIndex, duration);
    }

    public void detachSlideBar() {
        slideBarController.detachFromParent();
    }

    /**
    * Refresh the slide bar document state after page count changes.
    * Returns the clamped current page index.
    */
    public int refreshSlideBarDocumentState(int currentPageIndex) {
        CPDFDocument document = readerView != null ? readerView.getPDFDocument() : null;
        if (document == null) {
            detachSlideBar();
            return currentPageIndex;
        }
        currentPageIndex = Math.min(currentPageIndex, Math.max(document.getPageCount() - 1, 0));
        syncSlideBar(currentPageIndex);
        return currentPageIndex;
    }

    public View getSlideBarView() {
        return slideBarController.getView();
    }

    public void syncReaderViewState() {
        slideBarController.syncReaderViewState();
    }
}
