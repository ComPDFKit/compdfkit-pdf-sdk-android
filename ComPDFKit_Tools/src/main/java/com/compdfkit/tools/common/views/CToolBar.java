/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

/**
 * compdfkit-tools ui common toolbar
 * ︳------------------------------︳
 * ︳ <          title             ︳
 * ︳------------------------------︳
 * <p>
 * use samples
 * <com.compdfkit.tools.utils.view.CToolBar
 * android:id="@+id/tool_bar"
 * android:layout_width="match_parent"
 * android:layout_height="?android:attr/actionBarSize"
 * app:tools_toolbar_title="@string/tools_outline"
 * app:tools_toolbar_back_icon="@drawable/tools_ic_back"
 * android:elevation="4dp"/>
 * <p>
 * custom attributes:
 * android:title
 * tools_toolbar_back_icon
 * <p>
 * method:
 *
 * @see CToolBar#setTitle(String)
 * @see CToolBar#setTitle(int)
 * @see CToolBar#setBackImageIconResource(int)
 * @see CToolBar#setBackBtnClickListener(OnClickListener)
 */
public class CToolBar extends FrameLayout {
    private AppCompatTextView tvToolBarTitle;

    private AppCompatImageView ivToolBarBackBtn;

    public CToolBar(@NonNull Context context) {
        this(context, null);
    }

    public CToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initToolBar(context, attrs);
    }

    private void initToolBar(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.tools_ctool_bar, this);
        tvToolBarTitle = findViewById(R.id.tv_tool_bar_title);
        ivToolBarBackBtn = findViewById(R.id.iv_tool_bar_back);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CToolBar);
        if (typedArray != null) {
            String toolBarTitle = typedArray.getString(R.styleable.CToolBar_android_title);
            float textSize = typedArray.getDimensionPixelSize(R.styleable.CToolBar_android_textSize, CDimensUtils.spToPx(22, getContext()));
            tvToolBarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            if (!TextUtils.isEmpty(toolBarTitle)) {
                tvToolBarTitle.setText(toolBarTitle);
            }
            Drawable backIconDrawable = CViewUtils.loadDrawableFromAttributes(getContext(), typedArray, R.styleable.CToolBar_tools_toolbar_back_icon, R.drawable.tools_ic_back);
            if (backIconDrawable != null) {
                ivToolBarBackBtn.setImageDrawable(backIconDrawable);
            }
            CViewUtils.applyViewBackground(this);
            typedArray.recycle();
        }
    }

    public void setBackBtnClickListener(OnClickListener clickListener) {
        ivToolBarBackBtn.setOnClickListener(clickListener);
    }

    public void setTitle(@StringRes int titleResId) {
        tvToolBarTitle.setText(titleResId);
    }

    public void setTitle(String title) {
        tvToolBarTitle.setText(title);
    }

    /**
     * set left back icon resource
     *
     * @param iconResId resId
     */
    public void setBackImageIconResource(@DrawableRes int iconResId) {
        ivToolBarBackBtn.setImageResource(iconResId);
    }

    public AppCompatImageView getIvToolBarBackBtn() {
        return ivToolBarBackBtn;
    }
}
