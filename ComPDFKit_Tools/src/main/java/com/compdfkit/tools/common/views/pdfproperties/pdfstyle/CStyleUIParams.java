/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;


import android.content.Context;

import androidx.annotation.DrawableRes;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

import java.io.Serializable;

public class CStyleUIParams implements Serializable {

    public enum DialogStyleType{
        Dialog,

        Activity
    }

    public DialogStyleType dialogStyleType = DialogStyleType.Dialog;

    public boolean showToolbar;

    public boolean fillScreenHeight;

    public int theme = R.style.ComPDFKit_Theme_BottomSheetDialog_Light_Transparent;

    public float dimAmount = 0.2F;

    public @DrawableRes  int backgroundDrawable;

    public static CStyleUIParams defaultStyle(Context context, CStyleType styleType){
        CStyleUIParams params = new CStyleUIParams();
        params.showToolbar = true;
        params.dimAmount = 0.2F;
        params.backgroundDrawable = R.drawable.tools_annot_style_dialog_window_bg;
        params.dialogStyleType = DialogStyleType.Dialog;
        params.theme = CViewUtils.getThemeStyle(context, R.attr.compdfkit_BottomSheetDialog_Transparent_Theme);
        if (styleType == CStyleType.ANNOT_STAMP || styleType == CStyleType.ANNOT_SIGNATURE || styleType == CStyleType.FORM_SIGNATURE_FIELDS){
            params.fillScreenHeight = true;
            params.theme = CViewUtils.getThemeStyle(context, R.attr.compdfkit_BottomSheetDialog_Theme);
            params.dialogStyleType = CStyleUIParams.DialogStyleType.Activity;
            params.dimAmount = 0F;
        }
        if (styleType == CStyleType.ANNOT_PIC){
            params.showToolbar = false;
            params.theme = CViewUtils.getThemeStyle(context, R.attr.compdfkit_BottomSheetDialog_Theme);
        }
        return params;
    }

    private int getStyleTheme(Context context, int resId, int defaultResId){
        int themeId = CViewUtils.getThemeAttrResourceId(context.getTheme(), resId);
        if (themeId != 0){
            return themeId;
        }else {
            return defaultResId;
        }
    }
}
