/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 */
package com.compdfkit.tools.document;

import android.graphics.RectF;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFHeaderFooter;

/** Provides add, update, delete, and read operations for document headers and footers. */
public class CPDFHeaderFooterManager {

    public boolean add(CPDFDocument document, CPDFHeaderFooterConfig config) {
        return apply(document, config);
    }

    public boolean update(CPDFDocument document, CPDFHeaderFooterConfig config) {
        return apply(document, config);
    }

    public boolean delete(CPDFDocument document) {
        CPDFHeaderFooter headerFooter = requireHeaderFooter(document);
        headerFooter.clear();
        return true;
    }

    public CPDFHeaderFooterConfig get(CPDFDocument document) {
        CPDFHeaderFooter headerFooter = requireHeaderFooter(document);
        CPDFHeaderFooterConfig config = new CPDFHeaderFooterConfig()
                .setPages(headerFooter.getPages())
                .setMargin(headerFooter.getMargin())
                .setPageOffset(headerFooter.getPageOffset())
                .setRules(headerFooter.getRules());
        for (int position = 0; position < CPDFHeaderFooterConfig.POSITION_COUNT; position++) {
            config.setText(position, headerFooter.getText(position))
                    .setFontName(position, headerFooter.getFontName(position));
            float fontSize = headerFooter.getFontSize(position);
            if (fontSize > 0) {
                config.setFontSize(position, fontSize);
            }
            config.setTextColor(position, headerFooter.getTextColor(position));
        }
        return config;
    }

    protected CPDFHeaderFooter requireHeaderFooter(CPDFDocument document) {
        if (document == null || !document.isValid()) {
            throw new IllegalArgumentException("A valid PDF document is required.");
        }
        CPDFHeaderFooter headerFooter = document.getHeaderFooter();
        if (headerFooter == null || !headerFooter.isValid()) {
            throw new IllegalStateException("Unable to access the document header/footer.");
        }
        return headerFooter;
    }

    private boolean apply(CPDFDocument document, CPDFHeaderFooterConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Header/footer config must not be null.");
        }
        CPDFHeaderFooter headerFooter = requireHeaderFooter(document);
        boolean success = true;
        for (int position = 0; position < CPDFHeaderFooterConfig.POSITION_COUNT; position++) {
            String text = config.getText(position);
            String fontName = config.getFontName(position);
            Float fontSize = config.getFontSize(position);
            Integer textColor = config.getTextColor(position);
            if (text != null) success &= headerFooter.setText(position, text);
            if (fontName != null) success &= headerFooter.setFontName(position, fontName);
            if (fontSize != null) success &= headerFooter.setFontSize(position, fontSize);
            if (textColor != null) success &= headerFooter.setTextColor(position, textColor);
        }
        if (config.getPages() != null) success &= headerFooter.setPages(config.getPages());
        RectF margin = config.getMargin();
        if (margin != null) success &= headerFooter.setMargin(margin.left, margin.top, margin.right, margin.bottom);
        if (config.getPageOffset() != null) success &= headerFooter.setPageOffset(config.getPageOffset());
        if (config.getRules() != null) success &= headerFooter.setRules(config.getRules());
        if (success) headerFooter.update();
        return success;
    }
}
