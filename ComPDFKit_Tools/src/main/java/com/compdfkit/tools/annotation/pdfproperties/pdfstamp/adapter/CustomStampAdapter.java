/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.annotation.pdfproperties.pdfstamp.adapter;


import android.content.Context;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CCustomStampBean;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CTextStampBean;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.views.pdfproperties.stamp.CPDFStampTextView;

public class CustomStampAdapter extends CBaseQuickAdapter<CCustomStampBean, CBaseQuickViewHolder> {
    public CustomStampAdapter() {
    }

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        if (viewType == CCustomStampBean.ITEM_TEXT_STAMP_TITLE || viewType == CCustomStampBean.ITEM_IMAGE_STAMP_TITLE) {
            return new CBaseQuickViewHolder(R.layout.tools_annot_stamp_custom_stamp_title, parent);
        } else if (viewType == CCustomStampBean.ITEM_TEXT_STAMP) {
            return new CBaseQuickViewHolder(R.layout.tools_properties_stamp_text_stamp_list_item, parent);
        } else {
            return new CBaseQuickViewHolder(R.layout.tools_properties_stamp_image_stamp_list_item, parent);
        }
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CCustomStampBean item) {
        switch (item.getItemType()) {
            case CCustomStampBean.ITEM_TEXT_STAMP_TITLE:
            case CCustomStampBean.ITEM_IMAGE_STAMP_TITLE:
                holder.setText(R.id.tv_custom_text_stamp_title, item.getTitle());
                break;
            case CCustomStampBean.ITEM_TEXT_STAMP:
                CPDFStampTextView stampTextView = holder.getView(R.id.stamp_text_view);
                CTextStampBean textStampBean = item.getTextStampBean();
                stampTextView.setShape(CPDFStampAnnotation.TextStampShape.valueOf(textStampBean.getTextStampShapeId()));
                stampTextView.setColor(textStampBean.getBgColor());
                stampTextView.setContent(textStampBean.getTextContent());
                stampTextView.setDateSwitch(textStampBean.isShowDate());
                stampTextView.setTimeSwitch(textStampBean.isShowTime());
                break;
            case CCustomStampBean.ITEM_IMAGE_STAMP:
                AppCompatImageView imageView = holder.getView(R.id.iv_stamp_image);
                Glide.with(holder.itemView.getContext())
                        .load(item.getImageStampPath())
                        .into(imageView);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getItemType();
    }

    public void checkRemoveTextStampTitle() {
        boolean hasTextStamp = false;
        int textStampTitleIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            CCustomStampBean bean = list.get(i);
            if (bean.getItemType() == CCustomStampBean.ITEM_TEXT_STAMP_TITLE) {
                textStampTitleIndex = i;
            }
            if (bean.getItemType() == CCustomStampBean.ITEM_TEXT_STAMP) {
                hasTextStamp = true;
                break;
            }
        }
        if (!hasTextStamp && textStampTitleIndex >= 0) {
            remove(textStampTitleIndex);
        }
    }

    public void checkRemoveImageStampTitle() {
        boolean hasImageStamp = false;
        int imageStampTitleIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            CCustomStampBean bean = list.get(i);
            if (bean.getItemType() == CCustomStampBean.ITEM_IMAGE_STAMP_TITLE) {
                imageStampTitleIndex = i;
            }
            if (bean.getItemType() == CCustomStampBean.ITEM_IMAGE_STAMP) {
                hasImageStamp = true;
                break;
            }
        }
        if (!hasImageStamp && imageStampTitleIndex >= 0) {
            remove(imageStampTitleIndex);
        }
    }
}
