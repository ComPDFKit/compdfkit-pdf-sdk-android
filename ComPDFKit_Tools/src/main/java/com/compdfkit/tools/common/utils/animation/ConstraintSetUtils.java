/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.animation;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;


public class ConstraintSetUtils {

    public static final long CONFIG_SHORT_ANIM_TIME = 100L;

    public void hideFromTop(ConstraintSet constraintSet, View view){
        constraintSet.clear(view.getId(), ConstraintSet.TOP);
        constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
    }

    public void hideFromBottom(ConstraintSet constraintSet, View view){
        constraintSet.clear(view.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
    }

    public void hideFromLeft(ConstraintSet constraintSet, View view){
        constraintSet.clear(view.getId(), ConstraintSet.START);
        constraintSet.connect(view.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START);
    }

    public void hideFromRight(ConstraintSet constraintSet, View view){
        constraintSet.clear(view.getId(), ConstraintSet.END);
        constraintSet.connect(view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END);
    }

    public void showFromTop(ConstraintSet constraintSet, View view){
        constraintSet.clear(view.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
    }

    public void showFromBottom(ConstraintSet constraintSet, View view){
        constraintSet.clear(view.getId(), ConstraintSet.TOP);
        constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
    }

    public void showFromLeft(ConstraintSet constraintSet, View view){
        constraintSet.clear(view.getId(), ConstraintSet.END);
        constraintSet.connect(view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
    }

    public void showFromRight(ConstraintSet constraintSet, View view){
        constraintSet.clear(view.getId(), ConstraintSet.START);
        constraintSet.connect(view.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
    }

    private void fullVertical(ConstraintSet constraintSet, View view){
        constraintSet.connect(view.getId(), ConstraintSet.TOP,  ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, view.getId(), ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
    }

    public void show(ConstraintSet constraintSet, View view){
        constraintSet.setVisibility(view.getId(), ConstraintSet.VISIBLE);
    }

    public void hide(ConstraintSet constraintSet, View view){
        constraintSet.setVisibility(view.getId(), ConstraintSet.GONE);
    }

    public void apply(ConstraintSet constraintSet, ConstraintLayout rootView){
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(200);
        autoTransition.setInterpolator(new LinearOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(rootView, autoTransition);
        constraintSet.applyTo(rootView);
    }

}
