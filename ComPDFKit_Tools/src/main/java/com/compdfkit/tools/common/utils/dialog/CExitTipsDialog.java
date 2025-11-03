/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;


public class CExitTipsDialog extends CBasicThemeDialogFragment {


    private AppCompatButton btnCancel;

    private AppCompatButton btnConfirm;
    private AppCompatButton btnContinue;

    private View.OnClickListener cancelListener;

    private View.OnClickListener confirmListener;

    private View.OnClickListener continueListener;

    public static CExitTipsDialog newInstance() {
        CExitTipsDialog fragment = new CExitTipsDialog();
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_exit_tips_dialog;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
    @Override
    protected void onCreateView(View rootView) {
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnConfirm = rootView.findViewById(R.id.btn_confirm);
        btnContinue = rootView.findViewById(R.id.btn_continue);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCancel.setOnClickListener(cancelListener);
        btnConfirm.setOnClickListener(confirmListener);
        btnContinue.setOnClickListener(continueListener);
    }

    public void setCancelClickListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmClickListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setContinueClickListener(OnClickListener continueListener) {
        this.continueListener = continueListener;
    }
}
