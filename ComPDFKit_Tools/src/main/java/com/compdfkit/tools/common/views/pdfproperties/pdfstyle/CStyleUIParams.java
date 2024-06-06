/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;


import androidx.annotation.DrawableRes;

import com.compdfkit.tools.R;

import java.io.Serializable;

public class CStyleUIParams implements Serializable {

    public enum DialogStyleType{
        Dialog,

        Activity
    }

    public DialogStyleType dialogStyleType = DialogStyleType.Dialog;

    public boolean showToolbar;

    public boolean fillScreenHeight;

    public int theme = R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle_TopCorners;

    public float dimAmount = 0.2F;

    public @DrawableRes  int backgroundDrawable;

    public static CStyleUIParams defaultStyle(CStyleType styleType){
        CStyleUIParams params = new CStyleUIParams();
        params.showToolbar = true;
        params.dimAmount = 0.2F;
        params.backgroundDrawable = R.drawable.tools_annot_style_dialog_window_bg;
        params.dialogStyleType = DialogStyleType.Dialog;
        if (styleType == CStyleType.ANNOT_STAMP || styleType == CStyleType.ANNOT_SIGNATURE || styleType == CStyleType.FORM_SIGNATURE_FIELDS){
            params.fillScreenHeight = true;
            params.theme = R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle;
            params.dialogStyleType = CStyleUIParams.DialogStyleType.Activity;
            params.dimAmount = 0F;
        }
        if (styleType == CStyleType.ANNOT_PIC){
            params.showToolbar = false;
            params.theme = R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle;
        }
        return params;
    }
}
