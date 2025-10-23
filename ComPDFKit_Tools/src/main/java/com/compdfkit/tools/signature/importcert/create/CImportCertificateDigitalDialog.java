/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.importcert.create;


import static com.compdfkit.tools.common.utils.CFileUtils.CERTIFICATE_DIGITAL_TYPE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.signature.interfaces.COnSelectCertFileListener;

import java.io.File;

public class CImportCertificateDigitalDialog extends CBasicBottomSheetDialogFragment implements View.OnClickListener {

    public static final String EXTRA_CERTIFICATE_DIGITAL_URI = "extra_certificate_digital_uri";

    private AppCompatTextView tvTitle;

    private AppCompatEditText etPassword;

    private AppCompatImageView ivClearPassword;

    private AppCompatButton btnOk;

    private AppCompatTextView tvCertificateDigitalName;

    private AppCompatTextView tvPasswordError;

    private Uri certificateUri = null;

    private COnSelectCertFileListener selectCertFileListener;

    private ActivityResultLauncher<Intent> selectCertificateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null && result.getData().getData() != null) {
            Uri certificateUri = result.getData().getData();
            CFileUtils.takeUriPermission(getContext(), certificateUri);
            setCertificateInfo(certificateUri);
            if (getArguments() != null) {
                getArguments().putParcelable(EXTRA_CERTIFICATE_DIGITAL_URI, certificateUri);
            }
        }
    });


    public static CImportCertificateDigitalDialog newInstance(Uri certificateUri) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CERTIFICATE_DIGITAL_URI, certificateUri);
        CImportCertificateDigitalDialog fragment = new CImportCertificateDigitalDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_sign_import_certificate_digital_id_fragment;
    }

    @Override
    protected void onCreateView(View view) {
        tvTitle = view.findViewById(R.id.tv_tool_bar_title);
        AppCompatImageView ivClose = view.findViewById(R.id.iv_tool_bar_close);
        ConstraintLayout clCertificate = view.findViewById(R.id.cl_certificate);
        tvCertificateDigitalName = view.findViewById(R.id.tv_certificate);
        etPassword = view.findViewById(R.id.et_password);
        ivClearPassword = view.findViewById(R.id.iv_remove_password);
        tvPasswordError = view.findViewById(R.id.tv_password_error);
        btnOk = view.findViewById(R.id.btn_ok);
        ivClose.setOnClickListener(this);
        ivClearPassword.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        clCertificate.setOnClickListener(this);
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyCertificate();
                return true;
            }
            return false;
        });
    }


    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    protected int themeResId() {
        return R.attr.compdfkit_BottomSheetDialog_Transparent_Theme;
    }

    @Override
    protected boolean draggable() {
        return false;
    }

    @Override
    protected void onViewCreate() {
        Uri uri = getArguments().getParcelable(EXTRA_CERTIFICATE_DIGITAL_URI);
        if (uri != null) {
            setCertificateInfo(uri);
        }
        tvTitle.setText(R.string.tools_add_a_signature_field);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClearPassword.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
                if (TextUtils.isEmpty(s) && tvPasswordError.getVisibility() == View.VISIBLE) {
                    tvPasswordError.setVisibility(View.GONE);
                }
                btnOk.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_tool_bar_close) {
            dismiss();
        } else if (v.getId() == R.id.iv_remove_password) {
            etPassword.setText("");
        } else if (v.getId() == R.id.btn_ok) {
            verifyCertificate();
        } else if (v.getId() == R.id.cl_certificate) {
            selectCertificateLauncher.launch(CFileUtils.getIntent(CERTIFICATE_DIGITAL_TYPE));
        }
    }

    private void setCertificateInfo(Uri uri) {
        certificateUri = uri;
        String idName = CUriUtil.getUriFileName(getContext(), uri);
        tvCertificateDigitalName.setText(idName);
        etPassword.setText("");
        etPassword.requestFocus();
        CViewUtils.showKeyboard(etPassword);
    }

    private void verifyCertificate() {
        CViewUtils.hideKeyboard(etPassword);
        if (TextUtils.isEmpty(etPassword.getText())) {
            tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }
        String password = etPassword.getText().toString();
        String dir = new File(getContext().getCacheDir(), CFileUtils.CACHE_FOLDER + File.separator + "certFile").getAbsolutePath();
        String certPath = CFileUtils.copyFileToInternalDirectory(getContext(), certificateUri, dir, CUriUtil.getUriFileName(getContext(), certificateUri));
        if (!CPDFSignature.checkPKCS12Password(certPath, password)) {
            tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }
        if (selectCertFileListener != null && certificateUri != null) {
            selectCertFileListener.certificateFile(certPath, password);
        }
    }

    public void setSelectCertFileListener(COnSelectCertFileListener selectCertFileListener) {
        this.selectCertFileListener = selectCertFileListener;
    }
}
