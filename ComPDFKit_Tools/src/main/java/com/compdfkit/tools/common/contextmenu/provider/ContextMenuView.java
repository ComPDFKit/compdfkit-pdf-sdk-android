/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.provider;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.image.CBitmapUtil;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

import java.util.List;
import java.util.Map;

public class ContextMenuView extends LinearLayout {

    private LinearLayout llContent;

    public ContextMenuView(Context context) {
        this(context, null);
    }

    public ContextMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContextMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        removeAllViews();
        setBackgroundResource(R.drawable.tools_context_menu_window);
        LayoutInflater.from(context).inflate(R.layout.tools_context_menu_root_layout, this);
        llContent = findViewById(R.id.ll_content);

        ContextMenuConfig contextMenuConfig = CPDFApplyConfigUtil.getInstance().getContextMenuConfig();
        if (contextMenuConfig != null){
            try {
                List<Double> padding = contextMenuConfig.padding;
                if (padding != null&& padding.size() == 4){
                    llContent.setPadding(CDimensUtils.dp2px(context,
                            (int) padding.get(0).floatValue()),
                        CDimensUtils.dp2px(context, (int) padding.get(1).floatValue()),
                        CDimensUtils.dp2px(context, (int) padding.get(2).floatValue()),
                        CDimensUtils.dp2px(context, (int) padding.get(3).floatValue()));
                }
                GradientDrawable background = (GradientDrawable) getBackground();
                if (background != null){
                    background.setColor(Color.parseColor(contextMenuConfig.backgroundColor));
                }
            }catch (Exception ignored){
            }
        }
    }

    public ContextMenuView addItem(View view){
        addView(view);
        return this;
    }

    public ContextMenuView addItem(@StringRes int titleRes, OnClickListener clickListener){
        addItem(getContext().getString(titleRes), clickListener);
        return this;
    }

    public ContextMenuView addItem(String title, OnClickListener clickListener){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tools_context_menu_item_layout, null);
        AppCompatTextView textView = (AppCompatTextView) view;
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setId(View.generateViewId());
        textView.setText(title);
        ContextMenuConfig contextMenuConfig = CPDFApplyConfigUtil.getInstance().getContextMenuConfig();
        if (contextMenuConfig != null){
            textView.setTextSize(contextMenuConfig.fontSize);
        }
        textView.setOnClickListener(clickListener);
        if (llContent != null) {
            llContent.addView(view);
        }
        return this;
    }

    public ContextMenuView addItem(ContextMenuConfig.ContextMenuActionItem item, OnClickListener clickListener){
        return addItem(item, 0, clickListener);
    }

    public ContextMenuView addItem(ContextMenuConfig.ContextMenuActionItem item, @StringRes int normalTitleResId, OnClickListener clickListener){
        ContextMenuConfig contextMenuConfig = CPDFApplyConfigUtil.getInstance().getContextMenuConfig();
        View view;
        if (item.showType == ContextMenuConfig.ContextMenuActionItem.ShowType.text) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.tools_context_menu_item_layout, null);
            AppCompatTextView textView = (AppCompatTextView) view;
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setId(View.generateViewId());
            if (!TextUtils.isEmpty(item.title) || normalTitleResId != 0) {
                textView.setText(TextUtils.isEmpty(item.title) ? getContext().getString(normalTitleResId) : item.title);
            }else {
                textView.setText(item.identifier);
            }
            if (contextMenuConfig != null){
                textView.setTextSize(contextMenuConfig.fontSize);
            }
            textView.setOnClickListener(clickListener);
        }else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.tools_context_menu_image_item_option_layout, null);
            AppCompatImageView imageView = (AppCompatImageView) view;
            imageView.setId(View.generateViewId());
            int size = CDimensUtils.dp2px(getContext(), contextMenuConfig.iconSize != 0 ? contextMenuConfig.iconSize : 36);
            int margin = CDimensUtils.dp2px(getContext(), 4);
            LayoutParams layoutParams = new LayoutParams(size, size);
            layoutParams.setMarginStart(margin);
            layoutParams.setMarginEnd(margin);
            imageView.setLayoutParams(layoutParams);
            imageView.setContentDescription(item.title);
            int iconResId = CBitmapUtil.getBitmapResId(getContext(), item.icon);
            if (iconResId != 0){
                imageView.setImageResource(iconResId);
            }
            imageView.setOnClickListener(clickListener);
        }
        if (llContent != null) {
            llContent.addView(view);
        }
        return this;
    }
}
