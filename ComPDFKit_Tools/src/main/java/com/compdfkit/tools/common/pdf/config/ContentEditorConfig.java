package com.compdfkit.tools.common.pdf.config;


import android.graphics.Color;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.edit.CPDFEditTextArea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentEditorConfig implements Serializable {

    public static ContentEditorConfig normal(){
        ContentEditorConfig contentEditorConfig = new ContentEditorConfig();
        contentEditorConfig.availableTypes = Arrays.asList(
                ContentEditorType.EditorText,
                ContentEditorType.EditorImage
        );
        contentEditorConfig.availableTools = Arrays.asList(
                AnnotationsConfig.AnnotationTools.Setting,
                AnnotationsConfig.AnnotationTools.Undo,
                AnnotationsConfig.AnnotationTools.Redo
        );
        return contentEditorConfig;
    }

    public List<ContentEditorType> availableTypes = new ArrayList<>();

    public List<AnnotationsConfig.AnnotationTools> availableTools = new ArrayList<>();

    public ContentEditorAttr initAttribute = new ContentEditorAttr();

    public enum ContentEditorType {

        EditorText,

        EditorImage;

        public static ContentEditorType fromString(String str) {
            try {
                String firstLetter = str.substring(0, 1).toUpperCase();
                String result = firstLetter + str.substring(1);
                return ContentEditorType.valueOf(result);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class ContentEditorAttr implements Serializable{
        public TextAttr text = new TextAttr();
    }

    public static class TextAttr implements Serializable{

        private String fontColor;

        private int fontSize = 20;

        private boolean isBold;

        private boolean isItalic;

        private CPDFTextAttribute.FontNameHelper.FontType typeface = CPDFTextAttribute.FontNameHelper.FontType.Helvetica;

        private int fontColorAlpha = 255;

        private CPDFEditTextArea.PDFEditAlignType alignment = CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft;

        public String getFontColorHex() {
            return fontColor;
        }

        public int getFontColor(){
            try{
                return Color.parseColor(fontColor);
            }catch (Exception e){
                return Color.BLACK;
            }
        }

        public void setFontColor(String fontColor) {
            this.fontColor = fontColor;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        public boolean isBold() {
            return isBold;
        }

        public void setBold(boolean bold) {
            isBold = bold;
        }

        public boolean isItalic() {
            return isItalic;
        }

        public void setItalic(boolean italic) {
            isItalic = italic;
        }

        public CPDFTextAttribute.FontNameHelper.FontType getTypeface() {
            return typeface;
        }

        public void setTypeface(CPDFTextAttribute.FontNameHelper.FontType typeface) {
            this.typeface = typeface;
        }

        public int getFontColorAlpha() {
            return fontColorAlpha;
        }

        public void setFontColorAlpha(int fontColorAlpha) {
            this.fontColorAlpha = fontColorAlpha;
        }

        public CPDFEditTextArea.PDFEditAlignType getAlignment() {
            return alignment;
        }

        public void setAlignment(CPDFEditTextArea.PDFEditAlignType alignment) {
            this.alignment = alignment;
        }


    }

}
