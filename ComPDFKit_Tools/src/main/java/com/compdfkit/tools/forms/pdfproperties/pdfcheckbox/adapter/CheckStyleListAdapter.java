/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfcheckbox.adapter;


import android.content.Context;
import android.view.ViewGroup;

import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;

import java.util.ArrayList;

public class CheckStyleListAdapter extends CBaseQuickAdapter<CPDFWidget.CheckStyle, CBaseQuickViewHolder> {

    private CPDFWidget.CheckStyle selectCheckStyle = CPDFWidget.CheckStyle.CK_Check;

    public CheckStyleListAdapter() {
        ArrayList<CPDFWidget.CheckStyle> lineTypes = new ArrayList<>();
        lineTypes.add(CPDFWidget.CheckStyle.CK_Check);
        lineTypes.add(CPDFWidget.CheckStyle.CK_Circle);
        lineTypes.add(CPDFWidget.CheckStyle.CK_Cross);
        lineTypes.add(CPDFWidget.CheckStyle.CK_Diamond);
        lineTypes.add(CPDFWidget.CheckStyle.CK_Square);
        lineTypes.add(CPDFWidget.CheckStyle.CK_Star);
        setList(lineTypes);
    }

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_properties_check_box_style_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CPDFWidget.CheckStyle item) {
        holder.setSelected(R.id.iv_check_box_status, item == selectCheckStyle);
        switch (item){
            case CK_Check:
                holder.setImageResource(R.id.iv_check_box, R.drawable.tools_ic_check_box_check);
                holder.setText(R.id.tv_check_box, R.string.tools_check_box_check);
                break;
            case CK_Circle:
                holder.setImageResource(R.id.iv_check_box, R.drawable.tools_ic_check_box_circle);
                holder.setText(R.id.tv_check_box, R.string.tools_check_box_circle);
                break;
            case CK_Cross:
                holder.setImageResource(R.id.iv_check_box, R.drawable.tools_ic_check_box_cross);
                holder.setText(R.id.tv_check_box, R.string.tools_check_box_cross);
                break;
            case CK_Diamond:
                holder.setImageResource(R.id.iv_check_box, R.drawable.tools_ic_check_box_diamond);
                holder.setText(R.id.tv_check_box, R.string.tools_check_box_diamond);
                break;
            case CK_Square:
                holder.setImageResource(R.id.iv_check_box, R.drawable.tools_ic_check_box_square);
                holder.setText(R.id.tv_check_box, R.string.tools_check_box_square);
                break;
            case CK_Star:
                holder.setImageResource(R.id.iv_check_box, R.drawable.tools_ic_check_box_star);
                holder.setText(R.id.tv_check_box, R.string.tools_check_box_start);
                break;
            default:break;
        }
    }

    public void setSelectCheckStyle(CPDFWidget.CheckStyle checkStyle) {
        this.selectCheckStyle = checkStyle;
        notifyDataSetChanged();
    }
}
