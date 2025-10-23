/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {


    public static String transformPDFDate(String inputDate) {
        try{
            if (TextUtils.isEmpty(inputDate) || inputDate.length() < 16) {
                return "";
            }
            String year = inputDate.substring(2, 6);
            String month = inputDate.substring(6, 8);
            String day = inputDate.substring(8, 10);
            String hour = inputDate.substring(10, 12);
            String minute = inputDate.substring(12, 14);
            String second = inputDate.substring(14, 16);
            return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        }catch (Exception e){
            return inputDate;
        }
    }

    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }
}
