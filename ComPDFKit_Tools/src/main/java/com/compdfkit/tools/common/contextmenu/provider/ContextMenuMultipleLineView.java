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
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.compdfkit.tools.R;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuMultipleLineView extends LinearLayout {
    Context mContext;
    List<ContextMenuView> lineList = new ArrayList<>();
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
    }

    public void setLineNumber(int number) {
        for (int i = 0 ; i < number; i++) {
            ContextMenuView view = new ContextMenuView(mContext);
            lineList.add(view);
            addView(view);
        }
    }

    public ContextMenuMultipleLineView addItem(@StringRes int titleRes, int lineIndex, OnClickListener clickListener) {
        if (lineIndex >= lineList.size()) {
            return this;
        }
        lineList.get(lineIndex).addItem(titleRes, clickListener);
        return this;
    }

    public void addSecondView() {
        ContextMenuView view = new ContextMenuView(mContext);
        addView(view);
        secondView = view;
        secondView.setGravity(Gravity.CENTER_VERTICAL);
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
        for (int i = 0; i < lineList.size(); i++) {
            lineList.get(i).setVisibility(show == true ? View.GONE : View.VISIBLE);
        }
        secondView.setVisibility(show == true ? View.VISIBLE : View.GONE);
    }
}
