/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfshape.adapter;


import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.views.pdfproperties.preview.CAnnotLineTypePreviewView;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;

public class CLineTypeListAdapter extends CBaseQuickAdapter<CPDFLineAnnotation.LineType, CBaseQuickViewHolder> {

    private CPDFLineAnnotation.LineType selectLineType = CPDFLineAnnotation.LineType.LINETYPE_NONE;

    private boolean selectLineIsStart = true;

    public CLineTypeListAdapter() {
        ArrayList<CPDFLineAnnotation.LineType> lineTypes = new ArrayList<>();
        lineTypes.add(CPDFLineAnnotation.LineType.LINETYPE_NONE);
        lineTypes.add(CPDFLineAnnotation.LineType.LINETYPE_SQUARE);
        lineTypes.add(CPDFLineAnnotation.LineType.LINETYPE_CIRCLE);
        lineTypes.add(CPDFLineAnnotation.LineType.LINETYPE_DIAMOND);
        lineTypes.add(CPDFLineAnnotation.LineType.LINETYPE_ARROW);
        lineTypes.add(CPDFLineAnnotation.LineType.LINETYPE_CLOSEDARROW);
        setList(lineTypes);
    }

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_properties_arrow_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CPDFLineAnnotation.LineType item) {
        CAnnotLineTypePreviewView previewView = holder.getView(R.id.preview_line_type);
        if (selectLineIsStart){
            previewView.setStartLineType(item);
            previewView.setTailLineType(CPDFLineAnnotation.LineType.LINETYPE_NONE);
        }else {
            previewView.setStartLineType(CPDFLineAnnotation.LineType.LINETYPE_NONE);
            previewView.setTailLineType(item);
        }
        FrameLayout frameLayout = holder.getView(R.id.fl_line_root);
        frameLayout.setSelected(item == selectLineType);
        if (item == selectLineType){
            previewView.setBorderColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_annot_icon_select_color));
        }else {
            previewView.setBorderColor(MaterialColors.getColor(holder.itemView.getContext(),  com.google.android.material.R.attr.colorOnPrimary,
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_on_primary)));
        }
    }

    public void setSelectLineType(CPDFLineAnnotation.LineType lineType, boolean isStartLine) {
        this.selectLineType = lineType;
        this.selectLineIsStart = isStartLine;
        notifyDataSetChanged();
    }

    public void setSelectLineType(CPDFLineAnnotation.LineType lineType) {
        this.selectLineType = lineType;
        notifyDataSetChanged();
    }
}
