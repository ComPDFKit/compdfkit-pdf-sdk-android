/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils;


import android.util.Log;

import com.compdfkit.tools.BuildConfig;

public class CLog {

    public static void e(String tag, String message){
        if (BuildConfig.DEBUG){
            Log.e(tag, message);
        }
    }

    public static void d(String tag, String message){
        if (BuildConfig.DEBUG){
            Log.d(tag, message);
        }
    }
}
