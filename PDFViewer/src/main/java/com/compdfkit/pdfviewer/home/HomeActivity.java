/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.compdfkit.core.document.CPDFAbility;
import com.compdfkit.core.document.CPDFSdk;
import com.compdfkit.pdfviewer.R;
import com.compdfkit.pdfviewer.databinding.ActivityHomeBinding;
import com.compdfkit.tools.common.utils.CFileUtils;

import java.io.File;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.home_setting) {
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            }
            return false;
        });

        if (getSupportFragmentManager().findFragmentByTag("homeFunFragment") == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_content, new HomeFunFragment(), "homeFunFragment")
                    .commit();
        }
        String fontDir = new File(getFilesDir(), "extraFonts/").getAbsolutePath();
        CFileUtils.copyAssetsDirToPhone(this, "extraFonts", getFilesDir().getAbsolutePath());
        CPDFSdk.setImportFontDir(fontDir, true);
        CPDFSdk.initWithPath(this, "assets://license_key_android.xml", (verifyCode, verifyMsg) -> {
            Log.e("ComPDFKit", "verifyCode: " + verifyCode + ", verifyMsg: " + verifyMsg);
            CPDFAbility.checkLicenseAllAbility();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Handle UI mode (day/night theme) changes
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Switched to day mode - recreate to apply light theme
                recreate();
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Switched to night mode - recreate to apply dark theme
                recreate();
                break;
        }
    }
}
