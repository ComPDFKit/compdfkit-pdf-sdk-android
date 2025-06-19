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
        CPDFSdk.init(this, "DrThSLolukp6imgZ180g1cCUsPi8mnlKOr7Rz1M5c6NieAfsnMRGnumMfsnDoGyLnQ5ahTG/vSaGoA9m1FKdBFD+DV9mJ1nDd3cO0GqCE8lz002aEQDECyv0ijIYY/K9nkOCcYafEwP/6132hvZznhgjCYO6ufZ3U2OwENTvtdYzjJE35wK3stutkW8vByv/6467Ftr7XK9dv4S8Uz8QGBLgwbnCItft3IZy89PzBotQ+P0vDpg2zyoXpKHOTMV4na2ufYIauRHfoEYVrIjWij9mGjW0C2ftlF8cbHFlELnwjaUoqB3jUuyeH+8knDg92ZNA7JawRrLVPcEt8l3Ybmu0lH1oY17eVN2TRW5amzrVRSqpTOyx2LGvW1Ilra90nzlp2dEBHH+rU3Jo93jy94eWecFWMwgKBD5sABvhJFteiZTpP6NufkmmJm5UhS1bbWwQ3416ecpKs8D9TAlLLO+rbIocuxdoPE2dxWFYLq6zF8kJV3z7dKYtAwQKdoQiS08ryGXVZybCx2GjZp97I7zNemiorRWKQUrxpNk0vCLwL1yz7NzjlB6YQ8UxvmTkX/GU7T7Ubg9LoyZuVo4tLHLplMSlHIcA4guqZL7JbQ6/jomhcJGFpGo+X7tbrCvMTnbvjZoxJRlcNN1+9x100WEfF4A2XbJZEjcpxV9tk1rTt+jS8dkX803ij16yHI1THSycP2aKkWjgDAsAUt98KL+h4U+jgXH+NDKksS1nHNjVoJYvPaW7TLcBzT0GegIgpTh5HPGjhSvenazoxqM0TOvMPAhaQ6RtZ1Wp6INATJOKAjBXtE4qe9DPFrxmGF01lgJPavrrCEVoCQiYVQj5qvOhF3RP2QxPdZt/1H0VGl4jd0Nb1ZbDw227AfQ/+gGTGXgSQYB0vVI/KTagFaYgUBp6B/639EtPQpmebumbmvo5CAP8fDG/RKTCtIAmCc3FPRQJBXaX5m/KSrUACn4sAJV4KtBmCBOaX+VeVpfEg9JnTqT8siIh9UZEyD9ih8UBHl7MavztRB0suiGb+hG16Yog00Ovn2+qGQ7tn66udqLi9BW8ZK+bYnI3Y/ZFSq+p0QWW6oSaLEyLBQjJgNj5HQOORLVDVRgL5Gz51Pop1diuVZc9YGH5UF+TEOwtJaKQ7Q/t7iPRD903qgUbM7Kc3GuUipwBHyDlEHhLZlNQ3HtYBFvpGrZVUwOye+ta37rum9zAMtbJlKonzz4DAZXXabgUqdyq9c/2GD0pXaG8cKITmHl9XD0XazcflDnliRNi", true, new CPDFSdk.OnVerifyLicenseListener() {
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
