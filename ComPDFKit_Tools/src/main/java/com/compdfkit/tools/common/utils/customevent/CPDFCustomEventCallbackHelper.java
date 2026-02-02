/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.utils.customevent;

import com.compdfkit.tools.common.interfaces.CPDFCustomEventCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CPDFCustomEventCallbackHelper {

    private static CPDFCustomEventCallbackHelper instance;

    private final List<CPDFCustomEventCallback> callbacks = new ArrayList<>();

    private CPDFCustomEventCallbackHelper() {}

    public static synchronized CPDFCustomEventCallbackHelper getInstance() {
        if (instance == null) {
            instance = new CPDFCustomEventCallbackHelper();
        }
        return instance;
    }

    public void notifyClick(String identifier) {
        notifyClick(identifier, new HashMap<>());
    }

    public void notifyClick(String identifier, java.util.Map<String, Object> extraMap) {
        extraMap.put(CPDFCustomEventField.IDENTIFIER, identifier);
        for (CPDFCustomEventCallback callback : callbacks) {
            callback.click(extraMap);
        }
    }

    public void notifyClick(String identifier, String key, Object value) {
        java.util.Map<String, Object> extraMap = new java.util.HashMap<>();
        extraMap.put(CPDFCustomEventField.IDENTIFIER, identifier);
        extraMap.put(key, value);
        for (CPDFCustomEventCallback callback : callbacks) {
            callback.click(extraMap);
        }
    }

    public void addCustomEventCallback(CPDFCustomEventCallback callback) {
        if (callback != null && !callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public void removeCustomEventCallback(CPDFCustomEventCallback callback) {
        callbacks.remove(callback);
    }

    public void clear() {
        callbacks.clear();
    }
}