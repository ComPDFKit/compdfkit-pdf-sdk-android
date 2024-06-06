package com.compdfkit.tools.common.utils.view.colorpicker.widget;


import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;

class ColorPickerData {

    @ColorInt
    public int color;

    public int alpha = 255;

    public ColorPickerData(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        this.color = color;
        this.alpha = alpha;
    }

}
