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
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;


public class CGotoPageDialog extends CBasicThemeDialogFragment {

    public static final String EXTRA_EDIT_HINT_TEXT = "extra_edit_hint_text";

    private AppCompatTextView tvTitle;

    private AppCompatEditText editText;

    private Button btnCancel;

    private Button btnAdd;

    private String hintText;

    private int pageCount = 1;

    private COnSetPDFDisplayPageIndexListener pdfDisplayPageIndexListener;

    public static CGotoPageDialog newInstance(String hintText) {
        CGotoPageDialog dialog = new CGotoPageDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_EDIT_HINT_TEXT, hintText);
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
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }
    @Override
    protected void onCreateView(View rootView) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.tools_dialog_background);
        }
        editText = rootView.findViewById(R.id.tv_message);
        tvTitle = rootView.findViewById(R.id.tv_title);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnAdd = rootView.findViewById(R.id.btn_add);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            hintText = getArguments().getString(EXTRA_EDIT_HINT_TEXT);
            editText.setHint(hintText);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        CViewUtils.showKeyboard(editText);
        tvTitle.setText(R.string.tools_enter_a_page_number);
        btnCancel.setOnClickListener(v -> dismiss());
        btnAdd.setOnClickListener(v -> {
            if (null != pdfDisplayPageIndexListener && (!TextUtils.isEmpty(editText.getText()))) {
                int page;
                try {
                    String text = editText.getText().toString();
                    page = Integer.parseInt(text);
                    if (page >= 0) {
                        pdfDisplayPageIndexListener.displayPage(page);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            dismiss();
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    if (!TextUtils.isEmpty(s)) {
                        int page = Integer.parseInt(s.toString());
                        btnAdd.setEnabled(page > 0 && page <= pageCount);
                    } else {
                        btnAdd.setEnabled(false);
                    }
                }catch (Exception e){
                    btnAdd.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

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

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setOnPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.pdfDisplayPageIndexListener = displayPageIndexListener;
    }
}
