/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.viewutils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;


public class CDimensUtils {
    public static int px2dp(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int spToPx(float sp, Context context) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context){
        return context.getResources().getDisplayMetrics();
    }

    public static int getScreenWidth(Context context){
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenHeight(Context context){
        return getDisplayMetrics(context).heightPixels;
    }

    public static int getMinWidthOrHeight(Context context) {
        if (context == null) {
            return 0;
        }
        int height = getScreenHeight(context);

        int width = getScreenWidth(context);
        return Math.min(width, height);
    }

    /**
     * The calculated position aligns the y-direction with the top or bottom of the anchorView,
     * and the x-direction aligns with the right side of the screen.
     * If the position of the anchorView changes, you can add an appropriate offset to correct it.
     * @param anchorView the view that triggers the window
     * @param contentView the content layout of the window
     * @return the xOff and yOff coordinates of the top-left corner where the window is displayed
     */
    public static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int[] windowPos = new int[2];
        final int[] anchorLoc = new int[2];
        anchorView.getLocationInWindow(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // Get the screen height and width
        final int screenHeight = getScreenHeight(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // Calculate the height and width of the contentView
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // Determine whether to pop up or down
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = anchorLoc[0] - (windowWidth /2);
            windowPos[1] = anchorLoc[1] - windowHeight + anchorHeight;
        } else {
            windowPos[0] =  anchorLoc[0] - (windowWidth /2);
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        windowPos[0] -= contentView.getMeasuredWidth() / 2;
        return windowPos;
    }


}
