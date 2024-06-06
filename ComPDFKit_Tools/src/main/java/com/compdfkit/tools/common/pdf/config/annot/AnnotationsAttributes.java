/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.annot;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Various annotation attribute classes,
 * used to set default annotation attributes when opening [CPDFDocumentActivity] or [CPDFDocumentFragment]
 *
 */
public class AnnotationsAttributes implements Serializable {

    public AnnotAttr note = new AnnotAttr();

    public AnnotAttr highlight = new AnnotAttr();

    public AnnotAttr underline = new AnnotAttr();

    public AnnotAttr squiggly = new AnnotAttr();

    public AnnotAttr strikeout = new AnnotAttr();

    public AnnotInkAttr ink = new AnnotInkAttr();

    public AnnotShapeAttr square = new AnnotShapeAttr();

    public AnnotShapeAttr circle = new AnnotShapeAttr();

    public AnnotShapeAttr line = new AnnotShapeAttr();

    public AnnotShapeAttr arrow = new AnnotShapeAttr();

    public AnnotFreetextAttr freeText = new AnnotFreetextAttr();


    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}

