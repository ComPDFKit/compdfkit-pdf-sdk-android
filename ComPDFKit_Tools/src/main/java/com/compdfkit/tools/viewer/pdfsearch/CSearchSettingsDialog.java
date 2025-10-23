/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfsearch;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.interfaces.COnDialogDismissListener;
import com.compdfkit.tools.common.views.pdfproperties.CPropertiesSwitchView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class CSearchSettingsDialog extends CBasicBottomSheetDialogFragment {

    private boolean ignoreCase = true;

    private boolean wholeWordsOnly = false;

    private AppCompatTextView tvTitle;

    private AppCompatImageView ivBack;

    private AppCompatImageView ivClose;

    private CPropertiesSwitchView swIgnoreCase;

    private CPropertiesSwitchView swWholeWordsOnly;

    private CompoundButton.OnCheckedChangeListener ignoreCaseCheckedChangeListener;

    private CompoundButton.OnCheckedChangeListener wholeWordsOnlyCheckedChangeListener;

    private COnDialogDismissListener dialogDismissListener;

    @Override
    protected int themeResId() {
        return  R.attr.compdfkit_BottomSheetDialog_Transparent_Theme;
    }

    @Override
    protected float dimAmount() {
        return 0.2F;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setDraggable(false);
    }

    public static CSearchSettingsDialog newInstance(boolean ignoreCase, boolean wholeWordsOnly) {
        Bundle args = new Bundle();
        args.putBoolean("extra_ignore_case", ignoreCase);
        args.putBoolean("extra_whole_words_only", wholeWordsOnly);
        CSearchSettingsDialog fragment = new CSearchSettingsDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_search_settings_dialog;
    }

    @Override
    protected void onCreateView(View rootView) {
        tvTitle = rootView.findViewById(R.id.tv_tool_bar_title);
        ivBack = rootView.findViewById(R.id.iv_tool_bar_previous);
        ivClose = rootView.findViewById(R.id.iv_tool_bar_close);
        swIgnoreCase = rootView.findViewById(R.id.sw_ignore_case);
        swWholeWordsOnly = rootView.findViewById(R.id.sw_whole_words_only);

        tvTitle.setText(R.string.tools_setting);
        ivBack.setVisibility(View.GONE);
        ivClose.setOnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    protected void onViewCreate() {
        if (getArguments() != null) {
            ignoreCase = getArguments().getBoolean("extra_ignore_case", true);
            wholeWordsOnly = getArguments().getBoolean("extra_whole_words_only", false);
        }
        swIgnoreCase.setChecked(ignoreCase);
        swWholeWordsOnly.setChecked(wholeWordsOnly);

        swIgnoreCase.setListener(ignoreCaseCheckedChangeListener);
        swWholeWordsOnly.setListener(wholeWordsOnlyCheckedChangeListener);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dialogDismissListener != null) {
            dialogDismissListener.dismiss();
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dialogDismissListener != null) {
            dialogDismissListener.dismiss();
        }
    }

    public void setIgnoreCaseCheckedChangeListener(CompoundButton.OnCheckedChangeListener ignoreCaseCheckedChangeListener) {
        this.ignoreCaseCheckedChangeListener = ignoreCaseCheckedChangeListener;
    }

    public void setWholeWordsOnlyCheckedChangeListener(CompoundButton.OnCheckedChangeListener wholeWordsOnlyCheckedChangeListener) {
        this.wholeWordsOnlyCheckedChangeListener = wholeWordsOnlyCheckedChangeListener;
    }

    public void setDialogDismissListener(COnDialogDismissListener dialogDismissListener) {
        this.dialogDismissListener = dialogDismissListener;
    }
}
