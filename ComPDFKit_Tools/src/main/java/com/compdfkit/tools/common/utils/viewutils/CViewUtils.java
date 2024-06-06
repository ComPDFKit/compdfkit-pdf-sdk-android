/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.viewutils;


import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.compdfkit.tools.R;
import com.google.android.material.shape.MaterialShapeDrawable;

public class CViewUtils {

    public static Drawable loadDrawableFromAttributes(Context context, TypedArray typedArray, int index, int defValue) {
        Drawable drawable = null;
        try {
            int resId = typedArray.getResourceId(index, defValue);
            if (resId != -1) {
                drawable = AppCompatResources.getDrawable(context, resId);
            }
            return drawable;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getActionBarSize(Context context){
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
            return actionBarHeight;
        }else {
            return 0;
        }
    }

    public static int getAttrColor(Context context, int resId){
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(resId, tv, true)) {
            int colorRes = tv.resourceId;
            return ContextCompat.getColor(context, colorRes);
        }else {
            return Color.WHITE;
        }
    }

    public static void applyViewBackground(View view, int defaultColor) {
        int color;
        if (view.getBackground() != null && view.getBackground() instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
            color = colorDrawable.getColor();
        } else {
            color = defaultColor;
        }
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        materialShapeDrawable.setFillColor(ColorStateList.valueOf(color));
        materialShapeDrawable.initializeElevationOverlay(view.getContext());
        materialShapeDrawable.setElevation(ViewCompat.getElevation(view));
        ViewCompat.setBackground(view, materialShapeDrawable);
    }

    public static void applyViewBackground(View view) {
        applyViewBackground(view, ContextCompat.getColor(view.getContext(), R.color.tools_color_primary));
    }

    public static void showKeyboard(View view) {
        view.postDelayed(() -> {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }, 200);
    }

    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(@Nullable Dialog dialog) {
        if (dialog != null) {
            View view = dialog.getCurrentFocus();
            if (view instanceof EditText) {
                InputMethodManager mInputMethodManager = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)  ;
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }
    }

    public static boolean isLandScape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
