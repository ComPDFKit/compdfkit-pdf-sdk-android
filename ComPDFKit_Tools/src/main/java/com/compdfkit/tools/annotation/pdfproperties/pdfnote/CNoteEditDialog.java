/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfnote;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;
import com.compdfkit.tools.common.interfaces.COnDialogDismissListener;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;


public class CNoteEditDialog extends CBasicThemeDialogFragment {

    public static final String EXTRA_NOTE_CONTENT = "extra_note_content";

    private AppCompatImageView ivSave;

    private AppCompatImageView ivDelete;

    private AppCompatEditText etContent;

    private View.OnClickListener deleteListener;

    private View.OnClickListener saveListener;

    private COnDialogDismissListener dialogCancelListener;

    private DialogDismiss dialogDismissListener;

    public static CNoteEditDialog newInstance(String content) {
        Bundle args = new Bundle();
        args.putString(EXTRA_NOTE_CONTENT, content);
        CNoteEditDialog fragment = new CNoteEditDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateLayoutParams();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLayoutParams();
    }

    private void updateLayoutParams(){
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            window.setDimAmount(0.4F);
            WindowManager.LayoutParams attr = window.getAttributes();
            attr.width = CDimensUtils.getMinWidthOrHeight(getContext()) * 4/ 5;
            attr.height = CDimensUtils.getScreenHeight(getContext()) /2;
            window.setAttributes(attr);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_note_edit_dialog;
    }

    @Override
    protected void onCreateView(View rootView) {
        ivSave = rootView.findViewById(R.id.id_note_save);
        ivDelete = rootView.findViewById(R.id.id_note_delete);
        etContent  = rootView.findViewById(R.id.id_note_content);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivSave.setOnClickListener(saveListener);
        ivDelete.setOnClickListener(deleteListener);
        if (getArguments() != null) {
            String content = getArguments().getString(EXTRA_NOTE_CONTENT);
            etContent.setText(content);
            etContent.setSelection(content.length());
            CViewUtils.showKeyboard(etContent);
        }
    }

    public void setDeleteListener(View.OnClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setSaveListener(View.OnClickListener saveListener) {
        this.saveListener = saveListener;
    }

    public void setDismissListener(DialogDismiss dissmissListener) {
        this.dialogDismissListener = dissmissListener;
    }

    public String getContent(){
        return etContent.getText() != null ? etContent.getText().toString() : "";
    }

    @Override
    public void dismiss() {
        CViewUtils.hideKeyboard(etContent);
        super.dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (TextUtils.isEmpty(etContent.getText())) {
            deleteListener.onClick(ivDelete);
        }
        if (dialogCancelListener != null) {
            dialogCancelListener.dismiss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), getTheme()){
            @Override
            public void cancel() {
                if (getActivity() != null && getView() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }
                super.cancel();
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setDialogCancelListener(COnDialogDismissListener dialogCancelListener) {
        this.dialogCancelListener = dialogCancelListener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (dialogDismissListener != null) {
            dialogDismissListener.onDialogDismiss();
        }
        super.onDismiss(dialog);
    }

    public interface DialogDismiss {
        void onDialogDismiss();
    }
}
