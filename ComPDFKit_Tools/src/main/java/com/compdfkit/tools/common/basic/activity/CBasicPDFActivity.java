/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.basic.activity;


import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFLinkAnnotation;
import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.core.annotation.form.CPDFComboboxWidget;
import com.compdfkit.core.annotation.form.CPDFListboxWidget;
import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdflink.CLinkAnnotAttachHelper;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.CPDFtextAnnotAttachHelper;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.CPDFtextAnnotImpl;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.impl.CEditImageContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CEditTextContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSearchReplaceContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSignatureContextMenuView;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.docseditor.pdfpageedit.CPDFPageEditDialogFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfcombobox.CustomComboBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdflistbox.CustomListBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfsign.CustomSignatureWidgetImpl;
import com.compdfkit.tools.viewer.contextmenu.CopyContextMenuView;
import com.compdfkit.tools.viewer.pdfdisplaysettings.CPDFDisplaySettingDialogFragment;
import com.compdfkit.tools.viewer.pdfinfo.CPDFDocumentInfoDialogFragment;

import java.io.File;

public class CBasicPDFActivity extends CPermissionActivity {

    protected int curEditMode = CPDFEditPage.LoadNone;

    protected void resetContextMenu(CPDFViewCtrl pdfView, CPreviewMode mode) {
        switch (mode) {
            case Viewer:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .setSelectContentMenu(new CopyContextMenuView())
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
                                .create(pdfView));
                break;
            default:
                break;
        }
    }

    protected void initAnnotationAttr(CPDFViewCtrl pdfView) {
        int defaultColor = ContextCompat.getColor(this, R.color.tools_annotation_markup_default_color);
        new CStyleManager.Builder()
                .setNote(defaultColor, 255)
                .setMarkup(CStyleType.ANNOT_HIGHLIGHT, defaultColor, 255)
                .setMarkup(CStyleType.ANNOT_UNDERLINE, defaultColor, 255)
                .setMarkup(CStyleType.ANNOT_SQUIGGLY, defaultColor, 255)
                .setMarkup(CStyleType.ANNOT_STRIKEOUT, defaultColor, 255)
                .setShape(CStyleType.ANNOT_SQUARE, defaultColor, 255, Color.TRANSPARENT, 0, 10, null)
                .setShape(CStyleType.ANNOT_CIRCLE, defaultColor, 255, Color.TRANSPARENT, 0, 5, null)
                .setShape(CStyleType.ANNOT_LINE, defaultColor, 255, defaultColor, 255, 5, null)
                .setShapeArrow(defaultColor, 255, defaultColor, 255, 5, CPDFLineAnnotation.LineType.LINETYPE_NONE, CPDFLineAnnotation.LineType.LINETYPE_ARROW,
                         new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid,5,new float[]{8.0F, 0F}))
                .setInkAttribute(defaultColor, 255, 10, 10)
                .setFreeText(defaultColor, 255, 50)
                .init(pdfView, true);
    }

    protected void initFormAttr(CPDFViewCtrl pdfView) {
        int bgColor = ContextCompat.getColor(this, R.color.tools_form_default_bg_color);
        new CStyleManager.Builder()
                .setTextField(Color.TRANSPARENT, bgColor, Color.BLACK, 25, 2, false)
                .setCheckBox(Color.BLACK, bgColor, Color.BLACK, 2, CPDFWidget.CheckStyle.CK_Check, false)
                .setRadioButton(Color.BLACK, bgColor, Color.BLACK, 2, CPDFWidget.CheckStyle.CK_Circle, false)
                .setListBox(Color.TRANSPARENT, bgColor, Color.BLACK, 20, 2)
                .setComboBox(Color.TRANSPARENT, bgColor, Color.BLACK, 20, 2)
                .setPushButton(Color.BLACK, Color.WHITE, Color.BLACK, 20, 2, "Push Button")
                .setFormSignature(Color.TRANSPARENT, bgColor, 0)
                .init(pdfView, true);
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
                .registImpl(CPDFSignatureWidget.class, CustomSignatureWidgetImpl.class);
    }

    protected void showDisplaySettings(CPDFViewCtrl pdfView) {
        pdfView.exitEditMode();
        CPDFDisplaySettingDialogFragment displaySettingDialogFragment = CPDFDisplaySettingDialogFragment.newInstance();
        displaySettingDialogFragment.initWithPDFView(pdfView);
        displaySettingDialogFragment.show(getSupportFragmentManager(), "displaySettingsDialog");
    }

    protected void showDocumentInfo(CPDFViewCtrl pdfView) {
        pdfView.exitEditMode();
        CPDFDocumentInfoDialogFragment infoDialogFragment = CPDFDocumentInfoDialogFragment.newInstance();
        infoDialogFragment.initWithPDFView(pdfView);
        infoDialogFragment.show(getSupportFragmentManager(), "documentInfoDialogFragment");
    }

    protected void sharePDF(CPDFViewCtrl pdfView) {
        pdfView.exitEditMode();
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        if (document == null){
            return;
        }
        boolean isExternalFile = !TextUtils.isEmpty(document.getAbsolutePath()) &&
                document.getAbsolutePath().startsWith("/storage/emulated/0");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && isExternalFile) {
            if (CPermissionUtil.checkManifestPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) && !Environment.isExternalStorageManager()) {
                CPermissionUtil.openManageAllFileAppSettings(this);
                return;
            }
        }
        pdfView.savePDF((filePath, pdfUri) -> {
            if (!TextUtils.isEmpty(filePath)) {
                CFileUtils.shareFile(this, getString(R.string.tools_share_to), "application/pdf", new File(filePath));
            } else {
                CFileUtils.shareFile(this, getString(R.string.tools_share_to), "application/pdf", pdfUri);
            }
        }, e -> {
            if (e instanceof CPDFDocumentException){
                if (!document.isCanWrite() && document.hasRepaired()){
                    pdfView.showWritePermissionsDialog(document);
                }
            }
        });
    }

    protected void showPageEdit(CPDFViewCtrl pdfView, boolean enterEdit, CPDFPageEditDialogFragment.OnBackLisener backListener) {
        curEditMode = pdfView.getCPdfReaderView().getLoadType();
        pdfView.exitEditMode();
        CPDFPageEditDialogFragment pageEditDialogFragment = CPDFPageEditDialogFragment.newInstance();
        pageEditDialogFragment.initWithPDFView(pdfView);
        pageEditDialogFragment.setEnterEdit(enterEdit);
        pageEditDialogFragment.setOnBackListener(backListener);
        pageEditDialogFragment.show(getSupportFragmentManager(), "pageEditDialogFragment");
    }
}
