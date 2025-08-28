/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.basic.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnDialogDismissListener;
import com.compdfkit.tools.common.utils.dialog.CDialogFragmentUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public abstract class CBasicBottomSheetDialogFragment extends BottomSheetDialogFragment {

    protected COnDialogDismissListener dismissListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int styleId = CViewUtils.getThemeStyle(getContext(), themeResId());
        if (styleId != 0){
            setStyle(STYLE_NORMAL, styleId);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        if (fullScreen()) {
            CDialogFragmentUtil.setBottomSheetDialogFragmentFullScreen(requireActivity(), getDialog(), behavior);
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
        themedContext = new ContextThemeWrapper(getContext(), getStyle());
        LayoutInflater themedInflater = inflater.cloneInContext(themedContext);
        View view = themedInflater.inflate(layoutId(), container, false);
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
        return CViewUtils.getThemeStyle(getContext(), themeResId());
    }

    protected int themeResId(){
        return R.attr.compdfkit_BottomSheetDialog_Theme;
    }
    protected Context themedContext;

    @Nullable
    @Override
    public Context getContext() {
        return themedContext != null ? themedContext : super.getContext();
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

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dismissListener != null) {
            dismissListener.dismiss();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null){
            dismissListener.dismiss();
        }
    }

    public void setDismissListener(COnDialogDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }
}
