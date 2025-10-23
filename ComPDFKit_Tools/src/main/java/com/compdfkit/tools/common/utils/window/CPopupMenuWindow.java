/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.window;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;


public class CPopupMenuWindow extends CBasePopupWindow {

    private LinearLayout llRoot;

    public CPopupMenuWindow(Context context) {
        super(context);
    }

    @Override
    protected View setLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.tools_pdf_tool_bar_more_menu_layout, null);
    }

    @Override
    protected void initResource() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initView() {
        llRoot = mContentView.findViewById(R.id.ll_root);
    }

    @Override
    protected void onClickListener(View view) {
    }

    public void addItem(@DrawableRes int iconResId, @StringRes int stringResId, View.OnClickListener clickListener){
        View itemView = CViewUtils.getThemeLayoutInflater(mContext).inflate(R.layout.tools_menu_window_item, null);
        AppCompatImageView ivIcon = itemView.findViewById(R.id.iv_menu_icon);
        AppCompatTextView tvTitle = itemView.findViewById(R.id.tv_menu_title);
        ivIcon.setImageResource(iconResId);
        tvTitle.setText(stringResId);
        itemView.findViewById(R.id.ll_root).setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v);
            }
            dismiss();
        });
        if (llRoot != null) {
            llRoot.addView(itemView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CDimensUtils.dp2px(mContext, 48)));
        }
    }

    public void addItem(@StringRes int stringResId, View.OnClickListener clickListener){
        addItem(stringResId, true, clickListener);
    }

    public void addItem(@StringRes int stringResId,boolean enable, View.OnClickListener clickListener){
        View itemView = CViewUtils.getThemeLayoutInflater(mContext).inflate(R.layout.tools_menu_window_item, null);
        AppCompatImageView ivIcon = itemView.findViewById(R.id.iv_menu_icon);
        AppCompatTextView tvTitle = itemView.findViewById(R.id.tv_menu_title);
        ivIcon.setVisibility(View.GONE);
        ivIcon.setEnabled(enable);
        tvTitle.setText(stringResId);
        tvTitle.setEnabled(enable);
        itemView.setEnabled(enable);
        itemView.findViewById(R.id.ll_root).setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v);
            }
            dismiss();
        });
        if (llRoot != null) {
            llRoot.addView(itemView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CDimensUtils.dp2px(mContext, 48)));
        }
    }
}
