/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class CAddStampSwichDialogFragment  extends CBasicBottomSheetDialogFragment {

    private View.OnClickListener addImageStampClickListener;

    private View.OnClickListener addTextStampClickListener;

    private FloatingActionButton fab;

    public static CAddStampSwichDialogFragment newInstance() {
        Bundle args = new Bundle();
        CAddStampSwichDialogFragment fragment = new CAddStampSwichDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getStyle() {
        return R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle_TopCorners;
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setWindowAnimations(R.style.tools_fade_anim_dialog_style);
        }
    }


    @Override
    protected int layoutId() {
        return R.layout.tools_properties_stamp_add_stamp_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        AppCompatImageView ivAddImageStamp = rootView.findViewById(R.id.iv_add_image_stamp);
        AppCompatImageView ivAddTextStamp = rootView.findViewById(R.id.iv_add_text_stamp);
        fab = rootView.findViewById(R.id.fab_add_custom_stamp);
        fab.setOnClickListener(v -> {
            dismiss();
        });
        ivAddTextStamp.setOnClickListener(addTextStampClickListener);
        ivAddImageStamp.setOnClickListener(addImageStampClickListener);
        rootView.findViewById(R.id.cl_rootView).setOnClickListener(v -> dismiss());
        fabAnim(true);
    }

    @Override
    protected void onViewCreate() {

    }

    private void fabAnim(boolean enter){
        ObjectAnimator objectAnimator;
        if (enter){
            objectAnimator = ObjectAnimator.ofFloat(fab, "rotation", 0F, 45F);
        }else {
            objectAnimator = ObjectAnimator.ofFloat(fab, "rotation", 45F, 0F);
        }
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        objectAnimator.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        fabAnim(false);
    }

    public void setAddImageStampClickListener(View.OnClickListener addImageStampClickListener) {
        this.addImageStampClickListener = addImageStampClickListener;
    }

    public void setAddTextStampClickListener(View.OnClickListener addTextStampClickListener) {
        this.addTextStampClickListener = addTextStampClickListener;
    }
}
