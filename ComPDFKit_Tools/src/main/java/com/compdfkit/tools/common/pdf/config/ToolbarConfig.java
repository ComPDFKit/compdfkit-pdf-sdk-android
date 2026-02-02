/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.config;

import com.compdfkit.tools.common.views.CPDFToolBarMenuHelper.ToolBarAction;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


public class ToolbarConfig implements Serializable {

    public ToolbarConfig() {

    }

    public static ToolbarConfig normal(){
        ToolbarConfig toolbarConfig = new ToolbarConfig();
        toolbarConfig.toolbarRightItems = Arrays.asList(
            ToolBarAction.Thumbnail,
            ToolBarAction.Search,
            ToolBarAction.Bota,
            ToolBarAction.Menu
        );
        toolbarConfig.availableMenus = Arrays.asList(
            ToolBarAction.ViewSettings,
            ToolBarAction.DocumentEditor,
            ToolBarAction.Watermark,
            ToolBarAction.Security,
            ToolBarAction.DocumentInfo,
            ToolBarAction.Save,
            ToolBarAction.Share,
            ToolBarAction.OpenDocument
        );
        return toolbarConfig;
    }


    public boolean mainToolbarVisible = true;

    public boolean annotationToolbarVisible = true;

    public boolean contentEditorToolbarVisible = true;

    public boolean formToolbarVisible = true;

    public boolean signatureToolbarVisible = true;

    public List<ToolBarAction> toolbarLeftItems;
    public List<ToolBarAction> toolbarRightItems;

    public List<ToolBarAction> availableMenus;

    public List<CustomToolbarItem> customToolbarLeftItems;

    public List<CustomToolbarItem> customToolbarRightItems;

    public List<CustomToolbarItem> customMoreMenuItems;

    public boolean showInkToggleButton = true;

    public List<ToolBarAction> getMainToolbarActions(){
        return toolbarRightItems;
    }

}
