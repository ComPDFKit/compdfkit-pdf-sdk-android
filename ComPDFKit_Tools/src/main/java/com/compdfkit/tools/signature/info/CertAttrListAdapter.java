/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.info;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.signature.bean.CPDFCertAttrDataItem;


public class CertAttrListAdapter extends CBaseQuickAdapter<CPDFCertAttrDataItem, CBaseQuickViewHolder> {

    public static final int ITEM_HEAD = 0;

    public static final int ITEM_CONTENT = 1;

    public static final int ITEM_CERT_TRUSTED = 2;

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        if (viewType == ITEM_HEAD){
            return new CBaseQuickViewHolder(R.layout.tools_sign_certificate_attributes_list_head_item, parent);
        } else if (viewType == ITEM_CERT_TRUSTED) {
            return new CBaseQuickViewHolder(R.layout.tools_sign_certificate_attributes_list_trusted_cert_item, parent);
        } else {
            return new CBaseQuickViewHolder(R.layout.tools_sign_certificate_attributes_list_item, parent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isHead()) {
            return ITEM_HEAD;
        } else if (list.get(position).isCertTrustedType()) {
            return ITEM_CERT_TRUSTED;
        } else {
            return ITEM_CONTENT;
        }
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CPDFCertAttrDataItem item) {
        if (item.isHead()) {
            holder.setText(R.id.tv_head, item.getHeadTitle());
        } else if (item.isCertTrustedType()) {
            holder.setChecked(R.id.cb_1, item.isCertIsTrusted());
            holder.setChecked(R.id.cb_2, item.isCertIsTrusted());
            holder.getView(R.id.btn_trusted).setVisibility(item.isCertIsTrusted() ? View.INVISIBLE : View.VISIBLE);
        } else {
            holder.setText(R.id.tv_attr_title, item.getTitle() + ":");
            holder.setText(R.id.tv_attr_value, item.getValue());
        }
    }
}
