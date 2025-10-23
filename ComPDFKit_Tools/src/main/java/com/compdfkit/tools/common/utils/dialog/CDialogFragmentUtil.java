/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
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
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CDialogFragmentUtil {

    public static void setBottomSheetDialogFragmentFullScreen(FragmentActivity fragmentActivity, @Nullable Dialog dialog, BottomSheetBehavior<View> behavior) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
        if (dialog == null) {
            return;
        }
        FrameLayout bottomSheet =
            dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        View rootView =fragmentActivity.findViewById(android.R.id.content);
        layoutParams.height = CViewUtils.isInDesktopWindowMode(fragmentActivity) ?
            rootView.getHeight() : LayoutParams.MATCH_PARENT;
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