/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.LoggingOutputListener;

import java.io.File;

public class SampleDetailActivity extends AppCompatActivity {

    protected static final String EXTRA_SAMPLE_ID = "SAMPLE_ID";

    PDFSamples pdfSamples = null;

    public static void openDetail(Context context, int sampleId) {
        Intent intent = new Intent(context, SampleDetailActivity.class);
        intent.putExtra(EXTRA_SAMPLE_ID, sampleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sample_detail);
        AppCompatButton btnRun = findViewById(R.id.btn_run);
        AppCompatButton btnOpenFiles = findViewById(R.id.btn_open_files);
        AppCompatTextView logTextView = findViewById(R.id.tv_logging);
        ScrollView scrollView = findViewById(R.id.scroll_view);
        AppCompatTextView tvDescription = findViewById(R.id.tv_description);

        ActionBar actionBar = getSupportActionBar();
        LoggingOutputListener outputListener = new LoggingOutputListener(logTextView, scrollView);
        if (getIntent().hasExtra(EXTRA_SAMPLE_ID)) {
            pdfSamples = SampleApplication.getInstance().samplesList.get(getIntent().getIntExtra(EXTRA_SAMPLE_ID, 0));
            if (actionBar != null) {
                actionBar.setIcon(R.drawable.baseline_arrow_back_24);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(pdfSamples.getTitle());
                tvDescription.setText(pdfSamples.getDescription());
            }
            pdfSamples.getOutputFileList().clear();
        }
        btnRun.setOnClickListener(v -> {
            new Thread(() -> {
                if (pdfSamples != null) {
                    pdfSamples.run(outputListener);
                }
            }).start();
        });
        btnOpenFiles.setOnClickListener(v -> {
            if (pdfSamples.getOutputFileList() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.choose_a_file_to_open);
                builder.setItems(pdfSamples.getOutputFileNames(), (dialog, which) -> {
                    String filePath = pdfSamples.getOutputFileList().get(which);
                    String mimeType = "application/pdf";
                    if (filePath.endsWith(".pdf")){
                        mimeType = "application/pdf";
                    } else if (filePath.endsWith("png") || filePath.endsWith("jpg")){
                        mimeType = "image/*";
                    } else if (filePath.endsWith(".xfdf")) {
                        mimeType = "application/vnd.adobe.xfdf";
                    }
                    FileUtils.shareFile(this, "Open", mimeType, new File(filePath));
                });
                builder.create().show();
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
