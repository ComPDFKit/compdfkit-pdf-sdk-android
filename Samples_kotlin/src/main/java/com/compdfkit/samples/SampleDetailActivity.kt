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
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.compdfkit.samples.util.FileUtils
import com.compdfkit.samples.util.LoggingOutputListener
import com.google.android.material.button.MaterialButton
import java.io.File

open class SampleDetailActivity : AppCompatActivity() {

    private var pdfSamples: PDFSamples? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sample_detail)
        val btnRun = findViewById<MaterialButton>(R.id.btn_run)
        val btnOpenFiles = findViewById<MaterialButton>(R.id.btn_open_files)
        val logTextView = findViewById<AppCompatTextView>(R.id.tv_logging)
        val scrollView = findViewById<ScrollView>(R.id.scroll_view)
        val tvDescription = findViewById<AppCompatTextView>(R.id.tv_description)
        val outputListener = LoggingOutputListener(logTextView, scrollView)
        if (intent.hasExtra(EXTRA_SAMPLE_ID)) {
            pdfSamples = SampleApplication.instance.samplesList[intent.getIntExtra(EXTRA_SAMPLE_ID, 0)]
            supportActionBar?.let {
                it.setIcon(R.drawable.baseline_arrow_back_24)
                it.setHomeButtonEnabled(true)
                it.setDisplayHomeAsUpEnabled(true)
                it.title = pdfSamples!!.title
                tvDescription.text = pdfSamples!!.description
            }
            pdfSamples?.outputFileList?.clear()
        }
        btnRun.setOnClickListener {
            pdfSamples?.run(outputListener)
        }
        btnOpenFiles.setOnClickListener {
            pdfSamples?.let { pdfSamples ->
                pdfSamples.outputFileList.let {
                    AlertDialog.Builder(this)
                            .apply {
                                setTitle(R.string.choose_a_file_to_open)
                                setItems(pdfSamples.outputFileNames) { _: DialogInterface?, which: Int ->
                                    val filePath = it[which]
                                    var mimeType = "application/pdf"
                                    if (filePath.endsWith(".pdf")) {
                                        mimeType = "application/pdf"
                                    } else if (filePath.endsWith("png") || filePath.endsWith("jpg")) {
                                        mimeType = "image/*"
                                    } else if (filePath.endsWith(".xfdf")) {
                                        mimeType = "application/vnd.adobe.xfdf"
                                    }
                                    FileUtils.shareFile(this@SampleDetailActivity, "Open", mimeType, File(filePath))
                                }
                            }.create().show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        protected const val EXTRA_SAMPLE_ID = "SAMPLE_ID"
        fun openDetail(context: Context, sampleId: Int) {
            context.startActivity(Intent(context, SampleDetailActivity::class.java).apply {
                putExtra(EXTRA_SAMPLE_ID, sampleId)
            })
        }
    }
}


