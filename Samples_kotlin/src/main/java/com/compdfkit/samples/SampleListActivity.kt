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

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.compdfkit.core.document.CPDFAbility
import com.compdfkit.samples.util.FileUtils

class SampleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_list)
        CPDFAbility.checkLicenseAllAbility()
        val file = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        file?.let {
            FileUtils.deleteFile(it)
        }
        supportActionBar?.setTitle(R.string.samples)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_sample_list)
        val listAdapter = SampleListAdapter().apply {
            onItemClickListener = { position ->
                SampleDetailActivity.openDetail(this@SampleListActivity, position)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter
    }
}