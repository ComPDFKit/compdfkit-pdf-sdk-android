/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.date;


import android.text.TextUtils;

import com.compdfkit.core.common.CPDFDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CDateUtil {

    public static final String NORMAL_DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";

    public static final String NORMAL_DATE_FORMAT_1 = "yyyy.MM.dd HH:mm:ss";

    /**
     * Transform a PDF date string in the format D:20230130060237+00'00' to a human-readable yyyy-MM-dd HH:mm:ss date string.
     *
     * @param inputDate the PDF date string to be transformed
     * @return the transformed date string in yyyy-MM-dd HH:mm:ss format, or an empty string if the input string is null or too short
     */
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

    public static long transformToTimestamp(CPDFDate pdfDate) {
        try{
            String inputDate = CPDFDate.toStandardDate(pdfDate);
            if (TextUtils.isEmpty(inputDate) || inputDate.length() < 16) {
                return 0;
            }
            String year = inputDate.substring(2, 6);
            String month = inputDate.substring(6, 8);
            String day = inputDate.substring(8, 10);
            String hour = inputDate.substring(10, 12);
            String minute = inputDate.substring(12, 14);
            String second = inputDate.substring(14, 16);
            String timeStr =  year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(timeStr);
            return date.getTime();
        }catch (Exception e){
            return 0;
        }
    }

    public static String formatDate(String dateStr, String pattern){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        try {
            Date date = inputFormat.parse(dateStr);
            String formattedOutput = outputFormat.format(date);
            System.out.println(formattedOutput);
            return formattedOutput;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    public static String format(long time, String pattern){
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = new Date();
        date.setTime(time);
        return df.format(date);
    }

    public static String formatPDFUTCDate(String pdfUTCDate){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = inputFormat.parse(pdfUTCDate);
            String formattedOutput = outputFormat.format(date);
            return formattedOutput;
        } catch (ParseException e) {
            e.printStackTrace();
            return pdfUTCDate;
        }
    }

}
