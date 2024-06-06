/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.sliderbar;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;

public class CSliderBar extends FrameLayout implements SeekBar.OnSeekBarChangeListener {

    private AppCompatTextView tvTitle;

    private AppCompatSeekBar seekBar;

    private CSliderBarValueView sliderBarValueView;

    private boolean showTitle;

    private String title;

    private String valueUnit;

    private int sliderBarMaxValue;

    private int sliderBarMinValue;

    private CValueType showType = CValueType.Source;

    private boolean showValueTips = true;

    private OnProgressChangeListener changeListener;

    public CSliderBar(@NonNull Context context) {
        this(context, null);
    }

    public CSliderBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CSliderBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttr(context, attrs);

    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CSliderBar);
        if (typedArray != null) {
            showTitle = typedArray.getBoolean(R.styleable.CSliderBar_tools_show_title, true);
            title = typedArray.getString(R.styleable.CSliderBar_android_title);
            sliderBarMinValue = typedArray.getInt(R.styleable.CSliderBar_android_min, 0);
            showValueTips = typedArray.getBoolean(R.styleable.CSliderBar_tools_slider_bar_show_value, true);
            sliderBarMaxValue = typedArray.getInt(R.styleable.CSliderBar_android_max, 100);
            valueUnit = typedArray.getString(R.styleable.CSliderBar_tools_value_unit);
            int progress = typedArray.getInt(R.styleable.CSliderBar_android_progress, 0);
            int showTypeEnum = typedArray.getInt(R.styleable.CSliderBar_tools_slider_bar_value_show_type, CValueType.Source.id);
            if (showTypeEnum == 0) {
                showType = CValueType.Source;
            } else {
                showType = CValueType.Percentage;
            }
            tvTitle.setVisibility(showTitle ? VISIBLE : GONE);
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
            sliderBarValueView.setValueUnit(valueUnit == null ? "" : valueUnit);
            sliderBarValueView.setShowType(showType);
            sliderBarValueView.setVisibility(showValueTips ? VISIBLE : GONE);
            sliderBarValueView.setMaxValue(sliderBarMaxValue - sliderBarMinValue);
            seekBar.setMax(sliderBarMaxValue - sliderBarMinValue);
            seekBar.setProgress(Math.max(sliderBarMinValue, progress));
            typedArray.recycle();
        }
    }

    private void initView() {
        inflate(getContext(), R.layout.tools_slider_bar, this);
        tvTitle = findViewById(R.id.tv_message);
        sliderBarValueView = findViewById(R.id.value_view);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        sliderBarValueView.setMaxValue(sliderBarMaxValue - sliderBarMinValue);
        sliderBarValueView.setValueUnit(valueUnit);
        FrameLayout flSeekBar = findViewById(R.id.fl_seek_bar);
        flSeekBar.setOnTouchListener((v, event) -> {
            Rect seekRect = new Rect();
            seekBar.getHitRect(seekRect);
            if ((event.getY() >= (seekRect.top - 500)) && (event.getY() <= (seekRect.bottom + 500))) {
                float y = seekRect.top + seekRect.height() / 2;
                //seekBar only accept relative x
                float x = event.getX() - seekRect.left;
                if (x < 0) {
                    x = 0;
                } else if (x > seekRect.width()) {
                    x = seekRect.width();
                }
                MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                        event.getAction(), x, y, event.getMetaState());
                return seekBar.onTouchEvent(me);
            }
            return false;
        });
    }

    public void setProgress(int progress) {
        if (progress > sliderBarMaxValue){
            progress = sliderBarMaxValue;
        }
        progress = Math.max(sliderBarMinValue, progress);
        this.seekBar.setProgress(progress);
        updateSeekBarValue(progress,false);
    }

    public void setShowType(CValueType type){
        this.showType = type;
        updateSeekBarValue(seekBar.getProgress(), false);
    }

    public void setShowTitle(boolean show){
        this.showTitle = show;
        tvTitle.setVisibility(showTitle ? VISIBLE : GONE);
    }

    public void setTitle(String title){
        this.title = title;
        tvTitle.setText(title);
    }

    public void setTitle(@StringRes int stringResId){
        this.title = getContext().getString(stringResId);
        tvTitle.setText(title);
    }

    private void updateSeekBarValue(int value, boolean stop) {
        int v = value + sliderBarMinValue;
        int currentPercentage = Math.round((float) v / (float) sliderBarMaxValue * 100F);
        sliderBarValueView.setShowType(showType);
        sliderBarValueView.setValue(v);
        if (changeListener != null) {
            changeListener.changed(v, currentPercentage, stop);
        }
    }

    public void setSliderBarMinValue(int sliderBarMinValue) {
        this.sliderBarMinValue = sliderBarMinValue;
        seekBar.setMax(sliderBarMaxValue - sliderBarMinValue);
    }

    public void setSliderBarMaxValue(int sliderBarMaxValue) {
        this.sliderBarMaxValue = sliderBarMaxValue;
    }

    public int getProgress(){
      return seekBar.getProgress();
    }

    public void setChangeListener(OnProgressChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            updateSeekBarValue(progress, false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateSeekBarValue(seekBar.getProgress(), true);
    }

    public interface OnProgressChangeListener {
        void changed(int progress, int percentageValue, boolean isStopTouch);
    }
}
