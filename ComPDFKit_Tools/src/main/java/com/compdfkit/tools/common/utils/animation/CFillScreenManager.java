/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import java.util.Arrays;
import java.util.LinkedHashSet;


public class CFillScreenManager {

    public static final long CONFIG_SHORT_ANIM_TIME = 200L;

    public LinkedHashSet<View> topToolViewList = new LinkedHashSet<>();

    public LinkedHashSet<View> bottomToolViewList = new LinkedHashSet<>();

    private LinkedHashSet<View> leftToolViewList = new LinkedHashSet<>();

    private LinkedHashSet<View> rightToolViewList = new LinkedHashSet<>();

    public void showFromTop(View view, long duration) {
        if (view.getVisibility() == android.view.View.VISIBLE) {
            return;
        }
        if (!view.isHardwareAccelerated()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        view.setAlpha(0F);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animation.removeListener(this);
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                        view.clearAnimation();
                    }
                });
    }

    public void hideFromTop(View view, long duration) {
        if (view.getVisibility() != android.view.View.VISIBLE) {
            return;
        }
        if (!view.isHardwareAccelerated()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        view.animate()
                .alpha(0.0f)
                .translationY(-1.0f * view.getHeight())
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animation.removeListener(this);
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                        view.clearAnimation();
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public void showFromBottom(View view, long duration) {
        if (view.getVisibility() == android.view.View.VISIBLE) {
            return;
        }
        if (!view.isHardwareAccelerated()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        view.setAlpha(0F);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animation.removeListener(this);
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                        view.clearAnimation();
                    }
                });
    }

    public void hideFromBottom(View view, long duration) {
        if (view.getVisibility() != android.view.View.VISIBLE) {
            return;
        }
        if (!view.isHardwareAccelerated()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        view.animate()
                .alpha(0.0f)
                .translationY((float) view.getHeight())
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animation.removeListener(this);
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                        view.clearAnimation();
                        view.setVisibility(View.GONE);
                    }
                });
    }


    public void showFromLeft(View view,long duration) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        if (!view.isHardwareAccelerated()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        view.setAlpha(0F);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .translationX(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animation.removeListener(this);
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                        view.clearAnimation();
                    }
                });
    }

    public void hideFromLeft(View view, long duration) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        if (!view.isHardwareAccelerated()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        view.animate()
                .alpha(0.0f)
                .translationXBy(-1.0f * view.getWidth())
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animation.removeListener(this);
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                        view.clearAnimation();
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public void showFromRight(View view, long duration) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        if (!view.isHardwareAccelerated()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        view.setAlpha(0F);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .translationX(0.0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animation.removeListener(this);
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                        view.clearAnimation();
                    }
                });
    }

    public void hideFromRight(View view, long duration) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        if (!view.isHardwareAccelerated()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        view.animate()
                .alpha(0.0f)
                .translationX((float)view.getWidth())
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animation.removeListener(this);
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                        view.clearAnimation();
                        view.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public void bindTopToolView(View... topToolView) {
        topToolViewList.addAll(Arrays.asList(topToolView));
    }

    public void bindBottomToolViewList(View... bottomToolView) {
        bottomToolViewList.addAll(Arrays.asList(bottomToolView));
    }

    public void bindLeftToolViewList(View... leftToolView) {
        leftToolViewList.addAll(Arrays.asList(leftToolView));
    }

    public void bindRightToolViewList(View... rightToolView) {
        rightToolViewList.addAll(Arrays.asList(rightToolView));
    }

    public void fillScreenChange(boolean fillScreen) {
        if (fillScreen) {
            for (View view : topToolViewList) {
                hideFromTop(view, CONFIG_SHORT_ANIM_TIME);
            }
            for (View view : bottomToolViewList) {
                hideFromBottom(view, CONFIG_SHORT_ANIM_TIME);
            }
            for (View view : leftToolViewList) {
                hideFromLeft(view, CONFIG_SHORT_ANIM_TIME);
            }
            for (View view : rightToolViewList) {
                hideFromRight(view, CONFIG_SHORT_ANIM_TIME);
            }
        } else {
            for (View view : topToolViewList) {
                showFromTop(view, CONFIG_SHORT_ANIM_TIME);
            }
            for (View view : bottomToolViewList) {
                showFromBottom(view, CONFIG_SHORT_ANIM_TIME);
            }
            for (View view : leftToolViewList) {
                showFromLeft(view, CONFIG_SHORT_ANIM_TIME);
            }
            for (View view : rightToolViewList) {
                showFromRight(view, CONFIG_SHORT_ANIM_TIME);
            }
        }
    }

    public void removeToolView(View view){
        topToolViewList.remove(view);
        bottomToolViewList.remove(view);
        leftToolViewList.remove(view);
        rightToolViewList.remove(view);
    }

    public void removeAndHideToolView(View view){
        if (topToolViewList.contains(view)) {
            hideFromTop(view, 200);
            topToolViewList.remove(view);
            return;
        }
        if (leftToolViewList.contains(view)) {
            hideFromLeft(view, 200);
            leftToolViewList.remove(view);
            return;
        }
        if (rightToolViewList.contains(view)) {
            hideFromRight(view, 200);
            rightToolViewList.remove(view);
            return;
        }
        if (bottomToolViewList.contains(view)) {
            hideFromBottom(view, 200);
            bottomToolViewList.remove(view);
        }
    }


}
