/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu;

import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.impl.CCheckBoxContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CFreeTextContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CInkContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CLinkContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CListBoxContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CLongPressContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CMarkupContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CPushButtonContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CRadioButtonContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CScreenShotContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSearchReplaceContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSelectContentContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CShapeContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSignatureContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSoundContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CStampContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CTextFieldContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.ComboBoxContextMenuView;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuCheckBoxProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuComboBoxProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditImageProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditPathProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditTextProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuFormSignProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuFreeTextProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuInkProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuLinkProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuListBoxProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuLongPressProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuMarkupProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuRadioButtonProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuScreenShotProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSearchReplaceProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSelectContentProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSoundContentProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuStampProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuTextFieldProvider;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.security.encryption.CInputOwnerPwdDialog;
import com.compdfkit.tools.viewer.contextmenu.CopyContextMenuView;
import com.compdfkit.ui.contextmenu.CPDFContextMenuShowHelper;
import com.compdfkit.ui.edit.CPDFEditSelections;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.proxy.CPDFFreetextAnnotImpl;
import com.compdfkit.ui.proxy.CPDFInkAnnotImpl;
import com.compdfkit.ui.proxy.CPDFLinkAnnotImpl;
import com.compdfkit.ui.proxy.CPDFMarkupAnnotImpl;
import com.compdfkit.ui.proxy.CPDFSoundAnnotImpl;
import com.compdfkit.ui.proxy.CPDFStampAnnotImpl;
import com.compdfkit.ui.proxy.form.CPDFCheckboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFComboboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFListboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFPushbuttonWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFRadiobuttonWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFTextWidgetImpl;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;


public class CPDFContextMenuHelper extends CPDFContextMenuShowHelper {

    public ContextMenuProviderParams helperParams;

    private PopupWindow.OnDismissListener dismissListener;

    private String annotationAuthor;

    public CPDFContextMenuHelper(CPDFViewCtrl pdfView, ContextMenuProviderParams params) {
        super(pdfView.getCPdfReaderView());
        if (pdfView.getCPDFConfiguration() != null && pdfView.getCPDFConfiguration().annotationsConfig != null){
            annotationAuthor = pdfView.getCPDFConfiguration().annotationsConfig.annotationAuthor;
        }
        this.helperParams = params;
    }

    @Override
    public View getMarkupContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.markupProvider != null){
            return helperParams.markupProvider.createMarkupContentView(this, cpdfPageView, (CPDFMarkupAnnotImpl) cpdfBaseAnnot);
        }
        return super.getMarkupContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getSelectTextContentView(CPDFPageView cpdfPageView, LayoutInflater layoutInflater) {
        if (helperParams.selectContentProvider != null){
            return helperParams.selectContentProvider.createSelectTextContentView(this, cpdfPageView);
        }
        return super.getSelectTextContentView(cpdfPageView, layoutInflater);
    }

    @Override
    public View getSoundContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.soundContentProvider != null){
            return helperParams.soundContentProvider.createSoundContentView(this, cpdfPageView, (CPDFSoundAnnotImpl) cpdfBaseAnnot);
        }
        return super.getSoundContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getLongPressContentView(CPDFPageView cpdfPageView, PointF pointF, LayoutInflater layoutInflater) {
        if (helperParams.longPressProvider != null){
            return helperParams.longPressProvider.createLongPressContentView(this, cpdfPageView, pointF);
        }
        return super.getLongPressContentView(cpdfPageView, pointF, layoutInflater);
    }

    @Override
    public View getInkContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.inkProvider != null){
            return helperParams.inkProvider.createInkContentView(this, cpdfPageView, (CPDFInkAnnotImpl) cpdfBaseAnnot);
        }
        return super.getInkContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getShapeContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.shapeProvider != null){
            return helperParams.shapeProvider.createShapeContentView(this, cpdfPageView, cpdfBaseAnnot);
        }
        return super.getShapeContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getFreetextContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater, ContextMenuType contextMenuType) {
        if (helperParams.freeTextProvider != null){
            View view = helperParams.freeTextProvider.createFreeTextContentView(this, cpdfPageView, (CPDFFreetextAnnotImpl) cpdfBaseAnnot, contextMenuType);
            return view != null ? view : super.getFreetextContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater, contextMenuType);
        }
        return super.getFreetextContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater, contextMenuType);
    }

    @Override
    public View getEditTextAreaContentView(CPDFPageView cpdfPageView, LayoutInflater layoutInflater, CPDFEditSelections cpdfEditSelections) {
        this.cpdfEditSelections = cpdfEditSelections;
        if (helperParams.editTextProvider != null){
            return helperParams.editTextProvider.createEditTextAreaContentView(this, cpdfPageView, cpdfEditSelections);
        }
        return super.getEditTextAreaContentView(cpdfPageView, layoutInflater, cpdfEditSelections);
    }

    @Override
    public View getStampContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.stampProvider != null){
            return helperParams.stampProvider.createStampContentView(this, cpdfPageView, (CPDFStampAnnotImpl) cpdfBaseAnnot);
        }
        return super.getStampContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getLinkContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.linkProvider != null) {
            return helperParams.linkProvider.createLinkContentView(this, cpdfPageView, (CPDFLinkAnnotImpl) cpdfBaseAnnot);
        }
        return super.getLinkContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

        @Override
    public View getEditLongPressContentView(CPDFPageView pageView, PointF point, LayoutInflater layoutInflater) {
        if (helperParams.editTextProvider != null) {
            return helperParams.editTextProvider.createEditLongPressContentView(this, pageView, point);
        }
        return super.getEditLongPressContentView(pageView, point, layoutInflater);
    }

    @Override
    public  View getEditSelectTextContentView(CPDFPageView pageView, LayoutInflater layoutInflater, CPDFEditSelections selections) {
        cpdfEditSelections = selections;
        if (helperParams.editTextProvider != null) {
            return helperParams.editTextProvider.createEditSelectTextContentView(this, pageView, selections);
        }
        return super.getEditSelectTextContentView(pageView, layoutInflater, selections);
     }

    @Override
    public View getEditTextContentView(CPDFPageView pageView, LayoutInflater layoutInflater) {
        if (helperParams.editTextProvider != null) {
            return helperParams.editTextProvider.createEditTextContentView(this, pageView);
        }
        return super.getEditTextContentView(pageView, layoutInflater);
     }

    @Override
    public View getEditImageAreaContentView(final CPDFPageView pageView, LayoutInflater layoutInflater, RectF area) {
        this.pageView = pageView;
        cpdfEditSelections = null;
        if (helperParams.editImageProvider != null) {
            return helperParams.editImageProvider.createEditImageAreaContentView(this, pageView,area);
        }
        return super.getEditImageAreaContentView(pageView, layoutInflater, area);
    }

    @Override
    public View getEditPathAreaContentView(final CPDFPageView pageView, LayoutInflater layoutInflater) {
        this.pageView = pageView;
        if (helperParams.editPathProvider != null) {
            return helperParams.editPathProvider.createEditPathAreaContentView(this, pageView, null);
        }
        return super.getEditImageAreaContentView(pageView, layoutInflater, null);
    }

    @Override
    public View getCropImageAreaContentView(final CPDFPageView pageView, LayoutInflater layoutInflater) {
        this.pageView = pageView;
        cpdfEditSelections = null;
        if (helperParams.editImageProvider != null) {
            return helperParams.editImageProvider.createGetCropImageAreaContentView(this, pageView);
        }
        return super.getCropImageAreaContentView(pageView, layoutInflater);
    }

    @Override
    public View getListboxContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        CPDFListboxWidgetImpl widget = (CPDFListboxWidgetImpl) cpdfBaseAnnot;
        if (helperParams.listBoxProvider != null){
            return helperParams.listBoxProvider.createListBoxContentView(this, cpdfPageView, widget);
        }
        return super.getListboxContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getTextfieldContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.textFieldProvider != null){
            CPDFTextWidgetImpl textWidget = (CPDFTextWidgetImpl) cpdfBaseAnnot;
            return helperParams.textFieldProvider.createTextFieldContentView(this, cpdfPageView, textWidget);
        }
        return super.getTextfieldContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getCheckboxContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.checkBoxProvider != null){
            CPDFCheckboxWidgetImpl checkboxWidget = (CPDFCheckboxWidgetImpl) cpdfBaseAnnot;
            return helperParams.checkBoxProvider.createCheckBoxContentView(this, cpdfPageView, checkboxWidget);
        }
        return super.getCheckboxContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getRadiobuttonContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.radioButtonProvider != null){
            CPDFRadiobuttonWidgetImpl radiobuttonWidget = (CPDFRadiobuttonWidgetImpl) cpdfBaseAnnot;
            return helperParams.radioButtonProvider.createRadioButtonContentView(this, cpdfPageView, radiobuttonWidget);
        }
        return super.getRadiobuttonContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getComboboxContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.comboBoxProvider != null){
            return helperParams.comboBoxProvider.createComboBoxContentView(this, cpdfPageView, (CPDFComboboxWidgetImpl) cpdfBaseAnnot);
        }
        return super.getComboboxContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }


    @Override
    public View getSignatureContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.formSignatureProvider != null){
            return helperParams.formSignatureProvider.createFormSignContentView(this, cpdfPageView, cpdfBaseAnnot);
        }
        return super.getSignatureContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getPushbuttonContentView(CPDFPageView cpdfPageView, CPDFBaseAnnotImpl cpdfBaseAnnot, LayoutInflater layoutInflater) {
        if (helperParams.pushButtonProvider != null){
            CPDFPushbuttonWidgetImpl pushButtonWidget = (CPDFPushbuttonWidgetImpl) cpdfBaseAnnot;
            return helperParams.pushButtonProvider.createPushButtonContentView(this, cpdfPageView, pushButtonWidget);
        }
        return super.getPushbuttonContentView(cpdfPageView, cpdfBaseAnnot, layoutInflater);
    }

    @Override
    public View getSearchReplaceContentView(CPDFPageView cpdfPageView, LayoutInflater layoutInflater) {
        if (helperParams.searchReplaceProvider != null){
            return helperParams.searchReplaceProvider.createSearchReplaceContentView(cpdfPageView, layoutInflater, this);
        }
        return super.getSearchReplaceContentView(cpdfPageView, layoutInflater);
    }

    @Override
    public View getScreenShotContentView(CPDFPageView cpdfPageView, LayoutInflater layoutInflater, RectF rectF) {
        if (helperParams.screenShotProvider != null){
            return helperParams.screenShotProvider.getScreenShotContentView(this, cpdfPageView, layoutInflater, rectF);
        }
        return super.getScreenShotContentView(cpdfPageView, layoutInflater, rectF);
    }

    @Override
    public void dismissContextMenu() {
        if (popupWindow != null) {
            popupWindow.setOnDismissListener(()->{
                if (dismissListener != null) {
                    dismissListener.onDismiss();
                    dismissListener = null;
                }
            });
        }
        super.dismissContextMenu();
    }

    public String getAnnotationAuthor() {
        return annotationAuthor;
    }

    private int loadType = CPDFEditPage.LoadNone;

    public void showInputOwnerPasswordDialog(CInputOwnerPwdDialog.COwnerPasswordListener ownerPasswordListener){
        CPDFDocument document = getReaderView().getPDFDocument();
        Bundle bundle = new Bundle();
        bundle.putString(CInputOwnerPwdDialog.EXTRA_TITLE, getReaderView().getContext().getString(R.string.tools_enter_permission_password));
        CInputOwnerPwdDialog ownerPwdDialog = CInputOwnerPwdDialog.newInstance(bundle);
        ownerPwdDialog.setDocument(document);
        ownerPwdDialog.setConfirmClickListener(ownerPassword -> {
            ownerPwdDialog.dismiss();
            loadType = readerView.getLoadType();
            CPDFEditManager editManager = readerView.getEditManager();
            if (editManager.isEditMode()) {
                editManager.endEdit();
            }
            CPDFDocument.PDFDocumentError error = document.reload(ownerPassword);
            readerView.reloadPages();
            if (loadType > CPDFEditPage.LoadNone){
                readerView.getEditManager().beginEdit(loadType);
                loadType = CPDFEditPage.LoadNone;
            }
            if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess){
                if (ownerPasswordListener != null) {
                    ownerPasswordListener.result(ownerPassword);
                }
            }
        });
        ownerPwdDialog.setCancelClickListener(v -> {
            ownerPwdDialog.dismiss();
        });
        if (getReaderView().getContext() instanceof FragmentActivity) {
            ownerPwdDialog.show(((FragmentActivity) getReaderView().getContext()).getSupportFragmentManager(), "ownerPasswordDialog");
        }
    }


    public void setPopupWindowDismissListener(PopupWindow.OnDismissListener dismissListener){
        this.dismissListener = dismissListener;
    }

    public CPDFReaderView getReaderView(){
        return readerView;
    }

    public FragmentManager getFragmentManager(){
        if (context instanceof FragmentActivity){
            return ((FragmentActivity) context).getSupportFragmentManager();
        } else {
            return null;
        }
    }

    public boolean isAllowsCopying(){
        CPDFDocument document = getReaderView().getPDFDocument();
        return document.getPermissions() == CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsOwner ||
                document.getPermissionsInfo().isAllowsCopying();
    }

    public static class Builder{

        ContextMenuProviderParams params;

        public Builder(){
            params = new ContextMenuProviderParams();
        }

        public Builder setMarkupContentMenu(ContextMenuMarkupProvider markupProvider){
            params.markupProvider = markupProvider;
            return this;
        }

        public Builder setSelectContentMenu(ContextMenuSelectContentProvider selectContentProvider){
            params.selectContentProvider = selectContentProvider;
            return this;
        }

        public Builder setSoundContentMenu(ContextMenuSoundContentProvider soundContentProvider){
            this.params.soundContentProvider = soundContentProvider;
            return this;
        }

        public Builder setLongPressContentMenu(ContextMenuLongPressProvider longPressProvider){
            this.params.longPressProvider = longPressProvider;
            return this;
        }

        public Builder setInkContentMenu(ContextMenuInkProvider inkProvider){
            this.params.inkProvider = inkProvider;
            return this;
        }

        public Builder setFreeTextContentMenu(ContextMenuFreeTextProvider freeTextProvider){
            this.params.freeTextProvider = freeTextProvider;
            return this;
        }

        public Builder setStampContentMenu(ContextMenuStampProvider stampProvider){
            this.params.stampProvider = stampProvider;
            return this;
        }

        public Builder setLinkContentMenu(ContextMenuLinkProvider linkProvider){
            this.params.linkProvider = linkProvider;
            return this;
        }

        public Builder setEditTextContentMenu(ContextMenuEditTextProvider editTextProvider){
            this.params.editTextProvider = editTextProvider;
            return this;
        }

        public Builder setEditImageContentMenu(ContextMenuEditImageProvider editImageProvider){
            this.params.editImageProvider = editImageProvider;
            return this;
        }

        public Builder setEditPathContentMenu(ContextMenuEditPathProvider editPathProvider){
            this.params.editPathProvider = editPathProvider;
            return this;
        }

        public Builder setTextFieldsContentMenu(ContextMenuTextFieldProvider textFieldProvider){
            this.params.textFieldProvider = textFieldProvider;
            return this;
        }

        public Builder setCheckBoxContentMenu(ContextMenuCheckBoxProvider checkBoxProvider){
            this.params.checkBoxProvider = checkBoxProvider;
            return this;
        }

        public Builder setRadioButtonContextMenu(ContextMenuRadioButtonProvider radioButtonProvider){
            this.params.radioButtonProvider = radioButtonProvider;
            return this;
        }

        public Builder setListBoxContextMenu(ContextMenuListBoxProvider listBoxProvider){
            this.params.listBoxProvider = listBoxProvider;
            return this;
        }

        public Builder setComboBoxContextMenu(ContextMenuComboBoxProvider comboBoxProvider){
            this.params.comboBoxProvider = comboBoxProvider;
            return this;
        }

        public Builder setSignatureContextMenu(ContextMenuFormSignProvider signProvider){
            this.params.formSignatureProvider = signProvider;
            return this;
        }

        public Builder setSearchReplaceContextMenu(ContextMenuSearchReplaceProvider searchReplaceProvider){
            this.params.searchReplaceProvider = searchReplaceProvider;
            return this;
        }

        public Builder setScreenShotContextMenu(ContextMenuScreenShotProvider screenShotProvider){
            this.params.screenShotProvider = screenShotProvider;
            return this;
        }

        public CPDFContextMenuHelper create(CPDFViewCtrl pdfView){
            return new CPDFContextMenuHelper(pdfView, params);
        }

        public Builder defaultHelper(){
            params.markupProvider = new CMarkupContextMenuView();
            params.selectContentProvider = new CSelectContentContextMenuView();
            params.soundContentProvider = new CSoundContextMenuView();
            params.longPressProvider = new CLongPressContextMenuView();
            params.inkProvider = new CInkContextMenuView();
            params.shapeProvider = new CShapeContextMenuView();
            params.freeTextProvider = new CFreeTextContextMenuView();
            params.stampProvider = new CStampContextMenuView();
            params.linkProvider = new CLinkContextMenuView();
            params.searchReplaceProvider = new CSearchReplaceContextMenuView();
            params.screenShotProvider = new CScreenShotContextMenuView();
            return this;
        }

        public Builder defaultFormHelper(){
            params.textFieldProvider = new CTextFieldContextMenuView();
            params.checkBoxProvider = new CCheckBoxContextMenuView();
            params.radioButtonProvider = new CRadioButtonContextMenuView();
            params.listBoxProvider = new CListBoxContextMenuView();
            params.comboBoxProvider = new ComboBoxContextMenuView();
            params.formSignatureProvider = new CSignatureContextMenuView();
            params.pushButtonProvider = new CPushButtonContextMenuView();
            params.selectContentProvider = new CopyContextMenuView();
            params.screenShotProvider = new CScreenShotContextMenuView();
            return this;
        }
    }
}
