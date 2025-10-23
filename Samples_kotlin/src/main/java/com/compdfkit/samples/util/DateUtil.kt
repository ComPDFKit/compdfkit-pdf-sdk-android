/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.util

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.Date

object DateUtil {

    fun transformPDFDate(inputDate: String): String {
        return try {
            if (TextUtils.isEmpty(inputDate) || inputDate.length < 16) {
                return ""
            }
            val year = inputDate.substring(2, 6)
            val month = inputDate.substring(6, 8)
            val day = inputDate.substring(8, 10)
            val hour = inputDate.substring(10, 12)
            val minute = inputDate.substring(12, 14)
            val second = inputDate.substring(14, 16)
            "$year-$month-$day $hour:$minute:$second"
        } catch (e: Exception) {
            inputDate
        }
    }

    fun getDataTime(format: String?): String? {
        val df = SimpleDateFormat(format)
        return df.format(Date())
    }
}