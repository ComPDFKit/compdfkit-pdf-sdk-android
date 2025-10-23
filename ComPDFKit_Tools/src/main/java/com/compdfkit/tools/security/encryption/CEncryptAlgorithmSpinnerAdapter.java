/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.encryption;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;

import java.util.Arrays;
import java.util.List;


class CEncryptAlgorithmSpinnerAdapter extends BaseAdapter {

    public List<CPDFDocument.PDFDocumentEncryptAlgo> list;

    private Context mContext;

    private CPDFDocument.PDFDocumentEncryptAlgo encryptAlgo;

    public CEncryptAlgorithmSpinnerAdapter(@NonNull Context context, CPDFDocument.PDFDocumentEncryptAlgo encryptAlgo) {
        this.encryptAlgo = encryptAlgo;
        this.list = Arrays.asList(
                CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentRC4,
                CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentAES128,
                CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentAES256);
        mContext = context;
    }

    public void setSelectEncrypt(CPDFDocument.PDFDocumentEncryptAlgo encryptAlgo) {
        this.encryptAlgo = encryptAlgo;
    }

    public int getSelectEncryptAlgoIndex(){
        if (encryptAlgo != null){
            for (int i = 0; i < list.size(); i++) {
                CPDFDocument.PDFDocumentEncryptAlgo item = list.get(i);
                if (encryptAlgo.equals(item)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public CPDFDocument.PDFDocumentEncryptAlgo getSelectEncryptAlgo(){
        if (encryptAlgo != null){
            return encryptAlgo;
        }
        return CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentRC4;
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
            CPDFDocument.PDFDocumentEncryptAlgo item = list.get(position);
            textView.setText(getEncryptAlgorithmStr(textView.getContext(), item));
            textView.setTypeface(encryptAlgo != null && encryptAlgo == item ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        }
        return convertView;
    }


    private String getEncryptAlgorithmStr(Context context, CPDFDocument.PDFDocumentEncryptAlgo encryptAlgo){
        switch (encryptAlgo){
            case PDFDocumentRC4:
                return context.getString(R.string.tools_encryption_rc4);
            case PDFDocumentAES128:
                return context.getString(R.string.tools_encryption_128_aes);
            case PDFDocumentAES256:
                return context.getString(R.string.tools_encryption_256_aes);
            case PDFDocumentNoEncryptAlgo:
                return context.getString(R.string.tools_encryption_no_encryption);
            default:
                return context.getString(R.string.tools_encryption_128_aes);
        }
    }
}
