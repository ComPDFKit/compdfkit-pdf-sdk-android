/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.option.select;

import android.content.Context;
import android.view.ViewGroup;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.forms.pdfproperties.option.CWidgetItemBean;


public class CFormOptionListAdapter extends CBaseQuickAdapter<CWidgetItemBean, CBaseQuickViewHolder> {

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_properties_form_option_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CWidgetItemBean item) {
        holder.setText(R.id.tv_check_box, item.getText());
        holder.setSelected(R.id.iv_check_box_status, item.isSelect());
    }

    public void setSelect(int index){
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelect(index == i);
        }
        notifyDataSetChanged();
    }

}
