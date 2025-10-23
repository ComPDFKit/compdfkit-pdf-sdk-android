/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home.datas;

import android.content.Context;
import android.content.SharedPreferences;


public class SpUtils {

    private static final String SP_NAME = "compdfkit";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static boolean getBooleanValue(Context context, String key, boolean defaultValue){
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void setBooleanValue(Context context, String key, boolean value){
        getSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static String getStringValue(Context context, String key, String defaultValue){
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public static void setStringValue(Context context, String key, String value){
        getSharedPreferences(context).edit().putString(key, value).apply();
    }
}
