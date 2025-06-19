/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
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
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditConfig;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationbar.CAnnotationToolbar;
import com.compdfkit.tools.common.basic.fragment.CBasicPDFFragment;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.ToolbarConfig;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts.RequestType;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultLauncher;
import com.compdfkit.tools.common.utils.activitycontracts.CSelectPDFDocumentResultContract;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.dialog.CExitTipsDialog;
import com.compdfkit.tools.common.utils.dialog.CLoadingDialog;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.compdfkit.tools.common.views.CPDFToolBar;
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

    private CPopupMenuWindow menuWindow;

    private CPDFDocumentFragmentInitListener initListener;

    public CFillScreenChangeListener fillScreenChangeListener;

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

    private ActivityResultLauncher<Void> selectDocumentLauncher = registerForActivityResult(new CSelectPDFDocumentResultContract(), uri -> {
        if (uri != null) {
            CPDFReaderView readerView = pdfView.getCPdfReaderView();
            if (readerView != null && readerView.getEditManager() != null) {
                readerView.getEditManager().endEdit();
            }
            if (readerView != null && readerView.getContextMenuShowListener() != null) {
                readerView.getContextMenuShowListener().dismissContextMenu();
            }
            CFileUtils.takeUriPermission(getContext(), uri);
            pdfView.resetAnnotationType();
            formToolBar.reset();
            editToolBar.resetStatus();
            signatureToolBar.reset();
            setPreviewMode(cpdfConfiguration.modeConfig.initialViewMode);
            screenManager.changeWindowStatus(cpdfConfiguration.modeConfig.initialViewMode);
            screenManager.constraintHide(signStatusView);
            pdfView.openPDF(uri, null, () -> editToolBar.setEditMode(false));
        }
    });

    private CImageResultLauncher imageResultLauncher = new CImageResultLauncher(this);

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

                CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
                boolean hasChanges = document != null && document.hasChanges();
                boolean enableExitSaveTips = cpdfConfiguration.globalConfig.enableExitSaveTips;

                if (!hasChanges){
                    onBackPressedCallback.setEnabled(false);
                    requireActivity().onBackPressed();
                    return;
                }
                if (!enableExitSaveTips){
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
                    onBackPressedCallback.setEnabled(false);
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

    private void saveAndExit(){
        if (pdfView != null) {
            pdfView.savePDF((filePath, pdfUri) -> {
                try {
                    requireActivity().onBackPressed();
                } catch (Exception e) {
                }
            }, e -> {
                e.printStackTrace();
                try {
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
            String password = getArguments().getString(EXTRA_FILE_PASSWORD);
            if (!TextUtils.isEmpty(getArguments().getString(EXTRA_FILE_PATH))) {
                String path = getArguments().getString(EXTRA_FILE_PATH);
                pdfView.openPDF(path, password, callback);
            } else if (getArguments().getParcelable(EXTRA_FILE_URI) != null) {
                Uri uri = getArguments().getParcelable(EXTRA_FILE_URI);
                CFileUtils.takeUriPermission(getContext(), uri);
                pdfView.openPDF(uri, password, callback);
            }
        }
    }

    protected void initPDFView() {
        pdfView.setCPDFConfiguration(cpdfConfiguration);
        pdfView.getCPdfReaderView().setMinScaleEnable(false);
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
                if (!cpdfConfiguration.modeConfig.readerOnly) {
                    //Use the CFillScreenManager.class to manage fullscreen switching.
                    screenManager.fillScreenChange();
                    CPDFReaderView readerView = pdfView.getCPdfReaderView();
                    if (!readerView.isContinueMode() && CViewUtils.isLandScape(getContext()) && !readerView.isVerticalMode()) {
                        readerView.postDelayed(() -> {
                            readerView.setScale(1F);
                        }, 200);
                    }
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

            // Annotation creation completed listener, you can use cpdfBaseAnnot.getAnnotType() to determine the type of the added annotation
            if (cpdfBaseAnnot instanceof CPDFListboxWidgetImpl) {
                // When the ListBox form is created, display an editing dialog for adding list data
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showFormListEditFragment(getChildFragmentManager(), cpdfBaseAnnot, cpdfPageView, false);
            } else if (cpdfBaseAnnot instanceof CPDFComboboxWidgetImpl) {
                // When the ComboBox form is created, display an editing dialog for adding list data
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showFormComboBoxEditFragment(getChildFragmentManager(), cpdfBaseAnnot, cpdfPageView, true);
            } else if (cpdfBaseAnnot instanceof CPDFPushbuttonWidgetImpl) {
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
        if (cpdfConfiguration != null) {
            ToolbarConfig toolbarConfig = cpdfConfiguration.toolbarConfig;
            for (ToolbarConfig.ToolbarAction androidAvailableAction : toolbarConfig.androidAvailableActions) {
                switch (androidAvailableAction) {
                    case Back:
                        pdfToolBar.addBackPressedAction(v -> {
                            onBackPressedCallback.handleOnBackPressed();
                        });
                        break;
                    case Thumbnail:
                        pdfToolBar.addAction(R.drawable.tools_ic_thumbnail, v -> {
                            showPageEdit(false);
                        });
                        break;
                    case Search:
                        pdfToolBar.addAction(R.drawable.tools_ic_search, v -> {
                            showSearch();
                        });
                        break;
                    case Bota:
                        pdfToolBar.addAction(R.drawable.tools_ic_bookmark, v -> {
                            showBOTA();
                        });
                        break;
                    case Menu:
                        pdfToolBar.addAction(R.drawable.tools_ic_more, this::showToolbarMenuDialog);
                        break;
                    default:
                        break;
                }
            }
        }
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
        annotationToolbar.setAnnotationChangeListener(type -> {
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
                screenManager.constraintShow(cardTouchBrowse);
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
        pdfSearchToolBarView.setExitSearchListener(() -> {
            if (curEditMode > CPDFEditPage.LoadNone) {
                restoreEdit();
                editToolBar.updateUndoRedo();
            }
            pdfToolBar.setVisibility(VISIBLE);
            pdfSearchToolBarView.setVisibility(GONE);
            if (pdfView.getCPdfReaderView().getViewMode() == CPDFReaderView.ViewMode.PDFEDIT) {
                screenManager.fillScreenManager.bindBottomToolViewList(flBottomToolBar);
                screenManager.constraintShow(flBottomToolBar);
            }
            blockView.setVisibility(GONE);
        });
    }

    private void restoreEdit() {
        restoreEdit(pdfView, pdfToolBar.getMode() == CPreviewMode.Edit);
    }

    protected void initEditBar() {
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return;
        }
        editToolBar.initWithPDFView(pdfView);
        editToolBar.setEditMode(false);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.tools_color_accent_50));
        CPDFEditConfig editConfig = pdfView.getCPdfReaderView()
                .getEditManager()
                .getEditConfigBuilder()
                .setScreenshotRectColor(Color.TRANSPARENT)
                .setScreenshotBorderColor(ContextCompat.getColor(getContext(), R.color.tools_dark_color_accent))
                .setScreenshotBorderDash(new float[]{8.0F, 4F})
                .setFormPreviewPaint(paint)
                .setRotateAnnotationDrawableRes(R.drawable.tools_rotate)
                .build();
        pdfView.getCPdfReaderView().getEditManager().updateEditConfig(editConfig);

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
            imageResultLauncher.launch(RequestType.PHOTO_ALBUM,
                    result -> pdfView.getCPdfReaderView().addEditImage(result));
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

    protected void showToolbarMenuDialog(View anchorView) {
        //Show the PDF settings dialog fragment
        menuWindow = new CPopupMenuWindow(getContext());
        if (cpdfConfiguration != null && cpdfConfiguration.toolbarConfig != null) {
            List<ToolbarConfig.MenuAction> menuActions = cpdfConfiguration.toolbarConfig.availableMenus;
            if (menuActions == null || menuActions.size() == 0) {
                return;
            }
            anchorView.setSelected(true);
            for (ToolbarConfig.MenuAction menuAction : menuActions) {
                switch (menuAction) {
                    case ViewSettings:
                        menuWindow.addItem(R.drawable.tools_ic_preview_settings, R.string.tools_view_setting, v1 -> showDisplaySettings(pdfView));
                        break;
                    case DocumentEditor:
                        menuWindow.addItem(R.drawable.tools_page_edit, R.string.tools_page_edit_toolbar_title, v1 -> showPageEdit(true));
                        break;
                    case Security:
                        menuWindow.addItem(R.drawable.tools_ic_add_security, R.string.tools_security, v1 -> {
                            showSecurityDialog();
                        });
                        break;
                    case Watermark:
                        menuWindow.addItem(R.drawable.tools_ic_add_watermark, R.string.tools_watermark, v1 -> {
                            showAddWatermarkDialog();
                        });
                        break;
                    case DocumentInfo:
                        menuWindow.addItem(R.drawable.tools_ic_document_info, R.string.tools_document_info, v1 -> showDocumentInfo(pdfView));
                        break;
                    case Save:
                        menuWindow.addItem(R.drawable.tools_ic_menu_save, R.string.tools_save, v1 -> {
                            CLoadingDialog loadingDialog = CLoadingDialog.newInstance();
                            loadingDialog.show(getChildFragmentManager(), "saveLoadingDialog");
                            pdfView.savePDF((filePath, pdfUri) -> {
                                if (loadingDialog != null) {
                                    loadingDialog.dismiss();
                                }
                                CToastUtil.showLongToast(getContext(), R.string.tools_save_success);
                            }, e -> {
                                if (loadingDialog != null) {
                                    loadingDialog.dismiss();
                                }
                            });
                        });
                        break;
                    case Share:
                        menuWindow.addItem(R.drawable.tools_ic_share, R.string.tools_share, v1 -> {
                            editToolBar.resetStatus();
                            sharePDF(pdfView);
                        });
                        break;
                    case OpenDocument:
                        menuWindow.addItem(R.drawable.tools_ic_new_file, R.string.tools_open_document, v1 -> {
                            selectDocument();
                        });
                        break;
                    case Flattened:
                        menuWindow.addItem(R.drawable.tools_ic_flattened, R.string.tools_flattened, v1 -> {
                            showFlattenedDialog();
                        });
                        break;
                    case Snip:
                        menuWindow.addItem(R.drawable.tools_ic_snap, R.string.tools_snap, v -> {
                            enterSnipMode();
                        });
                        break;
                    default:
                        break;
                }
            }
        }
        menuWindow.setOnDismissListener(() -> anchorView.setSelected(false));
        menuWindow.showAsDropDown(anchorView);
    }

    protected void flattenedPdf() {
        CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(
                Environment.getExternalStorageDirectory().getAbsolutePath(),
                getContext().getString(R.string.tools_select_folder),
                getContext().getString(R.string.tools_save_to_this_directory)
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
                    } catch (Exception e) {
                        e.printStackTrace();
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
    protected void verifyDocumentSignStatus() {
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        if (CertificateDigitalDatas.hasDigitalSignature(document)) {
            CThreadPoolUtils.getInstance().executeIO(() -> {
                CPDFDocumentSignInfo status = CertificateDigitalDatas.verifyDocumentSignStatus(document);
                CThreadPoolUtils.getInstance().executeMain(() -> {
                    if (status != null) {
                        signStatusView.setStatus(status);
                        screenManager.fillScreenManager.bindTopToolView(signStatusView);
                        screenManager.constraintShow(signStatusView);
                    }
                });
            });
        } else {
            if (signStatusView.getVisibility() == View.VISIBLE) {
                screenManager.fillScreenManager.removeAndHideToolView(signStatusView);
                screenManager.constraintHide(signStatusView);
            }
        }
    }

    protected void selectDocument() {
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
            // save pdf document
            pdfView.savePDF((filePath, pdfUri) -> {
                alertDialog.dismiss();
                selectDocumentLauncher.launch(null);
            }, e -> {
                alertDialog.dismiss();
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
        documentEncryptionDialog.setEncryptionResultListener((isRemoveSecurity, result, filePath, passowrd) -> {
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
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean pageSameWidth = cpdfConfiguration.readerViewConfig.pageSameWidth;
        CPDFReaderView readerView = pdfView.getCPdfReaderView();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            pdfSearchToolBarView.showSearchReplaceContextMenu();
            if (pageSameWidth && !readerView.isContinueMode()) {
                readerView.setPageSameWidth(false);
                readerView.invalidateAllChildren();
            }
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            pdfSearchToolBarView.showSearchReplaceContextMenu();
            if (!readerView.isContinueMode()) {
                readerView.setPageSameWidth(pageSameWidth);
                readerView.invalidateAllChildren();
            }
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

    public void showSearch() {
        if (pdfView.getCPdfReaderView().getEditManager().isEditMode()) {
            curEditMode = pdfView.getCPdfReaderView().getLoadType();
        } else {
            curEditMode = CPDFEditPage.LoadNone;
        }
        pdfView.exitEditMode();
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

        tabs.add(outlineTab);
        tabs.add(bookmarkTab);
        tabs.add(annotationTab);
        if (pdfToolBar.getMode() == CPreviewMode.Annotation) {
            annotationTab.setDefaultSelect(true);
        }

        CPDFBotaDialogFragment dialogFragment = CPDFBotaDialogFragment.newInstance();
        dialogFragment.initWithPDFView(pdfView);
        dialogFragment.setBotaDialogTabs(tabs);
        dialogFragment.setDismissListener(this::restoreEdit);
        dialogFragment.show(getChildFragmentManager(), "annotationList");
    }

    public void showPageEdit(boolean enterEditMode) {
        showPageEdit(pdfView, enterEditMode, this::restoreEdit);
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
        showAddWatermarkDialog(cpdfConfiguration.globalConfig.watermark.saveAsNewFile);
    }

    public void showAddWatermarkDialog(boolean saveAsNewFile) {
        CWatermarkEditDialog watermarkEditDialog = CWatermarkEditDialog.newInstance();
        watermarkEditDialog.setDocument(pdfView.getCPdfReaderView().getPDFDocument());
        watermarkEditDialog.setSaveFileExtraFontSubset(pdfView.isSaveFileExtraFontSubset());
        watermarkEditDialog.setPageIndex(pdfView.currentPageIndex);
        watermarkEditDialog.setSaveAsNewFile(saveAsNewFile);
        watermarkEditDialog.setCompleteListener((saveAsNewFile1, pdfFile) -> {
            watermarkEditDialog.dismiss();
            pdfView.getCPdfReaderView().reloadPages();
            if (TextUtils.isEmpty(pdfFile)) {
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
                        CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
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
            CViewUtils.hideKeyboard(getActivity().getWindow().getDecorView());
            Log.e("ComPDFKit", "CPDFDocumentFragment:onDestroy() document close()");
            if (pdfView.getCPdfReaderView().getPDFDocument() != null) {
                pdfView.getCPdfReaderView().getPDFDocument().close();
            }
            pdfView.getCPdfReaderView().getContextMenuShowListener().dismissContextMenu();
            if (menuWindow != null) {
                menuWindow.dismiss();
            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    public void setInitListener(CPDFDocumentFragmentInitListener initListener) {
        this.initListener = initListener;
    }

    public void setFillScreenChangeListener(CFillScreenChangeListener fillScreenChangeListener) {
        this.fillScreenChangeListener = fillScreenChangeListener;
    }

    public interface CPDFDocumentFragmentInitListener {
        void compile(CPDFViewCtrl pdfView);
    }

    public interface CFillScreenChangeListener {
        void fillScreenChange(boolean fillScreen);
    }
}
