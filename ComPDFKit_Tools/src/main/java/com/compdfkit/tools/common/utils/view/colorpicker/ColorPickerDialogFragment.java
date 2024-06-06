/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.colorpicker;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.view.colorpicker.widget.ColorPickerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class ColorPickerDialogFragment extends CBasicBottomSheetDialogFragment {

    private static final String EXTRA_SELECT_COLOR = "extra_select_color";

    private ColorPickerView.COnColorChangeListener colorChangeListener;

    private ColorPickerView.COnColorAlphaChangeListener colorAlphaChangeListener;

    public static ColorPickerDialogFragment newInstance(int selectColor) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_SELECT_COLOR, selectColor);
        ColorPickerDialogFragment fragment = new ColorPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean draggable() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_color_picker_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {

    }

    @Override
    protected void onViewCreate() {
        int selectColor = -1;
        if (getArguments() != null && getArguments().containsKey(EXTRA_SELECT_COLOR)){
            selectColor = getArguments().getInt(EXTRA_SELECT_COLOR, -1);
        }
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.color_picker_fragment);
        if (fragment != null && fragment instanceof CColorPickerFragment){
            CColorPickerFragment colorPickerFragment = (CColorPickerFragment) fragment;
            colorPickerFragment.initColor(selectColor, 255);
            colorPickerFragment.setColorPickerListener(colorChangeListener);
            colorPickerFragment.setColorAlphaChangeListener(colorAlphaChangeListener);
        }
    }

    public void setColorChangeListener(ColorPickerView.COnColorChangeListener colorChangeListener) {
        this.colorChangeListener = colorChangeListener;
    }

    public void setColorAlphaChangeListener(ColorPickerView.COnColorAlphaChangeListener colorAlphaChangeListener) {
        this.colorAlphaChangeListener = colorAlphaChangeListener;
    }
}
