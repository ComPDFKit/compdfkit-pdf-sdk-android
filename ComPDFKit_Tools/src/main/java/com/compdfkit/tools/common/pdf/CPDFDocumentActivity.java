/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.activity.CPermissionActivity;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.CFileUtils;

public class CPDFDocumentActivity extends CPermissionActivity {

    public static final String EXTRA_FILE_PATH = CPDFDocumentFragment.EXTRA_FILE_PATH;
    public static final String EXTRA_FILE_PASSWORD = CPDFDocumentFragment.EXTRA_FILE_PASSWORD;
    public static final String EXTRA_CONFIGURATION = CPDFDocumentFragment.EXTRA_CONFIGURATION;

    public static void startActivity(Context context, String filePath, String password, CPDFConfiguration configuration){
        Intent intent = new Intent(context, CPDFDocumentActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        intent.putExtra(EXTRA_FILE_PASSWORD, password);
        intent.putExtra(EXTRA_CONFIGURATION, configuration);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Uri uri, String password, CPDFConfiguration configuration){
        Intent intent = new Intent(context, CPDFDocumentActivity.class);
        intent.setData(uri);
        intent.putExtra(EXTRA_FILE_PASSWORD, password);
        intent.putExtra(EXTRA_CONFIGURATION, configuration);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CPDFConfiguration configuration;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            configuration = getIntent().getSerializableExtra(CPDFDocumentFragment.EXTRA_CONFIGURATION, CPDFConfiguration.class);
        } else {
            configuration = (CPDFConfiguration) getIntent().getSerializableExtra(CPDFDocumentFragment.EXTRA_CONFIGURATION);
        }
        if (configuration == null) {
            configuration = CPDFConfigurationUtils.normalConfig(this, "tools_default_configuration.json");
        }
        int themeId = CPDFApplyConfigUtil.getInstance().getGlobalThemeId(getApplicationContext(), configuration);
        setTheme(themeId);
        setContentView(R.layout.tools_pdf_document_activity);
        if (getIntent() != null && getSupportFragmentManager().findFragmentByTag("documentFragment") == null) {
            String password = getIntent().getStringExtra(CPDFDocumentFragment.EXTRA_FILE_PASSWORD);

            CPDFDocumentFragment documentFragment;
            if (!TextUtils.isEmpty(getIntent().getStringExtra(CPDFDocumentFragment.EXTRA_FILE_PATH))){
                documentFragment = CPDFDocumentFragment.newInstance(
                        getIntent().getStringExtra(CPDFDocumentFragment.EXTRA_FILE_PATH),
                        password,
                        configuration);
            }else {
                Uri uri = getIntent().getData();
                CFileUtils.takeUriPermission(this, uri);
                documentFragment = CPDFDocumentFragment.newInstance(
                        uri,
                        password,
                        configuration);
            }
            int count = getSupportFragmentManager().getFragments().size();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, documentFragment, "documentFragment")
                    .commit();
        }
    }
}
