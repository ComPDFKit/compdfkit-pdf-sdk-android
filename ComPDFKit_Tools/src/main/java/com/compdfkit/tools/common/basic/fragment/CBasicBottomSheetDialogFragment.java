/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.basic.fragment;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.dialog.CDialogFragmentUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public abstract class CBasicBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, getStyle());
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        if (fullScreen()) {
            CDialogFragmentUtil.setBottomSheetDialogFragmentFullScreen(getDialog(), behavior);
        }
        if (CViewUtils.isPad(getContext())){
            CDialogFragmentUtil.setDimAmount(getDialog(),0.2F);
        }else {
            CDialogFragmentUtil.setDimAmount(getDialog(), dimAmount());
        }
        behavior.setDraggable(draggable());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId(), container, false);
        onCreateView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewCreate();
    }

    protected abstract @LayoutRes int layoutId();

    protected int getStyle(){
        return R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle;
    }

    protected boolean fullScreen(){
        return false;
    }

    protected float dimAmount(){
        return 0.2F;
    }

    protected boolean draggable(){
        return true;
    }

    protected abstract void onCreateView(View rootView);

    protected abstract void onViewCreate();

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        CDialogFragmentUtil.setDimAmount(getDialog(), dimAmount());
    }
}
