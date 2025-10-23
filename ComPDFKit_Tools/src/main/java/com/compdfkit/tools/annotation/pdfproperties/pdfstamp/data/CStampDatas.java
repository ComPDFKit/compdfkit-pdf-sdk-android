/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp.data;


import android.content.Context;
import android.net.Uri;

import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CStandardStampItemBean;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CTextStampBean;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.date.CDateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CStampDatas {

    public static List<CStandardStampItemBean> getStandardStampList() {
        return Arrays.asList(
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.APPROVED, com.compdfkit.ui.R.drawable.stamp_01_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.NOTAPPROVED, com.compdfkit.ui.R.drawable.stamp_02_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.DRAFT, com.compdfkit.ui.R.drawable.stamp_03_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.FINAL, com.compdfkit.ui.R.drawable.stamp_04_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.COMPLETED, com.compdfkit.ui.R.drawable.stamp_05_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.CONFIDENTIAL, com.compdfkit.ui.R.drawable.stamp_06_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.FORPUBLICRELEASE, com.compdfkit.ui.R.drawable.stamp_07_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.NOTFORPUBLICRELEASE, com.compdfkit.ui.R.drawable.stamp_08_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.FORCOMMENT, com.compdfkit.ui.R.drawable.stamp_09_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.VOID, com.compdfkit.ui.R.drawable.stamp_10_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.PRELIMINARYRESULTS, com.compdfkit.ui.R.drawable.stamp_11_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.INFORMATIONONLY, com.compdfkit.ui.R.drawable.stamp_12_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.WITNESS, com.compdfkit.ui.R.drawable.stamp_13_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.INITIALHERE, com.compdfkit.ui.R.drawable.stamp_14_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.SIGNHERE, com.compdfkit.ui.R.drawable.stamp_15_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.REVISED, com.compdfkit.ui.R.drawable.stamp_16_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.ACCEPTED, com.compdfkit.ui.R.drawable.stamp_20_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.REJECTED, com.compdfkit.ui.R.drawable.stamp_18_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.PRIVATEACCEPTED, com.compdfkit.ui.R.drawable.stamp_chick_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.PRIVATEREJECTED, com.compdfkit.ui.R.drawable.stamp_cross_2x),
                new CStandardStampItemBean(CPDFStampAnnotation.StandardStamp.PRIVATERADIOMARK, com.compdfkit.ui.R.drawable.stamp_circle_01_2x)
        );
    }

    public static List<CTextStampBean> getTextStampList(Context context) {
        File file = new File(context.getFilesDir(), CFileUtils.TEXT_STAMP_FOLDER);
        if (file.listFiles() != null) {
            List<File> files = Arrays.asList(file.listFiles());
            Collections.sort(files, (o1, o2) -> {
                if (o1.lastModified() < o2.lastModified()) {
                    return 1;
                } else if (o1.lastModified() == o2.lastModified()) {
                    return 0;
                } else {
                    return -1;
                }
            });
            List<CTextStampBean> list = new ArrayList<>();
            for (File file1 : files) {
                String json = CFileUtils.readFile2String(file1, null);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        CTextStampBean bean = new CTextStampBean(
                                jsonObject.getInt("bgColor"),
                                jsonObject.getInt("textColor"),
                                jsonObject.getInt("lineColor"),
                                jsonObject.getString("textContent"),
                                jsonObject.optString("dateStr",""),
                                jsonObject.getBoolean("showDate"),
                                jsonObject.getBoolean("showTime"),
                                jsonObject.getInt("textStampShapeId"),
                                jsonObject.getInt("textStampColorId")
                        );
                        bean.setFilePath(file1.getAbsolutePath());
                        list.add(bean);
                    } catch (JSONException e) {
                    }
                }
            }
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    public static List<String> getImageStampList(Context context){
        File file = new File(context.getFilesDir(), CFileUtils.IMAGE_STAMP_FOLDER);
        if (file.listFiles() != null){
            List<File> files = Arrays.asList(file.listFiles());
            Collections.sort(files, (o1, o2) -> {
                if (o1.lastModified() < o2.lastModified()) {
                    return 1;
                } else if (o1.lastModified() == o2.lastModified()) {
                    return 0;
                } else {
                    return -1;
                }
            });
            List<String> list = new ArrayList<>();
            for (File file1 : files) {
                list.add(file1.getAbsolutePath());
            }
            return list;
        }else {
            return new ArrayList<>();
        }
    }

    public static boolean removeStamp(String signaturePath) {
        File file = new File(signaturePath);
        return file.delete();
    }

    public static String saveTextStamp(Context context, CTextStampBean textStampBean) {
        File folderFile = new File(context.getFilesDir(), CFileUtils.TEXT_STAMP_FOLDER);
        folderFile.mkdirs();
        File file = new File(folderFile, "text_stamp_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".json");
        boolean success = CFileUtils.writeFileFromString(file, textStampBean.toJson(), false);
        if (success) {
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    public static String saveStampImage(Context context, Uri imageUri) {
        File file = new File(context.getFilesDir(), CFileUtils.IMAGE_STAMP_FOLDER);
        return CFileUtils.copyFileToInternalDirectory(context, imageUri, file.getAbsolutePath(),
                "stamp_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".png");
    }
}
