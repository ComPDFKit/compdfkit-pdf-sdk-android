/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.compdfkit.core.document.CPDFSdk;
import com.compdfkit.pdfviewer.R;
import com.compdfkit.pdfviewer.databinding.ActivitySettingBinding;
import com.compdfkit.pdfviewer.home.datas.SettingDatas;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.ui.utils.CPDFCommomUtils;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvVersionValue.setText("V " + CPDFSdk.getSDKVersion());
        binding.tvCompdfWebsite.setOnClickListener(this);
        binding.tvAboutUs.setOnClickListener(this);
        binding.tvTechnicalSupport.setOnClickListener(this);
        binding.tvContactSales.setOnClickListener(this);
        binding.tvSupportEmail.setOnClickListener(this);
        binding.tvPrivacyPolicy.setOnClickListener(this);
        binding.tvTermsOfService.setOnClickListener(this);
        binding.toolBar.setBackBtnClickListener(this);

        binding.swHighlightLink.setChecked(SettingDatas.isHighlightLink(this));
        binding.swHighlightForm.setChecked(SettingDatas.isHighlightForm(this));

        binding.swHighlightForm.setListener((buttonView, isChecked) -> {
            SettingDatas.setHighlightForm(this, isChecked);
        });
        binding.swHighlightLink.setListener((buttonView, isChecked) -> {
            SettingDatas.setHighlightLink(this, isChecked);
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_compdf_website) {
            CPDFCommomUtils.gotoWebsite(this, getString(R.string.tools_compdf_website), null);
        } else if (v.getId() == R.id.tv_about_us) {
            CPDFCommomUtils.gotoWebsite(this, getString(R.string.tools_about_compdfkit_url), null);
        } else if (v.getId() == R.id.tv_technical_support) {
            CPDFCommomUtils.gotoWebsite(this, getString(R.string.tools_technical_support_url), null);
        } else if (v.getId() == R.id.tv_contact_sales) {
            CPDFCommomUtils.gotoWebsite(this, getString(R.string.tools_contact_sales_url), null);
        } else if (v.getId() == R.id.tv_support_email) {
            CUriUtil.sendEmail(this, getString(R.string.tools_compdf_email), getString(R.string.tools_email_technical_support_title));
        } else if (v.getId() == R.id.tv_privacy_policy) {
            CPDFCommomUtils.gotoWebsite(this, getString(R.string.tools_privacy_policy_url), null);
        } else if (v.getId() == R.id.tv_terms_of_service) {
            CPDFCommomUtils.gotoWebsite(this, getString(R.string.tools_terms_of_service_url), null);
        } else if (v.getId() == binding.toolBar.getIvToolBarBackBtn().getId()) {
            onBackPressed();
        }
    }
}
