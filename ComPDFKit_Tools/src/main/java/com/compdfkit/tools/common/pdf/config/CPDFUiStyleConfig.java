package com.compdfkit.tools.common.pdf.config;

import android.graphics.Color;
import java.io.Serializable;

public class CPDFUiStyleConfig implements Serializable {

    public Icons icons;
    public String selectTextColor;
    public RectStyle displayPageRect;
    public ScreenshotRectStyle screenshot;
    public FormPreview formPreview;
    public BorderStyle defaultBorderStyle;
    public FocusBorderStyle focusBorderStyle;
    public BorderStyle cropImageStyle;

    public String bookmarkIcon;

    public static class Icons implements Serializable {
        public String selectTextLeftIcon;
        public String selectTextRightIcon;
        public String selectTextIcon;
        public String rotationAnnotationIcon;

        public Icons(String selectTextLeftIcon, String selectTextRightIcon, String selectTextIcon, String rotationAnnotationIcon) {
            this.selectTextLeftIcon = selectTextLeftIcon;
            this.selectTextRightIcon = selectTextRightIcon;
            this.selectTextIcon = selectTextIcon;
            this.rotationAnnotationIcon = rotationAnnotationIcon;
        }

    }

    public static class RectStyle implements Serializable {
        public String fillColor;
        public String borderColor;
        public int borderWidth;
        public float[] borderDashPattern;

        public RectStyle(String fillColor, String borderColor, int borderWidth, float[] borderDashPattern) {
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            this.borderDashPattern = borderDashPattern;
        }

        public int getFillColor(String defaultColor) {
            return parseColor(fillColor, defaultColor);
        }

        public int getBorderColor(String defaultColor){
            return parseColor(borderColor, defaultColor);
        }

        protected int parseColor(String value, String defaultColor){
            try{
                return Color.parseColor(value);
            } catch (Exception e) {
                return Color.parseColor(defaultColor);
            }
        }

    }

    public static class ScreenshotRectStyle extends RectStyle implements Serializable{

        public String outSideColor;

        public ScreenshotRectStyle(String outSideColor, String fillColor, String borderColor, int borderWidth,
            float[] borderDashPattern) {
            super(fillColor, borderColor, borderWidth, borderDashPattern);
            this.outSideColor = outSideColor;
        }

        public int getOutSideColor(String defaultColor){
            return parseColor(outSideColor, defaultColor);
        }
    }

    public static class BorderStyle implements Serializable {
        public String borderColor;

        public int borderWidth;
        public float[] borderDashPattern;

        public BorderStyle(String borderColor, int borderWidth, float[] borderDashPattern) {
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            this.borderDashPattern = borderDashPattern;
        }

        public int getBorderColor(String defaultColor){
            try{
                return Color.parseColor(borderColor);
            } catch (Exception e) {
                return Color.parseColor(defaultColor);
            }
        }

    }

    public static class FocusBorderStyle extends BorderStyle implements Serializable {
        public String nodeColor;

        public FocusBorderStyle(String borderColor, int borderWidth, float[] borderDashPattern, String nodeColor) {
            super(borderColor, borderWidth, borderDashPattern);
            this.nodeColor = nodeColor;
        }

        public int getNodeColor(String defaultColor){
            try{
                return Color.parseColor(nodeColor);
            } catch (Exception e) {
                return Color.parseColor(defaultColor);
            }
        }

    }

    public static class FormPreview implements Serializable {
        public String style;
        public int strokeWidth;
        public String color;

        public FormPreview(String style, int strokeWidth, String color) {
            this.style = style;
            this.strokeWidth = strokeWidth;
            this.color = color;
        }

        public int getColor(String defaultColor){
            try{
                return Color.parseColor(color);
            } catch (Exception e) {
                return Color.parseColor(defaultColor);
            }
        }

    }
}
