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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;


public class CLoadingDialog extends DialogFragment {

    public static CLoadingDialog newInstance() {

        Bundle args = new Bundle();

        CLoadingDialog fragment = new CLoadingDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int themeId = CViewUtils.getThemeAttrResourceId(getContext().getTheme(), R.attr.dialogTheme);
        if (themeId == 0){
            themeId = R.style.ComPDFKit_Theme_Dialog;
        }
        setStyle(STYLE_NO_TITLE, themeId);
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(false);
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.tools_dialog_background);
        }
        View rootView = inflater.inflate(R.layout.tools_loading_dialog_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
