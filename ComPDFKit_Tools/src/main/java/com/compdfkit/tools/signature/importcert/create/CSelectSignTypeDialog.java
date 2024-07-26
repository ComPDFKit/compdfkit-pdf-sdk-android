/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.importcert.create;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

public class CSelectSignTypeDialog extends DialogFragment implements View.OnClickListener {

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, CViewUtils.getThemeAttrResourceId(getContext().getTheme(), R.attr.dialogTheme));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.tools_dialog_background);
        }
        View view = inflater.inflate(R.layout.tools_sign_select_sign_type_dialog, container, false);
        rgType = view.findViewById(R.id.rg_type);
        AppCompatButton btnCancel = view.findViewById(R.id.btn_cancel);
        AppCompatButton btnDone = view.findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        return view;
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


