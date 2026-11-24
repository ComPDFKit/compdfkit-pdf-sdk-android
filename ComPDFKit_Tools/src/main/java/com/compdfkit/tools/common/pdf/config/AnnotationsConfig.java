/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
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

    public String annotationAuthor = "";


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
     * Controls which history source annotation Undo and Redo use while editing Ink.
     * The default preserves immediate Ink editing while allowing a fallback to document history.
     */
    public InkUndoRedoMode inkUndoRedoMode = InkUndoRedoMode.HYBRID;

    /**
     * Default annotation attributes set when opening CPDFDocumentActivity or CPDFDocumentFragment.
     * These attributes will be used for configuring annotations when they are added.
     */
    public AnnotationsAttributes initAttribute = new AnnotationsAttributes();

    public boolean autoShowSignPicker = true;

    public boolean autoShowStampPicker = true;

    public boolean autoShowLinkDialog = true;

    public boolean autoShowNoteEditDialog = true;

    public boolean autoShowPicPicker = true;

    /** @deprecated Use interceptAnnotationActions with Type.TEXT instead. */
    @Deprecated
    public transient boolean interceptNoteAction = false;

    /** @deprecated Use interceptAnnotationActions with Type.LINK instead. */
    @Deprecated
    public transient boolean interceptLinkAction = false;

    /** Null means unspecified until configuration binding; the effective default is false. */
    public Boolean interceptAllAnnotationActions;

    /** Null means unspecified; an explicitly empty list disables legacy flags. */
    public List<com.compdfkit.core.annotation.CPDFAnnotation.Type> interceptAnnotationActions;

    /** Resolve legacy inputs once, before using or exporting this configuration. */
    public void normalizeActionInterception() {
        boolean useLegacy = interceptAllAnnotationActions == null && interceptAnnotationActions == null;
        if (interceptAllAnnotationActions == null) interceptAllAnnotationActions = false;
        if (interceptAnnotationActions == null) interceptAnnotationActions = new ArrayList<>();
        if (useLegacy) {
            if (interceptNoteAction) interceptAnnotationActions.add(com.compdfkit.core.annotation.CPDFAnnotation.Type.TEXT);
            if (interceptLinkAction) interceptAnnotationActions.add(com.compdfkit.core.annotation.CPDFAnnotation.Type.LINK);
        }
    }

    private void writeObject(java.io.ObjectOutputStream output) throws java.io.IOException {
        normalizeActionInterception();
        output.defaultWriteObject();
    }

    @NonNull
    @Override
    public String toString() {
        return "[annotationsConfig: availableTypes:" + availableTypes.toString() +
            ", availableTools:" + availableTools.toString() +
            ", inkUndoRedoMode: " + inkUndoRedoMode +
            ", initAttribute: " + initAttribute.toString() +
            ", autoShowSignPicker: " + autoShowSignPicker +
            ", autoShowStampPicker: " + autoShowStampPicker +
            ", autoShowLinkDialog: " + autoShowLinkDialog +
            ", autoShowNoteEditDialog: " + autoShowNoteEditDialog +
            ", autoShowPicPicker: " + autoShowPicPicker +
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

    public enum InkUndoRedoMode implements Serializable {
        INK_ONLY,

        DOCUMENT_ONLY,

        HYBRID;

        public static InkUndoRedoMode fromString(String value) {
            if (value == null) {
                return null;
            }
            String normalized = value.replace("_", "").replace("-", "").toLowerCase();
            switch (normalized) {
                case "inkonly":
                    return INK_ONLY;
                case "documentonly":
                    return DOCUMENT_ONLY;
                case "hybrid":
                    return HYBRID;
                default:
                    return null;
            }
        }
    }
}
