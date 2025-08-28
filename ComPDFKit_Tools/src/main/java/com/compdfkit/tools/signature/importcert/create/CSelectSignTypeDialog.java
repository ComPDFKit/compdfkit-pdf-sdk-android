/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.importcert.create;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;

public class CSelectSignTypeDialog extends CBasicThemeDialogFragment implements View.OnClickListener {

    public enum SignatureType{

        ElectronicSignature,

        DigitalSignature;
    }

    private RadioGroup rgType;

    private CSelectSignaturesTypeListener selectSignaturesTypeListener;

    public static CSelectSignTypeDialog newInstance() {
        Bundle args = new Bundle();
        CSelectSignTypeDialog fragment = new CSelectSignTypeDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int themeResId() {
        return  com.google.android.material.R.attr.dialogTheme;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_sign_select_sign_type_dialog;
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
            if (selectSignaturesTypeListener != null) {
                selectSignaturesTypeListener.signatureType(rgType.getCheckedRadioButtonId() == R.id.rb_electronic_signature ? SignatureType.ElectronicSignature : SignatureType.DigitalSignature);
            }
        }
    }

    public void setSelectSignaturesTypeListener(CSelectSignaturesTypeListener selectSignaturesTypeListener) {
        this.selectSignaturesTypeListener = selectSignaturesTypeListener;
    }

    public interface CSelectSignaturesTypeListener{
        void signatureType(SignatureType type);
    }
}


