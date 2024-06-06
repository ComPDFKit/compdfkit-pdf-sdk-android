/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.sliderbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;


public class CSliderBarValueView extends FrameLayout {

    private CValueType valueType = CValueType.Source;

    private AppCompatTextView textView;

    private int value;

    private int maxValue;

    private String valueUnit;

    public CSliderBarValueView(@NonNull Context context) {
        this(context, null);
    }

    public CSliderBarValueView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CSliderBarValueView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView();
    }

    private void initAttr(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CSliderBarValueView);
        if (typedArray != null) {
            int showType = typedArray.getInt(R.styleable.CSliderBarValueView_tools_show_type, 0);
            if (showType == CValueType.Source.id){
                valueType = CValueType.Source;
            }else {
                valueType = CValueType.Percentage;
            }
            typedArray.recycle();
        }
    }

    private void initView(){
        inflate(getContext(), R.layout.tools_slider_bar_value_layout, this);
        textView = findViewById(R.id.tv_title);
    }

    public void setShowType(CValueType valueType){
        this.valueType = valueType;
        setValue(value);
    }

    public void setMaxValue(int maxValue){
        this.maxValue = maxValue;
    }

    public void setValue(int value){
        this.value = value;
        if (textView != null) {
            if (valueType == CValueType.Source){
                textView.setText(value +valueUnit);
            }else {
                int percentage = (int) (((float) value / maxValue) * 100);
                textView.setText(percentage + "%");
            }
        }
    }

    public void setValueUnit(String valueUnit){
        this.valueUnit = valueUnit;
        setValue(value);
    }
}
