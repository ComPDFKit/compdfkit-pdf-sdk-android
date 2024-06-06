/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationlist.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationlist.bean.CPDFAnnotListItem;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;

import java.util.HashMap;


public class CPDFAnnotListAdapter  extends CBaseQuickAdapter<CPDFAnnotListItem, CBaseQuickViewHolder> {

    private static final int ITEM_HEADER = 1;

    private static final int ITEM_CONTENT = 2;

    private static final HashMap<CPDFAnnotation.Type, Integer> ICON_RES_IDS = new HashMap<>();

    static {
        ICON_RES_IDS.put(CPDFAnnotation.Type.TEXT, R.drawable.tools_ic_annotation_note);
        ICON_RES_IDS.put(CPDFAnnotation.Type.HIGHLIGHT, R.drawable.tools_ic_annotation_highlight_default_bg);
        ICON_RES_IDS.put(CPDFAnnotation.Type.UNDERLINE, R.drawable.tools_ic_annotation_underline_default_bg);
        ICON_RES_IDS.put(CPDFAnnotation.Type.SQUIGGLY, R.drawable.tools_ic_annotation_squiggly_default_bg);
        ICON_RES_IDS.put(CPDFAnnotation.Type.STRIKEOUT, R.drawable.tools_ic_annotation_strikeout_default_bg);
        ICON_RES_IDS.put(CPDFAnnotation.Type.FREETEXT, R.drawable.tools_ic_annotation_freetext);
        ICON_RES_IDS.put(CPDFAnnotation.Type.INK, R.drawable.tools_ic_annotation_ink_default_bg);
        ICON_RES_IDS.put(CPDFAnnotation.Type.LINE, R.drawable.tools_ic_annotation_shape_line);
        ICON_RES_IDS.put(CPDFAnnotation.Type.SQUARE, R.drawable.tools_ic_annotation_shape_rectangle);
        ICON_RES_IDS.put(CPDFAnnotation.Type.CIRCLE, R.drawable.tools_ic_annotation_shape_circular);
        ICON_RES_IDS.put(CPDFAnnotation.Type.STAMP, R.drawable.tools_ic_annotation_stamp);
        ICON_RES_IDS.put(CPDFAnnotation.Type.LINK, R.drawable.tools_ic_annotation_link);
        ICON_RES_IDS.put(CPDFAnnotation.Type.SOUND, R.drawable.tools_ic_annotation_sound);
    }


    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        if (viewType == ITEM_HEADER){
            return new CBaseQuickViewHolder(R.layout.tools_bota_annotation_list_item_header, parent);
        }else {
            return new CBaseQuickViewHolder(R.layout.tools_bota_annotation_list_item_content, parent);
        }
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CPDFAnnotListItem item) {
        if (item.isHeader()){
            holder.setText(R.id.id_item_annot_head_page, String.valueOf(item.getPage() + 1));
            holder.setText(R.id.id_item_annot_count, String.valueOf(item.getAnnotationCount()));
        }else {
            Integer annotIcon = ICON_RES_IDS.get(item.getAnnotType());
            if (annotIcon != null) {
                if (item.getAnnotType() == CPDFAnnotation.Type.LINE){
                    if (item.isArrowLine()){
                        holder.setImageResource(R.id.iv_annot_icon, R.drawable.tools_ic_annotation_shape_arrow);
                    }else {
                        holder.setImageResource(R.id.iv_annot_icon, annotIcon);
                    }
                }else {
                    holder.setImageResource(R.id.iv_annot_icon, annotIcon);
                }
            }
            switch (item.getAnnotType()){
                case TEXT:
                case SQUARE:
                case CIRCLE:
                case LINE:
                    holder.setImageTintList(R.id.iv_annot_icon, ColorStateList.valueOf(item.getColor()));
                    holder.setBackgroundColor(R.id.view_icon_bg, Color.TRANSPARENT);
                    break;
                case HIGHLIGHT:
                case UNDERLINE:
                case SQUIGGLY:
                case STRIKEOUT:
                case INK:
                    holder.setBackgroundColor(R.id.view_icon_bg, item.getColor());
                    holder.setImageTintList(R.id.iv_annot_icon, null);
                    break;
                case FREETEXT:
                case STAMP:
                case SOUND:
                    holder.setBackgroundColor(R.id.view_icon_bg, 0);
                    holder.setImageTintList(R.id.iv_annot_icon, ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_text_color_primary)));
                    break;
                default:
                    holder.setBackgroundColor(R.id.view_icon_bg, 0);
                    holder.setImageTintList(R.id.iv_annot_icon, null);
                    break;
            }
            holder.setVisible(R.id.tv_annot_content, !TextUtils.isEmpty(item.getContent()));
            holder.setText(R.id.tv_annot_content, item.getContent());
            holder.setText(R.id.tv_annot_date, item.getModifyDate());
        }

    }

    @Override
    public int getItemViewType(int position) {
       return list.get(position).isHeader() ? ITEM_HEADER : ITEM_CONTENT;
    }

}
