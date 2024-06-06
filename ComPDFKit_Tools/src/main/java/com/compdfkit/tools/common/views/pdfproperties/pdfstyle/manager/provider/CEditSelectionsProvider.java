/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.edit.CPDFEditArea;
import com.compdfkit.core.edit.CPDFEditImageArea;
import com.compdfkit.core.edit.CPDFEditTextArea;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.ui.edit.CPDFEditSelections;
import com.compdfkit.ui.edit.CPDFEditTextSelections;
import com.compdfkit.ui.reader.CPDFPageView;

import java.io.File;
import java.util.LinkedHashSet;


public class CEditSelectionsProvider implements CStyleProvider {

    private CPDFEditSelections selections;

    private CPDFPageView pageView;

    public CEditSelectionsProvider(CPDFEditSelections selections, CPDFPageView pageView) {
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
                if (type == CAnnotStyle.EditUpdatePropertyType.FontSize) {
                    textSelections.setFontSize(style.getFontSize());
                } else if (type == CAnnotStyle.EditUpdatePropertyType.TextColor) {
                    textSelections.setColor(style.getTextColor());
                } else if (type == CAnnotStyle.EditUpdatePropertyType.TextColorOpacity) {
                    textSelections.setTransparancy(style.getTextColorOpacity());

                } else if (type == CAnnotStyle.EditUpdatePropertyType.Italic) {
                    textSelections.setItalic(style.isFontItalic());
                } else if (type == CAnnotStyle.EditUpdatePropertyType.Bold) {
                    textSelections.setBold(style.isFontBold());
                } else if (type == CAnnotStyle.EditUpdatePropertyType.Alignment) {
                    CAnnotStyle.Alignment align = style.getAlignment();
                    if (align == CAnnotStyle.Alignment.LEFT) {
                        textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft);
                    } else if (align == CAnnotStyle.Alignment.CENTER) {
                        textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignMiddle);
                    } else if (align == CAnnotStyle.Alignment.RIGHT) {
                        textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignRight);
                    }
                } else if (type == CAnnotStyle.EditUpdatePropertyType.FontType) {
                    CPDFTextAttribute.FontNameHelper.FontType font = style.getFontType();
                    if (font == CPDFTextAttribute.FontNameHelper.FontType.Courier) {
                        textSelections.setFontName(CPDFEditTextArea.FontCourier);
                    } else if (font == CPDFTextAttribute.FontNameHelper.FontType.Helvetica) {
                        textSelections.setFontName(CPDFEditTextArea.FontHelvtics);
                    } else if (font == CPDFTextAttribute.FontNameHelper.FontType.Times_Roman) {
                        textSelections.setFontName(CPDFEditTextArea.FontTimesRoman);
                    } else {
                        textSelections.setFontName(style.getExternFontName());
                    }
                } else {
                    textSelections.setColor(style.getTextColor());
                    textSelections.setTransparancy(style.getTextColorOpacity());
                    textSelections.setBold(style.isFontBold());
                    textSelections.setItalic(style.isFontItalic());
                    textSelections.setFontSize(style.getFontSize());
                    CAnnotStyle.Alignment align = style.getAlignment();
                    if (align == CAnnotStyle.Alignment.LEFT) {
                        textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft);
                    } else if (align == CAnnotStyle.Alignment.CENTER) {
                        textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignMiddle);
                    } else if (align == CAnnotStyle.Alignment.RIGHT) {
                        textSelections.setAlign(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignRight);
                    }
                    CPDFTextAttribute.FontNameHelper.FontType font = style.getFontType();
                    if (font == CPDFTextAttribute.FontNameHelper.FontType.Courier) {
                        textSelections.setFontName(CPDFEditTextArea.FontCourier);
                    } else if (font == CPDFTextAttribute.FontNameHelper.FontType.Helvetica) {
                        textSelections.setFontName(CPDFEditTextArea.FontHelvtics);
                    } else if (font == CPDFTextAttribute.FontNameHelper.FontType.Times_Roman) {
                        textSelections.setFontName(CPDFEditTextArea.FontTimesRoman);
                    } else {
                        textSelections.setFontName(style.getExternFontName());
                    }
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
                        if (!TextUtils.isEmpty(style.getImagePath())) {
                            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.REPLACE, style.getImagePath());
                            new File(style.getImagePath()).delete();
                        } else if (style.getImageUri() != null) {
                            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.REPLACE, style.getImageUri());
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
                                String sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                                File file = new File(sdPath, "ComPDFKit" + File.separator + System.currentTimeMillis() + ".png");
                                pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.EXTRACT_IMAGE, file.getAbsolutePath());
                                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                        file.getAbsolutePath(), file.getName(), "description");
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
            String fontname = textSelections.getFontName();
            if (fontname != null) {
                if (fontname.contains("Courier")) {
                    style.setFontType(CPDFTextAttribute.FontNameHelper.FontType.Courier);
                } else if (fontname.contains("Helvetica")) {
                    style.setFontType(CPDFTextAttribute.FontNameHelper.FontType.Helvetica);
                } else if (fontname.contains("Times")) {
                    style.setFontType(CPDFTextAttribute.FontNameHelper.FontType.Times_Roman);
                } else {
                    style.setExternFontName(fontname);
                }
            } else {
                style.setFontType(CPDFTextAttribute.FontNameHelper.FontType.Unknown);
            }
        } else if (style.getType() == CStyleType.EDIT_IMAGE) {
            CPDFEditArea editArea = pageView.getCurrentEditArea();
            if (editArea != null && editArea instanceof CPDFEditImageArea) {
                float transparency = ((CPDFEditImageArea)editArea).getTransparency();
                style.setOpacity((int) transparency);
            } else {
                style.setOpacity(255);
            }
        }
        return style;
    }
}
