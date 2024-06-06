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

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfsignature.CAddSignatureActivity;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.signature.interfaces.COnSelectCertFileListener;

public class CPDFSelectDigitalSignatureDialog extends DialogFragment implements View.OnClickListener {

    private RadioGroup rgType;

    private String certFilePath;

    private String certPassword;

    private COnCertDigitalSignListener certDigitalSignListener;

    private ActivityResultLauncher<String> requestStorageLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {

    });

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
                    certDigitalSignListener.sign(certFilePath,certPassword, signImagePath);
                }
            }else if (result.getData().getBooleanExtra(CAddSignatureActivity.RESULT_NONE, false)){
                //none
                signImagePath = "";
                if (certDigitalSignListener != null) {
                    certDigitalSignListener.sign(certFilePath,certPassword, signImagePath);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.tools_dialog_theme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.tools_dialog_background);
        }
        View view = inflater.inflate(R.layout.tools_sign_digital_sign_select_dialog, container, false);
        rgType = view.findViewById(R.id.rg_type);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnDone = view.findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            dismiss();
        } else if (v.getId() == R.id.btn_confirm) {
            if (!CPermissionUtil.hasStoragePermissions(getContext())){
                if (Build.VERSION.SDK_INT >= CPermissionUtil.VERSION_R &&
                CPermissionUtil.checkManifestPermission(getContext(), Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    CPermissionUtil.openManageAllFileAppSettings(getContext());
                }else {
                    requestStorageLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                return;
            }
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

    public interface COnCertDigitalSignListener{
        void sign(String certFilePath, String certPassword, String signImagePath);
    }
}


