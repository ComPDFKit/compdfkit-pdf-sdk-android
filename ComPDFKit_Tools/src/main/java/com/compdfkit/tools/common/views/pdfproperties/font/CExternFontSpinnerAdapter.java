/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.font;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.R;

import java.util.List;


public class CExternFontSpinnerAdapter extends BaseAdapter {

    private List<CPDFTextAttribute.FontNameHelper.FontType> list;
    List<String> externFontList = null;
    private Context mContext;

    public CExternFontSpinnerAdapter(@NonNull Context context, List<CPDFTextAttribute.FontNameHelper.FontType> fontTypes) {
        this.list = fontTypes;
        mContext = context;
    }

    public CExternFontSpinnerAdapter(@NonNull Context context, List<CPDFTextAttribute.FontNameHelper.FontType> fontTypes, List<String> fontList) {
        this.list = fontTypes;
        mContext = context;
        externFontList = fontList;
    }

    public int getStandardFontCount() {
        return list.size();
    }

    @Override
    public int getCount() {
        return list.size() + (externFontList == null ? 0 : externFontList.size());
    }

    @Override
    public Object getItem(int position) {
        if (position < list.size()) {
            return list.get(position);
        } else {
            return externFontList.get(position - list.size());
        }
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
            if (position < list.size()) {
                CPDFTextAttribute.FontNameHelper.FontType fontType = (CPDFTextAttribute.FontNameHelper.FontType) getItem(position);
                String fontName = CPDFTextAttribute.FontNameHelper.obtainFontName(fontType, false, false);
                Typeface typeface = CPDFTextAttribute.FontNameHelper.getInnerTypeface(textView.getContext(), fontName);
                textView.setTypeface(typeface);
                textView.setText(fontType == CPDFTextAttribute.FontNameHelper.FontType.Unknown ? "Unknown" : fontName);
            } else {
                textView.setText(externFontList.get(position - list.size()));
            }
        }
        return convertView;
    }
}
