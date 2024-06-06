/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.basic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.List;


public abstract class CBasicSpinnerAdapter<T> extends BaseAdapter {

    public List<T> list;

    private Context mContext;

    private T selectItem;

    public CBasicSpinnerAdapter(@NonNull Context context, List<T> list) {
        this.list = list;
        mContext = context;
    }

    public CBasicSpinnerAdapter(@NonNull Context context, List<T> list, T selectItem) {
        this.selectItem = selectItem;
        this.list = list;
        mContext = context;
    }

    public void setSelectItem(T item) {
        this.selectItem = item;
    }

    public int getSelectItemIndex(){
        if (selectItem != null){
            for (int i = 0; i < list.size(); i++) {
                T item = list.get(i);
                if (selectItem.equals(item)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public T getSelectItem(){
        if (selectItem != null){
            return selectItem;
        }
        return null;
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
        convertView = inflater.inflate(getLayoutResIds(), null);
        if (convertView != null){
            T item = list.get(position);
            onConvert(convertView, position, item);
        }

        return convertView;
    }

    public abstract int getLayoutResIds();

    public abstract void onConvert(View convertView, int position, T item);

}
