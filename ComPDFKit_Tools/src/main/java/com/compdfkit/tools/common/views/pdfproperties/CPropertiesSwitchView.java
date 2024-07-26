/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */


package com.compdfkit.tools.common.views.pdfproperties;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;


public class CPropertiesSwitchView extends FrameLayout {

    private Switch switchCompat;

    private AppCompatTextView title;

    public CPropertiesSwitchView(@NonNull Context context) {
        this(context, null);
    }

    public CPropertiesSwitchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPropertiesSwitchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CPropertiesSwitchView);
        if (typedArray != null) {
            String titleStr = typedArray.getString(R.styleable.CPropertiesSwitchView_android_title);
            if (!TextUtils.isEmpty(titleStr)){
                title.setText(titleStr);
            }
            boolean checked = typedArray.getBoolean(R.styleable.CPropertiesSwitchView_android_checked, false);
            switchCompat.setChecked(checked);
            typedArray.recycle();
        }
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tools_properties_switch_view_layout, this);
        switchCompat = findViewById(R.id.switch_view);
        title = findViewById(R.id.tv_switch_title);
    }

    public void setChecked(boolean checked){
        if (switchCompat != null) {
            switchCompat.setChecked(checked);
        }
    }

    public void setTitle(String value){
        if (title != null) {
            title.setText(value);
        }
    }

    public void setListener(CompoundButton.OnCheckedChangeListener checkedChangeListener){
        if (checkedChangeListener != null) {
            switchCompat.setOnCheckedChangeListener(checkedChangeListener);
        }
    }
}
