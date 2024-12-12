/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.preview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.CPDFAnnotation.CPDFBorderEffectType;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;


public class CStylePreviewView extends FrameLayout {

    private CStyleType annotationType = CStyleType.ANNOT_TEXT;

    private CBasicAnnotPreviewView previewView;

    public CStylePreviewView(@NonNull Context context) {
        this(context, null);
    }

    public CStylePreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CStylePreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.tools_style_preview_bg);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CStylePreviewView);
        if (typedArray != null) {
            int annotEnumId = typedArray.getInt(R.styleable.CStylePreviewView_tools_annot_type, 0);
            setAnnotType(getCAnnotTypeById(annotEnumId));
            typedArray.recycle();
        }
    }

    public void setAnnotType(CStyleType type) {
        this.annotationType = type;
        updatePreview();
    }

    public void setColor(@ColorInt int color) {
        if (previewView != null) {
            previewView.setColor(color);
        }
    }

    public void setColorOpacity(int colorOpacity) {
        if (previewView != null) {
            previewView.setOpacity(colorOpacity);
        }
    }

    public void setBorderWidth(int width) {
        if (previewView != null) {
            previewView.setBorderWidth(width);
        }
    }

    public void setBorderColor(@ColorInt int color) {
        if (previewView != null) {
            previewView.setBorderColor(color);
        }
    }

    public void setBorderColorOpacity(int opacity) {
        if (previewView != null) {
            previewView.setBorderColorOpacity(opacity);
        }
    }

    public void setDashedSpaceWidth(int dashedSpace) {
        if (previewView != null) {
            previewView.setDashedGsp(dashedSpace);
        }
    }

    public void setStartLineType(CPDFLineAnnotation.LineType lineType){
        if (previewView != null) {
            previewView.setStartLineType(lineType);
        }
    }

    public void setTailLineType(CPDFLineAnnotation.LineType lineType){
        if (previewView != null) {
            previewView.setTailLineType(lineType);
        }
    }

    public void setTextColor(int textColor){
        if (previewView != null) {
            previewView.setTextColor(textColor);
        }
    }

    public void setTextColorOpacity(int textColorOpacity){
        if (previewView != null) {
            previewView.setTextColorOpacity(textColorOpacity);
        }
    }

    public void setTextAlignment(CAnnotStyle.Alignment alignment){
        if (previewView != null) {
            previewView.setTextAlignment(alignment);
        }
    }

    public void setFontSize(int fontSize){
        if (previewView != null) {
            previewView.setFontSize(fontSize);
        }
    }

    public void setMirror(CAnnotStyle.Mirror mirror){
        if (previewView != null) {
            previewView.setMirror(mirror);
        }
    }

    public void setRotationAngle(float angle){
        if (previewView != null) {
            previewView.setRotationAngle(angle);
        }
    }

    public void setFontPsName(String fontPsName) {
        if (previewView != null) {
            previewView.setFontPsName(fontPsName);
        }
    }

    public void setImage(Bitmap bitmap){
        if (previewView != null){
            previewView.setImage(bitmap);
        }
    }

    public void setBorderEffectType(CPDFBorderEffectType effectType){
        if (previewView != null){
            previewView.setBorderEffectType(effectType);
        }
    }

    private void updatePreview() {
        removeAllViews();
        previewView = null;
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        switch (annotationType) {
            case ANNOT_TEXT:
                previewView = new CAnnotNotePreviewView(getContext());
                break;
            case ANNOT_HIGHLIGHT:
            case ANNOT_UNDERLINE:
            case ANNOT_SQUIGGLY:
            case ANNOT_STRIKEOUT:
                CAnnotMarkupPreviewView view = new CAnnotMarkupPreviewView(getContext());
                view.setMarkupType(annotationType);
                previewView = view;
                break;
            case ANNOT_INK:
                previewView = new CAnnotInkPreviewView(getContext());
                int inkSize = CDimensUtils.dp2px(getContext(), 80);
                layoutParams = new LayoutParams(inkSize, inkSize);
                layoutParams.gravity = Gravity.CENTER;
                break;
            case ANNOT_SQUARE:
                previewView = new CAnnotShapePreviewView(getContext());
                previewView.setShapeType(CAnnotShapePreviewView.CShapeView.ShapeType.SQUARE);
                layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CDimensUtils.dp2px(getContext(), 50));
                layoutParams.gravity = Gravity.CENTER;
                break;
            case ANNOT_CIRCLE:
                previewView = new CAnnotShapePreviewView(getContext());
                previewView.setShapeType(CAnnotShapePreviewView.CShapeView.ShapeType.CIRCLE);
                layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CDimensUtils.dp2px(getContext(), 60));
                layoutParams.gravity = Gravity.CENTER;
                break;
            case ANNOT_LINE:
                previewView = new CAnnotLineTypePreviewView(getContext());
                int lineSize = CDimensUtils.dp2px(getContext(), 40);
                layoutParams = new LayoutParams(lineSize, lineSize);
                layoutParams.gravity = Gravity.CENTER;
                previewView.setLayoutParams(layoutParams);
                previewView.setStartLineType(CPDFLineAnnotation.LineType.LINETYPE_NONE);
                previewView.setTailLineType(CPDFLineAnnotation.LineType.LINETYPE_NONE);
                break;
            case ANNOT_ARROW:
                previewView = new CAnnotLineTypePreviewView(getContext());
                int size = CDimensUtils.dp2px(getContext(), 40);
                layoutParams = new LayoutParams(size, size);
                layoutParams.gravity = Gravity.CENTER;
                previewView.setLayoutParams(layoutParams);
                previewView.setStartLineType(CPDFLineAnnotation.LineType.LINETYPE_ARROW);
                previewView.setTailLineType(CPDFLineAnnotation.LineType.LINETYPE_ARROW);
                break;
            case ANNOT_FREETEXT:
                previewView = new CAnnotFreeTextPreviewView(getContext());
                layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.CENTER;
                previewView.setLayoutParams(layoutParams);
                break;
            case EDIT_IMAGE:
                previewView = new CEditImagePreviewView(getContext());
                layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.CENTER;
                int margin = CDimensUtils.dp2px(getContext(), 4);
                layoutParams.setMargins(margin, margin,margin,margin);
                previewView.setLayoutParams(layoutParams);
                break;
            default:
                break;
        }
        if (previewView != null) {
            addView(previewView, layoutParams);
        }
    }

    private CStyleType getCAnnotTypeById(int attrEnumId) {
        switch (attrEnumId) {
            case 0:
                return CStyleType.ANNOT_TEXT;
            case 1:
                return CStyleType.ANNOT_HIGHLIGHT;
            case 2:
                return CStyleType.ANNOT_UNDERLINE;
            case 3:
                return CStyleType.ANNOT_SQUIGGLY;
            case 4:
                return CStyleType.ANNOT_STRIKEOUT;
            case 5:
                return CStyleType.ANNOT_INK;
            case 6:
                return CStyleType.ANNOT_CIRCLE;
            case 7:
                return CStyleType.ANNOT_SQUARE;
            case 8:
                return CStyleType.ANNOT_ARROW;
            case 9:
                return CStyleType.ANNOT_LINE;
            case 10:
                return CStyleType.ANNOT_FREETEXT;
            case 11:
                return CStyleType.ANNOT_SIGNATURE;
            default:
                return CStyleType.UNKNOWN;
        }
    }
}
