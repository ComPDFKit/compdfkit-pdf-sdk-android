/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
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

import com.compdfkit.tools.R;

import java.util.List;
import java.util.Locale;


public class CountryReginSpinnerAdapter extends BaseAdapter {

    public List<Locale> list;

    private Context mContext;

    private Locale selectLocal;

    public CountryReginSpinnerAdapter(@NonNull Context context, List<Locale> locales) {
        selectLocal = new Locale("", Locale.getDefault().getCountry());
        this.list = locales;
        mContext = context;
    }

    public void setSelectLocal(Locale selectLocal) {
        this.selectLocal = selectLocal;
    }

    public int getSelectPosition(){
        if (selectLocal != null){
            for (int i = 0; i < list.size(); i++) {
                Locale item = list.get(i);
                if (selectLocal.equals(item)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public String getSelectCountryRegin(){
        if (selectLocal != null){
            return selectLocal.getCountry();
        }
        return Locale.getDefault().getCountry();
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
            Locale locale = list.get(position);
            textView.setText(locale.getCountry() +" - " + locale.getDisplayName(Locale.US));
            textView.setTypeface(selectLocal != null && selectLocal == locale ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            convertView.setBackgroundResource(locale == selectLocal ? R.drawable.tools_annotation_font_bold_bg : 0);
        }
        return convertView;
    }
}
