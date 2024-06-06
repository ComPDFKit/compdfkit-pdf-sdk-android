/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples

import android.content.Context
import android.os.Environment
import androidx.annotation.StringRes
import com.compdfkit.core.common.CPDFDocumentException
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.samples.util.OutputListener
import java.io.File

abstract class PDFSamples {

    @JvmField
    protected var outputListener: OutputListener? = null

    val outputFileList: ArrayList<String> = ArrayList()

    var title = "{title}"

    var description = "{description}"

    fun setTitle(@StringRes titleResId: Int) {
        title = context().getString(titleResId)
    }

    fun setDescription(@StringRes descriptionResId: Int) {
        description = context().getString(descriptionResId)
    }

    fun getOutputFileList(): MutableList<String> = outputFileList

    val outputFileNames: Array<String?>
        get() {
            val names = arrayOfNulls<String>(outputFileList.size)
            outputFileList.toArray(names)
            for (i in names.indices) {
                val file = File(names[i])
                names[i] = file.name
            }
            return names
        }

    fun addFileList(file: String) {
        outputFileList.add(file)
    }

    protected fun printHead() {
        val head: String = context().getString(R.string.sample_header, title)
        outputListener?.println(head)
    }

    protected fun printFooter() {
        val footer: String = context().getString(R.string.sample_footer)
        outputListener?.println("\n${footer}")
        printDividingLine()
    }

    protected fun printDividingLine() {
        outputListener?.println("--------------------------------------------")
    }

    open fun run(outputListener: OutputListener?) {
        this.outputListener = outputListener
        outputFileList.clear()
    }

    protected open fun saveSamplePDF(document: CPDFDocument, file: File, close: Boolean = true) {
        try {
            file.parentFile?.mkdirs()
            document.saveAs(file.absolutePath, false)
            if (file.exists()) {
                getOutputFileList().add(file.absolutePath)
            }
            if (close) {
                document.close()
            }
        } catch (e: CPDFDocumentException) {
            e.printStackTrace()
        }
    }

    protected fun outputDir(): File? {
        return context().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    }

    protected fun context(): Context = SampleApplication.instance

    companion object {
        protected const val INPUT_PATH = "TestFiles/"
    }
}