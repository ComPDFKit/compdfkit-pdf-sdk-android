/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.bota.CPDFBotaConfig;
import com.compdfkit.tools.common.utils.animation.CFillScreenManager;
import com.compdfkit.tools.common.views.pdfbota.CPDFBOTA;
import com.compdfkit.tools.common.views.pdfbota.CPDFBotaDialogFragment;
import com.compdfkit.tools.common.views.pdfbota.CPDFBotaFragmentTabs;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.docseditor.pdfpageedit.CPDFPageEditDialogFragment;
import com.compdfkit.tools.viewer.pdfdisplaysettings.CPDFDisplaySettingDialogFragment;
import com.compdfkit.tools.viewer.pdfinfo.CPDFDocumentInfoDialogFragment;

import com.compdfkit.ui.contextmenu.IContextMenuShowListener;

import java.util.ArrayList;

/**
 * Manages BOTA (Bookmarks, Outline, Thumbnails, Annotations), page edit,
 * display settings, and document info dialogs.
 */
public class CBOTAController {

    private final CPDFDocumentController ctx;
    private final CPreviewModeController previewModeController;

    public CBOTAController(CPDFDocumentController ctx, CPreviewModeController previewModeController) {
        this.ctx = ctx;
        this.previewModeController = previewModeController;
    }

    /**
     * Show the BOTA (annotation list / outline / bookmark) dialog.
     */
    public void showBOTA() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        pdfView.getCPdfReaderView().removeAllAnnotFocus();
        previewModeController.saveCurrentEditModeFromLoadType();
        pdfView.exitEditMode();

        ArrayList<CPDFBotaFragmentTabs> tabs = new ArrayList<>();
        CPDFBotaFragmentTabs annotationTab = new CPDFBotaFragmentTabs(CPDFBOTA.ANNOTATION, fragment.getString(R.string.tools_annotations));
        CPDFBotaFragmentTabs outlineTab = new CPDFBotaFragmentTabs(CPDFBOTA.OUTLINE, fragment.getString(R.string.tools_outlines));
        CPDFBotaFragmentTabs bookmarkTab = new CPDFBotaFragmentTabs(CPDFBOTA.BOOKMARKS, fragment.getString(R.string.tools_bookmarks));

        CPDFBotaConfig botaConfig = ctx.getConfiguration().globalConfig.bota;
        for (Integer tab : botaConfig.tabs) {
            if (tab == CPDFBOTA.OUTLINE) {
                tabs.add(outlineTab);
            } else if (tab == CPDFBOTA.BOOKMARKS) {
                tabs.add(bookmarkTab);
            } else if (tab == CPDFBOTA.ANNOTATION) {
                tabs.add(annotationTab);
            }
        }
        if (fragment.pdfToolBar.getMode() == CPreviewMode.Annotation) {
            annotationTab.setDefaultSelect(true);
        }

        CPDFBotaDialogFragment dialogFragment = CPDFBotaDialogFragment.newInstance();
        dialogFragment.initWithPDFView(pdfView);
        dialogFragment.setBotaDialogTabs(tabs);
        dialogFragment.setMenus(botaConfig.getMenus());
        dialogFragment.setDismissListener(previewModeController::restoreEdit);
        dialogFragment.show(fragment.getChildFragmentManager(), "annotationList");
    }

    /**
     * Show the page edit (thumbnail) dialog.
     */
    public void showPageEdit(boolean enterEditMode, boolean enableEditMode) {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        previewModeController.saveCurrentEditModeFromLoadType();
        pdfView.exitEditMode();
        IContextMenuShowListener contextMenuShowListener =
                pdfView.getCPdfReaderView().getContextMenuShowListener();
        if (contextMenuShowListener != null) {
            contextMenuShowListener.dismissContextMenu();
        }
        CPDFPageEditDialogFragment pageEditDialogFragment = CPDFPageEditDialogFragment.newInstance();
        pageEditDialogFragment.initWithPDFView(pdfView);
        pageEditDialogFragment.setEnterEdit(enterEditMode);
        pageEditDialogFragment.setEnableEditMode(enableEditMode);
        pageEditDialogFragment.setOnBackListener(() -> {
            previewModeController.restoreEdit();
            int pageCount = pdfView.getCPdfReaderView().getPageCount();
            CPDFConfiguration configuration = pdfView.getCPDFConfiguration();
            if (configuration != null) {
                boolean enableSliderBar = configuration.readerViewConfig.enableSliderBar;
                if (enableSliderBar) {
                    boolean show = pageCount > 1;
                    pdfView.enableSliderBar(show);
                    if (show && !ctx.getScreenManager().isFillScreen) {
                        ctx.getScreenManager().fillScreenManager.showFromRight(pdfView.getSlideBarView(),
                                CFillScreenManager.CONFIG_SHORT_ANIM_TIME);
                    }
                }
            }
        });
        pageEditDialogFragment.setOnEnterBackPressedListener(() -> {
            CPDFPageEditDialogFragment.COnEnterBackPressedListener listener = fragment.getPageEditDialogOnBackListener();
            if (listener != null) {
                listener.onEnterBackPressed();
            }
        });
        pageEditDialogFragment.show(fragment.getChildFragmentManager(), "pageEditDialogFragment");
    }

    /**
     * Show the display settings dialog.
     */
    public void showDisplaySettings(CPDFViewCtrl pdfView) {
        previewModeController.saveCurrentEditModeFromLoadType();
        pdfView.exitEditMode();
        CPDFDisplaySettingDialogFragment displaySettingDialogFragment = CPDFDisplaySettingDialogFragment.newInstance();
        displaySettingDialogFragment.initWithPDFView(pdfView);
        displaySettingDialogFragment.setDismissListener(() -> previewModeController.restoreEdit(pdfView, true));
        displaySettingDialogFragment.show(ctx.getFragment().getChildFragmentManager(), "displaySettingsDialog");
    }

    /**
     * Show the document info dialog.
     */
    public void showDocumentInfo(CPDFViewCtrl pdfView) {
        CPDFDocumentInfoDialogFragment infoDialogFragment = CPDFDocumentInfoDialogFragment.newInstance();
        infoDialogFragment.initWithPDFView(pdfView);
        infoDialogFragment.show(ctx.getFragment().getChildFragmentManager(), "documentInfoDialogFragment");
    }
}
