/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer;


import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CPDFConfigurationUtils {


    public static CPDFConfiguration normalConfig(){
        CPDFConfiguration.Builder builder = new CPDFConfiguration.Builder()
                .setModeConfig(new CPDFConfiguration.ModeConfig(CPreviewMode.Viewer));

        CPDFConfiguration.ReaderViewConfig readerViewConfig = new CPDFConfiguration.ReaderViewConfig();
        readerViewConfig.linkHighlight = true;
        readerViewConfig.formFieldHighlight = true;
        builder.setReaderViewConfig(readerViewConfig);

        CPDFConfiguration.ToolbarConfig toolbarConfig = new CPDFConfiguration.ToolbarConfig();
        toolbarConfig.androidAvailableActions = Arrays.asList(
                CPDFConfiguration.ToolbarConfig.ToolbarAction.Thumbnail,
                CPDFConfiguration.ToolbarConfig.ToolbarAction.Search,
                CPDFConfiguration.ToolbarConfig.ToolbarAction.Bota,
                CPDFConfiguration.ToolbarConfig.ToolbarAction.Menu
        );
        toolbarConfig.availableMenus = Arrays.asList(
                CPDFConfiguration.ToolbarConfig.MenuAction.ViewSettings,
                CPDFConfiguration.ToolbarConfig.MenuAction.DocumentEditor,
                CPDFConfiguration.ToolbarConfig.MenuAction.Security,
                CPDFConfiguration.ToolbarConfig.MenuAction.Watermark,
                CPDFConfiguration.ToolbarConfig.MenuAction.DocumentInfo,
                CPDFConfiguration.ToolbarConfig.MenuAction.Save,
                CPDFConfiguration.ToolbarConfig.MenuAction.Share,
                CPDFConfiguration.ToolbarConfig.MenuAction.OpenDocument
        );
        builder.setToolbarConfig(toolbarConfig);
        return builder.create();
    }

    public static CPDFConfiguration fromJson(String json){
        try {
            CPDFConfiguration configuration = new CPDFConfiguration();
            JSONObject rootJsonObject = new JSONObject(json);
            configuration.modeConfig = parseModeConfig(rootJsonObject.getJSONObject("modeConfig"));
            configuration.toolbarConfig = parseToolbarConfig(rootJsonObject.getJSONObject("toolbarConfig"));
            configuration.readerViewConfig = parseReaderViewConfig(rootJsonObject.getJSONObject("readerViewConfig"));
            return configuration;
        } catch (Exception e) {
            return  CPDFConfigurationUtils.normalConfig();
        }
    }

    private static CPDFConfiguration.ModeConfig parseModeConfig(JSONObject jsonObject){
        CPDFConfiguration.ModeConfig modeConfig = new CPDFConfiguration.ModeConfig();
        CPreviewMode mode = CPreviewMode.fromAlias(jsonObject.optString("initialViewMode"));
        modeConfig.initialViewMode = mode != null ? mode : CPreviewMode.Viewer;
        return modeConfig;
    }

    private static CPDFConfiguration.ToolbarConfig parseToolbarConfig(JSONObject jsonObject){
        CPDFConfiguration.ToolbarConfig toolbarConfig = new CPDFConfiguration.ToolbarConfig();
        List<CPDFConfiguration.ToolbarConfig.ToolbarAction> androidAvailableActionsList = new ArrayList<>();
        JSONArray androidAvailableActions = jsonObject.optJSONArray("androidAvailableActions");
        if (androidAvailableActions != null) {
            for (int i = 0; i < androidAvailableActions.length(); i++) {
                CPDFConfiguration.ToolbarConfig.ToolbarAction action = CPDFConfiguration.ToolbarConfig.ToolbarAction.fromString(androidAvailableActions.optString(i));
                if (action != null) {
                    androidAvailableActionsList.add(action);
                }
            }
        }
        toolbarConfig.androidAvailableActions = androidAvailableActionsList;

        // parse more menu actions
        List<CPDFConfiguration.ToolbarConfig.MenuAction> menuActionList = new ArrayList<>();
        JSONArray availableMenusArray = jsonObject.optJSONArray("availableMenus");
        if (availableMenusArray != null) {
            for (int i = 0; i < availableMenusArray.length(); i++) {
                CPDFConfiguration.ToolbarConfig.MenuAction menuAction = CPDFConfiguration.ToolbarConfig.MenuAction.fromString(availableMenusArray.optString(i));
                if (menuAction != null) {
                    menuActionList.add(menuAction);
                }
            }
        }
        toolbarConfig.availableMenus = menuActionList;
        return toolbarConfig;
    }

    private static CPDFConfiguration.ReaderViewConfig parseReaderViewConfig(JSONObject jsonObject){
        CPDFConfiguration.ReaderViewConfig readerViewConfig = new CPDFConfiguration.ReaderViewConfig();
        readerViewConfig.linkHighlight = jsonObject.optBoolean("linkHighlight", true);
        readerViewConfig.formFieldHighlight = jsonObject.optBoolean("formFieldHighlight", true);
        return readerViewConfig;
    }
}
