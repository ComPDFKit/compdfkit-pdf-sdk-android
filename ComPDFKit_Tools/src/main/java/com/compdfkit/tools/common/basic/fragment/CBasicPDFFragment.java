/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.basic.fragment;


import com.compdfkit.core.annotation.CPDFLinkAnnotation;
import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.core.annotation.form.CPDFCheckboxWidget;
import com.compdfkit.core.annotation.form.CPDFComboboxWidget;
import com.compdfkit.core.annotation.form.CPDFListboxWidget;
import com.compdfkit.core.annotation.form.CPDFPushbuttonWidget;
import com.compdfkit.core.annotation.form.CPDFRadiobuttonWidget;
import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.annotation.form.CPDFTextWidget;
import com.compdfkit.tools.annotation.pdfproperties.pdflink.CLinkAnnotAttachHelper;
import com.compdfkit.tools.annotation.pdfproperties.pdflink.CPDFCustomLinkAnnotImpl;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.CPDFtextAnnotAttachHelper;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.CPDFtextAnnotImpl;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.dialog.CLoadingDialog;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.docseditor.pdfpageedit.CPDFPageEditDialogFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfcheckbox.CustomCheckBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfcombobox.CustomComboBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdflistbox.CustomListBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfpushbutton.CPushButtonWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfradiobutton.CustomRadioButtonWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfsign.SignatureWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdftextfield.CustomTextWidgetImpl;

/**
 * Base fragment providing shared infrastructure for PDF fragments.
 *
 * The business methods that previously lived here (sharePDF, showDisplaySettings,
 * showDocumentInfo, showPageEdit, resetContextMenu, restoreEdit, curEditMode)
 * have been migrated to dedicated controllers under
 * {@code com.compdfkit.tools.common.pdf.controller}.
 *
 * What remains is pure infrastructure:
 * - {@link #cpdfConfiguration} shared configuration
 * - {@link #registerAnnotHelper(CPDFViewCtrl)} / {@link #registerFormHelper(CPDFViewCtrl)}
 * - Loading dialog management
 * - Page edit dialog back-press listener
 */
public class CBasicPDFFragment extends CPermissionFragment {

    protected CPDFConfiguration cpdfConfiguration;

    private CPDFPageEditDialogFragment.COnEnterBackPressedListener pageEditDialogOnBackListener;

    public void registerAnnotHelper(CPDFViewCtrl pdfView) {
        // Default proxies and attach helpers are installed by CPDFViewCtrl.
    }

    public void registerFormHelper(CPDFViewCtrl pdfView) {
    }

    public void setPageEditDialogOnBackListener(
            CPDFPageEditDialogFragment.COnEnterBackPressedListener pageEditDialogOnBackListener) {
        this.pageEditDialogOnBackListener = pageEditDialogOnBackListener;
    }

    public CPDFPageEditDialogFragment.COnEnterBackPressedListener getPageEditDialogOnBackListener() {
        return pageEditDialogOnBackListener;
    }

    private CLoadingDialog loadingDialog;

    public void showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dismiss();
        }
        loadingDialog = CLoadingDialog.newInstance();
        loadingDialog.show(getChildFragmentManager(), "loadingDialog");
    }

    public void showLoadingDialog(String title) {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dismiss();
        }
        loadingDialog = CLoadingDialog.newInstance(title);
        loadingDialog.show(getChildFragmentManager(), "loadingDialog");
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

}
