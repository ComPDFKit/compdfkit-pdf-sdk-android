/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.preview.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;

public class CSignStyleReasonView extends FrameLayout {

    private Switch swReason;

    private RadioGroup rgReason;

    private View viewLine;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    private COnSelectReasonListener selectReasonListener;

    private String selectReason;

    public CSignStyleReasonView(@NonNull Context context) {
        this(context, null);
    }

    public CSignStyleReasonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CSignStyleReasonView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        inflate(context, R.layout.tools_sign_style_preview_reason, this);
        swReason = findViewById(R.id.sw_reason);
        rgReason = findViewById(R.id.rg_reason);
        viewLine = findViewById(R.id.view_line);
        swReason.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rgReason.setVisibility(isChecked ? VISIBLE : GONE);
            viewLine.setVisibility(isChecked ? VISIBLE : GONE);
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
            }
        });
        selectReason = context.getString(R.string.tools_i_am_the_owner_of_the_document);
        rgReason.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_approving){
                selectReason = context.getString(R.string.tools_i_am_approving_the_document);
            } else if (checkedId == R.id.rb_have_reviewed) {
                selectReason = context.getString(R.string.tools_i_have_reviewed_this_document);
            } else if (checkedId == R.id.rb_owner) {
                selectReason = context.getString(R.string.tools_i_am_the_owner_of_the_document);
            } else if (checkedId == R.id.rb_none){
                selectReason = context.getString(R.string.tools_none);
            }
            if (selectReasonListener != null) {
                selectReasonListener.reason(selectReason);
            }
        });
    }



    public String getReason(){
        return selectReason;
    }

    public boolean isEnableReason(){
        return swReason.isChecked();
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setSelectReasonListener(COnSelectReasonListener selectReasonListener) {
        this.selectReasonListener = selectReasonListener;
    }

    public interface COnSelectReasonListener{
        void reason(String reason);
    }
}
