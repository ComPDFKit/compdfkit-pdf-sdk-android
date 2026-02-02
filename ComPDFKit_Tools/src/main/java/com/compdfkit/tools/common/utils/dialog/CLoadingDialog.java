/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
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
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;


public class CLoadingDialog extends CBasicThemeDialogFragment {

    public static CLoadingDialog newInstance() {
        Bundle args = new Bundle();
        CLoadingDialog fragment = new CLoadingDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CLoadingDialog newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("extra_title", title);
        CLoadingDialog fragment = new CLoadingDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(false);
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_loading_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        AppCompatTextView tvTitle = rootView.findViewById(R.id.tv_title);
        if (getArguments() != null){
            String title = getArguments().getString("extra_title");
            if (!TextUtils.isEmpty(title)){
                tvTitle.setText(title);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
