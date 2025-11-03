/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.basic.fragment;


import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.compdfkit.core.annotation.CPDFLinkAnnotation;
import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.core.annotation.form.CPDFComboboxWidget;
import com.compdfkit.core.annotation.form.CPDFListboxWidget;
import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdflink.CLinkAnnotAttachHelper;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.CPDFtextAnnotAttachHelper;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.CPDFtextAnnotImpl;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.impl.CEditImageContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CEditPathContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CEditTextContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CScreenShotContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSearchReplaceContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSignatureContextMenuView;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.dialog.CLoadingDialog;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.docseditor.pdfpageedit.CPDFPageEditDialogFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfcombobox.CustomComboBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdflistbox.CustomListBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfsign.SignatureWidgetImpl;
import com.compdfkit.tools.viewer.contextmenu.CopyContextMenuView;
import com.compdfkit.tools.viewer.pdfdisplaysettings.CPDFDisplaySettingDialogFragment;
import com.compdfkit.tools.viewer.pdfinfo.CPDFDocumentInfoDialogFragment;

import java.io.File;

public class CBasicPDFFragment extends CPermissionFragment {

    protected CPDFConfiguration cpdfConfiguration;

    public int curEditMode = CPDFEditPage.LoadNone;

    private CPDFPageEditDialogFragment.COnEnterBackPressedListener pageEditDialogOnBackListener;


    protected void resetContextMenu(CPDFViewCtrl pdfView, CPreviewMode mode) {
        switch (mode) {
            case Viewer:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .setSelectContentMenu(new CopyContextMenuView())
                                .setScreenShotContextMenu(new CScreenShotContextMenuView())
                                .create(pdfView));
                break;
            case Annotation:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder().defaultHelper()
                                .create(pdfView));
                break;
            case Edit:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .setEditTextContentMenu(new CEditTextContextMenuView())
                                .setEditImageContentMenu(new CEditImageContextMenuView())
                                .setSearchReplaceContextMenu(new CSearchReplaceContextMenuView())
                                .setScreenShotContextMenu(new CScreenShotContextMenuView())
                                .setEditPathContentMenu(new CEditPathContextMenuView())
                                .create(pdfView));
                break;
            case Form:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .defaultFormHelper()
                                .create(pdfView));
                break;
            case Signature:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .setSignatureContextMenu(new CSignatureContextMenuView())
                                .setScreenShotContextMenu(new CScreenShotContextMenuView())
                                .create(pdfView));
                break;
            default:
                break;
        }
    }

    protected void registerAnnotHelper(CPDFViewCtrl pdfView) {
        pdfView.getCPdfReaderView().getAnnotImplRegistry().registAttachHelper(CPDFTextAnnotation.class, CPDFtextAnnotAttachHelper.class);
        pdfView.getCPdfReaderView().getAnnotImplRegistry().registImpl(CPDFTextAnnotation.class, CPDFtextAnnotImpl.class);
        pdfView.getCPdfReaderView().getAnnotImplRegistry().registAttachHelper(CPDFLinkAnnotation.class, CLinkAnnotAttachHelper.class);
    }

    protected void registerFormHelper(CPDFViewCtrl pdfView) {
        pdfView.getCPdfReaderView().getAnnotImplRegistry()
                //Register the CustomComboBoxWidgetImpl.class to implement a custom dropdown options popup.
                .registImpl(CPDFComboboxWidget.class, CustomComboBoxWidgetImpl.class)
                // Register the CustomListBoxWidgetImpl.class to implement a custom dropdown options popup.
                .registImpl(CPDFListboxWidget.class, CustomListBoxWidgetImpl.class)
                // Register the CustomSignatureWidgetImpl.class to implement a custom dropdown options popup.
                .registImpl(CPDFSignatureWidget.class, SignatureWidgetImpl.class);
    }

    public void showDisplaySettings(CPDFViewCtrl pdfView) {
        pdfView.exitEditMode();
        CPDFDisplaySettingDialogFragment displaySettingDialogFragment = CPDFDisplaySettingDialogFragment.newInstance();
        displaySettingDialogFragment.initWithPDFView(pdfView);
        displaySettingDialogFragment.show(getChildFragmentManager(), "displaySettingsDialog");
    }

    public void showDocumentInfo(CPDFViewCtrl pdfView) {
        pdfView.exitEditMode();
        CPDFDocumentInfoDialogFragment infoDialogFragment = CPDFDocumentInfoDialogFragment.newInstance();
        infoDialogFragment.initWithPDFView(pdfView);
        infoDialogFragment.show(getChildFragmentManager(), "documentInfoDialogFragment");
    }

    protected void sharePDF(CPDFViewCtrl pdfView) {
        curEditMode = pdfView.getCPdfReaderView().getLoadType();
        pdfView.savePDF((filePath, pdfUri) -> {
            restoreEdit(pdfView,true);
            if (pdfUri != null && pdfUri.toString().startsWith("content://")){
                CFileUtils.shareFile(getContext(), getString(R.string.tools_share_to), "application/pdf", pdfUri);
                return;
            }
            String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!TextUtils.isEmpty(filePath)) {
                if (filePath.startsWith(getContext().getCacheDir().getAbsolutePath()) ||
                filePath.startsWith(getContext().getFilesDir().getAbsolutePath())){
                    CFileUtils.shareFile(getContext(), getString(R.string.tools_share_to), "application/pdf", new File(filePath));
                } else if (filePath.startsWith(externalStoragePath)) {
                    Uri uri = CFileUtils.getUriBySystem(getContext(), new File(filePath));
                    CFileUtils.shareFile(getContext(), getString(R.string.tools_share_to), "application/pdf", uri);
                }
            }
        }, e -> {
            restoreEdit(pdfView,true);
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            if (document == null){
                return;
            }
            Uri pdfUri = document.getUri();
            String filePath = document.getAbsolutePath();
            if (pdfUri != null && pdfUri.toString().startsWith("content://")){
                CFileUtils.shareFile(getContext(), getString(R.string.tools_share_to), "application/pdf", pdfUri);
                return;
            }
            String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!TextUtils.isEmpty(filePath)) {
                if (filePath.startsWith(getContext().getCacheDir().getAbsolutePath()) ||
                        filePath.startsWith(getContext().getFilesDir().getAbsolutePath())){
                    CFileUtils.shareFile(getContext(), getString(R.string.tools_share_to), "application/pdf", new File(filePath));
                }else if (filePath.startsWith(externalStoragePath)) {
                    Uri uri = CFileUtils.getUriBySystem(getContext(), new File(filePath));
                    CFileUtils.shareFile(getContext(), getString(R.string.tools_share_to), "application/pdf", uri);
                }
            }
        });
    }


    public void setPageEditDialogOnBackListener(
            CPDFPageEditDialogFragment.COnEnterBackPressedListener pageEditDialogOnBackListener) {
        this.pageEditDialogOnBackListener = pageEditDialogOnBackListener;
    }

    protected void showPageEdit(CPDFViewCtrl pdfView, boolean enterEdit, boolean enableEditMode, CPDFPageEditDialogFragment.OnBackListener backListener) {
        curEditMode = pdfView.getCPdfReaderView().getLoadType();
        pdfView.exitEditMode();
        pdfView.getCPdfReaderView().getContextMenuShowListener().dismissContextMenu();
        CPDFPageEditDialogFragment pageEditDialogFragment = CPDFPageEditDialogFragment.newInstance();
        pageEditDialogFragment.initWithPDFView(pdfView);
        pageEditDialogFragment.setEnterEdit(enterEdit);
        pageEditDialogFragment.setEnableEditMode(enableEditMode);
        pageEditDialogFragment.setOnBackListener(backListener);
        pageEditDialogFragment.setOnEnterBackPressedListener(()->{
            if (pageEditDialogOnBackListener != null) {
                pageEditDialogOnBackListener.onEnterBackPressed();
            }
        });
        pageEditDialogFragment.show(getChildFragmentManager(), "pageEditDialogFragment");
    }

    protected void restoreEdit(CPDFViewCtrl pdfView, boolean isEditMode) {
        if (curEditMode > CPDFEditPage.LoadNone && isEditMode) {
            CPDFEditManager editManager = pdfView.getCPdfReaderView().getEditManager();
            if (!editManager.isEditMode()) {
                editManager.beginEdit(curEditMode);
            }
        }
    }

    private CLoadingDialog loadingDialog;

    protected void showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dismiss();
        }
        loadingDialog = CLoadingDialog.newInstance();
        loadingDialog.show(getChildFragmentManager(), "loadingDialog");
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }


}
