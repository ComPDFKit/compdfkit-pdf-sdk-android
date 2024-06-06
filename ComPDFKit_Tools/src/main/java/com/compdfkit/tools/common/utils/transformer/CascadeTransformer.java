package com.compdfkit.tools.common.utils.transformer;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

public class CascadeTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.95f;

    @Override
    public void transformPage(View page, float position) {
        page.animate().cancel();

        if (position < -1) {
            page.setAlpha(0f);
            page.setScaleX(1.0f);
            page.setScaleY(1.0f);
        } else if (position <= 0) {
            page.setAlpha(1f);
            page.setTranslationZ(0f);
            page.setTranslationX(0f);
            page.setScaleX(1.0f);
            page.setScaleY(1.0f);
        } else if (position <= 1) {
            page.setAlpha(1 - position);
            page.setTranslationX(-position * page.getWidth());
            float scale = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setTranslationZ(-position);
        } else {
            page.setAlpha(0f);
        }
    }
}
