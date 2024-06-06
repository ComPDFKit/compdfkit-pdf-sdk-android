/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config;


import androidx.annotation.NonNull;

import com.compdfkit.tools.common.pdf.config.annot.AnnotationsAttributes;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * [CPDFDocumentActivity] and [CPDFDocumentFragment] config,
 * Used to set enabled annotation functions, annotation tools,
 * and default annotation properties during initialization.
 *
 * @see CPDFConfiguration
 */
public class AnnotationsConfig implements Serializable {

    public AnnotationsConfig() {

    }

    /**
     * A collection of annotation functions enabled by the annotation list.
     * Annotation types not in the collection will be hidden.
     *
     * @see com.compdfkit.tools.annotation.pdfannotationbar.CAnnotationToolbar
     */
    public List<CAnnotationType> availableTypes = new ArrayList<>();

    /**
     * A collection of tools available in the annotation toolbar,
     * including settings, redo, and undo functionalities.
     * If the collection is empty, the UI for the tool section will be hidden.
     *
     * @see com.compdfkit.tools.annotation.pdfannotationbar.CAnnotationToolbar
     */
    public List<AnnotationTools> availableTools = new ArrayList<>();

    /**
     * Default annotation attributes set when opening CPDFDocumentActivity or CPDFDocumentFragment.
     * These attributes will be used for configuring annotations when they are added.
     */
    public AnnotationsAttributes initAttribute = new AnnotationsAttributes();

    @NonNull
    @Override
    public String toString() {
        return "[annotationsConfig: availableTypes:" + availableTypes.toString() +
                ", availableTools:" + availableTools.toString() +
                "initAttribute: " + initAttribute.toString() +
                " ]";
    }

    public enum AnnotationTools implements Serializable {

        Setting,

        Undo,

        Redo;

        public static AnnotationTools fromString(String str) {
            try {
                String firstLetter = str.substring(0, 1).toUpperCase();
                String result = firstLetter + str.substring(1);
                return AnnotationTools.valueOf(result);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
