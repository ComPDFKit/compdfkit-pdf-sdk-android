/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.compdfkit.tools.contenteditor.CEditToolbar.SELECT_AREA_IMAGE;
import static com.compdfkit.tools.contenteditor.CEditToolbar.SELECT_AREA_TEXT;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationbar.CAnnotationToolbar;
import com.compdfkit.tools.common.basic.fragment.CBasicPDFFragment;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.CPDFUIVisibilityMode;
import com.compdfkit.tools.common.pdf.config.CPDFWatermarkConfig;
import com.compdfkit.tools.common.pdf.config.FormsConfig;
import com.compdfkit.tools.common.pdf.config.ModeConfig;
import com.compdfkit.tools.common.pdf.config.bota.CPDFBotaConfig;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts.RequestType;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultLauncher;
import com.compdfkit.tools.common.utils.activitycontracts.CSelectPDFDocumentResultContract;
import com.compdfkit.tools.common.utils.animation.CFillScreenManager;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.dialog.CExitTipsDialog;
import com.compdfkit.tools.common.utils.glide.CPDFGlideInitializer;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.compdfkit.tools.common.views.CPDFToolBar;
import com.compdfkit.tools.common.views.CPDFToolBarMenuHelper.ToolBarAction;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.compdfkit.tools.common.views.pdfbota.CPDFBOTA;
import com.compdfkit.tools.common.views.pdfbota.CPDFBotaDialogFragment;
import com.compdfkit.tools.common.views.pdfbota.CPDFBotaFragmentTabs;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFIReaderViewCallback;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.contenteditor.CEditToolbar;
import com.compdfkit.tools.forms.pdfformbar.CFormToolbar;
import com.compdfkit.tools.security.encryption.CDocumentEncryptionDialog;
import com.compdfkit.tools.security.encryption.CInputOwnerPwdDialog;
import com.compdfkit.tools.security.watermark.CWatermarkEditDialog;
import com.compdfkit.tools.signature.CSignatureToolBar;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.bean.CPDFDocumentSignInfo;
import com.compdfkit.tools.signature.info.signlist.CPDFCertDigitalSignListDialog;
import com.compdfkit.tools.signature.verify.CVerifySignStatusView;
import com.compdfkit.tools.viewer.pdfsearch.CSearchReplaceToolbar;
import com.compdfkit.ui.contextmenu.IContextMenuShowListener;
import com.compdfkit.ui.proxy.attach.IInkDrawCallback;
import com.compdfkit.ui.proxy.form.CPDFComboboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFListboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFPushbuttonWidgetImpl;
import com.compdfkit.ui.reader.CPDFAddAnnotCallback;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CPDFDocumentFragment extends CBasicPDFFragment {

    public static final String EXTRA_FILE_PATH = "file_path";

    public static final String EXTRA_FILE_URI = "file_uri";

    public static final String EXTRA_FILE_PASSWORD = "file_password";

    public static final String EXTRA_CONFIGURATION = "extra_configuration";

    public static final String EXTRA_PAGE_INDEX = "extra_page_index";

    public CSampleScreenManager screenManager = new CSampleScreenManager();

    public ConstraintLayout clRoot;

    public CPDFViewCtrl pdfView;

    public FrameLayout flTool;

    public CPDFToolBar pdfToolBar;

    public CSearchReplaceToolbar pdfSearchToolBarView;

    public CVerifySignStatusView signStatusView;

    public FrameLayout flBottomToolBar;

    public CAnnotationToolbar annotationToolbar;

    public CEditToolbar editToolBar;

    public CFormToolbar formToolBar;

    public CSignatureToolBar signatureToolBar;

    private View blockView;

    private AppCompatImageView ivTouchBrowse;

    private CardView cardTouchBrowse;

    public OnBackPressedCallback onBackPressedCallback;

    public CPopupMenuWindow menuWindow;

    private CPDFDocumentFragmentInitListener initListener;

    public CFillScreenChangeListener fillScreenChangeListener;

    private CPDFAddAnnotCallback addAnnotCallback;

    public static CPDFDocumentFragment newInstance(String filePath, String password, CPDFConfiguration configuration) {
        Bundle args = new Bundle();
        args.putString(EXTRA_FILE_PATH, filePath);
        args.putString(EXTRA_FILE_PASSWORD, password);
        args.putSerializable(EXTRA_CONFIGURATION, configuration);
        CPDFDocumentFragment fragment = new CPDFDocumentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CPDFDocumentFragment newInstance(Uri uri, String password, CPDFConfiguration configuration) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FILE_URI, uri);
        args.putString(EXTRA_FILE_PASSWORD, password);
        args.putSerializable(EXTRA_CONFIGURATION, configuration);
        CPDFDocumentFragment fragment = new CPDFDocumentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CPDFDocumentFragment newInstance(Bundle bundle) {
        CPDFDocumentFragment fragment = new CPDFDocumentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private final ActivityResultLauncher<Void> selectDocumentLauncher = registerForActivityResult(new CSelectPDFDocumentResultContract(), uri -> {
        if (uri != null) {
            CPDFReaderView readerView = pdfView.getCPdfReaderView();
            if (readerView != null && readerView.getEditManager() != null) {
                readerView.getEditManager().endEdit();
            }
            if (readerView != null && readerView.getContextMenuShowListener() != null) {
                readerView.getContextMenuShowListener().dismissContextMenu();
            }
            CFileUtils.takeUriPermission(getContext(), uri);
            pdfView.getCPdfReaderView().getUndoManager().clearHistory();
            setPreviewMode(cpdfConfiguration.modeConfig.initialViewMode);
            pdfView.resetAnnotationType();
            formToolBar.reset();
            editToolBar.resetStatus();
            signatureToolBar.reset();
            annotationToolbar.reset();
            screenManager.changeWindowStatus(cpdfConfiguration.modeConfig.initialViewMode);
            screenManager.constraintHide(signStatusView);

            int pageIndex = 0;
            if (getArguments() != null) {
                pageIndex = getArguments().getInt(EXTRA_PAGE_INDEX, 0);
            }
            pdfView.openPDF(uri, null, pageIndex, () -> {
                editToolBar.setEditMode(false);
                boolean enableSliderBar = pdfView.isEnableSliderBar();
                if (enableSliderBar && !screenManager.isFillScreen) {
                    screenManager.fillScreenManager.showFromRight(pdfView.slideBar, CFillScreenManager.CONFIG_SHORT_ANIM_TIME);
                }
            });
        }
    });

    private final CImageResultLauncher imageResultLauncher = new CImageResultLauncher(this);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (pdfSearchToolBarView.getVisibility() == VISIBLE) {
                    pdfSearchToolBarView.exitSearch();
                    onBackPressedCallback.setEnabled(false);
                    return;
                }
                if (pdfView.getCPdfReaderView().getTouchMode() == CPDFReaderView.TouchMode.SCREENSHOT) {
                    onBackPressedCallback.setEnabled(false);
                    exitScreenShot();
                    return;
                }
                pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
                pdfView.getCPdfReaderView().pauseAllRenderProcess();
                pdfView.getCPdfReaderView().removeAllAnnotFocus();

                CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
                boolean hasChanges = document != null && document.hasChanges();
                boolean enableExitSaveTips = cpdfConfiguration.globalConfig.enableExitSaveTips;

                if (!hasChanges) {
                    onBackPressedCallback.setEnabled(false);
                    requireActivity().onBackPressed();
                    return;
                }
                if (!enableExitSaveTips) {
                    onBackPressedCallback.setEnabled(false);
                    saveAndExit();
                    return;
                }
                CExitTipsDialog exitTipsDialog = CExitTipsDialog.newInstance();
                exitTipsDialog.setCancelClickListener(v -> {
                    onBackPressedCallback.setEnabled(false);
                    exitTipsDialog.dismiss();
                    requireActivity().onBackPressed();
                });
                exitTipsDialog.setCancelable(false);
                exitTipsDialog.setConfirmClickListener(v -> {
                    exitTipsDialog.dismiss();
                    saveAndExit();
                });
                exitTipsDialog.setContinueClickListener(v -> {
                    onBackPressedCallback.setEnabled(true);
                    exitTipsDialog.dismiss();
                });
                exitTipsDialog.show(getChildFragmentManager(), "exitTipsDialog");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void saveAndExit() {
        if (pdfView != null) {
            showLoadingDialog(getString(R.string.tools_saveing));
            pdfView.savePDF((filePath, pdfUri) -> {
                try {
                    dismissLoadingDialog();
                    onBackPressedCallback.setEnabled(false);
                    requireActivity().onBackPressed();
                } catch (Exception ignored) {
                }
            }, e -> {
                try {
                    dismissLoadingDialog();
                    onBackPressedCallback.setEnabled(false);
                    requireActivity().onBackPressed();
                } catch (Exception ignored) {

                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parseConfiguration();
        int themeId = CPDFApplyConfigUtil.getInstance().getGlobalThemeId(getContext(), cpdfConfiguration);
        Context wrapper = new ContextThemeWrapper(getContext(), themeId);
        LayoutInflater themedInflater = inflater.cloneInContext(wrapper);
        View rootView = themedInflater.inflate(R.layout.tools_pdf_document_fragment, container, false);
        CPDFGlideInitializer.register(getContext());
        clRoot = rootView.findViewById(R.id.cl_root);
        pdfView = rootView.findViewById(R.id.pdf_view);
        flTool = rootView.findViewById(R.id.fl_tool);
        pdfToolBar = rootView.findViewById(R.id.pdf_tool_bar);
        pdfSearchToolBarView = rootView.findViewById(R.id.search_toolbar_view);
        signStatusView = rootView.findViewById(R.id.sign_status_view);
        flBottomToolBar = rootView.findViewById(R.id.fl_bottom_tool_bar);
        annotationToolbar = rootView.findViewById(R.id.annotation_tool_bar);
        editToolBar = rootView.findViewById(R.id.edit_tool_bar);
        formToolBar = rootView.findViewById(R.id.form_tool_bar);
        signatureToolBar = rootView.findViewById(R.id.signature_tool_bar);
        blockView = rootView.findViewById(R.id.block_view);
        ivTouchBrowse = rootView.findViewById(R.id.iv_touch_browse);
        cardTouchBrowse = rootView.findViewById(R.id.card_ink_touch_browse);
        CPDFApplyConfigUtil.getInstance().appleUiConfig(this, cpdfConfiguration);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        screenManager.bind(this);
        pdfView.setCPDFConfiguration(cpdfConfiguration);
        initDocument(() -> {
            initPDFView();
            initToolBarView();
            initSearchBar();
            initAnnotToolbar();
            initFormToolbar();
            initEditBar();
            initSignatureToolbar();
            applyConfiguration();
            onDoNext();
            if (initListener != null) {
                initListener.compile(pdfView);
            }
        });
    }

    protected void initDocument(CPDFViewCtrl.COnOpenPdfFinishCallback callback) {
        if (getArguments() != null) {
            int pageIndex = getArguments().getInt(EXTRA_PAGE_INDEX, 0);
            String password = getArguments().getString(EXTRA_FILE_PASSWORD);
            if (!TextUtils.isEmpty(getArguments().getString(EXTRA_FILE_PATH))) {
                String path = getArguments().getString(EXTRA_FILE_PATH);
                pdfView.openPDF(path, password, pageIndex, callback);
            } else if (getArguments().getParcelable(EXTRA_FILE_URI) != null) {
                Uri uri = getArguments().getParcelable(EXTRA_FILE_URI);
                CFileUtils.takeUriPermission(getContext(), uri);
                pdfView.openPDF(uri, password, pageIndex, callback);
            }
        }
    }

    protected void initPDFView() {
        registerAnnotHelper(pdfView);
        registerFormHelper(pdfView);
        pdfView.addReaderViewCallback(new CPDFIReaderViewCallback() {
            @Override
            public void onTapMainDocArea() {
                if (pdfSearchToolBarView.getVisibility() == VISIBLE) {
                    pdfSearchToolBarView.showSearchReplaceContextMenu();
                    return;
                }
                if (pdfView.getCPdfReaderView().getTouchMode() == CPDFReaderView.TouchMode.SCREENSHOT) {
                    return;
                }
                if (ivTouchBrowse.getVisibility() == VISIBLE && ivTouchBrowse.isSelected()) {
                    return;
                }
                if (annotationToolbar.toolListAdapter.getCurrentAnnotType() == CAnnotationType.INK) {
                    return;
                }
                CPDFUIVisibilityMode uiVisibilityMode = cpdfConfiguration.modeConfig.uiVisibilityMode;
                if (uiVisibilityMode == CPDFUIVisibilityMode.AUTOMATIC) {
                    //Use the CFillScreenManager.class to manage fullscreen switching.
                    screenManager.fillScreenChange();
                    if (fillScreenChangeListener != null) {
                        fillScreenChangeListener.fillScreenChange(screenManager.isFillScreen);
                    }
                }
            }

            @Override
            public void onEndScroll() {
                super.onEndScroll();
                pdfSearchToolBarView.showSearchReplaceContextMenu();
                editToolBar.updateUndoRedo();
            }
        });
        pdfView.getCPdfReaderView().setPdfAddAnnotCallback((cpdfPageView, cpdfBaseAnnot) -> {
            CPDFAnnotation annotation = cpdfBaseAnnot.onGetAnnotation();
            if (annotation.getType() != CPDFAnnotation.Type.WIDGET) {
                annotation.setTitle(cpdfConfiguration.annotationsConfig.annotationAuthor);
                annotation.updateAp();
            }
            if (addAnnotCallback != null) {
                addAnnotCallback.onAddAnnotation(cpdfPageView, cpdfBaseAnnot);
            }

            FormsConfig formsConfig = cpdfConfiguration.formsConfig;
            // Annotation creation completed listener, you can use cpdfBaseAnnot.getAnnotType() to determine the type of the added annotation
            if (cpdfBaseAnnot instanceof CPDFListboxWidgetImpl && formsConfig.showCreateListBoxOptionsDialog) {
                // When the ListBox form is created, display an editing dialog for adding list data
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showFormListEditFragment(getChildFragmentManager(), cpdfBaseAnnot, cpdfPageView, false);
            } else if (cpdfBaseAnnot instanceof CPDFComboboxWidgetImpl && formsConfig.showCreateComboBoxOptionsDialog) {
                // When the ComboBox form is created, display an editing dialog for adding list data
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showFormComboBoxEditFragment(getChildFragmentManager(), cpdfBaseAnnot, cpdfPageView, true);
            } else if (cpdfBaseAnnot instanceof CPDFPushbuttonWidgetImpl && formsConfig.showCreatePushButtonOptionsDialog) {
                // When the PushButton form is created, display a dialog for editing the action method
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showPushButtonActionDialog(getChildFragmentManager(), pdfView.getCPdfReaderView(),
                        cpdfBaseAnnot, cpdfPageView);
            }
        });
    }

    public void setPreviewMode(CPreviewMode mode) {
        if (pdfView.getCPdfReaderView() == null) {
            return;
        }
        CPDFReaderView readerView = pdfView.getCPdfReaderView();
        readerView.getInkDrawHelper().onSave();
        readerView.pauseAllRenderProcess();
        readerView.removeAllAnnotFocus();
        IContextMenuShowListener contextMenuShowListener = readerView.getContextMenuShowListener();
        if (contextMenuShowListener != null) {
            contextMenuShowListener.dismissContextMenu();
        }
        screenManager.changeWindowStatus(mode);
        pdfToolBar.selectMode(mode);
        formToolBar.reset();
        signatureToolBar.reset();
        resetContextMenu(pdfView, mode);
        CPDFEditManager editManager = readerView.getEditManager();
        if (mode == CPreviewMode.Edit) {
            readerView.setViewMode(CPDFReaderView.ViewMode.PDFEDIT);
            editToolBar.updateUndoRedo();
            if (editManager != null && !editManager.isEditMode()) {
                editManager.enable();
                editManager.beginEdit(CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath);
            }
        } else {

            if (readerView.getTouchMode() == CPDFReaderView.TouchMode.SCREENSHOT
                    && readerView.getViewMode() == CPDFReaderView.ViewMode.PDFEDIT) {
                readerView.setTouchMode(CPDFReaderView.TouchMode.EDIT);
            }
            pdfView.exitEditMode();
            switch (mode) {
                case Viewer:
                case Signature:
                    pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.VIEW);
                    break;
                case Annotation:
                    pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.ANNOT);
                    break;
                case Form:
                    pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.FORM);
                    break;
                default:
                    break;
            }
        }
    }
    protected void initToolBarView() {
        pdfToolBar.setPreviewModeChangeListener(this::setPreviewMode);
        ModeConfig modeConfig = cpdfConfiguration.modeConfig;
        pdfToolBar.addModes(modeConfig.availableViewModes);
        pdfToolBar.selectMode(modeConfig.initialViewMode);
        pdfToolBar.setMenuItems(this, cpdfConfiguration.toolbarConfig);
    }

    protected void requestStoragePermissions(CRequestPermissionListener permissionListener) {
        if (Build.VERSION.SDK_INT >= CPermissionUtil.VERSION_TIRAMISU) {
            CPermissionUtil.openManageAllFileAppSettings(getContext());
        } else {
            multiplePermissionResultLauncher.launch(STORAGE_PERMISSIONS, result -> {
                if (CPermissionUtil.hasStoragePermissions(getContext())) {
                    if (permissionListener != null) {
                        permissionListener.request();
                    }
                } else {
                    if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showPermissionsRequiredDialog();
                    }
                }
            });
        }
    }

    protected void initAnnotToolbar() {
        annotationToolbar.initWithPDFView(pdfView);
        annotationToolbar.addAnnotationChangeListener(type -> {
            screenManager.changeWindowStatus(type);
            //You are required to grant recording permission when selecting voice notes
            if (type == CAnnotationType.SOUND) {
                if (!hasPermission(Manifest.permission.RECORD_AUDIO)) {
                    permissionResultLauncher.launch(Manifest.permission.RECORD_AUDIO, hasRecordAudioPermission -> {
                        if (!hasRecordAudioPermission) {
                            pdfView.resetAnnotationType();
                            if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.RECORD_AUDIO)) {
                                showPermissionsRequiredDialog();
                            }
                        }
                    });
                }
            }
            if (type == CAnnotationType.INK){
                if (cpdfConfiguration.toolbarConfig.showInkToggleButton){
                    screenManager.constraintShow(cardTouchBrowse);
                }
            }else {
                screenManager.constraintHide(cardTouchBrowse);
                ivTouchBrowse.setSelected(false);
            }
        });
        ivTouchBrowse.setOnClickListener(view -> {
            ivTouchBrowse.setSelected(!ivTouchBrowse.isSelected());
            pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
            if (ivTouchBrowse.isSelected()){
                pdfView.getCPdfReaderView().getInkDrawHelper().setMode(IInkDrawCallback.Mode.VIEW);
            } else {
                pdfView.getCPdfReaderView().getInkDrawHelper().setMode(IInkDrawCallback.Mode.DRAW);
            }
        });
    }

    protected void initFormToolbar() {
        formToolBar.initWithPDFView(pdfView);
    }

    protected void initSearchBar() {
        pdfSearchToolBarView.initWithPDFView(pdfView);
        pdfSearchToolBarView.setExitSearchListener(this::hideTextSearchView);
    }

    private void restoreEdit() {
        restoreEdit(pdfView, pdfToolBar.getMode() == CPreviewMode.Edit);
    }

    protected void initEditBar() {
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return;
        }
        editToolBar.initWithPDFView(pdfView);


        editToolBar.setEditPropertyBtnClickListener((view) -> {
            int type = pdfView.getCPdfReaderView().getSelectAreaType();
            CStyleType styleType = CStyleType.UNKNOWN;
            if (type == SELECT_AREA_TEXT) {
                styleType = CStyleType.EDIT_TEXT;
            } else if (type == SELECT_AREA_IMAGE) {
                styleType = CStyleType.EDIT_IMAGE;
            }
            if (styleType != CStyleType.UNKNOWN) {
                CPDFReaderView readerView = pdfView.getCPdfReaderView();
                CPDFContextMenuHelper menuHelper = (CPDFContextMenuHelper) readerView.getContextMenuShowListener();
                if (menuHelper == null || menuHelper.getReaderView() == null) {
                    return;
                }
                CStyleManager styleManager = new CStyleManager(menuHelper.getEditSelection(), menuHelper.getPageView());
                CAnnotStyle annotStyle = styleManager.getStyle(styleType);
                CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(annotStyle);
                styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
                styleManager.setDialogHeightCallback(styleDialogFragment, pdfView.getCPdfReaderView());
                styleDialogFragment.show(getChildFragmentManager(), "textPropertyDialogFragment");
                menuHelper.dismissContextMenu();
            }
        });

        pdfView.getCPdfReaderView().setSelectImageCallback(() -> {
            if (cpdfConfiguration != null && cpdfConfiguration.readerViewConfig.enableCreateImagePickerDialog){
                imageResultLauncher.launch(RequestType.PHOTO_ALBUM,
                    result -> pdfView.getCPdfReaderView().addEditImage(result));
            }
        });
    }

    protected void initSignatureToolbar() {
        signStatusView.initWithPDFView(pdfView);
        signStatusView.getBtnDetails().setOnClickListener(v -> {
            CPDFCertDigitalSignListDialog signListDialog = CPDFCertDigitalSignListDialog.newInstance();
            signListDialog.initWithPDFView(pdfView);
            signListDialog.setDialogDismissListener(this::verifyDocumentSignStatus);
            signListDialog.show(getChildFragmentManager(), "signListDialog");
        });
        signatureToolBar.initWithPDFView(pdfView);
        signatureToolBar.getVerifySignButton().setOnClickListener(v -> verifyDocumentSignStatus());
    }

    public void showToolbarMenuDialog(View anchorView) {
        //Show the PDF settings dialog fragment
        menuWindow = new CPopupMenuWindow(getContext());
        if (cpdfConfiguration != null && cpdfConfiguration.toolbarConfig != null) {
            List<ToolBarAction> menuActions = cpdfConfiguration.toolbarConfig.availableMenus;
            if (menuActions == null || menuActions.isEmpty()) {
                return;
            }
            anchorView.setSelected(true);
            pdfToolBar.setMoreMenuActions(this, cpdfConfiguration.toolbarConfig);
        }
        menuWindow.setOnDismissListener(() -> anchorView.setSelected(false));
        menuWindow.showAsDropDown(anchorView);
    }

    protected void flattenedPdf() {
        CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(
                Environment.getExternalStorageDirectory().getAbsolutePath(),
                getString(R.string.tools_select_folder),
                getString(R.string.tools_save_to_this_directory)
        );
        directoryDialog.setSelectFolderListener(dir -> {
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            if (document == null) {
                CToastUtil.showLongToast(getContext(), R.string.tools_save_failed);
                return;
            }
            showLoadingDialog();
            File file = new File(dir, CFileUtils.getFileNameNoExtension(document.getFileName()) + getString(R.string.tools_flattened_suffix));
            file = CFileUtils.renameNameSuffix(file);
            File finalFile = file;
            CThreadPoolUtils.getInstance().executeIO(() -> {
                boolean result = document.flattenAllPages(CPDFPage.PDFFlattenOption.FLAT_NORMALDISPLAY);
                if (result) {
                    try {
                        boolean saveResult = document.saveAs(finalFile.getAbsolutePath(), false, cpdfConfiguration.globalConfig.fileSaveExtraFontSubset);
                        CThreadPoolUtils.getInstance().executeMain(() -> {
                            dismissLoadingDialog();
                            if (document.shouleReloadDocument()) {
                                document.reload();
                            }
                            if (saveResult) {
                                CToastUtil.showLongToast(getContext(), R.string.tools_save_success);
                                pdfView.openPDF(finalFile.getAbsolutePath());
                            }
                        });
                    } catch (Exception ignored) {
                    }
                } else {
                    dismissLoadingDialog();
                }
            });

        });
        directoryDialog.show(getChildFragmentManager(), "dirDialog");
    }

    /**
     *
     */
    public void verifyDocumentSignStatus() {
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        if (CertificateDigitalDatas.hasDigitalSignature(document)) {
            CThreadPoolUtils.getInstance().executeIO(() -> {
                CPDFDocumentSignInfo status = CertificateDigitalDatas.verifyDocumentSignStatus(document);
                CThreadPoolUtils.getInstance().executeMain(() -> {
                    signStatusView.setStatus(status);
                    screenManager.fillScreenManager.bindTopToolView(signStatusView);
                    screenManager.constraintShow(signStatusView);
                });
            });
        } else {
            if (signStatusView.getVisibility() == View.VISIBLE) {
                screenManager.fillScreenManager.removeAndHideToolView(signStatusView);
                screenManager.constraintHide(signStatusView);
            }
        }
    }

    public void hideDigitalSignStatusView(){
        if (signStatusView.getVisibility() == View.VISIBLE) {
            screenManager.fillScreenManager.removeAndHideToolView(signStatusView);
            screenManager.constraintHide(signStatusView);
        }
    }

    public void selectDocument() {
        if (pdfToolBar.getMode() == CPreviewMode.Edit) {
            pdfView.exitEditMode();
        }

        if (pdfView.getCPdfReaderView().getPDFDocument() == null) {
            selectDocumentLauncher.launch(null);
            return;
        }
        if (!pdfView.getCPdfReaderView().getPDFDocument().hasChanges()) {
            selectDocumentLauncher.launch(null);
            return;
        }
        CAlertDialog alertDialog = CAlertDialog.newInstance(getString(R.string.tools_save_title), getString(R.string.tools_save_message));
        alertDialog.setConfirmClickListener(v -> {
            alertDialog.dismiss();
            // save pdf document
            if (cpdfConfiguration != null && cpdfConfiguration.globalConfig.fileSaveExtraFontSubset){
                showLoadingDialog(getString(R.string.tools_saveing));
            }
            pdfView.savePDF((filePath, pdfUri) -> {
                dismissLoadingDialog();
                selectDocumentLauncher.launch(null);
            }, e -> {
                dismissLoadingDialog();
                selectDocumentLauncher.launch(null);
            });
        });
        alertDialog.setCancelClickListener(v -> {
            alertDialog.dismiss();
            selectDocumentLauncher.launch(null);
        });
        alertDialog.show(getChildFragmentManager(), "alertDialog");
    }

    protected void onDoNext() {
        multiplePermissionResultLauncher.launch(STORAGE_PERMISSIONS, result -> {

        });
    }

    protected void showSettingEncryptionDialog() {
        CDocumentEncryptionDialog documentEncryptionDialog = CDocumentEncryptionDialog.newInstance();
        documentEncryptionDialog.setDocument(pdfView.getCPdfReaderView().getPDFDocument());
        documentEncryptionDialog.setSaveFileExtraFontSubset(pdfView.isSaveFileExtraFontSubset());
        documentEncryptionDialog.setEncryptionResultListener((isRemoveSecurity, result, filePath, password) -> {
            pdfView.getCPdfReaderView().reloadPages();
            pdfView.openPDF(filePath);
            documentEncryptionDialog.dismiss();
            int msgResId;
            if (isRemoveSecurity) {
                msgResId = result ? R.string.tools_password_remove_success : R.string.tools_password_remove_fail;
            } else {
                msgResId = result ? R.string.tools_set_password_successfully : R.string.tools_set_password_failures;
            }
            CToastUtil.showLongToast(getContext(), msgResId);
        });
        documentEncryptionDialog.show(getChildFragmentManager(), "documentEncryption");
    }

    protected void parseConfiguration() {
        if (getArguments() != null && getArguments().containsKey(EXTRA_CONFIGURATION)) {
            if (Build.VERSION.SDK_INT >= CPermissionUtil.VERSION_TIRAMISU) {
                cpdfConfiguration = getArguments().getSerializable(EXTRA_CONFIGURATION, CPDFConfiguration.class);
            } else {
                cpdfConfiguration = (CPDFConfiguration) getArguments().getSerializable(EXTRA_CONFIGURATION);
            }
        }
        if (cpdfConfiguration == null) {
            cpdfConfiguration = CPDFConfigurationUtils.normalConfig(getContext(), "tools_default_configuration.json");
        }
    }

    protected void applyConfiguration() {
        CPDFApplyConfigUtil.getInstance().applyConfiguration(this, cpdfConfiguration);
        CPDFApplyConfigUtil.getInstance().applyModeConfig(this, cpdfConfiguration);
        CPDFApplyConfigUtil.getInstance().applyReaderViewUiStyle(pdfView.getCPdfReaderView(), cpdfConfiguration.readerViewConfig.uiStyle);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            pdfSearchToolBarView.showSearchReplaceContextMenu();
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            pdfSearchToolBarView.showSearchReplaceContextMenu();
        }
    }

    public void exitScreenShot() {
        CPDFReaderView readerView = pdfView.getCPdfReaderView();
        if (readerView.getContextMenuShowListener() != null) {
            readerView.getContextMenuShowListener().dismissContextMenu();
        }
        for (int i = 0; i < readerView.getChildCount(); i++) {
            CPDFPageView view = (CPDFPageView) readerView.getChildAt(i);
            view.clearScreenShotRect();
        }
        CPDFReaderView.ViewMode viewMode = readerView.getViewMode();
        if (viewMode == CPDFReaderView.ViewMode.PDFEDIT) {
            readerView.setTouchMode(CPDFReaderView.TouchMode.EDIT);
            CPDFEditManager editManager = readerView.getEditManager();
            if (editManager != null && !editManager.isEditMode()) {
                editManager.enable();
                editManager.beginEdit(CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath);
            }
        } else {
            readerView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
        }
        screenManager.fillScreenChange();
    }

    public void showTextSearchView() {
        if (pdfView.getCPdfReaderView().getEditManager().isEditMode()) {
            curEditMode = pdfView.getCPdfReaderView().getLoadType();
        } else {
            curEditMode = CPDFEditPage.LoadNone;
        }
        pdfView.exitEditMode();
        if (flTool.getVisibility() == GONE && !cpdfConfiguration.toolbarConfig.mainToolbarVisible) {
            screenManager.fillScreenManager.showFromTop(flTool, 200);
        }
        pdfToolBar.setVisibility(View.GONE);
        CSearchReplaceToolbar.ViewType viewType = pdfView.getCPdfReaderView().getViewMode() == CPDFReaderView.ViewMode.PDFEDIT
                ? CSearchReplaceToolbar.ViewType.SearchReplace : CSearchReplaceToolbar.ViewType.Search;
        onBackPressedCallback.setEnabled(true);
        pdfSearchToolBarView.setViewType(viewType);
        pdfSearchToolBarView.setVisibility(View.VISIBLE);
        pdfSearchToolBarView.showKeyboard();
        if (viewType == CSearchReplaceToolbar.ViewType.SearchReplace) {
            screenManager.fillScreenManager.removeToolView(flBottomToolBar);
            screenManager.fillScreenManager.hideFromBottom(flBottomToolBar, 200);
        }
    }

    public void hideTextSearchView() {
        if (curEditMode > CPDFEditPage.LoadNone) {
            restoreEdit();
            editToolBar.updateUndoRedo();
        }
        if (!cpdfConfiguration.toolbarConfig.mainToolbarVisible) {
            flTool.setVisibility(GONE);
        }
        pdfToolBar.setVisibility(VISIBLE);
        pdfSearchToolBarView.hideKeyboard();
        pdfSearchToolBarView.setVisibility(GONE);
        if (pdfView.getCPdfReaderView().getViewMode() == CPDFReaderView.ViewMode.PDFEDIT) {
            screenManager.fillScreenManager.bindBottomToolViewList(flBottomToolBar);
            screenManager.constraintShow(flBottomToolBar);
        }
        blockView.setVisibility(GONE);
    }

    public void showBOTA() {
        pdfView.getCPdfReaderView().removeAllAnnotFocus();
        if (pdfView.getCPdfReaderView().getEditManager().isEditMode()) {
            curEditMode = pdfView.getCPdfReaderView().getLoadType();
        } else {
            curEditMode = CPDFEditPage.LoadNone;
        }
        pdfView.exitEditMode();
        ArrayList<CPDFBotaFragmentTabs> tabs = new ArrayList<>();
        CPDFBotaFragmentTabs annotationTab = new CPDFBotaFragmentTabs(CPDFBOTA.ANNOTATION, getString(R.string.tools_annotations));
        CPDFBotaFragmentTabs outlineTab = new CPDFBotaFragmentTabs(CPDFBOTA.OUTLINE, getString(R.string.tools_outlines));
        CPDFBotaFragmentTabs bookmarkTab = new CPDFBotaFragmentTabs(CPDFBOTA.BOOKMARKS, getString(R.string.tools_bookmarks));

        CPDFBotaConfig botaConfig = cpdfConfiguration.globalConfig.bota;
        for (Integer tab : botaConfig.tabs) {
            if (tab == CPDFBOTA.OUTLINE) {
                tabs.add(outlineTab);
            } else if (tab == CPDFBOTA.BOOKMARKS) {
                tabs.add(bookmarkTab);
            } else if (tab == CPDFBOTA.ANNOTATION) {
                tabs.add(annotationTab);
            }
        }
        if (pdfToolBar.getMode() == CPreviewMode.Annotation) {
            annotationTab.setDefaultSelect(true);
        }

        CPDFBotaDialogFragment dialogFragment = CPDFBotaDialogFragment.newInstance();
        dialogFragment.initWithPDFView(pdfView);
        dialogFragment.setBotaDialogTabs(tabs);
        dialogFragment.setMenus(botaConfig.getMenus());
        dialogFragment.setDismissListener(this::restoreEdit);
        dialogFragment.show(getChildFragmentManager(), "annotationList");
    }

    public void showPageEdit(boolean enterEditMode, boolean enableEditMode) {
        showPageEdit(pdfView, enterEditMode, enableEditMode, () -> {
            restoreEdit();
            int pageCount = pdfView.getCPdfReaderView().getPageCount();
            CPDFConfiguration configuration = pdfView.getCPDFConfiguration();
            if (configuration != null) {
                boolean enableSliderBar = configuration.readerViewConfig.enableSliderBar;
                if (enableSliderBar) {
                    boolean show = pageCount > 1;
                    pdfView.enableSliderBar(show);
                    if (show && !screenManager.isFillScreen) {
                        screenManager.fillScreenManager.showFromRight(pdfView.slideBar, CFillScreenManager.CONFIG_SHORT_ANIM_TIME);
                    }
                }
            }
        });
    }

    public void showSecurityDialog() {
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        if (document == null) {
            return;
        }
        if (document.getPermissions() == CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsUser) {
            CInputOwnerPwdDialog inputOwnerPwdDialog = CInputOwnerPwdDialog.newInstance();
            inputOwnerPwdDialog.setDocument(document);
            inputOwnerPwdDialog.setCancelClickListener(v2 -> inputOwnerPwdDialog.dismiss());
            inputOwnerPwdDialog.setConfirmClickListener(ownerPassword -> {
                document.reload(ownerPassword);
                showSettingEncryptionDialog();
                inputOwnerPwdDialog.dismiss();
            });
            inputOwnerPwdDialog.show(getChildFragmentManager(), "inputPasswordDialog");
            return;
        }
        showSettingEncryptionDialog();
    }

    public void showAddWatermarkDialog() {
        showAddWatermarkDialog(cpdfConfiguration.globalConfig.watermark);
    }

    public void showAddWatermarkDialog(CPDFWatermarkConfig watermarkConfig) {
        CWatermarkEditDialog watermarkEditDialog = CWatermarkEditDialog.newInstance();
        watermarkEditDialog.setDocument(pdfView.getCPdfReaderView().getPDFDocument());
        watermarkEditDialog.setSaveFileExtraFontSubset(pdfView.isSaveFileExtraFontSubset());
        watermarkEditDialog.setPageIndex(pdfView.currentPageIndex);
        watermarkEditDialog.setWatermarkConfig(watermarkConfig);
        watermarkEditDialog.setCompleteListener((success, saveAsNewFile1, pdfFile) -> {
            watermarkEditDialog.dismiss();
            pdfView.getCPdfReaderView().reloadPages();
            if (!success) {
                CToastUtil.showLongToast(getContext(), R.string.tools_watermark_add_failed);
                return;
            }
            CToastUtil.showLongToast(getContext(), R.string.tools_watermark_add_success);
            if (saveAsNewFile1) {
                pdfView.openPDF(pdfFile);
            }
        });
        watermarkEditDialog.show(getChildFragmentManager(), "watermarkEditDialog");
    }

    public void showFlattenedDialog() {
        if (Build.VERSION.SDK_INT < CPermissionUtil.VERSION_R) {
            multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
                if (CPermissionUtil.hasStoragePermissions(getContext())) {
                    pdfView.savePDF((filePath, pdfUri) -> flattenedPdf(), e -> flattenedPdf());
                } else {
                    if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), requireContext());
                    }
                }
            });
        } else {
            pdfView.savePDF((filePath, pdfUri) -> flattenedPdf(), e -> flattenedPdf());
        }
    }

    public void enterSnipMode() {
        CPDFReaderView readerView = pdfView.getCPdfReaderView();
        readerView.removeAllAnnotFocus();
        if (readerView.getContextMenuShowListener() != null) {
            readerView.getContextMenuShowListener().dismissContextMenu();
        }
        for (int i = 0; i < readerView.getChildCount(); i++) {
            CPDFPageView view = (CPDFPageView) readerView.getChildAt(i);
            view.clearScreenShotRect();
        }
        onBackPressedCallback.setEnabled(true);
        // enter fill screen mode
        screenManager.fillScreenChange();
        // enter screenshot mode , Please select the screenshot area in the reader view
        readerView.setTouchMode(CPDFReaderView.TouchMode.SCREENSHOT);
    }

    public void exitSnipMode() {
        CPDFReaderView readerView = pdfView.getCPdfReaderView();
        readerView.removeAllAnnotFocus();
        if (readerView.getContextMenuShowListener() != null) {
            readerView.getContextMenuShowListener().dismissContextMenu();
        }
        for (int i = 0; i < readerView.getChildCount(); i++) {
            CPDFPageView view = (CPDFPageView) readerView.getChildAt(i);
            view.clearScreenShotRect();
        }
        CPDFReaderView.ViewMode viewMode = readerView.getViewMode();
        if (viewMode == CPDFReaderView.ViewMode.PDFEDIT) {
            readerView.setTouchMode(CPDFReaderView.TouchMode.EDIT);
            CPDFEditManager editManager = readerView.getEditManager();
            if (editManager != null && !editManager.isEditMode()) {
                editManager.enable();
                editManager.beginEdit(CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath);
            }
        } else {
            readerView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
        }
        screenManager.fillScreenChange();
    }

    protected interface CRequestPermissionListener {

        void request();
    }

    @Override
    public void onDestroy() {
        try {
            CLog.e("ComPDFKit", "CPDFDocumentFragment:onDestroy() document close()");
            CViewUtils.hideKeyboard(getActivity().getWindow().getDecorView());
            onBackPressedCallback = null;
            initListener = null;
            fillScreenChangeListener = null;
            addAnnotCallback = null;
            dismissLoadingDialog();
            pdfView.close();
            if (menuWindow != null) {
                menuWindow.dismiss();
            }
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    public void setInitListener(CPDFDocumentFragmentInitListener initListener) {
        this.initListener = initListener;
    }

    public void setFillScreenChangeListener(CFillScreenChangeListener fillScreenChangeListener) {
        this.fillScreenChangeListener = fillScreenChangeListener;
    }

    public void setAddAnnotCallback(CPDFAddAnnotCallback addAnnotCallback) {
        this.addAnnotCallback = addAnnotCallback;
    }

    public interface CPDFDocumentFragmentInitListener {
        void compile(CPDFViewCtrl pdfView);
    }

    public interface CFillScreenChangeListener {
        void fillScreenChange(boolean fillScreen);
    }
}
