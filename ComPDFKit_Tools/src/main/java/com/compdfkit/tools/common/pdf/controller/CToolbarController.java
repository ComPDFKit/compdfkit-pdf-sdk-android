/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import static android.view.View.VISIBLE;
import static com.compdfkit.tools.contenteditor.CEditToolbar.SELECT_AREA_IMAGE;
import static com.compdfkit.tools.contenteditor.CEditToolbar.SELECT_AREA_TEXT;

import android.Manifest;
import android.view.View;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.common.basic.fragment.CBasicPDFFragment;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.pdf.config.FormsConfig;
import com.compdfkit.tools.common.pdf.config.ModeConfig;
import com.compdfkit.tools.common.pdf.config.CPDFUIVisibilityMode;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts.RequestType;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultLauncher;
import com.compdfkit.tools.common.utils.annotation.CAnnotationCreationContext;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.compdfkit.tools.common.views.CPDFToolBarMenuHelper.ToolBarAction;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFIReaderViewCallback;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.signature.CSignatureToolBar;
import com.compdfkit.tools.signature.info.signlist.CPDFCertDigitalSignListDialog;
import com.compdfkit.ui.proxy.attach.IInkDrawCallback;
import com.compdfkit.ui.proxy.form.CPDFComboboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFListboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFPushbuttonWidgetImpl;
import com.compdfkit.ui.reader.CPDFAddAnnotCallback;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.List;

/**
 * Manages initialization of all toolbars (main, annotation, edit, form, signature),
 * reader view callbacks, and the toolbar overflow menu dialog.
 */
public class CToolbarController {

    private final CPDFDocumentController ctx;
    private final CPreviewModeController previewModeController;
    private final CSignStatusController signStatusController;
    private final CImageResultLauncher imageResultLauncher;

    public CToolbarController(CPDFDocumentController ctx,
                              CPreviewModeController previewModeController,
                              CSignStatusController signStatusController,
                              CImageResultLauncher imageResultLauncher) {
        this.ctx = ctx;
        this.previewModeController = previewModeController;
        this.signStatusController = signStatusController;
        this.imageResultLauncher = imageResultLauncher;
    }

    /**
     * Initialize all toolbars and reader view callbacks in the correct order.
     */
    public void initAll() {
        initPDFView();
        initToolBarView();
        initAnnotToolbar();
        initFormToolbar();
        initEditBar();
        initSignatureToolbar();
    }

    private void initPDFView() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        CBasicPDFFragment basic = ctx.getBasicFragment();

        basic.registerAnnotHelper(pdfView);
        basic.registerFormHelper(pdfView);

        pdfView.addReaderViewCallback(new CPDFIReaderViewCallback() {
            @Override
            public void onTapMainDocArea() {
                if (fragment.pdfSearchToolBarView.getVisibility() == VISIBLE) {
                    fragment.pdfSearchToolBarView.showSearchReplaceContextMenu();
                    return;
                }
                if (pdfView.getCPdfReaderView().getTouchMode() == CPDFReaderView.TouchMode.SCREENSHOT) {
                    return;
                }
                if (fragment.ivTouchBrowse.getVisibility() == VISIBLE && fragment.ivTouchBrowse.isSelected()) {
                    return;
                }
                if (fragment.annotationToolbar.toolListAdapter.getCurrentAnnotType() == CAnnotationType.INK) {
                    return;
                }
                CPDFUIVisibilityMode uiVisibilityMode = ctx.getConfiguration().modeConfig.uiVisibilityMode;
                if (uiVisibilityMode == CPDFUIVisibilityMode.AUTOMATIC) {
                    ctx.getScreenManager().fillScreenChange();
                    if (fragment.fillScreenChangeListener != null) {
                        fragment.fillScreenChangeListener.fillScreenChange(ctx.getScreenManager().isFillScreen);
                    }
                }
            }

            @Override
            public void onEndScroll() {
                super.onEndScroll();
                fragment.pdfSearchToolBarView.showSearchReplaceContextMenu();
                fragment.editToolBar.updateUndoRedo();
            }
        });

        pdfView.getCPdfReaderView().setPdfAddAnnotCallback((cpdfPageView, cpdfBaseAnnot) -> {
            CPDFAnnotation annotation = cpdfBaseAnnot.onGetAnnotation();
            if (annotation.getType() != CPDFAnnotation.Type.WIDGET) {
                annotation.setTitle(ctx.getConfiguration().annotationsConfig.annotationAuthor);
                annotation.updateAp();
            }
            if (fragment.addAnnotCallback != null) {
                fragment.addAnnotCallback.onAddAnnotation(cpdfPageView, cpdfBaseAnnot);
            }

            // Programmatic additions should still emit creation callbacks, but must not
            // open the post-creation editing dialogs reserved for toolbar-created forms.
            if (CAnnotationCreationContext.isProgrammaticCreation()) {
                return;
            }

            FormsConfig formsConfig = ctx.getConfiguration().formsConfig;
            if (cpdfBaseAnnot instanceof CPDFListboxWidgetImpl && formsConfig.showCreateListBoxOptionsDialog) {
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showFormListEditFragment(fragment.getChildFragmentManager(), cpdfBaseAnnot, cpdfPageView, false);
            } else if (cpdfBaseAnnot instanceof CPDFComboboxWidgetImpl && formsConfig.showCreateComboBoxOptionsDialog) {
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showFormComboBoxEditFragment(fragment.getChildFragmentManager(), cpdfBaseAnnot, cpdfPageView, true);
            } else if (cpdfBaseAnnot instanceof CPDFPushbuttonWidgetImpl && formsConfig.showCreatePushButtonOptionsDialog) {
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showPushButtonActionDialog(fragment.getChildFragmentManager(), pdfView.getCPdfReaderView(),
                        cpdfBaseAnnot, cpdfPageView);
            }
        });
    }

    private void initToolBarView() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        fragment.pdfToolBar.setPreviewModeChangeListener(previewModeController::setPreviewMode);
        ModeConfig modeConfig = ctx.getConfiguration().modeConfig;
        fragment.pdfToolBar.addModes(modeConfig.availableViewModes);
        fragment.pdfToolBar.selectMode(modeConfig.initialViewMode);
        fragment.pdfToolBar.setMenuItems(fragment, ctx.getConfiguration().toolbarConfig);
    }

    private void initAnnotToolbar() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        CBasicPDFFragment basic = ctx.getBasicFragment();

        fragment.annotationToolbar.initWithPDFView(pdfView);
        fragment.annotationToolbar.addAnnotationChangeListener(type -> {
            ctx.getScreenManager().changeWindowStatus(type);
            if (type == CAnnotationType.SOUND) {
                if (!basic.hasPermission(Manifest.permission.RECORD_AUDIO)) {
                    basic.permissionResultLauncher.launch(Manifest.permission.RECORD_AUDIO, hasRecordAudioPermission -> {
                        if (!hasRecordAudioPermission) {
                            pdfView.resetAnnotationType();
                            if (!CPermissionUtil.shouldShowRequestPermissionRationale(fragment.requireActivity(), Manifest.permission.RECORD_AUDIO)) {
                                basic.showPermissionsRequiredDialog();
                            }
                        }
                    });
                }
            }
            if (type == CAnnotationType.INK) {
                if (ctx.getConfiguration().toolbarConfig.showInkToggleButton) {
                    ctx.getScreenManager().constraintShow(fragment.cardTouchBrowse);
                }
            } else {
                ctx.getScreenManager().constraintHide(fragment.cardTouchBrowse);
                fragment.ivTouchBrowse.setSelected(false);
            }
        });
        fragment.ivTouchBrowse.setOnClickListener(view -> {
            fragment.ivTouchBrowse.setSelected(!fragment.ivTouchBrowse.isSelected());
            pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
            if (fragment.ivTouchBrowse.isSelected()) {
                pdfView.getCPdfReaderView().getInkDrawHelper().setMode(IInkDrawCallback.Mode.VIEW);
            } else {
                pdfView.getCPdfReaderView().getInkDrawHelper().setMode(IInkDrawCallback.Mode.DRAW);
            }
        });
    }

    private void initFormToolbar() {
        ctx.getFragment().formToolBar.initWithPDFView(ctx.getPdfView());
    }

    private void initEditBar() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return;
        }
        fragment.editToolBar.initWithPDFView(pdfView);

        fragment.editToolBar.setEditPropertyBtnClickListener((view) -> {
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
                styleDialogFragment.show(fragment.getChildFragmentManager(), "textPropertyDialogFragment");
                menuHelper.dismissContextMenu();
            }
        });

        pdfView.getCPdfReaderView().setSelectImageCallback(() -> {
            if (ctx.getConfiguration() != null && ctx.getConfiguration().readerViewConfig.enableCreateImagePickerDialog) {
                imageResultLauncher.launch(RequestType.PHOTO_ALBUM,
                        result -> pdfView.getCPdfReaderView().addEditImage(result));
            }
        });
    }

    private void initSignatureToolbar() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        CSignatureToolBar signatureToolBar = fragment.signatureToolBar;

        signStatusController.init();
        signatureToolBar.initWithPDFView(pdfView);
        signatureToolBar.getVerifySignButton().setOnClickListener(v -> signStatusController.verify());
    }

    /**
     * Show the toolbar overflow menu dialog anchored to the given view.
     */
   public void showMenuDialog(View anchorView) {
       CPDFDocumentFragment fragment = ctx.getFragment();
       if (ctx.getConfiguration() == null || ctx.getConfiguration().toolbarConfig == null) {
           return;
       }
       List<ToolBarAction> menuActions = ctx.getConfiguration().toolbarConfig.availableMenus;
       if (menuActions == null || menuActions.isEmpty()) {
           // Don't leave a stale, un-shown CPopupMenuWindow reference on the fragment.
           fragment.menuWindow = null;
           return;
       }
       // Construct the popup only once we know it will actually be shown;
       // a stray menuWindow that was new'd-then-skipped would leak an
       // un-displayed instance and confuse onDestroy cleanup.
       fragment.menuWindow = new CPopupMenuWindow(fragment.getContext());
       anchorView.setSelected(true);
       fragment.pdfToolBar.setMoreMenuActions(fragment, ctx.getConfiguration().toolbarConfig);
       fragment.menuWindow.setOnDismissListener(() -> anchorView.setSelected(false));
       fragment.menuWindow.showAsDropDown(anchorView);
   }
}
