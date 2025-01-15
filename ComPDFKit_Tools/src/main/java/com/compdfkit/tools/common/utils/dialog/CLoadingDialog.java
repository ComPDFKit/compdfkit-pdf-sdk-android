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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;


public class CLoadingDialog extends CBasicThemeDialogFragment {

    public static CLoadingDialog newInstance() {
        Bundle args = new Bundle();
        CLoadingDialog fragment = new CLoadingDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int themeResId() {
        return R.attr.dialogTheme;
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(false);
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_loading_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
