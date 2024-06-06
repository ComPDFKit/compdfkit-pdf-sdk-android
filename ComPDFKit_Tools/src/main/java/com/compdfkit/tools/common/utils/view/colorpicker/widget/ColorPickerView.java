/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.colorpicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBarValueView;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;


public class ColorPickerView extends FrameLayout implements View.OnTouchListener {

    private ColorRectShowView mCurrentIndicatorColorView;

    private ColorRectShowView mLastColorShowView;

    private ColorRectView colorRectView;

    private ColorAlphaSliderView colorOpacitySliderView;

    private CSliderBarValueView alphaValueView;

    private ColorListView colorListView;

    private int color = Color.BLACK;

    private int colorAlpha = 255;

    private int lastColor = Color.WHITE;

    private int lastColorAlpha = 255;

    private COnColorChangeListener onColorChangeListener;

    private COnColorAlphaChangeListener colorAlphaChangeListener;

    private boolean showAlphaSliderBar = true;

    public ColorPickerView(@NonNull Context context) {
        this(context, null);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView(context);
    }

    private void initAttrs(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
        if (typedArray != null) {
            showAlphaSliderBar = typedArray.getBoolean(R.styleable.ColorPickerView_tools_show_alpha_slider_bar, true);
            typedArray.recycle();
        }
    }

    private void initView(Context context){
        inflate(context, R.layout.tools_color_pick_view, this);
        mCurrentIndicatorColorView = findViewById(R.id.id_bottom_customize_cv_indicator);
        mLastColorShowView = findViewById(R.id.id_bottom_customize_cv_last);
        colorRectView = findViewById(R.id.id_bottom_customize_v_color_set);
        colorOpacitySliderView = findViewById(R.id.id_customize_color_alpha_slider);
        alphaValueView = findViewById(R.id.alpha_slider_value_view);
        colorListView = findViewById(R.id.color_list_view);
        alphaValueView.setMaxValue(255);
        alphaValueView.setValue(255);
        if (!showAlphaSliderBar) {
            alphaValueView.setVisibility(GONE);
            colorOpacitySliderView.setVisibility(GONE);
        }
        colorRectView.setColorSelectListener(color1 -> {
            mCurrentIndicatorColorView.changeColor(color1);
            colorOpacitySliderView.setBaseColor(color1);
        });

        colorOpacitySliderView.setColorOpacityChangeListener(opacity -> {
            mCurrentIndicatorColorView.changeAlpha(opacity);
            alphaValueView.setValue(opacity);
            if (colorAlphaChangeListener != null) {
                colorAlphaChangeListener.opacity(opacity);
            }
        });
        colorListView.setOnColorSelectListener(color1 -> {
            colorOpacitySliderView.setBaseColor(color1);
            colorOpacitySliderView.setPercent(1);
            mCurrentIndicatorColorView.changeColor(color1);
            mCurrentIndicatorColorView.changeAlpha(255);
            alphaValueView.setValue(255);
            colorRectView.setColor(color1);
            if (onColorChangeListener != null) {
                onColorChangeListener.color(color1);
            }
        });
        colorRectView.setOnTouchListener(this::onTouch);
    }

    public void initColor(@ColorInt int color, int colorAlpha){
        this.color = color;
        this.colorAlpha = colorAlpha;
        this.lastColor = color;
        this.lastColorAlpha = colorAlpha;
        updateColorView();
    }

    public void initColor(@ColorInt int color){
        this.color = color;
        this.lastColor = color;
        updateColorView();
    }

    public void initOpacity(int colorAlpha){
        this.colorAlpha = colorAlpha;
        this.lastColorAlpha = colorAlpha;
        updateColorView();
    }

    public void setShowAlphaSliderBar(boolean show){
        colorOpacitySliderView.setVisibility(show ? VISIBLE : GONE);
        alphaValueView.setVisibility(show ? VISIBLE : GONE);
    }


    public void refreshColor(){
        initColor(this.color, this.colorAlpha);
    }


    private void updateColorView(){
        colorRectView.setColor(color);
        colorOpacitySliderView.setBaseColor(color);
        colorOpacitySliderView.setPercent(colorAlpha / 255F);
        mCurrentIndicatorColorView.changeColor(color);
        mCurrentIndicatorColorView.changeAlpha(colorAlpha);
        colorOpacitySliderView.invalidate();
        mLastColorShowView.changeAlpha(lastColorAlpha);
        mLastColorShowView.changeColor(lastColor);
        alphaValueView.setValue(colorAlpha);
        colorListView.setSelectColor(color);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP){
            if (onColorChangeListener != null) {
                onColorChangeListener.color(mCurrentIndicatorColorView.getColor());
            }
            colorListView.setSelectColor(mCurrentIndicatorColorView.getColor());
        }
        return false;
    }

    public void setColorChangeListener(COnColorChangeListener colorChangeListener) {
        this.onColorChangeListener = colorChangeListener;
    }

    public void setColorAlphaChangeListener(COnColorAlphaChangeListener colorAlphaChangeListener) {
        this.colorAlphaChangeListener = colorAlphaChangeListener;
    }

    public interface COnColorChangeListener{
        void color(int color);
    }

    public interface COnColorAlphaChangeListener{
        void opacity(int opacity);
    }
}
