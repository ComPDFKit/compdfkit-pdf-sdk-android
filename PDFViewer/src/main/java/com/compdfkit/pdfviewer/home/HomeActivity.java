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
        String savePath = new File(getFilesDir(), "extraFonts/").getAbsolutePath();
        CFileUtils.copyAssetsDirToPhone(this, "extraFonts", getFilesDir().getAbsolutePath());
        CPDFSdk.setImportFontDir(savePath, true);
        CPDFSdk.init(this, "rbbJE34FnGSu19mumutDtH/JnZhGG/F6seFnRkRsptiPhTZhNJOzf51sCT12LQ156NyvXbTeAyPG0OjCqApqvQc6DWibQP8onZSn/sUb6BBAlbx+t4+7MCivLFhFFIpVq591KeYsrLJrvaZKDqVBYVpy7BHvTSgyLs3+NgQaWFJRQsRmUsyu45BZDXA2wY+IZp+JwQM2/SF/WqulTugytbdep2zBlvPoBKQRGNlcTa2dRTbZaR5OMh6yqOCPcEVFA5GXzynDoz4MOLZsEs8i5ia5k6LuzDWLcdBvALMyQqWOahVsg7lnGGuV8pKWva9JO6l/ID5e3mMDDTS+kF3g6mu0lH1oY17eVN2TRW5amzrVRSqpTOyx2LGvW1Ilra90nzlp2dEBHH+rU3Jo93jy94eWecFWMwgKBD5sABvhJFteiZTpP6NufkmmJm5UhS1bbWwQ3416ecpKs8D9TAlLLO+rbIocuxdoPE2dxWFYLq6zF8kJV3z7dKYtAwQKdoQiS08ryGXVZybCx2GjZp97I7zNemiorRWKQUrxpNk0vCLwL1yz7NzjlB6YQ8UxvmTkX/GU7T7Ubg9LoyZuVo4tLHLplMSlHIcA4guqZL7JbQ6/jomhcJGFpGo+X7tbrCvMTnbvjZoxJRlcNN1+9x100WEfF4A2XbJZEjcpxV9tk1rTt+jS8dkX803ij16yHI1THSycP2aKkWjgDAsAUt98KL+h4U+jgXH+NDKksS1nHNj/D5gUzVZb9YIL3msCOLra4rzx64xPkJIcG6OXS1I7ASw3WWaEyJI4QKz6HXbZVHElJx5uuHIhpDNpBfXjax9kTT1Mzoph8Erom3hpRQDJSC2kvsKCucB2mdJLA7WTQ9Dq5pRmWXKTD/ai5zNtuN7pC8yRr9oKkPeGPYIjBPgWARDCEIZ7Rf9+/RTkZdbGHfll7zGmrsqVrVuYKYpSwbyNgiXJYToIyivs4+KgV2p+F7tt51Noj0WSS2ElzuLDGBt30KBnZPmMtd+2b8DUWw/52swmGAQwiAvmwIXS7p/3qNnMOP0anG+yUppiVnRlTB8fA11CjAWlPcYHpNU0Rne3ByvR3dptlknz9cdRS0IBFgtdeLznIunz3Fy/mdGGKU5mRPfGllKeWrZUHA7apS2GPE8xv7A9FrUFMFzWJLqFIHzaC1MQ3kkvme60gCvofx8BiQipXClbzvo7TI5qTVVozCXZF48sRJQbUOelPyjSCwBU35tqO2nPB0yHmCnEk+O4mPrM4DEwHsRi9KaXrnj9", true, new CPDFSdk.OnVerifyLicenseListener() {
            @Override
            public void onVerifyEnd(int i, String s) {
                CPDFAbility.checkLicenseAllAbility();
            }
        });
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
