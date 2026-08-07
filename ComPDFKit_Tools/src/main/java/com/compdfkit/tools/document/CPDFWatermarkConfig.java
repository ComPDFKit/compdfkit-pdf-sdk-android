/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 */
package com.compdfkit.tools.document;

import android.graphics.Bitmap;

import com.compdfkit.core.watermark.CPDFWatermark;

/** Configures a text or image watermark. Null properties are left unchanged during an update. */
public class CPDFWatermarkConfig {

    private CPDFWatermark.Type type;
    private String text;
    private String fontName;
    private Float fontSize;
    private Integer textColor;
    private Bitmap image;
    private Integer imageWidth;
    private Integer imageHeight;
    private Float scale;
    private Float rotation;
    private Float opacity;
    private CPDFWatermark.Vertalign vertalign;
    private CPDFWatermark.Horizalign horizalign;
    private Float vertOffset;
    private Float horizOffset;
    private String pages;
    private Boolean front;
    private Boolean fullScreen;
    private Float horizontalSpacing;
    private Float verticalSpacing;

    public CPDFWatermark.Type getType() { return type; }
    public CPDFWatermarkConfig setType(CPDFWatermark.Type type) { this.type = type; return this; }
    public String getText() { return text; }
    public CPDFWatermarkConfig setText(String text) { this.text = text; return this; }
    public String getFontName() { return fontName; }
    public CPDFWatermarkConfig setFontName(String fontName) { this.fontName = fontName; return this; }
    public Float getFontSize() { return fontSize; }
    public CPDFWatermarkConfig setFontSize(float fontSize) { this.fontSize = fontSize; return this; }
    public Integer getTextColor() { return textColor; }
    public CPDFWatermarkConfig setTextColor(int textColor) { this.textColor = textColor; return this; }
    public Bitmap getImage() { return image; }

    public CPDFWatermarkConfig setImage(Bitmap image, int width, int height) {
        if (image == null || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("A bitmap and positive image dimensions are required.");
        }
        this.image = image;
        this.imageWidth = width;
        this.imageHeight = height;
        return this;
    }

    public Integer getImageWidth() { return imageWidth; }
    public Integer getImageHeight() { return imageHeight; }
    public Float getScale() { return scale; }
    public CPDFWatermarkConfig setScale(float scale) { this.scale = scale; return this; }
    public Float getRotation() { return rotation; }
    public CPDFWatermarkConfig setRotation(float rotation) { this.rotation = rotation; return this; }
    public Float getOpacity() { return opacity; }
    public CPDFWatermarkConfig setOpacity(float opacity) { this.opacity = opacity; return this; }
    public CPDFWatermark.Vertalign getVertalign() { return vertalign; }
    public CPDFWatermarkConfig setVertalign(CPDFWatermark.Vertalign vertalign) { this.vertalign = vertalign; return this; }
    public CPDFWatermark.Horizalign getHorizalign() { return horizalign; }
    public CPDFWatermarkConfig setHorizalign(CPDFWatermark.Horizalign horizalign) { this.horizalign = horizalign; return this; }
    public Float getVertOffset() { return vertOffset; }
    public CPDFWatermarkConfig setVertOffset(float vertOffset) { this.vertOffset = vertOffset; return this; }
    public Float getHorizOffset() { return horizOffset; }
    public CPDFWatermarkConfig setHorizOffset(float horizOffset) { this.horizOffset = horizOffset; return this; }
    public String getPages() { return pages; }
    public CPDFWatermarkConfig setPages(String pages) { this.pages = pages; return this; }
    public Boolean getFront() { return front; }
    public CPDFWatermarkConfig setFront(boolean front) { this.front = front; return this; }
    public Boolean getFullScreen() { return fullScreen; }
    public CPDFWatermarkConfig setFullScreen(boolean fullScreen) { this.fullScreen = fullScreen; return this; }
    public Float getHorizontalSpacing() { return horizontalSpacing; }
    public CPDFWatermarkConfig setHorizontalSpacing(float horizontalSpacing) { this.horizontalSpacing = horizontalSpacing; return this; }
    public Float getVerticalSpacing() { return verticalSpacing; }
    public CPDFWatermarkConfig setVerticalSpacing(float verticalSpacing) { this.verticalSpacing = verticalSpacing; return this; }
}
