/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
        String savePath = new File(getFilesDir(), "extraFonts/").getAbsolutePath();
        CFileUtils.copyAssetsDirToPhone(this, "extraFonts", getFilesDir().getAbsolutePath());
        CPDFSdk.setImportFontDir(savePath, true);
        CPDFSdk.init(this, "vWG81pDfg5Ojm9ycyAHoBf6Qb/yT4YuLPM521RVvGf8yhKFjx+KH12mgD+E64Snkacdj2aqW8P3Uk9kEjk9OfgvHtozTmo+zqbVIygogMpz9RvhZO/IdEE3jhQWkISSI+N2D2XyGtu1M8o2mLpceugjCrTphVTMW1tBNIFAlynGyIVXu5+2konU4Hh9V9nJtQzpGD2Ew1VWvcklNST1e2UgTaTa+C5fkHpc3vC9PlPj3wTMxORp6BGqdj45SV3G4N0PuCG5V4B1laeQbsAAIY1SKzY1fCE7G7iJCGJ8dJoZ5n6PzHVn1BkXR6UdsI9x1fw+ckJsStMpgWHAeLX9GTmu0lH1oY17eVN2TRW5amzrVRSqpTOyx2LGvW1Ilra90nzlp2dEBHH+rU3Jo93jy94eWecFWMwgKBD5sABvhJFteiZTpP6NufkmmJm5UhS1bbWwQ3416ecpKs8D9TAlLLO+rbIocuxdoPE2dxWFYLq6zF8kJV3z7dKYtAwQKdoQiS08ryGXVZybCx2GjZp97I7zNemiorRWKQUrxpNk0vCLwL1yz7NzjlB6YQ8UxvmTkX/GU7T7Ubg9LoyZuVo4tLHLplMSlHIcA4guqZL7JbQ6/jomhcJGFpGo+X7tbrCvMTnbvjZoxJRlcNN1+9x100WEfF4A2XbJZEjcpxV9tk1rTt+jS8dkX803ij16yHI1THSycP2aKkWjgDAsAUt98KCUHLaZi4F73ifo4l7JQoj0MxqN2wuiDb2PRCz4539YtkdVIZEXZTwWHfrGGmBtk3eTjulgN6KX4N8Zl8HTm1QDd0eY4XLXdZrWhe9nc1DGvLY1gsBg7f03Pf+Ch9WtB1I5hPMBVEyy26KZtF484aCb9XagMRnF+iot0GttPzd+uPWA4icpbsc75X0fCRahxJTWAXtYCUc/pCTm54v24bIoq3Zxj1lx7tKDtad8Ou3diwfFGTMtsMs+ga7i5gUny3WSd1idswHBlZEZh4+h7L6QgFnxa20xs3LwkoQTfhr6nZgjh0SZri4haYRMpOBtGEdSntVKJv2Vka7yk0uAGQ9GincrC/EqOa0HVmqcVvf3E3S6Mb4qT4xeOVeKWTCeJpKWznHb57bcaXEe18jn7YpE2l1oio78O3ElTVCvjufcILgQ8c4Bt5yXCNSZoIQsS3ge+rhLMiBMMhVVVZQm05qf1ku8hc0JT8C2n1OAx6ouQRTvhnA9CqXO+obtoFKSODA==", true);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
