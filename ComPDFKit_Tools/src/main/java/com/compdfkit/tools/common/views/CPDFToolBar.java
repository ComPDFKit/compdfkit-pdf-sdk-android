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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CModeSwitchDialogFragment;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

import java.util.LinkedHashSet;
import java.util.List;


/**
 * pdf ui tool bar <br/>
 * ︳------------------------------------------------︳<br/>
 * ︳　　　　　　　　title　　　　　icon1  icon2 icon3   ︳<br/>
 * ︳------------------------------------------------︳<br/>
 * icon1: searchIcon <br/>
 * icon2: boTaIcon <br/>
 * icon3: moreIcon <br/>
 * <p/>
 * use samples:<br/>
 * com.compdfkit.tools.common.utils.view.CPDFToolBar <br/>
 * android:id="@+id/pdf_tool_bar" <br/>
 * android:layout_width="match_parent" <br/>
 * android:layout_height="?android:attr/actionBarSize" <br/>
 * android:title="@string/viewer_toolbar_title" <br/>
 * app:tools_toolbar_bota_icon="@drawable/xxx" <br/>
 * app:tools_toolbar_more_icon="@drawable/xxx" <br/>
 * app:tools_toolbar_search_icon="@drawable/xxx"/> <br/>
 * <p/>
 * custom attrs: <br/>
 * android:title="@string/xxx" <br/>
 * app:tools_toolbar_search_icon="@drawable/xxx" <br/>
 * app:tools_toolbar_bota_icon="@drawable/xxx" <br/>
 * app:tools_toolbar_more_icon="@drawable/xxx" <br/>
 * <p/>
 */
public class CPDFToolBar extends FrameLayout {

    private AppCompatTextView tvToolBarTitle;

    private LinearLayout llMenu;

    private AppCompatImageView ivBack;

    private LinkedHashSet<CPreviewMode> previewModes = new LinkedHashSet<>();

    private CPreviewMode currentPreviewMode = CPreviewMode.Viewer;

    private CModeSwitchDialogFragment.OnPreviewModeChangeListener changeListener;

    public CPDFToolBar(@NonNull Context context) {
        this(context, null);
    }

    public CPDFToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPDFToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initToolBar(context, attrs);
    }

    private void initToolBar(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.tools_cpdf_tool_bar, this);
        ConstraintLayout clMainToolbar = findViewById(R.id.cl_pdf_root_tool_bar);
        clMainToolbar.setOnClickListener(v -> {});
        llMenu = findViewById(R.id.ll_menu);
        tvToolBarTitle = findViewById(R.id.tv_tool_bar_title);
        ivBack = findViewById(R.id.iv_back_action);
        LinearLayout llTitle = findViewById(R.id.ll_title);
        llTitle.setOnClickListener(v -> {
            if (previewModes != null && previewModes.size() > 1) {
                CViewUtils.hideKeyboard(this);
                CModeSwitchDialogFragment fragment = CModeSwitchDialogFragment.newInstance(previewModes, currentPreviewMode);
                fragment.setSwitchModeListener(changeListener);
                if (getContext() instanceof FragmentActivity) {
                    fragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "modeSwitchDialogFragment");
                }
            }
        });
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CPDFToolBar);
        if (typedArray != null) {
            String toolBarTitle = typedArray.getString(R.styleable.CPDFToolBar_android_title);
            if (!TextUtils.isEmpty(toolBarTitle)) {
                tvToolBarTitle.setText(toolBarTitle);
            }
            CViewUtils.applyViewBackground(this);
            typedArray.recycle();
        }
    }

    public void selectMode(CPreviewMode mode){
        currentPreviewMode = mode;
        tvToolBarTitle.setText(mode.getTitleByMode());
    }

    public void addMode(CPreviewMode previewMode) {
        previewModes.add(previewMode);
        if (previewModes.size() > 1) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.tools_ic_syas_arrow);
            if (drawable != null) {
                drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvToolBarTitle.setCompoundDrawables(null, null, drawable, null);
            }
        }else {
            tvToolBarTitle.setCompoundDrawables(null,null,null,null);
        }
    }

    public void addModes(List<CPreviewMode> modes){
        for (CPreviewMode mode : modes) {
            addMode(mode);
        }
    }

    public CPreviewMode getMode() {
        return currentPreviewMode;
    }

    public void addAction(@DrawableRes int drawableResId, View.OnClickListener listener){
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.tools_cpdf_tool_bar_pdf_view_menu_action, null);
        AppCompatImageView actionView =itemView.findViewById(R.id.iv_action);
        actionView.setImageResource(drawableResId);
        actionView.setOnClickListener(listener);
        if (llMenu != null) {
            llMenu.addView(itemView);
        }
    }

    public void addBackPressedAction(View.OnClickListener listener){
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(listener);
    }

    public void setPreviewModeChangeListener(CModeSwitchDialogFragment.OnPreviewModeChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}
