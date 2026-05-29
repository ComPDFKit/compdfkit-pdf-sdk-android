/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.animation.CFillScreenManager;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.widget.CPDFPageNavigator;

final class CPDFSlideBarController {

  private final ConstraintLayout parent;
  private CPDFPageNavigator.NavigatorPosition position = CPDFPageNavigator.NavigatorPosition.RIGHT;
  private CPDFPageNavigator navigator;
  private SlideBarHostLayout container;

  CPDFSlideBarController(ConstraintLayout parent) {
    this.parent = parent;
  }

  CPDFPageNavigator getNavigator() {
    if (navigator == null) {
      navigator = (CPDFPageNavigator) LayoutInflater.from(parent.getContext())
          .inflate(getNavigatorLayoutResId(position), ensureContainer(), false);
    }
    return navigator;
  }

  boolean hasNavigator() {
    return navigator != null;
  }

  void setPosition(@NonNull CPDFPageNavigator.NavigatorPosition position) {
    if (this.position == position) {
      ensureContainer().setNavigatorPosition(position);
      updateContainerLayout();
      return;
    }
    this.position = position;
    SlideBarHostLayout hostContainer = ensureContainer();
    hostContainer.setNavigatorPosition(position);
    updateContainerLayout();
    if (navigator != null) {
      navigator.detachReaderView();
      if (navigator.getParent() instanceof ViewGroup) {
        ((ViewGroup) navigator.getParent()).removeView(navigator);
      }
      navigator = null;
    }
  }

  @NonNull
  private SlideBarHostLayout ensureContainer() {
    if (container == null) {
      container = new SlideBarHostLayout(parent.getContext());
      container.setId(View.generateViewId());
      container.setNavigatorPosition(position);
      updateContainerLayout();
    }
    return container;
  }

  private void updateContainerLayout() {
    if (container == null) {
      return;
    }
    container.setOrientation(isHorizontal(position) ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
    ConstraintLayout.LayoutParams layoutParams;
    switch (position) {
      case LEFT:
        layoutParams = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, 0);
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        break;
      case TOP:
        layoutParams = new ConstraintLayout.LayoutParams(
            0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        break;
      case BOTTOM:
        layoutParams = new ConstraintLayout.LayoutParams(
            0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        break;
      case RIGHT:
      default:
        layoutParams = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, 0);
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        break;
    }
    container.setLayoutParams(layoutParams);
  }

  private boolean isHorizontal(CPDFPageNavigator.NavigatorPosition position) {
    return position == CPDFPageNavigator.NavigatorPosition.TOP
        || position == CPDFPageNavigator.NavigatorPosition.BOTTOM;
  }

  private int getNavigatorLayoutResId(CPDFPageNavigator.NavigatorPosition position) {
    switch (position) {
      case LEFT:
        return R.layout.tools_page_navigator_left;
      case TOP:
        return R.layout.tools_page_navigator_top;
      case BOTTOM:
        return R.layout.tools_page_navigator_bottom;
      case RIGHT:
      default:
        return R.layout.tools_page_navigator_right;
    }
  }

  void configureAppearance(CPDFReaderView readerView, @DrawableRes int handleResId, int previewWidth, int previewHeight,
                           @NonNull CPDFPageNavigator.DragPreviewRenderer renderer,
                           @NonNull CPDFPageNavigator.OnDragListener dragListener) {
    CPDFPageNavigator slideBar = getNavigator();
    slideBar.attachReaderView(readerView);
    slideBar.setHandleDrawable(handleResId);
    slideBar.setThumbnailPreviewSize(previewWidth, previewHeight);
    slideBar.setThumbnailPreviewEnabled(true);
    slideBar.setDragPreviewMode(CPDFPageNavigator.DragPreviewMode.CUSTOM);
    slideBar.setDragPreviewRenderer(renderer);
    slideBar.setOnDragListener(dragListener);
  }

  void syncDocumentState(int pageCount, int currentPageIndex) {
    CPDFPageNavigator slideBar = getNavigator();
    slideBar.setPageCount(pageCount);
    slideBar.setPageIndex(currentPageIndex);
    slideBar.requestLayout();
  }

  void syncReaderViewState() {
    if (navigator != null) {
      navigator.syncReaderViewState();
    }
  }

  void animateToPage(int pageIndex, int duration) {
    getNavigator().animateToPage(pageIndex, duration);
  }

  void attachToParent() {
    CPDFPageNavigator slideBar = getNavigator();
    SlideBarHostLayout hostContainer = ensureContainer();
    if (slideBar.getParent() instanceof ViewGroup && slideBar.getParent() != container) {
      ((ViewGroup) slideBar.getParent()).removeView(slideBar);
    }
    if (hostContainer.getParent() == null) {
      parent.addView(hostContainer);
    }
    if (slideBar.getParent() == null) {
      LinearLayout.LayoutParams layoutParams;
      if (isHorizontal(position)) {
        layoutParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      } else {
        layoutParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
      }
      hostContainer.addView(slideBar, layoutParams);
    }
  }

  void detachFromParent() {
    if (navigator != null && navigator.getParent() instanceof ViewGroup) {
      ((ViewGroup) navigator.getParent()).removeView(navigator);
    }
    if (container != null && container.getParent() instanceof ViewGroup) {
      ((ViewGroup) container.getParent()).removeView(container);
    }
  }

  View getView() {
    return ensureContainer();
  }

  private static final class SlideBarHostLayout extends LinearLayout implements CFillScreenManager.DirectionAwareToolView {

    private CPDFPageNavigator.NavigatorPosition navigatorPosition = CPDFPageNavigator.NavigatorPosition.RIGHT;

    private SlideBarHostLayout(android.content.Context context) {
      super(context);
      setClipChildren(false);
      setClipToPadding(false);
    }

    private void setNavigatorPosition(CPDFPageNavigator.NavigatorPosition navigatorPosition) {
      this.navigatorPosition = navigatorPosition;
    }

    @Override
    public CFillScreenManager.ToolViewDirection getToolViewDirection() {
      switch (navigatorPosition) {
        case LEFT:
          return CFillScreenManager.ToolViewDirection.LEFT;
        case TOP:
          return CFillScreenManager.ToolViewDirection.TOP;
        case BOTTOM:
          return CFillScreenManager.ToolViewDirection.BOTTOM;
        case RIGHT:
        default:
          return CFillScreenManager.ToolViewDirection.RIGHT;
      }
    }
  }
}
