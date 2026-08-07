/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 */
package com.compdfkit.tools.document;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.watermark.CPDFWatermark;

/** Provides add, read, update, single-delete, and all-delete operations for document watermarks. */
public class CPDFWatermarkManager {

    public boolean add(CPDFDocument document, CPDFWatermarkConfig config) {
        requireDocument(document);
        if (config == null || config.getType() == null) {
            throw new IllegalArgumentException("Watermark type and config are required.");
        }
        CPDFWatermark watermark = document.createWatermark(config.getType());
        if (watermark == null || !watermark.isValid()) {
            return false;
        }
        try {
            return apply(watermark, config) && watermark.update();
        } finally {
            watermark.release();
        }
    }

    public CPDFWatermarkConfig get(CPDFDocument document, int index) {
        CPDFWatermark watermark = requireWatermark(document, index);
        try {
            CPDFWatermarkConfig config = new CPDFWatermarkConfig()
                    .setType(watermark.getType())
                    .setScale(watermark.getScale())
                    .setRotation(watermark.getRotation())
                    .setOpacity(watermark.getOpacity())
                    .setVertalign(watermark.getVertalign())
                    .setHorizalign(watermark.getHorizalign())
                    .setVertOffset(watermark.getVertOffset())
                    .setHorizOffset(watermark.getHorizOffset())
                    .setPages(watermark.getPages())
                    .setFront(watermark.isFront())
                    .setFullScreen(watermark.isFullScreen())
                    .setHorizontalSpacing(watermark.getHorizontalSpacing())
                    .setVerticalSpacing(watermark.getVerticalSpacing());
            if (watermark.getType() == CPDFWatermark.Type.WATERMARK_TYPE_TEXT) {
                config.setText(watermark.getText()).setFontName(watermark.getFontName());
                if (watermark.getFontSize() > 0) {
                    config.setFontSize(watermark.getFontSize());
                }
                config.setTextColor(watermark.getTextRGBColor());
            } else {
                android.graphics.Bitmap image = watermark.getImage();
                if (image != null) {
                    config.setImage(image, image.getWidth(), image.getHeight());
                }
            }
            return config;
        } finally {
            watermark.release();
        }
    }

    public boolean update(CPDFDocument document, int index, CPDFWatermarkConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Watermark config must not be null.");
        }
        CPDFWatermark watermark = requireWatermark(document, index);
        try {
            return apply(watermark, config) && watermark.update();
        } finally {
            watermark.release();
        }
    }

    public boolean delete(CPDFDocument document, int index) {
        CPDFWatermark watermark = requireWatermark(document, index);
        try {
            return watermark.clear();
        } finally {
            watermark.release();
        }
    }

    public boolean deleteAll(CPDFDocument document) {
        requireDocument(document);
        boolean success = true;
        int count = document.getWatermarkCount();
        for (int index = 0; index < count; index++) {
            CPDFWatermark watermark = document.getWatermark(index);
            if (watermark == null || !watermark.isValid()) {
                success = false;
                continue;
            }
            try {
                success &= watermark.clear();
            } finally {
                watermark.release();
            }
        }
        return success;
    }

    private boolean apply(CPDFWatermark watermark, CPDFWatermarkConfig config) {
        boolean success = true;
        if (config.getText() != null) success &= watermark.setText(config.getText());
        if (config.getFontName() != null) success &= watermark.setFontName(config.getFontName());
        if (config.getFontSize() != null) success &= watermark.setFontSize(config.getFontSize());
        if (config.getTextColor() != null) success &= watermark.setTextRGBColor(config.getTextColor());
        if (config.getImage() != null) success &= watermark.setImage(config.getImage(), config.getImageWidth(), config.getImageHeight());
        if (config.getScale() != null) success &= watermark.setScale(config.getScale());
        if (config.getRotation() != null) success &= watermark.setRotation(config.getRotation());
        if (config.getOpacity() != null) success &= watermark.setOpacity(config.getOpacity());
        if (config.getVertalign() != null) success &= watermark.setVertalign(config.getVertalign());
        if (config.getHorizalign() != null) success &= watermark.setHorizalign(config.getHorizalign());
        if (config.getVertOffset() != null) success &= watermark.setVertOffset(config.getVertOffset());
        if (config.getHorizOffset() != null) success &= watermark.setHorizOffset(config.getHorizOffset());
        if (config.getPages() != null) success &= watermark.setPages(config.getPages());
        if (config.getFront() != null) success &= watermark.setFront(config.getFront());
        if (config.getFullScreen() != null) success &= watermark.setFullScreen(config.getFullScreen());
        if (config.getHorizontalSpacing() != null) success &= watermark.setHorizontalSpacing(config.getHorizontalSpacing());
        if (config.getVerticalSpacing() != null) success &= watermark.setVerticalSpacing(config.getVerticalSpacing());
        return success;
    }

    private CPDFWatermark requireWatermark(CPDFDocument document, int index) {
        requireDocument(document);
        if (index < 0 || index >= document.getWatermarkCount()) {
            throw new IllegalArgumentException("Watermark index is out of range.");
        }
        CPDFWatermark watermark = document.getWatermark(index);
        if (watermark == null || !watermark.isValid()) {
            throw new IllegalStateException("Unable to access the watermark.");
        }
        return watermark;
    }

    private void requireDocument(CPDFDocument document) {
        if (document == null || !document.isValid()) {
            throw new IllegalArgumentException("A valid PDF document is required.");
        }
    }
}
