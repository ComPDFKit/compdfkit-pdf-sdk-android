/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CTextStampBean;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.views.pdfproperties.stamp.CPDFStampTextView;


public class CTextStampAdapter extends CBaseQuickAdapter<CTextStampBean, CBaseQuickViewHolder> {

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_properties_stamp_text_stamp_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CTextStampBean item) {
        CPDFStampTextView stampTextView = holder.getView(R.id.stamp_text_view);
        stampTextView.setShape(CPDFStampAnnotation.TextStampShape.valueOf(item.getTextStampShapeId()));
        stampTextView.setColor(item.getBgColor());
        stampTextView.setContent(item.getTextContent());
        stampTextView.setDateSwitch(item.isShowDate());
        stampTextView.setTimeSwitch(item.isShowTime());
    }
}
