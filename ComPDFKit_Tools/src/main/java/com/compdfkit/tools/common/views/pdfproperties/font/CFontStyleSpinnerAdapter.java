/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.views.pdfproperties.font;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.views.pdfproperties.font.bean.CFontStyleItemBean;

import java.util.ArrayList;
import java.util.List;


public class CFontStyleSpinnerAdapter extends BaseAdapter {

    private List<CFontStyleItemBean> list;

    private Context mContext;

    public CFontStyleSpinnerAdapter(@NonNull Context context) {
        list = new ArrayList<>();
        mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CFontStyleItemBean getItem(int position) {
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
        if (convertView != null) {
            AppCompatTextView textView = convertView.findViewById(R.id.tv_menu_title);
            textView.setText(list.get(position).getStyleName());
        }
        return convertView;
    }

    public void resetFontStyles(List<CFontStyleItemBean> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
}