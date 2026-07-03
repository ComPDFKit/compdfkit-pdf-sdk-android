/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
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

    public interface DirectionAwareToolView {

        ToolViewDirection getToolViewDirection();
    }

    public enum ToolViewDirection {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public static final long CONFIG_SHORT_ANIM_TIME = 200L;

    public LinkedHashSet<View> topToolViewList = new LinkedHashSet<>();

    public LinkedHashSet<View> bottomToolViewList = new LinkedHashSet<>();

    private LinkedHashSet<View> leftToolViewList = new LinkedHashSet<>();

    private LinkedHashSet<View> rightToolViewList = new LinkedHashSet<>();

    private ToolViewDirection resolveDirection(View view, ToolViewDirection fallbackDirection) {
        if (view instanceof DirectionAwareToolView) {
            return ((DirectionAwareToolView) view).getToolViewDirection();
        }
        return fallbackDirection;
    }

    private void show(View view, long duration, ToolViewDirection defaultDirection, boolean resolveDynamicDirection) {
        ToolViewDirection direction = resolveDynamicDirection ? resolveDirection(view, defaultDirection) : defaultDirection;
        switch (direction) {
            case TOP:
                showFromTopInternal(view, duration);
                break;
            case BOTTOM:
                showFromBottomInternal(view, duration);
                break;
            case LEFT:
                showFromLeftInternal(view, duration);
                break;
            case RIGHT:
            default:
                showFromRightInternal(view, duration);
                break;
        }
    }

    private void hide(View view, long duration, ToolViewDirection defaultDirection, boolean resolveDynamicDirection) {
        ToolViewDirection direction = resolveDynamicDirection ? resolveDirection(view, defaultDirection) : defaultDirection;
        switch (direction) {
            case TOP:
                hideFromTopInternal(view, duration);
                break;
            case BOTTOM:
                hideFromBottomInternal(view, duration);
                break;
            case LEFT:
                hideFromLeftInternal(view, duration);
                break;
            case RIGHT:
            default:
                hideFromRightInternal(view, duration);
                break;
        }
    }

    private void showFromTopInternal(View view, long duration) {
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

    private void hideFromTopInternal(View view, long duration) {
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

    private void showFromBottomInternal(View view, long duration) {
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

    private void hideFromBottomInternal(View view, long duration) {
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

    private void showFromLeftInternal(View view,long duration) {
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

    private void hideFromLeftInternal(View view, long duration) {
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

    private void showFromRightInternal(View view, long duration) {
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

    private void hideFromRightInternal(View view, long duration) {
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

    public void showFromTop(View view, long duration) {
        show(view, duration, ToolViewDirection.TOP, true);
    }

    public void hideFromTop(View view, long duration) {
        hide(view, duration, ToolViewDirection.TOP, true);
    }

    public void showFromBottom(View view, long duration) {
        show(view, duration, ToolViewDirection.BOTTOM, true);
    }

    public void hideFromBottom(View view, long duration) {
        hide(view, duration, ToolViewDirection.BOTTOM, true);
    }


    public void showFromLeft(View view,long duration) {
        show(view, duration, ToolViewDirection.LEFT, true);
    }

    public void hideFromLeft(View view, long duration) {
        hide(view, duration, ToolViewDirection.LEFT, true);
    }

    public void showFromRight(View view, long duration) {
        show(view, duration, ToolViewDirection.RIGHT, true);
    }

    public void hideFromRight(View view, long duration) {
        hide(view, duration, ToolViewDirection.RIGHT, true);
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
                hideImmediately(view, ToolViewDirection.LEFT);
            }
            for (View view : rightToolViewList) {
                hideImmediately(view, ToolViewDirection.RIGHT);
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

    private void hideImmediately(View view, ToolViewDirection defaultDirection) {
        ToolViewDirection direction = resolveDirection(view, defaultDirection);
        view.animate().cancel();
        view.setAlpha(0F);
        switch (direction) {
            case LEFT:
                view.setTranslationX(-1.0f * view.getWidth());
                view.setVisibility(View.GONE);
                break;
            case RIGHT:
                view.setTranslationX((float) view.getWidth());
                view.setVisibility(View.INVISIBLE);
                break;
            case TOP:
                view.setTranslationY(-1.0f * view.getHeight());
                view.setVisibility(View.GONE);
                break;
            case BOTTOM:
                view.setTranslationY((float) view.getHeight());
                view.setVisibility(View.GONE);
                break;
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
            hide(view, 200, ToolViewDirection.TOP, true);
            topToolViewList.remove(view);
            return;
        }
        if (leftToolViewList.contains(view)) {
            hide(view, 200, ToolViewDirection.LEFT, true);
            leftToolViewList.remove(view);
            return;
        }
        if (rightToolViewList.contains(view)) {
            hide(view, 200, ToolViewDirection.RIGHT, true);
            rightToolViewList.remove(view);
            return;
        }
        if (bottomToolViewList.contains(view)) {
            hide(view, 200, ToolViewDirection.BOTTOM, true);
            bottomToolViewList.remove(view);
        }
    }


}
