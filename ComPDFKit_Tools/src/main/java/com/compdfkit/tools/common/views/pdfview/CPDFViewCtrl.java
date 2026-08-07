/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfview;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.OnEditStatusChangeListener;
import com.compdfkit.core.edit.OnSelectEditAreaChangeListener;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.helper.CPDFDocumentHelper;
import com.compdfkit.tools.common.views.pdfview.helper.CPDFErrorTipHelper;
import com.compdfkit.tools.common.views.pdfview.helper.CPDFIndicatorHelper;
import com.compdfkit.tools.common.views.pdfview.helper.CPDFSlideBarHelper;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.reader.CPDFSelectAnnotCallback;
import com.compdfkit.ui.reader.IDocumentStatusCallback;
import com.compdfkit.ui.reader.IReaderViewCallback;
import com.compdfkit.ui.reader.OnFocusedTypeChangedListener;
import com.compdfkit.ui.reader.OnViewModeChangedListener;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.widget.CPDFPageNavigator;

import java.util.ArrayList;
import java.util.List;


/**
 * compdfkit sdk CPDFReaderView ctrl view <br/>
 * <p>
 * <p>
 * ︳--------------------------------︳<BR/>
 * ︳　　　　　　　    　　 　 |---------︳<BR/>
 * ︳　　　　　　　　　　  　|sliderbar︳<BR/>
 * ︳　　　　　　　　　    　　|---------︳<BR/>
 * ︳　　　　　　　　　　 　　     　　　 ︳<br/>
 * ︳　　　　　　　　　　 　　     　　　 ︳<br/>
 * ︳　　　　　　　　　　 　　     　　　 ︳<br/>
 * ︳ 　CPDFViewCtrl 　　      　　︳<br/>
 * ︳　　　　　　　　　　 　　     　　　 ︳<br/>
 * ︳　　　　　　　　　　 　　     　　　 ︳<br/>
 * ︳　　　　　　　　　　 　　     　　　 ︳<br/>
 * ︳|---------|　　　　　　 　    　　　 ︳<br/>
 * ︳|indicator|　　　　　　 　    　　　 ︳<br/>
 * ︳|---------|　　　　　　 　    　　　 ︳<br/>
 * ︳--------------------------------︳<br/>
 * <p>
 * STEP 1:<br/>
 * Use CPDFViewCtrl in xml layout <br/>
 * <br/>
 * com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl<br/>
 * android:id="@+id/pdf_reader_view" <br/>
 * android:layout_width="match_parent"<br/>
 * android:layout_height="match_parent"<br/>
 * />
 * <p>
 * <p/>
 * STEP 2:
 * open pdf file <br/>
 * use {@link CPDFViewCtrl#openPDF(String pdfFilePath)}
 * <p>
 * <p/>
 * Congratulations you have completed the pdf file display!!!
 * <br/>
 * <p>
 * <br/>
 * custom attributes:<br/>
 * app:tools_enable_slider_bar="true|false"  <br/>
 * <p/>
 * app:tools_slider_bar_position="left|top|right|bottom"
 * <p/>
 * app:tools_slider_bar_icon="@drawable/xxx"
 * <p/>
 * app:tools_enable_page_indicator="true|false"
 */
public class CPDFViewCtrl extends ConstraintLayout implements IReaderViewCallback,
    OnFocusedTypeChangedListener, CPDFDocumentHelper.CPDFViewCtrlDelegate {

  private static final String TAG = "CPDFViewCtrl";

  private CPDFReaderView cPdfReaderView;

  public CPDFPageIndicatorView indicatorView;

  private final CPDFSlideBarController slideBarController = new CPDFSlideBarController(this);

  /**
   * Current page index. Public for Flutter/RN SDK access; external code
   * should treat this as read-only.
   */
  public int currentPageIndex = 0;

  private List<CPDFIReaderViewCallback> readerViewCallbacks = new ArrayList<>();

  private List<OnEditStatusChangeListener> editStatusChangeListeners = new ArrayList<>();

  private List<OnSelectEditAreaChangeListener> selectEditAreaChangeListeners = new ArrayList<>();

  private List<OnFocusedTypeChangedListener> pdfViewFocusedListenerList = new ArrayList<>();

  private List<CPDFSelectAnnotCallback> pdfSelectAnnotCallbackList = new ArrayList<>();

  private List<OnViewModeChangedListener> pdfViewModeChangedListenerList = new ArrayList<>();

  private CPDFSelectAnnotCallback dispatchSelectAnnotCallback;

  private CPDFConfiguration cpdfConfiguration;

  // === Helpers ===
  private CPDFDocumentHelper documentHelper;
  private CPDFSlideBarHelper slideBarHelper;
  private CPDFIndicatorHelper indicatorHelper;

  public CPDFViewCtrl(@NonNull Context context) {
    this(context, null);
  }

  public CPDFViewCtrl(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CPDFViewCtrl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttr(context, attrs);
    initCPDFReaderView();
    initHelpers();
  }

  private void initAttr(Context context, AttributeSet attributeSet) {
    try {
      TypedArray typedArray = context.obtainStyledAttributes(attributeSet,
          R.styleable.CPDFViewCtrl);
      boolean enableSliderBar = typedArray.getBoolean(R.styleable.CPDFViewCtrl_tools_enable_slider_bar,
          true);
      CPDFPageNavigator.NavigatorPosition slideBarPosition = parseSlideBarPosition(
          typedArray.getInt(R.styleable.CPDFViewCtrl_tools_slider_bar_position, 2));
      slideBarController.setPosition(slideBarPosition);
      int sliderBarIconResId = typedArray.getResourceId(
          R.styleable.CPDFViewCtrl_tools_slider_bar_icon, R.drawable.tools_ic_pdf_slider_bar);
      boolean enablePageIndicator = typedArray.getBoolean(
          R.styleable.CPDFViewCtrl_tools_enable_page_indicator, true);
      int pageIndicatorMarginBottom = typedArray.getDimensionPixelOffset(
          R.styleable.CPDFViewCtrl_tools_page_indicator_margin_bottom, 0);
      if (enablePageIndicator) {
        indicatorView = new CPDFPageIndicatorView(getContext());
      }
      CViewUtils.applyViewBackground(this,
          ContextCompat.getColor(getContext(), R.color.tools_pdf_view_ctrl_background_color));
      typedArray.recycle();
      // Store parsed values for helpers
      pendingEnableSliderBar = enableSliderBar;
      pendingSlideBarPosition = slideBarPosition;
      pendingSliderBarIconResId = sliderBarIconResId;
      pendingEnablePageIndicator = enablePageIndicator;
      pendingPageIndicatorMarginBottom = pageIndicatorMarginBottom;
    } catch (Exception e) {
      CLog.e(TAG, "initAttr failed: " + e.getMessage());
    }
  }

  // Temp fields for attr values until helpers are initialized
  private boolean pendingEnableSliderBar = true;
  private CPDFPageNavigator.NavigatorPosition pendingSlideBarPosition = CPDFPageNavigator.NavigatorPosition.RIGHT;
  private int pendingSliderBarIconResId = R.drawable.tools_ic_pdf_slider_bar;
  private boolean pendingEnablePageIndicator = true;
 private int pendingPageIndicatorMarginBottom = 0;

 private CPDFPageNavigator.NavigatorPosition parseSlideBarPosition(int value) {
    switch (value) {
      case 0:
        return CPDFPageNavigator.NavigatorPosition.LEFT;
      case 1:
        return CPDFPageNavigator.NavigatorPosition.TOP;
      case 3:
        return CPDFPageNavigator.NavigatorPosition.BOTTOM;
      case 2:
      default:
        return CPDFPageNavigator.NavigatorPosition.RIGHT;
    }
  }

  private void initCPDFReaderView() {
    cPdfReaderView = new CPDFReaderView(getContext());
    cPdfReaderView.setDoublePageMode(false);
    cPdfReaderView.setReaderViewCallback(this);
    cPdfReaderView.setOnFocusedTypeChangedListener(this);
    dispatchSelectAnnotCallback = new CPDFSelectAnnotCallback() {
      @Override
      public void onAnnotationSelected(CPDFPageView pageView,
                                       CPDFBaseAnnotImpl<CPDFAnnotation> annotImpl) {
        for (CPDFSelectAnnotCallback callback : pdfSelectAnnotCallbackList) {
          callback.onAnnotationSelected(pageView, annotImpl);
        }
      }

      @Override
      public void onAnnotationDeselected(CPDFPageView pageView,
                                         CPDFBaseAnnotImpl<CPDFAnnotation> annotImpl) {
        for (CPDFSelectAnnotCallback callback : pdfSelectAnnotCallbackList) {
          callback.onAnnotationDeselected(pageView, annotImpl);
        }
      }
    };
    cPdfReaderView.setOnViewModeChangedListener(viewMode -> {
      for (OnViewModeChangedListener listener : pdfViewModeChangedListenerList) {
        listener.onViewModeChange(viewMode);
      }
    });
    addView(cPdfReaderView);

    CPDFEditManager editManager = cPdfReaderView.getEditManager();
    editManager.disable();
    editManager.addEditStatusChangeListener(new OnEditStatusChangeListener() {
      @Override
      public void onBegin(int i) {
        for (OnEditStatusChangeListener listener : editStatusChangeListeners) {
          listener.onBegin(i);
        }
      }

      @Override
      public void onUndoRedo(int pageIndex, boolean canUndo, boolean canRedo) {
        for (OnEditStatusChangeListener listener : editStatusChangeListeners) {
          listener.onUndoRedo(pageIndex, canUndo, canRedo);
        }
      }

      @Override
      public void onExit() {
        for (OnEditStatusChangeListener listener : editStatusChangeListeners) {
          listener.onExit();
        }
      }
    });

    cPdfReaderView.setSelectEditAreaChangeListener(selectType -> {
      for (OnSelectEditAreaChangeListener selectEditAreaChangeListener : selectEditAreaChangeListeners) {
        selectEditAreaChangeListener.onSelectEditAreaChange(selectType);
      }
    });

    cPdfReaderView.setDocumentStatusCallback(new IDocumentStatusCallback() {
      @Override
      public void onLoading() {
      }

      @Override
      public void onLoadFailed() {
        CLog.e(TAG, "onLoadFailed: PDF document failed to load");
      }

      @Override
      public void onLoadComplete() {
        if (documentHelper != null && documentHelper.isInitOpenPDF()) {
          currentPageIndex = documentHelper.getInitPageIndex();
          slideBarHelper.syncSlideBar(currentPageIndex);
          cPdfReaderView.post(() -> {
            if (cPdfReaderView.getPageNum() != documentHelper.getInitPageIndex()) {
              cPdfReaderView.setDisplayPageIndex(documentHelper.getInitPageIndex());
            }
            cPdfReaderView.post(slideBarController::syncReaderViewState);
          });
        }
      }
    });
  }

  private void initHelpers() {
    slideBarHelper = new CPDFSlideBarHelper(cPdfReaderView, slideBarController);
    slideBarHelper.setEnableSliderBar(pendingEnableSliderBar);
    slideBarHelper.setSlideBarPosition(pendingSlideBarPosition);
    slideBarHelper.setSliderBarIconResId(pendingSliderBarIconResId);

    indicatorHelper = new CPDFIndicatorHelper(cPdfReaderView, indicatorView,
            pendingPageIndicatorMarginBottom, this);

    documentHelper = new CPDFDocumentHelper(cPdfReaderView, this);
  }

  // ========================================================================
  // Delegate implementation (callbacks from CPDFDocumentHelper)
  // ========================================================================

  @Override
  public void updateScaleForLayout() {
    getCPdfReaderView().setScale(1f);
  }

  @Override
  public void addPageIndicator() {
    indicatorHelper.addPageIndicator();
  }

  @Override
  public void applyErrorCallback() {
    CPDFErrorTipHelper.applyErrorCallback(cPdfReaderView, cpdfConfiguration, getContext());
  }

  @Override
  public void exitEditMode() {
    documentHelper.exitEditMode();
  }

  @Override
  public void detachSlideBar() {
    slideBarHelper.detachSlideBar();
  }

  @Override
  public void clearListeners() {
    editStatusChangeListeners.clear();
    selectEditAreaChangeListeners.clear();
    pdfViewFocusedListenerList.clear();
    pdfSelectAnnotCallbackList.clear();
    pdfViewModeChangedListenerList.clear();
  }

  // ========================================================================
  // View mode and vertical mode
  // ========================================================================

  public void setViewMode(CPDFReaderView.ViewMode viewMode) {
    cPdfReaderView.setViewMode(viewMode);
  }

  public void setVerticalMode(boolean verticalMode) {
    cPdfReaderView.setVerticalMode(verticalMode);
    slideBarHelper.syncSlideBarPositionWithVerticalMode(verticalMode);
  }

  // ========================================================================
  // openPDF — delegates to CPDFDocumentHelper
  // ========================================================================

  /**
   * @deprecated Use {@link #openPDF(String, String, int, COnOpenPdfFinishCallback)} for the full API.
   */
  @Deprecated
  public void openPDF(String pdfFilePath) {
    documentHelper.openPDF(pdfFilePath);
  }

  /**
   * @deprecated Use {@link #openPDF(String, String, int, COnOpenPdfFinishCallback)} for the full API.
   */
  @Deprecated
  public void openPDF(String pdfFilePath, String password) {
    documentHelper.openPDF(pdfFilePath, password);
  }

  /**
  * @deprecated Use {@link #openPDF(String, String, int, COnOpenPdfFinishCallback)} for the full API.
  */
  @Deprecated
  public void openPDF(String pdfFilePath, String password, int pageIndex) {
    documentHelper.openPDF(pdfFilePath, password, pageIndex, null);
  }

  /**
   * @deprecated Use {@link #openPDF(String, String, int, COnOpenPdfFinishCallback)} for the full API.
   */
  @Deprecated
  public void openPDF(String pdfFilePath, String password, COnOpenPdfFinishCallback openPdfFinishCallback) {
    documentHelper.openPDF(pdfFilePath, password, openPdfFinishCallback);
  }

  public void openPDF(String pdfFilePath, String password, int pageIndex,
      COnOpenPdfFinishCallback openPdfFinishCallback) {
    documentHelper.openPDF(pdfFilePath, password, pageIndex, openPdfFinishCallback);
  }

  /**
   * @deprecated Use {@link #openPDF(Uri, String, int, COnOpenPdfFinishCallback)} for the full API.
   */
  @Deprecated
  public void openPDF(Uri pdfUri) {
    documentHelper.openPDF(pdfUri);
  }

  /**
   * @deprecated Use {@link #openPDF(Uri, String, int, COnOpenPdfFinishCallback)} for the full API.
   */
  @Deprecated
  public void openPDF(Uri pdfUri, String password) {
    documentHelper.openPDF(pdfUri, password);
  }

  /**
  * @deprecated Use {@link #openPDF(Uri, String, int, COnOpenPdfFinishCallback)} for the full API.
  */
  @Deprecated
  public void openPDF(Uri pdfUri, String password, int pageIndex) {
    documentHelper.openPDF(pdfUri, password, pageIndex, null);
  }

  /**
   * @deprecated Use {@link #openPDF(Uri, String, int, COnOpenPdfFinishCallback)} for the full API.
   */
  @Deprecated
  public void openPDF(Uri pdfUri, String password, COnOpenPdfFinishCallback openPdfFinishCallback) {
    documentHelper.openPDF(pdfUri, password, openPdfFinishCallback);
  }

  public void openPDF(Uri pdfUri, String password, int pageIndex, COnOpenPdfFinishCallback openPdfFinishCallback) {
    documentHelper.openPDF(pdfUri, password, pageIndex, openPdfFinishCallback);
  }

  // ========================================================================
  // setPDFDocument
  // ========================================================================

  public void setPDFDocument(CPDFDocument cpdfDocument, Object pdf, int pageIndex,
      CPDFDocument.PDFDocumentError error, COnOpenPdfFinishCallback openPdfFinishCallback) {
    documentHelper.setPDFDocument(cpdfDocument, pdf, pageIndex, error, openPdfFinishCallback);
  }

  public void showWritePermissionsDialog(CPDFDocument document) {
    documentHelper.showWritePermissionsDialog(document);
  }

  // ========================================================================
  // savePDF
  // ========================================================================

  public void savePDF(COnSaveCallback callback, COnSaveError error) {
    documentHelper.savePDF(callback, error);
  }

  public void savePDF(boolean saveIncremental, boolean fontSubset, COnSaveCallback callback, COnSaveError error) {
    documentHelper.savePDF(saveIncremental, fontSubset, callback, error);
  }

  // ========================================================================
  // Form / annotation / edit mode
  // ========================================================================

  public void changeFormType(CPDFWidget.WidgetType widgetType) {
    cPdfReaderView.setCurrentFocusedType(CPDFAnnotation.Type.WIDGET);
    cPdfReaderView.setCurrentFocusedFormType(widgetType);
    cPdfReaderView.setTouchMode(CPDFReaderView.TouchMode.ADD_ANNOT);
  }

  public void resetFormType() {
    cPdfReaderView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
    cPdfReaderView.setCurrentFocusedType(CPDFAnnotation.Type.WIDGET);
    cPdfReaderView.setCurrentFocusedFormType(CPDFWidget.WidgetType.Widget_Unknown);
  }

  public void changeAnnotationType(CPDFAnnotation.Type type) {
    cPdfReaderView.setTouchMode(CPDFReaderView.TouchMode.ADD_ANNOT);
    cPdfReaderView.setCurrentFocusedType(type);
  }

 public void resetAnnotationType() {
   cPdfReaderView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
  cPdfReaderView.setCurrentFocusedType(CPDFAnnotation.Type.UNKNOWN);
}


 public com.compdfkit.ui.reader.CPDFReaderView getCPdfReaderView() {
    return cPdfReaderView;
  }

  // ========================================================================
  // IReaderViewCallback dispatch
  // ========================================================================

  @Override
  public void onTypeChanged(CPDFAnnotation.Type type) {
    if (pdfViewFocusedListenerList != null) {
      for (OnFocusedTypeChangedListener onFocusedTypeChangedListener : pdfViewFocusedListenerList) {
        onFocusedTypeChangedListener.onTypeChanged(type);
      }
    }
  }

  @Override
  public void onTapMainDocArea() {
    if (readerViewCallbacks != null) {
      for (CPDFIReaderViewCallback readerViewCallback : readerViewCallbacks) {
        readerViewCallback.onTapMainDocArea();
      }
    }
  }

  @Override
  public void onMoveToChild(int pageIndex) {
    currentPageIndex = pageIndex;
    if (documentHelper != null) {
      documentHelper.setInitOpenPDF(false);
    }
    indicatorHelper.setCurrentPageIndex(pageIndex);
    if (readerViewCallbacks != null) {
      for (CPDFIReaderViewCallback readerViewCallback : readerViewCallbacks) {
        readerViewCallback.onMoveToChild(pageIndex);
      }
    }
  }

  @Override
  public void onEndScroll() {
    indicatorHelper.hidePageIndicator();
    if (readerViewCallbacks != null) {
      for (CPDFIReaderViewCallback readerViewCallback : readerViewCallbacks) {
        readerViewCallback.onEndScroll();
      }
    }
  }

  @Override
  public void onScrolling() {
    indicatorHelper.onScrolling();
    if (readerViewCallbacks != null) {
      for (CPDFIReaderViewCallback readerViewCallback : readerViewCallbacks) {
        readerViewCallback.onScrolling();
      }
    }
  }

  @Override
  public void onRecordLastJumpPageNum(int i) {
    if (readerViewCallbacks != null) {
      for (CPDFIReaderViewCallback readerViewCallback : readerViewCallbacks) {
        readerViewCallback.onRecordLastJumpPageNum(i);
      }
    }
  }

  // ========================================================================
  // Slide bar public API
  // ========================================================================

  public void enableSliderBar(boolean enableSliderBar) {
    slideBarHelper.setEnableSliderBar(enableSliderBar);
    slideBarHelper.syncSlideBar(currentPageIndex);
  }

  public boolean isEnableSliderBar() {
    return slideBarHelper.isEnableSliderBar();
  }

  public boolean isSaveFileExtraFontSubset() {
    if (cpdfConfiguration != null && cpdfConfiguration.globalConfig != null) {
      return cpdfConfiguration.globalConfig.fileSaveExtraFontSubset;
    } else {
      return false;
    }
  }

  public void refreshSlideBarDocumentState() {
    currentPageIndex = slideBarHelper.refreshSlideBarDocumentState(currentPageIndex);
  }

  public View getSlideBarView() {
    return slideBarHelper.getSlideBarView();
  }

  // ========================================================================
  // Listener add/remove
  // ========================================================================

  public void addOnPDFFocusedTypeChangeListener(OnFocusedTypeChangedListener listener) {
    pdfViewFocusedListenerList.add(listener);
  }

  public void addOnPDFSelectAnnotChangeListener(CPDFSelectAnnotCallback callback) {
    if (callback != null && !pdfSelectAnnotCallbackList.contains(callback)) {
      boolean shouldRegisterReaderCallback = pdfSelectAnnotCallbackList.isEmpty();
      pdfSelectAnnotCallbackList.add(callback);
      if (shouldRegisterReaderCallback) {
        cPdfReaderView.setSelectAnnotCallback(dispatchSelectAnnotCallback);
      }
    }
  }

  public void removeOnPDFSelectAnnotChangeListener(CPDFSelectAnnotCallback callback) {
    pdfSelectAnnotCallbackList.remove(callback);
    if (pdfSelectAnnotCallbackList.isEmpty()) {
      cPdfReaderView.setSelectAnnotCallback(null);
    }
  }

  public void addOnPDFViewModeChangeListener(OnViewModeChangedListener listener) {
    if (listener != null && !pdfViewModeChangedListenerList.contains(listener)) {
      pdfViewModeChangedListenerList.add(listener);
    }
  }

  public void removeOnPDFViewModeChangeListener(OnViewModeChangedListener listener) {
    pdfViewModeChangedListenerList.remove(listener);
  }

  public void addReaderViewCallback(CPDFIReaderViewCallback callback) {
    this.readerViewCallbacks.add(callback);
  }

  public void addEditStatusChangeListener(OnEditStatusChangeListener listener) {
    editStatusChangeListeners.add(listener);
  }

  public void removeEditStatusChangeListener(OnEditStatusChangeListener listener) {
    editStatusChangeListeners.remove(listener);
  }

  public void addSelectEditAreaChangeListener(OnSelectEditAreaChangeListener listener) {
    selectEditAreaChangeListeners.add(listener);
  }

  public void removeSelectEditAreaChangeListener(OnSelectEditAreaChangeListener listener) {
    selectEditAreaChangeListeners.remove(listener);
  }

  // ========================================================================
  // Page indicator
  // ========================================================================

 public void enablePageIndicator(boolean enablePageIndicator) {
    if (enablePageIndicator && indicatorView == null) {
      indicatorView = new CPDFPageIndicatorView(getContext());
    }
    indicatorHelper.enablePageIndicator(enablePageIndicator, indicatorView);
 }

  // ========================================================================
  // Configuration
  // ========================================================================

  public void setCPDFConfiguration(CPDFConfiguration cpdfConfiguration) {
    this.cpdfConfiguration = cpdfConfiguration;
    slideBarHelper.setCPDFConfiguration(cpdfConfiguration);
    documentHelper.setCPDFConfiguration(cpdfConfiguration);
  }

  public CPDFConfiguration getCPDFConfiguration() {
    return cpdfConfiguration;
  }

  // ========================================================================
  // Configuration changes — dead branch merged
  // ========================================================================

  @Override
  protected void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    getCPdfReaderView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        getCPdfReaderView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        // Both landscape and portrait need the same scale refresh + slide bar sync.
        updateScaleForLayout();
        refreshSlideBarDocumentState();
      }
    });
  }

  // ========================================================================
  // Close
  // ========================================================================

  public void close() {
    documentHelper.close();
  }

  public void setSaveCallback(COnSaveCallback saveGlobalCallback, COnSaveError error) {
    documentHelper.setSaveCallback(saveGlobalCallback, error);
  }

  // ========================================================================
  // Callback interfaces
  // ========================================================================

  public interface COnSaveCallback {
    void callback(String filePath, Uri pdfUri);
  }

  public interface COnSaveError {
    void error(Exception e);
  }

  public interface COnOpenPdfFinishCallback {
    void onOpenPdfFinishCallback();
  }
}
