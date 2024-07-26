/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    fun shareFile(context: Context, title: String?, type: String?, file: File?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                putExtra(Intent.EXTRA_SUBJECT, title)
                val uri = getUriBySystem(context, file)
                putExtra(Intent.EXTRA_STREAM, uri)
                setDataAndType(uri, type)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getUriBySystem(context: Context, file: File?): Uri? {
        return try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                Uri.fromFile(file)
            } else {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file!!)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getAssetsTempFile(context: Context, assetsName: String): String? {
        return copyFileFromAssets(context, assetsName, context.cacheDir.absolutePath, assetsName, true)
    }

    fun getNameWithoutExtension(name: String): String {
        val index = name.lastIndexOf(".")
        return if (index == -1) name else name.substring(0, index)
    }

    fun copyFileFromAssets(context: Context,
                           assetName: String?,
                           savePath: String,
                           saveName: String,
                           overwriteExisting: Boolean): String? {
        //if save path folder not exists, create directory
        val dir = File(savePath)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null
            }
        }

        // 拷贝文件
        val filename = "$savePath/$saveName"
        val file = File(filename)
        if (file.exists()) {
            file.delete()
        }
        return if (!file.exists() || overwriteExisting) {
            try {
                val inStream = context.assets.open(assetName!!)
                val fileOutputStream = FileOutputStream(filename)
                var byteRead: Int
                val buffer = ByteArray(1024)
                while (inStream.read(buffer).also { byteRead = it } != -1) {
                    fileOutputStream.write(buffer, 0, byteRead)
                }
                fileOutputStream.flush()
                inStream.close()
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            file.absolutePath
        } else {
            file.absolutePath
        }
    }

    fun deleteFile(file: File) {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    val f = files[i]
                    deleteFile(f)
                }
            }
            file.delete() //如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete()
        }
    }
}