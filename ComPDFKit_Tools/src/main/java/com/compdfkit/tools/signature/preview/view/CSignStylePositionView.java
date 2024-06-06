/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
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
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnTextChangedListener;
import com.compdfkit.tools.common.utils.view.CEditText;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;


public class CSignStylePositionView extends FrameLayout {

    private Switch swPosition;

    private CEditText etPosition;

    private View line;

    private COnTextChangedListener textChangedListener;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener;

    public CSignStylePositionView(@NonNull Context context) {
        this(context, null);
    }

    public CSignStylePositionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CSignStylePositionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_sign_style_preview_position, this);
        line = findViewById(R.id.view_line);
        swPosition = findViewById(R.id.sw_position);
        etPosition = findViewById(R.id.et_position);
        swPosition.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etPosition.setVisibility(isChecked ? VISIBLE : GONE);
            line.setVisibility(isChecked ? VISIBLE : GONE);
            if (!isChecked){
                hideKeyboard();
                etPosition.setText("");
            }
            if (checkedChangeListener != null) {
                checkedChangeListener.onCheckedChanged(buttonView, isChecked);
            }
        });
        etPosition.addTextChangedListener((s, start, before, count) -> {
            if (textChangedListener != null) {
                textChangedListener.onTextChanged(s, start, before, count);
            }
        });
    }

    public boolean isEnablePosition() {
        return swPosition.isChecked();
    }

    public String getPosition() {
        return etPosition.getText();
    }

    public void hideKeyboard(){
        CViewUtils.hideKeyboard(etPosition);
    }

    public void setCheckedChangeListener(CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    public void setTextChangedListener(COnTextChangedListener textChangedListener) {
        this.textChangedListener = textChangedListener;
    }

}
