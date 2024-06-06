/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;


public class CEditDialog extends DialogFragment {

    public static final String EXTRA_EDIT_TEXT_INFO = "extra_edit_text_info";

    public static final String EXTRA_TITLE = "extra_dialog_title";

    private AppCompatTextView tvTitle;

    private AppCompatEditText editText;

    private Button btnCancel;

    private Button btnAdd;

    private OnEditBookmarkListener editListener;

    private String hintText;

    public static CEditDialog newInstance(String title, String editTextInfo) {
        CEditDialog dialog = new CEditDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        bundle.putString(EXTRA_EDIT_TEXT_INFO, editTextInfo);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.tools_dialog_theme);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.tools_dialog_background);
        }
        View rootView = inflater.inflate(R.layout.tools_bota_bookmark_input_dialog, container, false);
        editText = rootView.findViewById(R.id.tv_message);
        tvTitle = rootView.findViewById(R.id.tv_title);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnAdd = rootView.findViewById(R.id.btn_add);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String defaultTitle = getArguments().getString(EXTRA_EDIT_TEXT_INFO);
            String title = getArguments().getString(EXTRA_TITLE, "");
            editText.setText(defaultTitle);
            editText.setSelection(defaultTitle.length());
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(hintText)) {
            editText.setHint(hintText);
        }
        CViewUtils.showKeyboard(editText);
        btnCancel.setOnClickListener(v -> dismiss());
        btnAdd.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(editText.getText())) {
                String title = editText.getText().toString().trim();
                if (editListener != null) {
                    editListener.edit(title);
                }
            }
        });
    }

    public void setHint(String hint){
        this.hintText = hint;
        if (editText != null){
            editText.setHint(hint);
        }
    }

    @Override
    public void dismiss() {
        CViewUtils.hideKeyboard(getDialog());
        super.dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override public void cancel() {
                if (getActivity() != null && getView() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }
                super.cancel();
            }
        };
    }

    public void setEditListener(OnEditBookmarkListener editListener) {
        this.editListener = editListener;
    }

    public interface OnEditBookmarkListener {
        void edit(String title);
    }
}
