/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.option.edit;

import android.content.Context;
import android.view.ViewGroup;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.forms.pdfproperties.option.CWidgetItemBean;

import java.util.Collections;
import java.util.List;


public class CFormEditItemsAdapter extends CBaseQuickAdapter<CWidgetItemBean, CBaseQuickViewHolder> {

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_properties_form_option_edit_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CWidgetItemBean item) {
        holder.setText(R.id.tv_title, item.getText());
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CWidgetItemBean item, List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, item, payloads);
        } else {
            onBindViewHolder(holder, position, item);
        }
    }

    public void onMove(int fromPosition, int toPosition) {
        //对原数据进行移动
        Collections.swap(list, fromPosition, toPosition);
        //通知数据移动
        notifyItemMoved(fromPosition, toPosition);
    }
}
