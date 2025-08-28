/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationbar.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationbar.bean.CAnnotToolBean;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.google.android.material.color.MaterialColors;

import java.util.List;


public class CPDFAnnotationToolListAdapter extends CBaseQuickAdapter<CAnnotToolBean, CBaseQuickViewHolder> {

    public static final String REFRESH_ITEM = "refresh_item";

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_fun_tool_bar_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CAnnotToolBean item) {
        int isLightTheme = CViewUtils.getThemeAttrData(holder.itemView.getContext().getTheme(),  com.google.android.material.R.attr.isLightTheme);
        boolean isDarkTheme = isLightTheme == 0;
        holder.setImageResource(R.id.iv_annot_type, isDarkTheme && item.getIconDarkResId() != 0 ? item.getIconDarkResId() : item.getIconResId());
        CardView cardView = holder.getView(R.id.card_view);
        int selectColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_annot_list_item_select_bg_color);
        int normalColor = MaterialColors.getColor(holder.itemView.getContext(), android.R.attr.colorPrimary,
                ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_color_primary));
        cardView.setCardBackgroundColor(item.isSelect() ? selectColor : normalColor);
        refreshAnnotColor(holder, item);
        holder.setItemHorizontalMargin(list, 16, 0, 16);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CAnnotToolBean item, List<Object> payloads) {
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

    private void refreshAnnotColor(CBaseQuickViewHolder holder, CAnnotToolBean item) {
        switch (item.getType()) {
            case HIGHLIGHT:
            case UNDERLINE:
            case SQUIGGLY:
            case STRIKEOUT:
            case INK:
                int color = Color.argb(item.getColorOpacity(), Color.red(item.getBgColor()), Color.green(item.getBgColor()), Color.blue(item.getBgColor()));
                holder.setBackgroundColor(R.id.iv_annot_type, color);
                holder.setImageTintList(R.id.iv_annot_type, null);
                holder.setSelected(R.id.iv_annot_type, item.isSelect());
                break;
            case TEXT:
                holder.setBackgroundColor(R.id.iv_annot_type, Color.TRANSPARENT);
                if (item.getBgColor() == Color.TRANSPARENT) {
                    holder.setImageTintList(R.id.iv_annot_type, ColorStateList.valueOf(Color.BLACK));
                } else {
                    int textColor = Color.argb(item.getColorOpacity(), Color.red(item.getBgColor()), Color.green(item.getBgColor()), Color.blue(item.getBgColor()));
                    holder.setImageTintList(R.id.iv_annot_type, ColorStateList.valueOf(textColor));
                }
                break;
            default:
                holder.setBackgroundColor(R.id.iv_annot_type, Color.TRANSPARENT);
                Context context = holder.itemView.getContext();
                if (item.isSelect()) {
                    holder.setImageTintList(R.id.iv_annot_type, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.tools_annot_icon_select_color)));
                } else {
                    holder.setImageTintList(R.id.iv_annot_type, ColorStateList.valueOf(MaterialColors.getColor(context,  com.google.android.material.R.attr.colorOnPrimary, ContextCompat.getColor(context, R.color.tools_text_color_primary))));
                }
                break;
        }
    }


    public void selectItem(int position) {
        for (int i = 0; i < list.size(); i++) {
            CAnnotToolBean item = list.get(i);
            if (i == position) {
                item.setSelect(!item.isSelect());
            } else {
                item.setSelect(false);
            }
            notifyItemChanged(i, REFRESH_ITEM);
        }
    }

    public void selectByType(CAnnotationType type) {
        for (int i = 0; i < list.size(); i++) {
            CAnnotToolBean item = list.get(i);
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
            CAnnotToolBean item = list.get(i);
            if (item.isSelect()) {
                return true;
            }
        }
        return false;
    }

    public CAnnotationType getCurrentAnnotType() {
        for (CAnnotToolBean cAnnotToolBean : list) {
            if (cAnnotToolBean.isSelect()) {
                return cAnnotToolBean.getType();
            }
        }
        return CAnnotationType.UNKNOWN;
    }

    public CAnnotToolBean getSelectItem() {
        for (CAnnotToolBean cAnnotToolBean : list) {
            if (cAnnotToolBean.isSelect()) {
                return cAnnotToolBean;
            }
        }
        return null;
    }



    public boolean annotEnableSetting() {
        CAnnotationType type = getCurrentAnnotType();
        CAnnotToolBean item = getSelectItem();
        return type != CAnnotationType.SIGNATURE &&
                type != CAnnotationType.STAMP &&
                type != CAnnotationType.PIC &&
                type != CAnnotationType.LINK &&
                type != CAnnotationType.SOUND &&
                type != CAnnotationType.UNKNOWN &&
                type != CAnnotationType.INK_ERASER;
    }

    public void updateItemColor(CAnnotationType type,@ColorInt int color) {
        for (int i = 0; i < list.size(); i++) {
            CAnnotToolBean item = list.get(i);
            if (item.getType() == type) {
                item.setBgColor(color);
                notifyItemChanged(i, REFRESH_ITEM);
                return;
            }
        }
    }

    public void updateItemColorOpacity(CAnnotationType type,
                                       @IntRange(from = 0, to = 255) int opacity) {
        for (int i = 0; i < list.size(); i++) {
            CAnnotToolBean item = list.get(i);
            if (item.getType() == type) {
                item.setColorOpacity(opacity);
                notifyItemChanged(i, REFRESH_ITEM);
                return;
            }
        }
    }

    public void updateItem(CAnnotationType type,
                                       @ColorInt int color,
                                       @IntRange(from = 0, to = 255) int opacity) {
        for (int i = 0; i < list.size(); i++) {
            CAnnotToolBean item = list.get(i);
            if (item.getType() == type) {
                item.setColorOpacity(opacity);
                item.setBgColor(color);
                notifyItemChanged(i, REFRESH_ITEM);
                return;
            }
        }
    }
}
