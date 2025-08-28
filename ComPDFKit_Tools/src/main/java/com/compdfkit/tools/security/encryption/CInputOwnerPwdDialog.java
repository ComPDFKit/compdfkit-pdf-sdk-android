/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.encryption;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;


public class CInputOwnerPwdDialog extends CBasicThemeDialogFragment {

    public static final String EXTRA_TITLE = "extra_title";

    private AppCompatButton btnCancel;

    private AppCompatButton btnConfirm;

    private AppCompatEditText etPassword;

    private View.OnClickListener cancelListener;

    private COwnerPasswordListener ownerPasswordListener;

    private CPDFDocument document;

    private AppCompatImageView ivPasswordVisible;

    private AppCompatTextView tvPasswordError;

    private AppCompatTextView tvTitle;

    public void setDocument(CPDFDocument document) {
        this.document = document;
    }

    public static CInputOwnerPwdDialog newInstance() {
        Bundle args = new Bundle();
        CInputOwnerPwdDialog fragment = new CInputOwnerPwdDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CInputOwnerPwdDialog newInstance(Bundle bundle) {
        CInputOwnerPwdDialog fragment = new CInputOwnerPwdDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int themeResId() {
        return  com.google.android.material.R.attr.dialogTheme;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_cpdf_security_input_owner_pwd_dialog;
    }

    @Override
    protected void onCreateView(View rootView) {
        etPassword = rootView.findViewById(R.id.et_password);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnConfirm = rootView.findViewById(R.id.btn_confirm);
        ivPasswordVisible = rootView.findViewById(R.id.iv_input_visible);
        tvPasswordError = rootView.findViewById(R.id.tv_pwd_error);
        tvTitle = rootView.findViewById(R.id.tv_title);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String title = getArguments().getString(EXTRA_TITLE, getString(R.string.tools_enter_owner_pwd_title));
            if (!TextUtils.isEmpty(title)){
                tvTitle.setText(title);
            }
        }
        btnConfirm.setEnabled(false);
        btnCancel.setOnClickListener(cancelListener);
        btnConfirm.setOnClickListener(v -> {
            if (ownerPasswordListener != null && !TextUtils.isEmpty(etPassword.getText())) {
                String password = etPassword.getText().toString();
                if (document.checkOwnerPassword(password)) {
                    ownerPasswordListener.result(password);
                }else {
                    tvPasswordError.setVisibility(View.VISIBLE);
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPasswordError.setVisibility(View.INVISIBLE);
                btnConfirm.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivPasswordVisible.setOnClickListener(v -> {
            ivPasswordVisible.setSelected(!ivPasswordVisible.isSelected());
            etPassword.setInputType(ivPasswordVisible.isSelected() ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    public void setCancelClickListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmClickListener(COwnerPasswordListener ownerPasswordListener) {
        this.ownerPasswordListener = ownerPasswordListener;
    }

    public interface COwnerPasswordListener {
        void result(String ownerPassword);
    }
}
