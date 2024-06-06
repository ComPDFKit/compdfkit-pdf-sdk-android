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


public class CFontSpinnerAdapter extends BaseAdapter {

    private List<CPDFTextAttribute.FontNameHelper.FontType> list;

    private Context mContext;

    public CFontSpinnerAdapter(@NonNull Context context, List<CPDFTextAttribute.FontNameHelper.FontType> fontTypes) {
        this.list = fontTypes;
        mContext = context;
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
            CPDFTextAttribute.FontNameHelper.FontType fontType = (CPDFTextAttribute.FontNameHelper.FontType) getItem(position);
            String fontName = CPDFTextAttribute.FontNameHelper.obtainFontName(fontType, false, false);
            Typeface typeface = CPDFTextAttribute.FontNameHelper.getInnerTypeface(textView.getContext(), fontName);
            if (fontType == CPDFTextAttribute.FontNameHelper.FontType.Unknown){
                textView.setTypeface(Typeface.DEFAULT);
                textView.setText(CPDFTextAttribute.FontNameHelper.FontType.Unknown.name());
            } else {
                textView.setTypeface(typeface);
                textView.setText(fontName);
            }
        }
        return convertView;
    }
}
