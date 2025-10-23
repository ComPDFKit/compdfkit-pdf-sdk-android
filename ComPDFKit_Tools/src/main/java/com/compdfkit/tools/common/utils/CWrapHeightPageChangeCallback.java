/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;


public class CWrapHeightPageChangeCallback extends ViewPager2.OnPageChangeCallback {

    private FragmentManager fragmentManager;

    private ViewPager2 viewPager2;

    private CViewHeightCallback callback;

    private boolean isFirstCallbackHeight = true;

    public CWrapHeightPageChangeCallback(ViewPager2 viewPager2, FragmentManager childFragmentManager) {
        this.fragmentManager = childFragmentManager;
        this.viewPager2 = viewPager2;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        updatePagerHeight(fragmentManager, viewPager2, position);
    }

    public void updatePagerHeight(FragmentManager fragmentManager, ViewPager2 viewPager2, int position){
        if (fragmentManager != null && viewPager2.getAdapter() != null) {
            Fragment myFragment = fragmentManager.findFragmentByTag("f" + viewPager2.getAdapter().getItemId(position));
            if (myFragment != null && myFragment.getView() != null) {
                updatePagerHeightForChild(myFragment.getView());
            }
        }
    }

    public void updatePagerHeightForChild(View view) {
        view.post(() -> {
            int wMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
            int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(wMeasureSpec, hMeasureSpec);
            view.requestLayout();
            int measuredHeight = view.getMeasuredHeight();
            int layoutParamsHeight = viewPager2.getLayoutParams().height;
            int screenHeight = CDimensUtils.getScreenHeight(view.getContext());
            if (CViewUtils.isLandScape(view.getContext())) {
                screenHeight -= CViewUtils.getActionBarSize(view.getContext()) * 2;
                measuredHeight = Math.min(measuredHeight, screenHeight);
            }else {
                measuredHeight = Math.min(measuredHeight, CDimensUtils.dp2px(view.getContext(), 500));
            }
            if (layoutParamsHeight != measuredHeight) {
                if (callback != null && !CViewUtils.isLandScape(viewPager2.getContext())) {
                    callback.height(measuredHeight + CViewUtils.getActionBarSize(view.getContext()), isFirstCallbackHeight);
                    if (isFirstCallbackHeight) {
                        isFirstCallbackHeight = false;
                    }
                }
                if (layoutParamsHeight > 0 && measuredHeight > 0) {
                    requestHeightAnimation(viewPager2, layoutParamsHeight, measuredHeight);
                } else {
                    ViewGroup.LayoutParams layoutParams = viewPager2.getLayoutParams();
                    layoutParams.height = measuredHeight;
                    viewPager2.setLayoutParams(layoutParams);
                }
            }
        });
    }

    public static void requestHeightAnimation(ViewPager2 view, int startHeight, int endHeight) {
        ViewWrapper viewWrapper = new ViewWrapper(view);
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(viewWrapper, "height", startHeight, endHeight);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    private static class ViewWrapper {

        private View rView;

        public ViewWrapper(View target) {
            rView = target;
        }

        public int getWidth() {
            return rView.getLayoutParams().width;
        }

        public void setWidth(int width) {
            rView.getLayoutParams().width = width;
            rView.requestLayout();
        }

        public int getHeight() {
            return rView.getLayoutParams().height;
        }

        public void setHeight(int height) {
            rView.getLayoutParams().height = height;
            rView.requestLayout();
        }
    }

    public void setViewHeightCallback(CViewHeightCallback callback) {
        this.callback = callback;
    }

    public interface CViewHeightCallback{
        void height(int layoutHeight, boolean isFirst);
    }
}
