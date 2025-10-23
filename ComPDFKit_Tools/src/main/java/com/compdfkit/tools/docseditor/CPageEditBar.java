/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.docseditor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

public class CPageEditBar extends FrameLayout implements View.OnClickListener{
    private AppCompatTextView tvToolBarTitle;

    private AppCompatImageView ivToolBarBackBtn;

    private AppCompatImageView ivToolBarEdit;
    private AppCompatImageView ivToolBarSelect;
    private AppCompatTextView tvToolBarDone;
    OnEnableEditPageCallback enableEditPageCallback = null;
    OnAllSelectCallback allSelectCallback = null;

    OnDoneClickCallback onDoneClickCallback = null;
    public CPageEditBar(@NonNull Context context) {
        this(context, null);
    }

    public CPageEditBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CPageEditBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPageEditBar(context, attrs);
    }

    private void initPageEditBar(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.tools_page_edit_bar, this);
        tvToolBarTitle = findViewById(R.id.tv_tool_bar_title);
        ivToolBarBackBtn = findViewById(R.id.iv_tool_bar_back);
        ivToolBarEdit = findViewById(R.id.iv_tool_bar_edit);
        ivToolBarSelect = findViewById(R.id.iv_tool_bar_select);
        tvToolBarDone = findViewById(R.id.tv_tool_bar_done);
        initAttributes(context, attrs);
        ivToolBarEdit.setOnClickListener((v) ->  {
            if (enableEditPageCallback != null) {
                ivToolBarEdit.setVisibility(View.GONE);
                ivToolBarSelect.setVisibility(View.VISIBLE);
                tvToolBarDone.setVisibility(View.VISIBLE);
                enableEditPageCallback.onEnableEditPage(true);
            }
        });
        ivToolBarSelect.setOnClickListener(this);
        tvToolBarDone.setOnClickListener(this);
    }

    private void initAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CPageEditBar);
        if (typedArray != null) {
            String toolBarTitle = typedArray.getString(R.styleable.CPageEditBar_android_title);
            String doneTitle = typedArray.getString(R.styleable.CPageEditBar_tools_done_title);
            if (!TextUtils.isEmpty(toolBarTitle)) {
                tvToolBarTitle.setText(toolBarTitle);
            }
            if (!TextUtils.isEmpty(doneTitle)) {
                tvToolBarDone.setText(doneTitle);
            }
            Drawable backIconDrawable = CViewUtils.loadDrawableFromAttributes(getContext(), typedArray, R.styleable.CPageEditBar_tools_pageeditbar_back_icon, R.drawable.tools_ic_back);
            if (backIconDrawable != null) {
                ivToolBarBackBtn.setImageDrawable(backIconDrawable);
            }
            CViewUtils.applyViewBackground(this);
            typedArray.recycle();
        }
    }

    public void enterEditMode(){
        ivToolBarEdit.performClick();
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

    public void showEditButton(boolean show) {
        ivToolBarEdit.setVisibility(show == true ? View.VISIBLE : View.GONE);
    }

    public void showSelectButton(boolean show) {
        ivToolBarSelect.setVisibility(show == true ? View.VISIBLE : View.GONE);
    }

    public void showDoneButton(boolean show) {
        tvToolBarDone.setVisibility(show == true ? View.VISIBLE : View.GONE);
    }

    public void setDoneButtonText(String text){
        tvToolBarDone.setText(text);
    }

    public void enableDone(boolean enable){
        tvToolBarDone.setEnabled(enable);
    }

    /**
     * set left back icon resource
     *
     * @param iconResId resId
     */
    public void setBackImageIconResource(@DrawableRes int iconResId) {
        ivToolBarBackBtn.setImageResource(iconResId);
    }

    public void setOnEnableEditPageCallback(OnEnableEditPageCallback callback) {
        this.enableEditPageCallback = callback;
    }

    public void setOnSelectAllCallback(OnAllSelectCallback callback) {
        this.allSelectCallback = callback;
    }

    public void setOnDoneClickCallback(OnDoneClickCallback callback) {
        this.onDoneClickCallback = callback;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_tool_bar_select) {
            boolean select = view.isSelected();
            if (allSelectCallback != null) {
                allSelectCallback.onAllSelect(!select);
            }
            view.setSelected(!select);
        } else if (id == R.id.tv_tool_bar_done) {
            if (onDoneClickCallback != null) {
                onDoneClickCallback.onDoneClick();
            }
        }
    }

    public interface OnEnableEditPageCallback {
        void onEnableEditPage(boolean enable);
    }

    public interface OnAllSelectCallback {
        void onAllSelect(boolean select);
    }

    public interface OnDoneClickCallback {
        void onDoneClick();
    }

    public interface OnEditDoneCallback {
        void onEditDone();
    }
}
