/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;

import androidx.annotation.StringRes;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdffreetext.CFreeTextStyleFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdflnk.CInkStyleFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfmarkup.CMarkupStyleFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.CNoteStyleFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfpic.CImageStyleFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfshape.CLineArrowTypeListFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfshape.CShapeStyleFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfsignature.CSignatureStyleFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.CStampStyleFragment;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.contenteditor.pdfproperties.CEditImagePropertiesFragment;
import com.compdfkit.tools.contenteditor.pdfproperties.CEditTextProperFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfcheckbox.CheckBoxStyleFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfcheckbox.CheckBoxStyleListFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfcombobox.ComboBoxStyleFragment;
import com.compdfkit.tools.forms.pdfproperties.pdflistbox.CListBoxStyleFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfpushbutton.CPushButtonStyleFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfradiobutton.CRadioButtonStyleFragment;
import com.compdfkit.tools.forms.pdfproperties.pdftextfield.CTextFieldStyleFragment;
import com.compdfkit.tools.security.watermark.pdfproperties.CWatermarkImageStyleFragment;
import com.compdfkit.tools.security.watermark.pdfproperties.CWatermarkTextStyleFragment;


public class CStyleFragmentDatas {

    public static CPropertiesFragmentBean getStyleFragment(CAnnotStyle style) {
        switch (style.getType()) {
            case ANNOT_STRIKEOUT:
            case ANNOT_HIGHLIGHT:
            case ANNOT_UNDERLINE:
            case ANNOT_SQUIGGLY:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CMarkupStyleFragment.class);
            case ANNOT_TEXT:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CNoteStyleFragment.class);
            case ANNOT_INK:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CInkStyleFragment.class);
            case ANNOT_SQUARE:
            case ANNOT_CIRCLE:
            case ANNOT_LINE:
            case ANNOT_ARROW:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CShapeStyleFragment.class);
            case ANNOT_FREETEXT:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CFreeTextStyleFragment.class);
            case ANNOT_SIGNATURE:
            case FORM_SIGNATURE_FIELDS:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CSignatureStyleFragment.class);
            case ANNOT_STAMP:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CStampStyleFragment.class);
            case ANNOT_PIC:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CImageStyleFragment.class);
            case EDIT_TEXT:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CEditTextProperFragment.class);
            case EDIT_IMAGE:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CEditImagePropertiesFragment.class);
            case FORM_TEXT_FIELD:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CTextFieldStyleFragment.class);
            case FORM_CHECK_BOX:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CheckBoxStyleFragment.class);
            case FORM_RADIO_BUTTON:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CRadioButtonStyleFragment.class);
            case FORM_LIST_BOX:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CListBoxStyleFragment.class);
            case FORM_COMBO_BOX:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), ComboBoxStyleFragment.class);
            case FORM_PUSH_BUTTON:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CPushButtonStyleFragment.class);
            case WATERMARK_TEXT:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CWatermarkTextStyleFragment.class);
            case WATERMARK_IMAGE:
                return new CPropertiesFragmentBean(style.getAnnotTypeTitleResId(), CWatermarkImageStyleFragment.class);
            default:
                break;
        }
        return null;
    }

    public static CPropertiesFragmentBean colorPicker(){
        return new CPropertiesFragmentBean(R.string.tools_colors, CColorPickerFragment.class);
    }

    public static CPropertiesFragmentBean lineType(@StringRes int titleResId){
        return new CPropertiesFragmentBean(titleResId, CLineArrowTypeListFragment.class);
    }

    public static CPropertiesFragmentBean checkBoxStyle(){
        return new CPropertiesFragmentBean(R.string.tools_button_style, CheckBoxStyleListFragment.class);
    }

    public static <T extends CBasicPropertiesFragment> CBasicPropertiesFragment createPropertiesFragment(Class<T> tClass, CAnnotStyle style) {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
