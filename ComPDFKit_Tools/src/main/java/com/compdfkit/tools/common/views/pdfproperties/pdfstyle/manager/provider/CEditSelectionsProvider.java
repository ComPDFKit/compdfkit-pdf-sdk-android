/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import com.compdfkit.core.edit.CPDFEditArea;
import com.compdfkit.core.edit.CPDFEditImageArea;
import com.compdfkit.core.edit.CPDFEditTextArea;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.ui.edit.CPDFEditSelections;
import com.compdfkit.ui.edit.CPDFEditTextSelections;
import com.compdfkit.ui.reader.CPDFPageView;

import java.io.File;
import java.util.LinkedHashSet;


public class CEditSelectionsProvider implements CStyleProvider {

    private CPDFEditSelections selections;

    private @Nullable CPDFPageView pageView;

    public CEditSelectionsProvider(CPDFEditSelections selections, @Nullable  CPDFPageView pageView) {
        this.selections = selections;
        this.pageView = pageView;
    }

    @Override
    public void updateStyle(CAnnotStyle annotStyle) {
        LinkedHashSet<CAnnotStyle> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(annotStyle);
        updateStyle(linkedHashSet);
    }

    @Override
    public void updateStyle(LinkedHashSet<CAnnotStyle> annotStyle) {
        for (CAnnotStyle style : annotStyle) {
            if (selections instanceof CPDFEditTextSelections && style.getType() == CStyleType.EDIT_TEXT) {
                CPDFEditTextSelections textSelections = (CPDFEditTextSelections) selections;
                CAnnotStyle.EditUpdatePropertyType type = style.getUpdatePropertyType();
                switch (type){
                    case FontSize:
                        textSelections.setFontSize(style.getFontSize());
                        break;
                    case TextColor:
                        textSelections.setColor(style.getTextColor());
                        break;
                    case TextColorOpacity:
                        textSelections.setTransparancy(style.getTextColorOpacity());
                        break;
                    case Italic:
                        textSelections.setItalic(style.isFontItalic());
                        break;
                    case Bold:
                        textSelections.setBold(style.isFontBold());
                        break;
                    case Alignment:
                        CAnnotStyle.Alignment align = style.getAlignment();
                        if (align == CAnnotStyle.Alignment.LEFT) {
                            textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft);
                        } else if (align == CAnnotStyle.Alignment.CENTER) {
                            textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignMiddle);
                        } else if (align == CAnnotStyle.Alignment.RIGHT) {
                            textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignRight);
                        }
                        if (pageView != null) {
                          pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.ATTR);
                          pageView.onUpdateUI(pageView.getPageNum());
                        }
                        break;

                    case FontType:
                        textSelections.setFontName(getAnnotStyleFontName(style));
                        textSelections.setBold(style.isFontBold());
                        textSelections.setItalic(style.isFontItalic());
                        break;

                    case UnderLine:
                        if (style.isEditTextUnderLine()){
                            textSelections.addUnderline();
                        }else {
                            textSelections.removeUnderline();
                        }
                        break;
                    case StrikeThrough:
                        if (style.isEditTextStrikeThrough()){
                            textSelections.addStrikethrough();
                        }else {
                            textSelections.removeStrikethrough();
                        }
                        break;
                    default:
                        textSelections.setColor(style.getTextColor());
                        textSelections.setTransparancy(style.getTextColorOpacity());
                        textSelections.setBold(style.isFontBold());
                        textSelections.setItalic(style.isFontItalic());
                        textSelections.setFontSize(style.getFontSize());
                        CAnnotStyle.Alignment align1 = style.getAlignment();
                        if (align1 == CAnnotStyle.Alignment.LEFT) {
                            textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft);
                        } else if (align1 == CAnnotStyle.Alignment.CENTER) {
                            textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignMiddle);
                        } else if (align1 == CAnnotStyle.Alignment.RIGHT) {
                            textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignRight);
                        }
                        textSelections.setFontName(getAnnotStyleFontName(style));
                        if (style.isEditTextUnderLine()){
                            textSelections.addUnderline();
                        }else {
                            textSelections.removeUnderline();
                        }
                        if (style.isEditTextStrikeThrough()){
                            textSelections.addStrikethrough();
                        }else {
                            textSelections.removeStrikethrough();
                        }
                        break;
                }
            } else if (style.getType() == CStyleType.EDIT_IMAGE) {
                CAnnotStyle.EditUpdatePropertyType updatePropertyType = style.getUpdatePropertyType();
                switch (updatePropertyType) {
                    case Rotation:
                        if (pageView != null) {
                            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.ROTATE, style.getRotationAngle());
                        }
                        break;
                    case Mirror:
                        if (pageView != null) {
                            if (style.getMirror() != null) {
                                if (style.getMirror() == CAnnotStyle.Mirror.Horizontal) {
                                    pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.HORIZENTAL_MIRROR, null);
                                } else {
                                    pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.VERTICLE_MIRROR, null);
                                }
                            }
                        }
                        break;
                    case ReplaceImage:
                        if (pageView != null){
                            if (!TextUtils.isEmpty(style.getImagePath())) {
                                pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.REPLACE, style.getImagePath());
                                new File(style.getImagePath()).delete();
                            } else if (style.getImageUri() != null) {
                                pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.REPLACE, style.getImageUri());
                            }
                        }
                        break;
                    case Crop:
                        if (pageView != null) {
                            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.ENTER_CROP, null);
                        }
                        break;
                    case Export:
                        try {
                            if (pageView != null) {
                                Context context = pageView.getContext();
                                File file = CPDFStorageManager.createTempFile(context, System.currentTimeMillis() + ".png");
                                CLog.d("CPDFStorage", "CEditSelectionsProvider extract image before path=" + file.getAbsolutePath());
                                pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.EXTRACT_IMAGE, file.getAbsolutePath());
                                CLog.d("CPDFStorage", "CEditSelectionsProvider extract image after path=" + file.getAbsolutePath()
                                        + ", exists=" + file.exists()
                                        + ", size=" + (file.exists() ? file.length() : -1));
                                CUriUtil.MediaStoreSaveResult result = CUriUtil.publishFileToMediaStore(context,
                                        file,
                                        CPDFStorageManager.getImageRelativePath(),
                                        file.getName(),
                                        "image/png",
                                        true);
                                if (!result.isSuccess()) {
                                    CLog.e("CPDFStorage", "CEditSelectionsProvider export image failed path=" + file.getAbsolutePath()
                                            + ", exists=" + file.exists()
                                            + ", size=" + (file.exists() ? file.length() : -1)
                                            + ", error=" + result.getErrorMessage());
                                    CToastUtil.showLongToast(pageView.getContext(), R.string.tools_page_edit_extract_fail);
                                }
                            }
                        }catch (Exception e){
                            CToastUtil.showLongToast(pageView.getContext(), R.string.tools_page_edit_extract_fail);
                        }
                        break;
                    default:
                        if (pageView != null) {
                            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.TRANCPARENCY, (float)style.getOpacity());
                        }
                        break;
                }
            }
        }

    }

    @Override
    public CAnnotStyle getStyle(CStyleType type) {
        CAnnotStyle style = new CAnnotStyle(type);
        if (selections != null && selections instanceof CPDFEditTextSelections) {
            CPDFEditTextSelections textSelections = (CPDFEditTextSelections) selections;
            style.setTextColorOpacity((int) (textSelections.getTransparancy()));
            style.setFontBold(textSelections.isBold());
            style.setFontItalic(textSelections.isItalic());
            style.setOpacity(255);
            CPDFEditTextArea.PDFEditAlignType alignType = textSelections.getAlign();
            if (alignType == CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft) {
                style.setAlignment(CAnnotStyle.Alignment.LEFT);
            } else if (alignType == CPDFEditTextArea.PDFEditAlignType.PDFEditAlignMiddle) {
                style.setAlignment(CAnnotStyle.Alignment.CENTER);
            } else if (alignType == CPDFEditTextArea.PDFEditAlignType.PDFEditAlignRight) {
                style.setAlignment(CAnnotStyle.Alignment.RIGHT);
            }
            style.setFontColor(textSelections.getColor());
            int fontsize = (int) textSelections.getFontSize();
            style.setFontSize(fontsize > 0 ? fontsize : 25);
            updateAnnotStyleFont(style, textSelections.getFontName());
        } else if (style.getType() == CStyleType.EDIT_IMAGE) {
            if (pageView != null){
                CPDFEditArea editArea = pageView.getCurrentEditArea();
                if (editArea != null && editArea instanceof CPDFEditImageArea) {
                    CPDFEditImageArea editImageArea = (CPDFEditImageArea) editArea;
                    float transparency = editImageArea.getTransparency();
                    style.setOpacity((int) transparency);
                    style.setEditImageBitmap(editImageArea.getImage());
                } else {
                    style.setOpacity(255);
                }
            }
        }
        return style;
    }
}
