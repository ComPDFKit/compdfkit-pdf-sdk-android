/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfsign;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFImageScaleType;
import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.tools.common.utils.image.CBitmapUtil;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleUIParams;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.proxy.form.CPDFSignatureWidgetImpl;

/**
 * Form signature form, electronic signature click interaction
 */
public class CustomSignatureWidgetImpl extends CPDFSignatureWidgetImpl {

    @Override
    public void onSignatureWidgetFocused(CPDFSignatureWidget cpdfSignatureWidget) {
        CStyleManager styleManager = new CStyleManager(this, pageView);
        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(styleManager.getStyle(CStyleType.FORM_SIGNATURE_FIELDS));
        CStyleUIParams styleUiParams = CStyleUIParams.defaultStyle(pageView.getContext(), CStyleType.FORM_SIGNATURE_FIELDS);
        styleDialogFragment.setStyleUiConfig(styleUiParams);
        styleDialogFragment.setStyleDialogDismissListener(() -> {
            CAnnotStyle annotStyle = styleDialogFragment.getAnnotStyle();
            if (!TextUtils.isEmpty(annotStyle.getImagePath())) {
                try {
                    Bitmap bitmap = CBitmapUtil.decodeBitmap(annotStyle.getImagePath());
                    ((CPDFSignatureWidget) pdfAnnotation).updateApWithBitmap(bitmap, CPDFImageScaleType.SCALETYPE_fitCenter);
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (readerView.getContext() instanceof FragmentActivity) {
            styleDialogFragment.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "styleDialog");
        }
    }
}
