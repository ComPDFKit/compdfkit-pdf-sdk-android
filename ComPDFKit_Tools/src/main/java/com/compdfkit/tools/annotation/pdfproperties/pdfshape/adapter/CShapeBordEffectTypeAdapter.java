/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfshape.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.R;

import java.util.Arrays;
import java.util.List;


public class CShapeBordEffectTypeAdapter extends BaseAdapter {


    public List<CPDFAnnotation.CPDFBorderEffectType> list;

    private Context mContext;

    private CPDFAnnotation.CPDFBorderEffectType selectBordEffectType;

    public CShapeBordEffectTypeAdapter(@NonNull Context context) {
        this.list = Arrays.asList(CPDFAnnotation.CPDFBorderEffectType.CPDFBorderEffectTypeSolid, CPDFAnnotation.CPDFBorderEffectType.CPDFBorderEffectTypeCloudy);
        mContext = context;
    }

    public void setBordEffectType(CPDFAnnotation.CPDFBorderEffectType bordEffectType) {
        this.selectBordEffectType = bordEffectType;
    }

    public int getSelectPosition(){
        if (selectBordEffectType != null){
            for (int i = 0; i < list.size(); i++) {
                CPDFAnnotation.CPDFBorderEffectType item = list.get(i);
                if (selectBordEffectType.equals(item)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public CPDFAnnotation.CPDFBorderEffectType getSelectBordEffectType(){
        if (selectBordEffectType != null){
            return selectBordEffectType;
        }
        return CPDFAnnotation.CPDFBorderEffectType.CPDFBorderEffectTypeSolid;
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
        CEffectTypeViewHolder holder;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.tools_spinner_list_image_item, null);
            holder = new CEffectTypeViewHolder();
            holder.ivEffectType = convertView.findViewById(R.id.iv_image_menu);
            convertView.setTag(holder);
        }else {
            holder = (CEffectTypeViewHolder) convertView.getTag();
        }
        CPDFAnnotation.CPDFBorderEffectType shapeStyle = list.get(position);
        holder.ivEffectType.setImageResource(shapeStyle == CPDFAnnotation.CPDFBorderEffectType.CPDFBorderEffectTypeSolid ?
                R.drawable.tools_bord_effect_type_solid : R.drawable.tools_bord_effect_type_cloudy);
        return convertView;
    }

    static class CEffectTypeViewHolder {
        AppCompatImageView ivEffectType;
    }
}
