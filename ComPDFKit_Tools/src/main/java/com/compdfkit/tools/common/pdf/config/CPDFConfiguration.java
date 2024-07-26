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

import java.io.Serializable;


public class CPDFConfiguration implements Serializable {

    public ModeConfig modeConfig = new ModeConfig();

    public ToolbarConfig toolbarConfig = new ToolbarConfig();

    public AnnotationsConfig annotationsConfig = new AnnotationsConfig();

    public ContentEditorConfig contentEditorConfig = new ContentEditorConfig();

    public FormsConfig formsConfig = new FormsConfig();

    public ReaderViewConfig readerViewConfig = new ReaderViewConfig();

    public GlobalConfig globalConfig = new GlobalConfig();

    @NonNull
    @Override
    public String toString() {
        return modeConfig.toString() + "\n" + readerViewConfig.toString() + "\n" + annotationsConfig.toString();
    }

    public static class Builder {

        CPDFConfiguration configuration = new CPDFConfiguration();

        public Builder setModeConfig(ModeConfig modeConfig) {
            configuration.modeConfig = modeConfig;
            return this;
        }

        public Builder setToolbarConfig(ToolbarConfig toolbarConfig) {
            configuration.toolbarConfig = toolbarConfig;
            return this;
        }

        public Builder setReaderViewConfig(ReaderViewConfig readerViewConfig) {
            configuration.readerViewConfig = readerViewConfig;
            return this;
        }

        public Builder setAnnotationsConfig(AnnotationsConfig annotationsConfig){
            configuration.annotationsConfig = annotationsConfig;
            return this;
        }

        public Builder setContentEditorConfig(ContentEditorConfig contentEditorConfig){
            configuration.contentEditorConfig = contentEditorConfig;
            return this;
        }

        public Builder setFormsConfig(FormsConfig formsConfig){
            configuration.formsConfig = formsConfig;
            return this;
        }

        public CPDFConfiguration create() {
            return configuration;
        }
    }
}
