/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.provider;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;

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
        removeAllViews();
        setBackgroundResource(R.drawable.tools_context_menu_window);
        LayoutInflater.from(context).inflate(R.layout.tools_context_menu_root_layout, this);
        llContent = findViewById(R.id.ll_content);

    }

    public ContextMenuView addItem(View view){
        addView(view);
        return this;
    }

    public ContextMenuView addItem(@StringRes int titleRes, OnClickListener clickListener){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tools_context_menu_item_layout, null);
        AppCompatTextView textView = (AppCompatTextView) view;
        textView.setId(View.generateViewId());
        textView.setText(titleRes);
        textView.setOnClickListener(clickListener);
        if (llContent != null) {
            llContent.addView(view);
        }
        return this;
    }
}
