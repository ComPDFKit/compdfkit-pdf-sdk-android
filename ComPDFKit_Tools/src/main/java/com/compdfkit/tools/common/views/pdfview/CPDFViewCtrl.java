/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.dialog.CGotoPageDialog;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.reader.IReaderViewCallback;
import com.compdfkit.ui.reader.OnFocusedTypeChangedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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
 * ︳　　 　CPDFViewCtrl 　　      　　︳<br/>
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
 * app:tools_slider_bar_position="left|right"
 * <p/>
 * app:tools_slider_bar_icon="@drawable/xxx"
 * <p/>
 * app:tools_slider_bar_thumbnail_width="120dp"
 * <p/>
 * app:tools_slider_bar_thumbnail_hei="200dp"
 * <p/>
 * app:tools_enable_page_indicator="true|false"
 */
public class CPDFViewCtrl extends ConstraintLayout implements IReaderViewCallback, OnFocusedTypeChangedListener {

    private CPDFReaderView cPdfReaderView;

    public CPDFPageIndicatorView indicatorView;

    public CPDFSlideBar slideBar;

    public int currentPageIndex = 0;

    private boolean enableSliderBar = true;

    private CPDFSlideBar.SlideBarPosition slideBarPosition = CPDFSlideBar.SlideBarPosition.RIGHT;

    private int sliderBarThumbnailWidth = 314;

    private int sliderBarThumbnailHeight = 444;

    @DrawableRes
    private int sliderBarIconResId = R.drawable.tools_ic_pdf_slider_bar;

    private boolean enablePageIndicator = true;

    private int pageIndicatorMarginBottom = 0;

    private List<CPDFIReaderViewCallback> readerViewCallbacks = new ArrayList<>();

    private boolean isScrolling = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    private ObjectAnimator pageIndicatorAnimator = null;

    private List<OnFocusedTypeChangedListener> pdfViewFocusedListenerList = new ArrayList<>();

    private CPDFConfiguration cpdfConfiguration;

    private COnSaveCallback saveGlobalCallback;

    private COnSaveError saveGlobalErrorCallback;

    private Runnable hideIndicatorRunnable = () -> {
        if (pageIndicatorAnimator != null) {
            isScrolling = false;
            pageIndicatorAnimator.reverse();
        }
    };

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
    }

    private void initAttr(Context context, AttributeSet attributeSet) {
        try {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CPDFReaderView);
            if (typedArray != null) {
                enableSliderBar = typedArray.getBoolean(R.styleable.CPDFReaderView_tools_enable_slider_bar, true);
                int sliderBarPositionEnum = typedArray.getInt(R.styleable.CPDFReaderView_tools_slider_bar_position, 1);
                if (sliderBarPositionEnum == 0) {
                    slideBarPosition = CPDFSlideBar.SlideBarPosition.LEFT;
                } else {
                    slideBarPosition = CPDFSlideBar.SlideBarPosition.RIGHT;
                }
                sliderBarThumbnailWidth = typedArray.getDimensionPixelOffset(R.styleable.CPDFReaderView_tools_slider_bar_thumbnail_width, 314);
                sliderBarThumbnailHeight = typedArray.getDimensionPixelOffset(R.styleable.CPDFReaderView_tools_slider_bar_thumbnail_height, 444);
                sliderBarIconResId = typedArray.getResourceId(R.styleable.CPDFReaderView_tools_slider_bar_icon, R.drawable.tools_ic_pdf_slider_bar);
                enablePageIndicator = typedArray.getBoolean(R.styleable.CPDFReaderView_tools_enable_page_indicator, true);
                pageIndicatorMarginBottom = typedArray.getDimensionPixelOffset(R.styleable.CPDFReaderView_tools_page_indicator_margin_bottom, 0);
                if (enableSliderBar) {
                    slideBar = new CPDFSliderBarView(getContext());
                }
                if (enablePageIndicator) {
                    indicatorView = new CPDFPageIndicatorView(getContext());
                }
                CViewUtils.applyViewBackground(this, ContextCompat.getColor(getContext(), R.color.tools_pdf_view_ctrl_background_color));
                typedArray.recycle();
            }
        } catch (Exception e) {

        }
    }


    private void initCPDFReaderView() {
        cPdfReaderView = new CPDFReaderView(getContext());
        cPdfReaderView.setDoublePageMode(false);
        cPdfReaderView.setReaderViewCallback(this);
        cPdfReaderView.setOnFocusedTypeChangedListener(this);

        addView(cPdfReaderView);
    }

    private void setErrorCallback() {
        cPdfReaderView.setPdfErrorMessageCallback(errorId -> {
            switch (errorId) {
                case NO_TEXT_ON_PAGE:
                    CAlertDialog alertDialog = CAlertDialog.newInstance(
                            getContext().getString(R.string.tools_warning),
                            getContext().getString(R.string.tools_scan_pdf_annot_warning)
                    );
                    alertDialog.setConfirmClickListener(v -> {
                        alertDialog.dismiss();
                    });
                    FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
                    if (fragmentActivity != null) {
                        alertDialog.show(fragmentActivity.getSupportFragmentManager(), "alertDialog");
                    }
                    break;
                case CANNOT_EDIT:
                    CToastUtil.showToast(getContext(), R.string.tools_can_not_edit);
                    break;
                case NO_EMAIL_APP:
                    CToastUtil.showToast(getContext(), R.string.tools_reader_view_error_no_email);
                    break;
                case NO_BROWSE_APP:
                    CToastUtil.showToast(getContext(), R.string.tools_reader_view_error_no_browser);
                    break;
                case INVALID_LINK:
                    CToastUtil.showToast(getContext(), R.string.tools_reader_view_error_invalid_link);
                    break;
                default:
                    break;
            }
        });
    }

    public void setViewMode(CPDFReaderView.ViewMode viewMode) {
        cPdfReaderView.setViewMode(viewMode);
    }

    /**
     * This method opens a PDF file located at the given file path.
     *
     * @param pdfFilePath The file path of the PDF file to be opened.
     */
    public void openPDF(String pdfFilePath) {
        openPDF(pdfFilePath, null);
    }

    public void openPDF(String pdfFilePath, String password) {
        openPDF(pdfFilePath, password, null);
    }

    public void openPDF(String pdfFilePath, String password, COnOpenPdfFinishCallback openPdfFinishCallback) {
        CThreadPoolUtils.getInstance().executeIO(() -> {
            // Create a new instance of the CPDFDocument class, passing in the current context.
            CPDFDocument cpdfDocument = new CPDFDocument(getContext());
            // Attempt to open the PDF file at the given file path using the open method of the CPDFDocument class.

            CPDFDocument.PDFDocumentError pdfDocumentError = cpdfDocument.open(pdfFilePath, password);
            CThreadPoolUtils.getInstance().executeMain(() -> {
                setPDFDocument(cpdfDocument, pdfFilePath, pdfDocumentError, openPdfFinishCallback);
            });
        });
    }

    public void openPDF(Uri pdfUri) {
        openPDF(pdfUri, null);
    }

    public void openPDF(Uri pdfUri, String password) {
        openPDF(pdfUri, password, null);
    }

    public void openPDF(Uri pdfUri, String password, COnOpenPdfFinishCallback openPdfFinishCallback) {
        CThreadPoolUtils.getInstance().executeIO(() -> {
            // Create a new instance of the CPDFDocument class, passing in the current context.
            CPDFDocument cpdfDocument = new CPDFDocument(getContext());
            // Attempt to open the PDF file at the given file path using the open method of the CPDFDocument class.
            CPDFDocument.PDFDocumentError pdfDocumentError = cpdfDocument.open(pdfUri, password);
            CThreadPoolUtils.getInstance().executeMain(() -> {
                setPDFDocument(cpdfDocument, pdfUri, pdfDocumentError, openPdfFinishCallback);
            });
        });
    }

    private void setPDFDocument(CPDFDocument cpdfDocument, Object pdf, CPDFDocument.PDFDocumentError error, COnOpenPdfFinishCallback openPdfFinishCallback) {
        CLog.e("ComPDFKit", "CPDFViewCtrl-openPDF:" + error.name());
        // Switch on the result of the open method to handle the different possible outcomes.
        switch (error) {
            // If the PDF file was successfully opened, set the PDF document of the CPdfReaderView instance to the opened document.
            case PDFDocumentErrorSuccess:
                CLog.e("ComPDFKit", "canWrite:" + cpdfDocument.isCanWrite() + ", hasRepaired:" + cpdfDocument.hasRepaired());
                cPdfReaderView.setPDFDocument(cpdfDocument);
                cPdfReaderView.setDisplayPageIndex(0);
                initCPDFSliderBar();
                addPageIndicator();
                if (cPdfReaderView.getEditManager() != null) {
                    cPdfReaderView.getEditManager().disable();
                }
                if (openPdfFinishCallback != null) {
                    openPdfFinishCallback.onOpenPdfFinishCallback();
                }
                if (!cpdfDocument.isCanWrite() && cpdfDocument.hasRepaired()) {
                    showWritePermissionsDialog(cpdfDocument);
                }
                setErrorCallback();
                break;
            // If the PDF file requires a password to be opened, do nothing for now.
            case PDFDocumentErrorPassword:
                FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
                CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
                if (pdf instanceof String) {
                    verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(cpdfDocument, (String) pdf);
                } else {
                    verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(cpdfDocument, (Uri) pdf);
                }
                verifyPasswordDialogFragment.setDismissListener(() -> {
                    if (getCPdfReaderView().getPDFDocument() == null) {
                        if (fragmentActivity != null) {
                            fragmentActivity.onBackPressed();
                        }
                    }
                });
                verifyPasswordDialogFragment.setVerifyCompleteListener(document -> {
                    cPdfReaderView.setPDFDocument(document);
                    cPdfReaderView.setDisplayPageIndex(0);
                    initCPDFSliderBar();
                    addPageIndicator();
                    if (cPdfReaderView.getEditManager() != null) {
                        cPdfReaderView.getEditManager().disable();
                    }
                    if (openPdfFinishCallback != null) {
                        openPdfFinishCallback.onOpenPdfFinishCallback();
                    }
                });
                if (fragmentActivity != null) {
                    verifyPasswordDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "verifyPwdDialog");
                }
                break;
            // For all other errors, do nothing for now.
            default:
                break;
        }
    }

    public void showWritePermissionsDialog(CPDFDocument document) {
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
        if (fragmentActivity == null) {
            return;
        }
        Fragment rwPermissionDialog = fragmentActivity.getSupportFragmentManager().findFragmentByTag("rwPermissionDialog");
        if (rwPermissionDialog != null && rwPermissionDialog instanceof DialogFragment) {
            ((DialogFragment) rwPermissionDialog).dismiss();
        }
        CAlertDialog alertDialog = CAlertDialog.newInstance(
                getContext().getString(R.string.tools_warning),
                getContext().getString(R.string.tools_repair_pdf_file_mes)
        );
        alertDialog.setCancelClickListener(v -> alertDialog.dismiss());
        alertDialog.setConfirmClickListener(v -> {
            try {
                File tempFile = new File(getContext().getCacheDir(), CFileUtils.CACHE_FOLDER + File.separator + document.getFileName());
                if (tempFile.exists()) {
                    String name = UUID.randomUUID().toString().substring(0, 4) + "_" + document.getFileName();
                    tempFile = new File(getContext().getCacheDir(), CFileUtils.CACHE_FOLDER + File.separator + name);
                }
                boolean result = document.saveAs(tempFile.getAbsolutePath(), false);
                if (result) {
                    openPDF(tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
            }
            alertDialog.dismiss();
        });
        alertDialog.show(fragmentActivity.getSupportFragmentManager(), "rwPermissionDialog");
    }

    public void savePDF(COnSaveCallback callback, COnSaveError error) {
        CThreadPoolUtils.getInstance().executeMain(() -> {
            cPdfReaderView.pauseAllRenderProcess();
            cPdfReaderView.removeAllAnnotFocus();
            if (cPdfReaderView.getContextMenuShowListener() != null) {
                cPdfReaderView.getContextMenuShowListener().dismissContextMenu();
            }
            CPDFDocument document = cPdfReaderView.getPDFDocument();
            if (document == null) {
                if (error != null) {
                    error.error(new Exception("document is null"));
                }
                if (saveGlobalErrorCallback != null) {
                    saveGlobalErrorCallback.error(new Exception("document is null"));
                }
                return;
            }
            exitEditMode();
            if (document.hasChanges()) {
                CThreadPoolUtils.getInstance().executeIO(() -> {
                    try {
                        boolean saveFileExtraFontSubset = false;
                        if (cpdfConfiguration != null && cpdfConfiguration.globalConfig != null) {
                            saveFileExtraFontSubset = cpdfConfiguration.globalConfig.fileSaveExtraFontSubset;
                        }
                        CLog.e("ComPDFKit", "save pdf extra font subset:" + saveFileExtraFontSubset);
                        boolean success = document.save(CPDFDocument.PDFDocumentSaveType.PDFDocumentSaveIncremental, saveFileExtraFontSubset);
                        if (!success) {
                            document.save(CPDFDocument.PDFDocumentSaveType.PDFDocumentSaveNoIncremental, saveFileExtraFontSubset);
                        }
                        CThreadPoolUtils.getInstance().executeMain(() -> {
                            if (callback != null) {
                                callback.callback(document.getAbsolutePath(), document.getUri());
                            }
                            if (saveGlobalCallback != null) {
                                saveGlobalCallback.callback(document.getAbsolutePath(), document.getUri());
                            }
                        });
                    } catch (CPDFDocumentException e) {
                        e.printStackTrace();
                        CLog.e("ComPDFKit", "save fail:" + e.getMessage());
                        if (error != null) {
                            error.error(e);
                        }
                        if (saveGlobalErrorCallback != null) {
                            saveGlobalErrorCallback.error(new Exception("document is null"));
                        }
                    }
                });
            } else {
                if (callback != null) {
                    callback.callback(document.getAbsolutePath(), document.getUri());
                }
                if (saveGlobalCallback != null) {
                    saveGlobalCallback.callback(document.getAbsolutePath(), document.getUri());
                }
            }
        });
    }

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

    public void exitEditMode() {
        CPDFEditManager editManager = cPdfReaderView.getEditManager();
        if (editManager != null && editManager.isEditMode()) {
            editManager.endEdit();
        }
    }

    public com.compdfkit.ui.reader.CPDFReaderView getCPdfReaderView() {
        return cPdfReaderView;
    }

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
        if (slideBar != null) {
            slideBar.setPageIndexByAnimation(pageIndex, 500);
        }
        if (indicatorView != null) {
            indicatorView.setCurrentPageIndex(pageIndex);
        }
        if (readerViewCallbacks != null) {
            for (CPDFIReaderViewCallback readerViewCallback : readerViewCallbacks) {
                readerViewCallback.onMoveToChild(pageIndex);
            }
        }
    }

    @Override
    public void onEndScroll() {
        hidePageIndicator();
        if (readerViewCallbacks != null) {
            for (CPDFIReaderViewCallback readerViewCallback : readerViewCallbacks) {
                readerViewCallback.onEndScroll();
            }
        }
    }

    @Override
    public void onScrolling() {
        isHiding = false;
        handler.removeCallbacks(hideIndicatorRunnable);
        if (!isScrolling) {
            isScrolling = true;
            showPageIndicator();
        }
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

    private void initCPDFSliderBar() {
        if (!enableSliderBar) {
            if (slideBar.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) slideBar.getParent();
                viewGroup.removeAllViews();
            }
            return;
        }
        CPDFSliderBarView cpdfSliderBarView = (CPDFSliderBarView) slideBar;
        cpdfSliderBarView.initWithPDFView(this);
        cpdfSliderBarView.initSliderBar(CPDFSlideBar.SlideBarPosition.RIGHT, sliderBarThumbnailWidth, sliderBarThumbnailHeight);
        cpdfSliderBarView.setSlideBarBitmap(sliderBarIconResId);
        setSliderBar(cpdfSliderBarView);
    }

    private void setSliderBar(CPDFSlideBar cpdfSlideBar) {
        enableSliderBar = true;
        if (slideBar.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) slideBar.getParent();
            viewGroup.removeAllViews();
        }
        slideBar = cpdfSlideBar;
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setId(View.generateViewId());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_CONSTRAINT);
        linearLayout.setLayoutParams(layoutParams);
        int actionBarHeight = CViewUtils.getActionBarSize(getContext());
        if (actionBarHeight > 0) {
            layoutParams.setMargins(0, actionBarHeight, 0, actionBarHeight);
        }
        linearLayout.addView(slideBar);
        slideBar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        addView(linearLayout);
    }

    /**
     * Add a page indicator in the lower left corner of the page.
     * After clicking, a page number jump input box will pop up, and you can jump to the corresponding PDF page
     * page input limit: 1 ~ pdf total page
     *
     * @see CPDFPageIndicatorView
     */
    private void addPageIndicator() {
        if (!enablePageIndicator) {
            removeView(indicatorView);
            return;
        }
        removeView(indicatorView);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.startToStart = LayoutParams.PARENT_ID;
        layoutParams.endToEnd = LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        int margin = CDimensUtils.dp2px(getContext(), 16);
        layoutParams.setMargins(margin, 0, margin, pageIndicatorMarginBottom);
        indicatorView.setLayoutParams(layoutParams);
        indicatorView.setAlpha(0F);
        addView(indicatorView);
        CPDFDocument document = cPdfReaderView.getPDFDocument();
        if (document == null) {
            return;
        }
        int totalPageCount = cPdfReaderView.getPDFDocument().getPageCount();
        indicatorView.setTotalPage(totalPageCount);
        indicatorView.setCurrentPageIndex(0);
        indicatorView.setPageIndicatorClickListener(pageIndex -> {
            CGotoPageDialog dialog = CGotoPageDialog.newInstance((getContext().getString(R.string.tools_page) + String.format(" (%d/%d)", 1, cPdfReaderView.getPDFDocument().getPageCount())));
            dialog.setPageCount(cPdfReaderView.getPDFDocument().getPageCount());
            dialog.setOnPDFDisplayPageIndexListener(page -> {
                if (page <= cPdfReaderView.getPDFDocument().getPageCount() && page > 0) {
                    cPdfReaderView.setDisplayPageIndex(page - 1, true);
                }
            });
            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
            if (fragmentActivity != null) {
                dialog.show(fragmentActivity.getSupportFragmentManager(), "gotoPageDialog");
            }
        });
        pageIndicatorAnimator = ObjectAnimator.ofFloat(indicatorView, "alpha", 0F, 1F);
        pageIndicatorAnimator.setDuration(100);
        pageIndicatorAnimator.setInterpolator(new FastOutLinearInInterpolator());
        showPageIndicator();
        hidePageIndicator();
    }

    private boolean isHiding = false;

    private void hidePageIndicator() {
        if (!isHiding) {
            isHiding = true;
            handler.postDelayed(hideIndicatorRunnable, 3000);
        }
    }

    private void showPageIndicator() {
        if (pageIndicatorAnimator != null && indicatorView.getAlpha() != 1.0F) {
            isHiding = false;
            pageIndicatorAnimator.start();
        }
    }

    public void addOnPDFFocusedTypeChangeListener(OnFocusedTypeChangedListener listener) {
        pdfViewFocusedListenerList.add(listener);
    }

    public void addReaderViewCallback(CPDFIReaderViewCallback callback) {
        this.readerViewCallbacks.add(callback);
    }

    public void enablePageIndicator(boolean enablePageIndicator) {
        this.enablePageIndicator = enablePageIndicator;
        addPageIndicator();
    }

    public void enableSliderBar(boolean enableSliderBar) {
        this.enableSliderBar = enableSliderBar;
        initCPDFSliderBar();
    }

    public boolean isSaveFileExtraFontSubset() {
        if (cpdfConfiguration != null && cpdfConfiguration.globalConfig != null) {
            return cpdfConfiguration.globalConfig.fileSaveExtraFontSubset;
        } else {
            return false;
        }
    }

    public void setCPDFConfiguration(CPDFConfiguration cpdfConfiguration) {
        this.cpdfConfiguration = cpdfConfiguration;
    }

    public CPDFConfiguration getCPDFConfiguration() {
        return cpdfConfiguration;
    }

    public void setSaveCallback(COnSaveCallback saveGlobalCallback, COnSaveError error) {
        this.saveGlobalCallback = saveGlobalCallback;
        this.saveGlobalErrorCallback = error;
    }

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
