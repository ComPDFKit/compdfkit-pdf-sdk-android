/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.importcert.create;


import static com.compdfkit.tools.common.utils.CFileUtils.CERTIFICATE_DIGITAL_TYPE;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfsignature.CAddSignatureActivity;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.signature.interfaces.COnSelectCertFileListener;

public class CPDFSelectDigitalSignatureDialog extends CBasicThemeDialogFragment implements View.OnClickListener {

    private RadioGroup rgType;

    private String certFilePath;

    private String certPassword;

    private COnCertDigitalSignListener certDigitalSignListener;

    private ActivityResultLauncher<Intent> addSignatureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Fragment fragment = getChildFragmentManager().findFragmentByTag("certDialog");
        if (fragment != null) {
            ((DialogFragment) fragment).dismiss();
        }
        if (result.getData() != null) {
            String signImagePath;
            if (result.getData().getStringExtra("file_path") != null){
                signImagePath = result.getData().getStringExtra("file_path");
                if (certDigitalSignListener != null) {
                    certDigitalSignListener.sign(certFilePath, certPassword, signImagePath);
                }
            } else if (result.getData().getBooleanExtra(CAddSignatureActivity.RESULT_NONE, false)){
                signImagePath = "";
                if (certDigitalSignListener != null) {
                    certDigitalSignListener.sign(certFilePath, certPassword, signImagePath);
                }
            }
        }
    });

    private COnSelectCertFileListener selectCertFileListener = (filePath, password) -> {
        certFilePath = filePath;
        certPassword = password;
        openSignActivity();
    };

    private void openSignActivity(){
        Intent intent = new Intent(getContext(), CAddSignatureActivity.class);
        intent.putExtra(CAddSignatureActivity.EXTRA_SHOW_NONE_TYPE, true);
        intent.putExtra(CAddSignatureActivity.EXTRA_TITLE, getString(R.string.tools_customize_the_signature_appearance));
        intent.putExtra(CAddSignatureActivity.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        intent.putExtra(CAddSignatureActivity.EXTRA_HIDE_TYPEFACE, true);
        intent.putExtra(CAddSignatureActivity.EXTRA_THEME_ID, CPDFApplyConfigUtil.getInstance().getGlobalThemeId());
        addSignatureLauncher.launch(intent);
    }

    public ActivityResultLauncher<Intent> selectCertificateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null && result.getData().getData()!=null) {
            Uri certificateUri = result.getData().getData();
            CImportCertificateDigitalDialog digitalDialog = CImportCertificateDigitalDialog.newInstance(certificateUri);
            digitalDialog.setSelectCertFileListener(selectCertFileListener);
            digitalDialog.show(getChildFragmentManager(), "certDialog");
        }
    });

    public static CPDFSelectDigitalSignatureDialog newInstance() {
        Bundle args = new Bundle();
        CPDFSelectDigitalSignatureDialog fragment = new CPDFSelectDigitalSignatureDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int themeResId() {
        return  com.google.android.material.R.attr.dialogTheme;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_sign_digital_sign_select_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog()!=null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    protected void onCreateView(View rootView) {
        rgType = rootView.findViewById(R.id.rg_type);
        AppCompatButton btnCancel = rootView.findViewById(R.id.btn_cancel);
        AppCompatButton btnDone = rootView.findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            dismiss();
        } else if (v.getId() == R.id.btn_confirm) {
            if (rgType.getCheckedRadioButtonId() == R.id.rb_import_digital_sign){
                selectCertificateLauncher.launch(CFileUtils.getIntent(CERTIFICATE_DIGITAL_TYPE));
            }else {
                CreateCertificateDigitalDialog dialog = CreateCertificateDigitalDialog.newInstance();
                dialog.setSelectCertFileListener(selectCertFileListener);
                dialog.show(getChildFragmentManager(), "certDialog");
            }
        }
    }

    public void setCertDigitalSignListener(COnCertDigitalSignListener certDigitalSignListener) {
        this.certDigitalSignListener = certDigitalSignListener;
    }

    public interface COnCertDigitalSignListener {
        void sign(String certFilePath, String certPassword, String signImagePath);
    }
}


