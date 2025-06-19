/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContextMenuConfig implements Serializable {

    public Map<String, List<ContextMenuActionItem>> global;
    public Map<String, List<ContextMenuActionItem>> viewMode;
    public Map<String, List<ContextMenuActionItem>> annotationMode;
    public Map<String, List<ContextMenuActionItem>> contentEditorMode;
    public Map<String, List<ContextMenuActionItem>> formMode;
    public Map<String, List<ContextMenuActionItem>> signatureMode;

    public static ContextMenuConfig fromJson(JSONObject rootJsonObject) {
        ContextMenuConfig contextMenuConfig = new ContextMenuConfig();
        contextMenuConfig.global = parseMode(rootJsonObject, "global");
        contextMenuConfig.viewMode = parseMode(rootJsonObject, "viewMode");
        contextMenuConfig.annotationMode = parseMode(rootJsonObject, "annotationMode");
        contextMenuConfig.contentEditorMode = parseMode(rootJsonObject, "contentEditorMode");
        contextMenuConfig.formMode = parseMode(rootJsonObject, "formMode");
        contextMenuConfig.signatureMode = parseMode(rootJsonObject, "signatureMode");
        return contextMenuConfig;
    }

    public static class ContextMenuActionItem implements Serializable{

        public String key;

        public List<String> subItems;

        public ContextMenuActionItem(String key, List<String> subItems) {
            this.key = key;
            this.subItems = subItems;
        }

        @Override
        public String toString() {
            if (subItems == null){
                return "key=" + key;
            }
            return "key=" + key + ", subItems=" + subItems;
        }
    }


    private static Map<String, List<ContextMenuConfig.ContextMenuActionItem>> parseMode(JSONObject parent, String modeName) {
        Map<String, List<ContextMenuActionItem>> resultMap = new HashMap<>();

        try {
            if (!parent.has(modeName)) return resultMap;

            JSONObject modeObject = parent.getJSONObject(modeName);
            for (Iterator<String> it = modeObject.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONArray array = modeObject.getJSONArray(key);
                List<ContextMenuActionItem> itemList = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String itemKey = obj.getString("key");

                    List<String> subItems = new ArrayList<>();
                    if (obj.has("subItems")) {
                        JSONArray subArray = obj.getJSONArray("subItems");
                        for (int j = 0; j < subArray.length(); j++) {
                            subItems.add(subArray.getString(j));
                        }
                    }

                    itemList.add(new ContextMenuActionItem(itemKey, subItems));
                }
                resultMap.put(key, itemList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    private static void printMode(String title, Map<String, List<ContextMenuActionItem>> modeMap) {
        Log.d("PDFConfig", "==== " + title + " ====");
        for (Map.Entry<String, List<ContextMenuActionItem>> entry : modeMap.entrySet()) {
            Log.d("PDFConfig", "  " + entry.getKey() + ":");
            for (ContextMenuActionItem item : entry.getValue()) {
                Log.d("PDFConfig", "    " + item);
            }
        }
    }
}
