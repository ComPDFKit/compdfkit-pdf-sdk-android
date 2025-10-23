/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
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

        applyViewBackground(view, getThemeAttrData(view.getContext().getTheme(), android.R.attr.colorPrimary));
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

    public static int getThemeAttrResourceId(Resources.Theme theme, int resId){
        try {
            TypedValue typedValue = new TypedValue();
            boolean result = theme.resolveAttribute(resId, typedValue, true );
            if (result){
                return typedValue.resourceId;
            }else {
                return 0;
            }
        }catch (Exception e){
            return 0;
        }
    }
    public static int getThemeAttrData(Resources.Theme theme, int resId){
        try {
            TypedValue typedValue = new TypedValue();
            boolean result = theme.resolveAttribute(resId, typedValue, true);
            int data = typedValue.data;
            if (result){
                return data;
            }else {
                return 0;
            }
        }catch (Exception e){
            return 0;
        }
    }

    public static boolean isDarkMode(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            int uiMode = context.getResources().getConfiguration().uiMode;
            return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        }else {
            return false;
        }
    }

    public static int getColor(int color,@IntRange(from = 0, to = 255) int alpha){
        return Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
    }

    public static @Nullable FragmentActivity getFragmentActivity(Context context) {
        if (context instanceof FragmentActivity){
            return (FragmentActivity) context;
        }
        Context baseContext = (context instanceof ContextThemeWrapper)
                ? ((ContextThemeWrapper) context).getBaseContext()
                : context;
        if (baseContext instanceof FragmentActivity){
            return (FragmentActivity) baseContext;
        }else {
            return null;
        }
    }

    public static int getThemeStyle(Context context, int themeResId){
        int themeId = CPDFApplyConfigUtil.getInstance().getGlobalThemeId();
        Resources.Theme mTheme = context.getResources().newTheme();
        mTheme.applyStyle(themeId, true);
        return CViewUtils.getThemeAttrResourceId(mTheme, themeResId);
    }

    public static LayoutInflater getThemeLayoutInflater(Context context){
        int themeId = CPDFApplyConfigUtil.getInstance().getGlobalThemeId();
        Context wrapper = new ContextThemeWrapper(context, themeId);
        LayoutInflater themedInflater = LayoutInflater.from(context).cloneInContext(wrapper);
        return themedInflater;
    }

    public static boolean isInDesktopWindowMode(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (activity.isInMultiWindowMode()) return true;
        }

        Rect bounds = new Rect();
        activity.getWindowManager().getDefaultDisplay().getRectSize(bounds);

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        return bounds.width() < dm.widthPixels || bounds.height() < dm.heightPixels;
    }
}
