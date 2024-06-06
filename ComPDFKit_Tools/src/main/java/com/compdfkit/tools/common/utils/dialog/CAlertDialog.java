/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.compdfkit.tools.R;


public class CAlertDialog extends DialogFragment {

    public static final String EXTRA_TITLE = "extra_title";

    public static final String EXTRA_MESSAGE = "extra_message";

    public static final String EXTRA_CONFIRM_BUTTON_TEXT = "extra_confirm_button_text";
    public static final String EXTRA_CANCEL_BUTTON_TEXT = "extra_cancel_button_text";
    private AppCompatTextView tvTitle;

    private AppCompatTextView tvMessage;

    private Button btnCancel;

    private Button btnConfirm;

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
        View rootView = inflater.inflate(R.layout.tools_alert_dialog, container,false);
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvMessage = rootView.findViewById(R.id.tv_message);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnConfirm = rootView.findViewById(R.id.btn_confirm);
        return rootView;
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
