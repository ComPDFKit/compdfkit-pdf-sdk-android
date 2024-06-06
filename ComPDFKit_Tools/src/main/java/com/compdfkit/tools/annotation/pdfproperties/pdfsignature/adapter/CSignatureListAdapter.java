/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfsignature.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;


public class CSignatureListAdapter extends CBaseQuickAdapter<String, CBaseQuickViewHolder> {

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_properties_signature_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, String item) {
        AppCompatImageView imageView = holder.getView(R.id.iv_signature_image);
        Glide.with(holder.itemView.getContext())
                .load(item)
                .into(imageView);
    }
}
