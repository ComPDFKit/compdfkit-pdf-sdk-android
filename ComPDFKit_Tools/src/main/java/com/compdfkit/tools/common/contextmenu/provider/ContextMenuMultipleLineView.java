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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.CFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuMultipleLineView extends LinearLayout {
    Context mContext;

    CFlowLayout flowLayout;

    ContextMenuView secondView = null;
    public ContextMenuMultipleLineView(Context context) {
        this(context, null);
    }

    public ContextMenuMultipleLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContextMenuMultipleLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundResource(R.drawable.tools_context_menu_window);
        LayoutInflater.from(context).inflate(R.layout.tools_context_menu_mulit_line_root_layout, this);
        flowLayout = findViewById(R.id.flow_layout);
    }

    public ContextMenuMultipleLineView addItem(@StringRes int titleRes, OnClickListener clickListener) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tools_context_menu_item_layout, null);
        AppCompatTextView textView = (AppCompatTextView) view;
        textView.setId(View.generateViewId());
        textView.setText(titleRes);
        textView.setOnClickListener(clickListener);
        if (flowLayout != null) {
            flowLayout.addView(view);
        }
        return this;
    }

    public void addSecondView() {
        ContextMenuView view = new ContextMenuView(mContext);
        addView(view);
        secondView = view;
        secondView.setGravity(Gravity.CENTER);
        secondView.setVisibility(View.GONE);
    }

    public ContextMenuMultipleLineView addItemToSecondView(@StringRes int titleRes, OnClickListener clickListener) {
        if (secondView == null) {
            return this;
        }
        secondView.addItem(titleRes, clickListener);
        return this;
    }

    public ContextMenuMultipleLineView addItemToSecondView(View view){
        secondView.addItem(view);
        return this;
    }

    public void showSecondView(boolean show) {
        if (secondView == null) {
            return;
        }
        flowLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        secondView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
