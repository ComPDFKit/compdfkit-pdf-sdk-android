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

import com.compdfkit.pdfviewer.R;

public class SettingDatas {

    public static boolean isHighlightLink(Context context){
        return SpUtils.getBooleanValue(context, "highlight_link", true);
    }

    public static void setHighlightLink(Context context, boolean highLight){
        SpUtils.setBooleanValue(context, "highlight_link", highLight);
    }

    public static boolean isHighlightForm(Context context){
        return SpUtils.getBooleanValue(context, "highlight_form", true);
    }

    public static void setHighlightForm(Context context, boolean highLight){
        SpUtils.setBooleanValue(context, "highlight_form", highLight);
    }


    public static boolean isExtraFontSet(Context context){
        return SpUtils.getBooleanValue(context, "file_save_extra_font_set", true);
    }

    public static void setFileSaveExtraFontSet(Context context, boolean extra){
        SpUtils.setBooleanValue(context, "file_save_extra_font_set", extra);
    }

    public static void setDocumentAuthor(Context context, String author){
        SpUtils.setStringValue(context, "document_author", author);
    }

    public static String getDocumentAuthor(Context context){
        return SpUtils.getStringValue(context, "document_author", context.getString(R.string.tools_compdfkit_author));
    }

    public static void setAnnotationAuthor(Context context, String author){
        SpUtils.setStringValue(context, "annotation_author", author);
    }

    public static String getAnnotationAuthor(Context context){
        return SpUtils.getStringValue(context, "annotation_author", context.getString(R.string.tools_default_annotation_author_name));
    }

}
