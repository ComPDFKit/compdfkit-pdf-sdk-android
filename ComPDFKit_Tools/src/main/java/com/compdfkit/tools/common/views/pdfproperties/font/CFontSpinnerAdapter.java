/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
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
import com.compdfkit.core.font.CPDFFont;
import com.compdfkit.core.font.CPDFFontName;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.views.pdfproperties.font.bean.CFontStyleItemBean;

import java.util.ArrayList;
import java.util.List;


public class CFontSpinnerAdapter extends BaseAdapter {

    private List<CPDFFontName> list;

    private Context mContext;

    public CFontSpinnerAdapter(@NonNull Context context) {
        list = CPDFFont.getFontName();
        mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CPDFFontName getItem(int position) {
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
            CPDFFontName cpdfFontName = list.get(position);
            String familyName = cpdfFontName.getFamilyName();
            String outFont = CPDFTextAttribute.FontNameHelper.obtainFontName(familyName, false, false);
            Typeface typeface = CPDFTextAttribute.FontNameHelper.getTypeface(mContext, outFont);
            textView.setTypeface(typeface);
            textView.setText(familyName);

        }
        return convertView;
    }

    public int getPosition(String familyName) {
        for (int i = 0; i < list.size(); i++) {
            CPDFFontName fontName = list.get(i);
            if (fontName.getFamilyName().equals(familyName)) {
                return i;
            }
        }
        return 0;
    }

    public List<CFontStyleItemBean> getFontStyleList(CPDFFontName cpdfFontName) {
        List<CFontStyleItemBean> styleItemBeans = new ArrayList<>();
        for (String styleName : cpdfFontName.getStyleName()) {
            String psName = CPDFTextAttribute.FontNameHelper.obtainFontName(cpdfFontName.getFamilyName(), styleName);
            if ("Regular".equals(styleName)) {
                styleItemBeans.add(0, new CFontStyleItemBean(styleName, psName));
            } else {
                styleItemBeans.add(new CFontStyleItemBean(styleName, psName));
            }
        }
        return styleItemBeans;
    }
}
