/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 */
package com.compdfkit.tools.document;

import android.graphics.RectF;

/**
 * Configures the six header/footer or Bates positions of a PDF document.
 * A {@code null} property is left unchanged when the config is applied.
 */
public class CPDFHeaderFooterConfig {

    public static final int POSITION_TOP_LEFT = 0;
    public static final int POSITION_TOP_CENTER = 1;
    public static final int POSITION_TOP_RIGHT = 2;
    public static final int POSITION_BOTTOM_LEFT = 3;
    public static final int POSITION_BOTTOM_CENTER = 4;
    public static final int POSITION_BOTTOM_RIGHT = 5;
    public static final int POSITION_COUNT = 6;

    private final String[] texts = new String[POSITION_COUNT];
    private final String[] fontNames = new String[POSITION_COUNT];
    private final Float[] fontSizes = new Float[POSITION_COUNT];
    private final Integer[] textColors = new Integer[POSITION_COUNT];

    private String pages;
    private RectF margin;
    private Integer pageOffset;
    private Integer rules;

    public CPDFHeaderFooterConfig setText(int position, String text) {
        validatePosition(position);
        texts[position] = text;
        return this;
    }

    public String getText(int position) {
        validatePosition(position);
        return texts[position];
    }

    public CPDFHeaderFooterConfig setFontName(int position, String fontName) {
        validatePosition(position);
        fontNames[position] = fontName;
        return this;
    }

    public String getFontName(int position) {
        validatePosition(position);
        return fontNames[position];
    }

    public CPDFHeaderFooterConfig setFontSize(int position, float fontSize) {
        validatePosition(position);
        if (fontSize <= 0) {
            throw new IllegalArgumentException("Font size must be greater than zero.");
        }
        fontSizes[position] = fontSize;
        return this;
    }

    public Float getFontSize(int position) {
        validatePosition(position);
        return fontSizes[position];
    }

    public CPDFHeaderFooterConfig setTextColor(int position, int textColor) {
        validatePosition(position);
        textColors[position] = textColor;
        return this;
    }

    public Integer getTextColor(int position) {
        validatePosition(position);
        return textColors[position];
    }

    public CPDFHeaderFooterConfig setPages(String pages) {
        this.pages = pages;
        return this;
    }

    public String getPages() {
        return pages;
    }

    public CPDFHeaderFooterConfig setMargin(RectF margin) {
        this.margin = margin == null ? null : new RectF(margin);
        return this;
    }

    public RectF getMargin() {
        return margin == null ? null : new RectF(margin);
    }

    public CPDFHeaderFooterConfig setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
        return this;
    }

    public Integer getPageOffset() {
        return pageOffset;
    }

    public CPDFHeaderFooterConfig setRules(int rules) {
        this.rules = rules;
        return this;
    }

    public Integer getRules() {
        return rules;
    }

    static void validatePosition(int position) {
        if (position < 0 || position >= POSITION_COUNT) {
            throw new IllegalArgumentException("Position must be between 0 and 5.");
        }
    }
}
