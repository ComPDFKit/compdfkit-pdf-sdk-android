/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.config;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


public class ToolbarConfig implements Serializable {

    public ToolbarConfig() {

    }

    public static ToolbarConfig normal(){
        ToolbarConfig toolbarConfig = new ToolbarConfig();
        toolbarConfig.androidAvailableActions = Arrays.asList(
                ToolbarAction.Thumbnail,
                ToolbarAction.Search,
                ToolbarAction.Bota,
                ToolbarAction.Menu
        );
        toolbarConfig.availableMenus = Arrays.asList(
                MenuAction.ViewSettings,
                MenuAction.DocumentEditor,
                MenuAction.Watermark,
                MenuAction.Security,
                MenuAction.DocumentInfo,
                MenuAction.Save,
                MenuAction.Share,
                MenuAction.OpenDocument
        );
        return toolbarConfig;
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
        OpenDocument,

        Flattened,

        Snip;

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
    public boolean mainToolbarVisible = true;

    public boolean annotationToolbarVisible = true;

    public boolean contentEditorToolbarVisible = true;

    public boolean formToolbarVisible = true;

    public boolean signatureToolbarVisible = true;

    public List<ToolbarAction> androidAvailableActions;

    public List<MenuAction> availableMenus;

    public boolean showInkToggleButton = true;


}
