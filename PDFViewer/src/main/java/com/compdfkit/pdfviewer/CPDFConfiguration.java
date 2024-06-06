/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer;

import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

import java.io.Serializable;
import java.util.List;


public class CPDFConfiguration implements Serializable {

    public ModeConfig modeConfig = new ModeConfig();

    public ToolbarConfig toolbarConfig = new ToolbarConfig();

    public ReaderViewConfig readerViewConfig = new ReaderViewConfig();

    public static class ModeConfig implements Serializable {

        public ModeConfig() {

        }

        public ModeConfig(CPreviewMode initialViewMode) {
            this.initialViewMode = initialViewMode;
        }

        public CPreviewMode initialViewMode = CPreviewMode.Viewer;

    }

    public static class ToolbarConfig implements Serializable {

        public ToolbarConfig() {

        }

        public enum ToolbarAction {
            Back,
            Thumbnail,
            Search,
            Bota,
            Menu;

            public static ToolbarAction fromString(String str) {
                try {
                    String firstLetter = str.substring(0, 1).toUpperCase();
                    String result = firstLetter + str.substring(1);

                    return ToolbarAction.valueOf(result);

                } catch (Exception e) {
                    return null;
                }
            }
        }

        public enum MenuAction {
            ViewSettings,
            DocumentEditor,
            Security,
            Watermark,
            DocumentInfo,
            Save,
            Share,
            OpenDocument;

            public static MenuAction fromString(String str) {
                try {
                    String firstLetter = str.substring(0, 1).toUpperCase();
                    String result = firstLetter + str.substring(1);
                    return MenuAction.valueOf(result);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        public List<ToolbarAction> androidAvailableActions;

        public List<MenuAction> availableMenus;

    }

    public static class ReaderViewConfig implements Serializable {

        public boolean linkHighlight;

        public boolean formFieldHighlight;

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

        public CPDFConfiguration create() {
            return configuration;
        }
    }
}
