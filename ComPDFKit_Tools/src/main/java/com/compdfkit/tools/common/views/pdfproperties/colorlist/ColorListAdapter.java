/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.colorlist;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.google.android.material.color.MaterialColors;

import java.util.List;

class ColorListAdapter extends CBaseQuickAdapter<CColorItemBean, CBaseQuickViewHolder> {

    public static final String REFRESH_COLOR_SELECT = "refresh_color";

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_color_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CColorItemBean item) {
        holder.setItemHorizontalMargin(list, 12, 4, 12);
        holder.setVisible(R.id.iv_color, !item.isColorPicker());
        holder.getView(R.id.iv_color_select).setVisibility(item.isSelect() ? View.VISIBLE : View.INVISIBLE);
        holder.setVisible(R.id.iv_color_picker, item.isColorPicker());
        if (!item.isColorPicker()) {
            if (item.getColor() == Color.TRANSPARENT) {
                holder.setImageResource(R.id.iv_color, R.drawable.tools_ic_color_transparent);
            }else {
                GradientDrawable drawable = (GradientDrawable) holder.getView(R.id.iv_color).getBackground();
                drawable.setColor(item.getColor());
                if (item.getColor() == Color.WHITE){
                    drawable.setStroke(CDimensUtils.dp2px(holder.itemView.getContext(), 1),
                            MaterialColors.getColor(holder.itemView.getContext(),  com.google.android.material.R.attr.dividerColor,
                                    ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_divider_color)));
                }else {
                    drawable.setStroke(CDimensUtils.dp2px(holder.itemView.getContext(), 2),
                            Color.TRANSPARENT);
                }
                holder.setImageDrawable(R.id.iv_color, drawable);
            }
        }
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CColorItemBean item, List<Object> payloads) {
        this.onBindViewHolder(holder, position);
    }

    public void selectItem(int position) {
        for (int i = 0; i < list.size(); i++) {
            CColorItemBean bean = list.get(i);
            if (i == position) {
                if (bean.isSelect()) {
                    continue;
                }
                bean.setSelect(true);
            } else {
                bean.setSelect(false);
            }
            notifyItemChanged(i, REFRESH_COLOR_SELECT);
        }
    }

    public void selectByColor(@ColorInt int color) {
        for (int i = 0; i < list.size(); i++) {
            CColorItemBean bean = list.get(i);
            if (bean.getColor() == color) {
                bean.setSelect(true);
            } else {
                bean.setSelect(false);
            }
            notifyItemChanged(i, REFRESH_COLOR_SELECT);
        }
        if (!hasSelectColor() && hasColorPickerItem()){
            CColorItemBean colorPickerItem = list.get(list.size() - 1);
            if (colorPickerItem.isColorPicker()) {
                colorPickerItem.setSelect(true);
            }
            notifyItemChanged(list.size() - 1, REFRESH_COLOR_SELECT);
        }
    }

    public void selectIndex(int index){
        for (int i = 0; i < list.size(); i++) {
            CColorItemBean bean = list.get(i);
            bean.setSelect(i == index);
            notifyItemChanged(i, REFRESH_COLOR_SELECT);
        }
    }

    public int getSelectColor(){
        for (int i = 0; i < list.size(); i++) {
            CColorItemBean bean = list.get(i);
            if (bean.isSelect()) {
                return bean.getColor();
            }
        }
        return -1;
    }

    public boolean hasSelectColor(){
        for (int i = 0; i < list.size(); i++) {
            CColorItemBean bean = list.get(i);
            if (bean.isSelect() && !bean.isColorPicker()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasColorPickerItem(){
        for (int i = 0; i < list.size(); i++) {
            CColorItemBean bean = list.get(i);
            if (bean.isColorPicker()) {
                return true;
            }
        }
        return false;
    }
}
