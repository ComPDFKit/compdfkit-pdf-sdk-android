/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
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
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;


public class CAlertDialog extends CBasicThemeDialogFragment {

    public static final String EXTRA_TITLE = "extra_title";

    public static final String EXTRA_MESSAGE = "extra_message";

    public static final String EXTRA_CONFIRM_BUTTON_TEXT = "extra_confirm_button_text";
    public static final String EXTRA_CANCEL_BUTTON_TEXT = "extra_cancel_button_text";
    private AppCompatTextView tvTitle;

    private AppCompatTextView tvMessage;

    private AppCompatButton btnCancel;

    private AppCompatButton btnConfirm;

    private View.OnClickListener cancelListener;

    private View.OnClickListener confirmListener;

    public static CAlertDialog newInstance(String title, String message) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_MESSAGE, message);
        CAlertDialog fragment = new CAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CAlertDialog newInstance(Bundle args) {
        CAlertDialog fragment = new CAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int themeResId() {
        return R.attr.dialogTheme;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_alert_dialog;
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
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvMessage = rootView.findViewById(R.id.tv_message);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnConfirm = rootView.findViewById(R.id.btn_confirm);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCancel.setOnClickListener(cancelListener);
        btnConfirm.setOnClickListener(confirmListener);
        if (cancelListener == null) {
            btnCancel.setVisibility(View.GONE);
        }
        if (getArguments() != null) {
            String title = getArguments().getString(EXTRA_TITLE, "");
            String message = getArguments().getString(EXTRA_MESSAGE, "");
            tvTitle.setText(title);
            tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
            tvMessage.setText(message);

            String confirmBtn = getArguments().getString(EXTRA_CONFIRM_BUTTON_TEXT, getString(R.string.tools_okay));
            String cancelBtn = getArguments().getString(EXTRA_CANCEL_BUTTON_TEXT, getString(R.string.tools_cancel));
            btnConfirm.setText(confirmBtn);
            btnCancel.setText(cancelBtn);
        }
    }

    public void setCancelClickListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmClickListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }
}
