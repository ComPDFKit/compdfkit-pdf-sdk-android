/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.importcert.create.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.tools.R;

import java.util.List;


public class PurposeSpinnerAdapter extends BaseAdapter {

    public List<CPDFSignature.CertUsage> list;

    private Context mContext;

    private CPDFSignature.CertUsage selectCertUsage;

    public PurposeSpinnerAdapter(@NonNull Context context, List<CPDFSignature.CertUsage> data) {
        this.list = data;
        mContext = context;
    }

    public void setSelectUseAge(CPDFSignature.CertUsage certUsage) {
        this.selectCertUsage = certUsage;
    }

    public int getSelectPosition(){
        if (selectCertUsage != null){
            for (int i = 0; i < list.size(); i++) {
                CPDFSignature.CertUsage item = list.get(i);
                if (selectCertUsage.equals(item)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public CPDFSignature.CertUsage getSelectUsage(){
        if (selectCertUsage != null){
            return selectCertUsage;
        }
        return CPDFSignature.CertUsage.PDFDigSig;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.tools_spinner_list_item, null);
        if (convertView != null){
            AppCompatTextView textView = convertView.findViewById(R.id.tv_menu_title);
            CPDFSignature.CertUsage certUsage = list.get(position);
            switch (certUsage){
                case PDFDigSig:
                    textView.setText(mContext.getString(R.string.tools_digital_signatures));
                    break;
                case PDFDataEnc:
                    textView.setText(mContext.getString(R.string.tools_data_encryption));
                    break;
                default:
                    textView.setText(mContext.getString(R.string.tools_digital_signatures_and_data_encryption));
                    break;
            }
            textView.setTypeface(selectCertUsage != null && selectCertUsage == certUsage ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            convertView.setBackgroundResource(certUsage == selectCertUsage ? R.drawable.tools_annotation_font_bold_bg : 0);
        }
        return convertView;
    }
}
