/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.compdfkit.tools.R;


public class CCommonInputDialog extends AppCompatDialog {

    private String title;

    private OnCancelClickListener cancelClickListener;

    private OnInputCompleteListener onInputCompleteListener;

    private TextView tvTitle;

    private TextView tvMessage;

    private String messageStrTv;

    private EditText etMessage;

    private Button btnCancel;

    private Button btnConfirm;

    public CCommonInputDialog(Context context) {
        super(context);
        initView(context);
    }

    public CCommonInputDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    protected CCommonInputDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = View.inflate(context, R.layout.tools_common_input_dialog, null);
        tvTitle = rootView.findViewById(R.id.common_input_dialog_title);
        tvMessage = rootView.findViewById(R.id.common_input_dialog_message);
        etMessage = rootView.findViewById(R.id.common_input_dialog_et);
        btnCancel = rootView.findViewById(R.id.common_input_dialog_cancel);
        btnConfirm = rootView.findViewById(R.id.common_input_dialog_ok);
        btnCancel.setOnClickListener(v -> {
            if (cancelClickListener != null) {
                cancelClickListener.click();
            }
        });
        btnConfirm.setOnClickListener(v -> {
            if (onInputCompleteListener != null) {
                onInputCompleteListener.getInputText(etMessage.getText().toString());
            }
        });
        setContentView(rootView);
    }

    private void initVisibility() {
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        tvMessage.setVisibility(TextUtils.isEmpty(messageStrTv) ? View.GONE : View.VISIBLE);
        btnCancel.setVisibility(cancelClickListener == null ? View.GONE : View.VISIBLE);
    }

    public void setInputType(int type) {
        etMessage.setInputType(type);
    }

    public CCommonInputDialog setTitle(String titleText) {
        this.title = titleText;
        tvTitle.setText(titleText);
        return this;
    }

    public void setMessage(String message) {
        this.messageStrTv = message;
        tvMessage.setText(message);
    }

    public void setHintInputText(String hintMessageText) {
        etMessage.setHint(hintMessageText);
    }

    public void setEditTextValue(String value){
        etMessage.setText(value);
    }

    public void setCancelCallback(String cancelText, OnCancelClickListener cancelClickListener) {
        btnCancel.setText(cancelText);
        this.cancelClickListener = cancelClickListener;
    }

    public void setConfirmCallback(String confirmText, OnInputCompleteListener onInputCompleteListener) {
        btnConfirm.setText(confirmText);
        this.onInputCompleteListener = onInputCompleteListener;
    }

    public EditText getEtMessage() {
        return etMessage;
    }

    public Button getBtnConfirm() {
        return btnConfirm;
    }

    @Override
    public void show() {
        initVisibility();
        super.show();
    }

    public interface OnInputCompleteListener {
        void getInputText(String text);
    }

    public interface OnCancelClickListener {
        void click();
    }
}
