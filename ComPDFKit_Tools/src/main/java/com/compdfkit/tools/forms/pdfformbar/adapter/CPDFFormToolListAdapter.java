/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfformbar.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.forms.pdfformbar.bean.CFormToolBean;
import com.google.android.material.color.MaterialColors;

import java.util.List;


public class CPDFFormToolListAdapter extends CBaseQuickAdapter<CFormToolBean, CBaseQuickViewHolder> {

    public static final String REFRESH_ITEM = "refresh_item";

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_fun_tool_bar_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CFormToolBean item) {
        holder.setImageResource(R.id.iv_annot_type, item.getIconResId());
        CardView cardView = holder.getView(R.id.card_view);
        int selectColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_annot_list_item_select_bg_color);
        int normalColor = MaterialColors.getColor(holder.itemView.getContext(), android.R.attr.colorPrimary,
                ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_color_primary));
        cardView.setCardBackgroundColor(item.isSelect() ? selectColor : normalColor);
        refreshAnnotColor(holder, item);
        holder.setItemHorizontalMargin(list, 16, 0, 16);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CFormToolBean item, List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, item, payloads);
        } else {
            for (Object payload : payloads) {
                if (payload == REFRESH_ITEM) {
                    CardView cardView = holder.getView(R.id.card_view);
                    int selectColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_annot_list_item_select_bg_color);
                    int normalColor = MaterialColors.getColor(holder.itemView.getContext(), android.R.attr.colorPrimary,
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_color_primary));
                    cardView.setCardBackgroundColor(item.isSelect() ? selectColor : normalColor);
                    refreshAnnotColor(holder, item);
                }
            }
        }
    }

    private void refreshAnnotColor(CBaseQuickViewHolder holder, CFormToolBean item) {
        holder.setBackgroundColor(R.id.iv_annot_type, Color.TRANSPARENT);
        Context context = holder.itemView.getContext();
        if (item.isSelect()) {
            holder.setImageTintList(R.id.iv_annot_type, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.tools_annot_icon_select_color)));
        } else {
            holder.setImageTintList(R.id.iv_annot_type, ColorStateList.valueOf(MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnPrimary, ContextCompat.getColor(context, R.color.tools_text_color_primary))));
        }
    }

    public void selectItem(int position) {
        for (int i = 0; i < list.size(); i++) {
            CFormToolBean item = list.get(i);
            if (i == position) {
                item.setSelect(!item.isSelect());
            } else {
                item.setSelect(false);
            }
            notifyItemChanged(i, REFRESH_ITEM);
        }
    }

    public void selectByType(CPDFWidget.WidgetType type) {
        for (int i = 0; i < list.size(); i++) {
            CFormToolBean item = list.get(i);
            if (item.getType() == type) {
                item.setSelect(true);
            } else {
                item.setSelect(false);
            }
            notifyItemChanged(i, REFRESH_ITEM);
        }
    }

    public boolean hasSelectAnnotType() {
        for (int i = 0; i < list.size(); i++) {
            CFormToolBean item = list.get(i);
            if (item.isSelect()) {
                return true;
            }
        }
        return false;
    }

    public CPDFWidget.WidgetType getCurrentWidgetType() {
        for (CFormToolBean cAnnotToolBean : list) {
            if (cAnnotToolBean.isSelect()) {
                return cAnnotToolBean.getType();
            }
        }
        return CPDFWidget.WidgetType.Widget_Unknown;
    }

    public boolean annotEnableSetting() {
        CPDFWidget.WidgetType type = getCurrentWidgetType();
        return type != CPDFWidget.WidgetType.Widget_Unknown;
    }

    public void updateItemColor(CPDFWidget.WidgetType type, @ColorInt int color) {
        for (int i = 0; i < list.size(); i++) {
            CFormToolBean item = list.get(i);
            if (item.getType() == type) {
                item.setBgColor(color);
                notifyItemChanged(i, REFRESH_ITEM);
                return;
            }
        }
    }

    public void updateItemColorOpacity(CPDFWidget.WidgetType type,
                                       @IntRange(from = 0, to = 255) int opacity) {
        for (int i = 0; i < list.size(); i++) {
            CFormToolBean item = list.get(i);
            if (item.getType() == type) {
                item.setColorOpacity(opacity);
                notifyItemChanged(i, REFRESH_ITEM);
                return;
            }
        }
    }

    public void updateItem(CPDFWidget.WidgetType type,
                           @ColorInt int color,
                           @IntRange(from = 0, to = 255) int opacity) {
        for (int i = 0; i < list.size(); i++) {
            CFormToolBean item = list.get(i);
            if (item.getType() == type) {
                item.setColorOpacity(opacity);
                item.setBgColor(color);
                notifyItemChanged(i, REFRESH_ITEM);
                return;
            }
        }
    }
}
