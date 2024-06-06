/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home.datas;


import android.content.Context;

public class SettingDatas {

    public static boolean isHighlightLink(Context context){
        return SpUtils.getBooleanValue(context, "highlight_link", false);
    }

    public static void setHighlightLink(Context context, boolean highLight){
        SpUtils.setBooleanValue(context, "highlight_link", highLight);
    }

    public static boolean isHighlightForm(Context context){
        return SpUtils.getBooleanValue(context, "highlight_form", false);
    }

    public static void setHighlightForm(Context context, boolean highLight){
        SpUtils.setBooleanValue(context, "highlight_form", highLight);
    }
}
