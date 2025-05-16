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
        CPDFSdk.init(this, "RmQtBKZp3ZBOLrTE9iT6rx5mX25R63bWZYNFYVtXmxLJ52gsquPRZR3SC+Y/9ebuuNHte5ewi6b+RJhsLH1CC8fZ1fa9WgcgzAyUk6tkTKVQ0IlNzpIl6avn6VZZ8z739mEtExZHF/jIOeF4wi6oUcnD57UHEHLlorviCr7ezeBJG3nJuR7CbOsDGTxFPz1mAQPXPno82TsYIQOVd0YZH+FL3PsyKYaOnSpzl2vErP1ykUhKSLGqX0UjF3/aamA3hxrsXLCnH6N2G1L5jwyr4Bw+ZigoHMQglgWkyr2pxMDvpAC5ODUdCU43GHzKUy2ZmqGm4k3cQ1dexFQVg1krMGu0lH1oY17eVN2TRW5amzrVRSqpTOyx2LGvW1Ilra90nzlp2dEBHH+rU3Jo93jy94eWecFWMwgKBD5sABvhJFteiZTpP6NufkmmJm5UhS1bbWwQ3416ecpKs8D9TAlLLO+rbIocuxdoPE2dxWFYLq6zF8kJV3z7dKYtAwQKdoQiS08ryGXVZybCx2GjZp97I7zNemiorRWKQUrxpNk0vCLwL1yz7NzjlB6YQ8UxvmTkX/GU7T7Ubg9LoyZuVo4tLHLplMSlHIcA4guqZL7JbQ6/jomhcJGFpGo+X7tbrCvMTnbvjZoxJRlcNN1+9x100WEfF4A2XbJZEjcpxV9tk1rTt+jS8dkX803ij16yHI1THSycP2aKkWjgDAsAUt98KL+h4U+jgXH+NDKksS1nHNj/D5gUzVZb9YIL3msCOLra5s0djhCW/JhHu8oi6evWdC/l9/zka20p2J+S0gYBJZVV5PqpP/oz1yrxd3HL4q+XfQLspe7UnwbzsomDK6UNMOB6wL5IX/83nH3cl+UQPxeYRErTdWFnoeZzM8/GIywzMMgjtLWq6R7TnuUISCr546/OVWed0jdIirfoinBa7gxOOBksvggxxsojoM7/ibLYeGd1bpQRolFEjmon/x/uepb+Qu0eSnK+vc/PksIFmR8e9r3XfNPfa0vGVFG5iuYc+IQqltAs8+zcFcFcwRT5oyPnefdyXsoErUnnJ8WMqohz514PVUGRuOb1aq9iOWg5xUk1cnFIp61BJlh7lypQEtbVCheDOJRIFsKY4SMlzMXEGNHXasYHCtgjkF6rkxDNel8IuIrT6GXxYBIAVYmRbMlxqetnGiE7rcK2kvckRgC5qXRifiQqN5qZswrqFN0pB0MN8h8RRa4B7vone+Bm3lvO2jicERnlS08z2v/+ICk=", true, new CPDFSdk.OnVerifyLicenseListener() {
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
