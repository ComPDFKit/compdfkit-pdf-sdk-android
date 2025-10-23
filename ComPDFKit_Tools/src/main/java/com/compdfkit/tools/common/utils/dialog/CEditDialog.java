/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;


public class CEditDialog extends CBasicThemeDialogFragment {

    public static final String EXTRA_EDIT_TEXT_INFO = "extra_edit_text_info";

    public static final String EXTRA_TITLE = "extra_dialog_title";

    public static final String EXTRA_IME_OPTIONS = "extra_ime_options";

    private AppCompatTextView tvTitle;

    private AppCompatEditText editText;

    private AppCompatButton btnCancel;

    private AppCompatButton btnAdd;

    private OnEditBookmarkListener editListener;

    private String hintText;

    public static CEditDialog newInstance(String title, String editTextInfo) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        bundle.putString(EXTRA_EDIT_TEXT_INFO, editTextInfo);
        return newInstance(bundle);
    }

    public static CEditDialog newInstance(Bundle bundle) {
        CEditDialog dialog = new CEditDialog();
        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    protected int layoutId() {
        return R.layout.tools_bota_bookmark_input_dialog;
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
        editText = rootView.findViewById(R.id.tv_message);
        tvTitle = rootView.findViewById(R.id.tv_title);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnAdd = rootView.findViewById(R.id.btn_add);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String defaultTitle = getArguments().getString(EXTRA_EDIT_TEXT_INFO);
            String title = getArguments().getString(EXTRA_TITLE, "");
            int imeOptions = getArguments().getInt(EXTRA_IME_OPTIONS, EditorInfo.IME_ACTION_UNSPECIFIED);
            editText.setImeOptions(imeOptions);
            if (imeOptions != EditorInfo.IME_ACTION_UNSPECIFIED){
                editText.setSingleLine(true);
            }
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
