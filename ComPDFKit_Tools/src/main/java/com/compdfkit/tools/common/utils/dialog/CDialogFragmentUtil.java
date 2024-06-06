/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.dialog;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CDialogFragmentUtil {
    public static void setBottomSheetDialogFragmentFullScreen(@Nullable Dialog dialog, BottomSheetBehavior<View> behavior) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
        if (dialog == null) {
            return;
        }
        FrameLayout bottomSheet =
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        bottomSheet.setLayoutParams(layoutParams);
    }

    public static void resetBottomSheetDialogFragment(@Nullable Dialog dialog, BottomSheetBehavior<View> behavior) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(false);

        if (dialog == null) {
            return;
        }
        FrameLayout bottomSheet =
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        bottomSheet.setLayoutParams(layoutParams);
    }

    public static void setDimAmount(@Nullable Dialog dialog, float amount) {
        if (dialog != null) {
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = amount;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }
}