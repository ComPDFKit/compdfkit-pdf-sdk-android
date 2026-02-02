/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config;



import androidx.annotation.NonNull;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContextMenuConfig implements Serializable {

    public String backgroundColor;

    public int fontSize = 14;

    public List<Double> padding = new ArrayList<>();

    public int iconSize;

    public Map<String, List<ContextMenuActionItem>> global;
    public Map<String, List<ContextMenuActionItem>> viewMode;
    public Map<String, List<ContextMenuActionItem>> annotationMode;
    public Map<String, List<ContextMenuActionItem>> contentEditorMode;
    public Map<String, List<ContextMenuActionItem>> formMode;
    public Map<String, List<ContextMenuActionItem>> signatureMode;

    public static ContextMenuConfig fromJson(JSONObject rootJsonObject) {
        ContextMenuConfig contextMenuConfig = new ContextMenuConfig();
        contextMenuConfig.backgroundColor = rootJsonObject.optString("backgroundColor", "");
        contextMenuConfig.fontSize = rootJsonObject.optInt("fontSize", 14);
        JSONArray paddingArray = rootJsonObject.optJSONArray("padding");
        if (paddingArray != null) {
            for (int i = 0; i < paddingArray.length(); i++) {
                contextMenuConfig.padding.add(paddingArray.optDouble(i, 0));
            }
        }
        contextMenuConfig.iconSize = rootJsonObject.optInt("iconSize", 36);
        contextMenuConfig.global = parseMode(rootJsonObject, "global");
        contextMenuConfig.viewMode = parseMode(rootJsonObject, "viewMode");
        contextMenuConfig.annotationMode = parseMode(rootJsonObject, "annotationMode");
        contextMenuConfig.contentEditorMode = parseMode(rootJsonObject, "contentEditorMode");
        contextMenuConfig.formMode = parseMode(rootJsonObject, "formMode");
        contextMenuConfig.signatureMode = parseMode(rootJsonObject, "signatureMode");
        return contextMenuConfig;
    }

    public static class ContextMenuActionItem implements Serializable {

        public enum ShowType {
            text,

            icon;
        }

        public String key;

        public String identifier;

        public String title;

        public ShowType showType = ShowType.text;

        public String icon;

        public List<String> subItems;

        public ContextMenuActionItem(String key, List<String> subItems) {
            this.key = key;
            this.subItems = subItems;
        }

        @NonNull
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
                    String identifier = obj.optString("identifier", "");
                    String title = obj.optString("title", "");
                    String showTypeStr = obj.optString("showType", "text");
                    ContextMenuActionItem.ShowType showType = ContextMenuActionItem.ShowType.valueOf(showTypeStr);

                    String icon = obj.optString("icon", "");

                    ContextMenuActionItem item = new ContextMenuActionItem(itemKey, subItems);
                    item.showType = showType;
                    item.identifier = identifier;
                    item.title = title;
                    item.icon = icon;
                    itemList.add(item);
                }
                resultMap.put(key, itemList);
            }
        } catch (Exception ignored) {
        }

        return resultMap;
    }
}
