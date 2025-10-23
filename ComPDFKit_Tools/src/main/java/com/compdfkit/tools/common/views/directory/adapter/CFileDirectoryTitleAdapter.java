/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.directory.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.ViewGroup;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.views.directory.bean.CFileDirectoryTitleBean;

import java.io.File;


public class CFileDirectoryTitleAdapter extends CBaseQuickAdapter<CFileDirectoryTitleBean, CBaseQuickViewHolder> {

    public static final int ITEM_TITLE = 0;

    public static final int ITEM_TITLE_SEPARATOR = 1;

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        if (viewType == ITEM_TITLE) {
            return new CBaseQuickViewHolder(R.layout.tools_file_directory_list_title_item, parent);
        } else {
            return new CBaseQuickViewHolder(R.layout.tools_file_directory_list_title_separator_item, parent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isSeparator() ?
                ITEM_TITLE_SEPARATOR : ITEM_TITLE;
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CFileDirectoryTitleBean item) {
        if (item.isSeparator()) {

        } else {
            File file = item.getFile();
            if (file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())){
                holder.setText(R.id.tv_name, "SD Card");
            }else {
                holder.setText(R.id.tv_name, item.getFile().getName());
            }
        }
    }

    public void removeRange(int startPosition){
        for (int i = list.size() - 1; i > startPosition; i--) {
            list.remove(i);
            notifyItemRemoved(i);
        }
    }

    public void toupperLevel(){
        for (int i = list.size() - 1; i >= 0; i--) {
            CFileDirectoryTitleBean bean = list.get(i);
            if (!bean.isSeparator() && i != list.size() -1){
                removeRange(i);
                return;
            }
        }
    }


    public String getLastFolder(){
        for (int i = list.size() - 1; i >= 0; i--) {
            CFileDirectoryTitleBean bean = list.get(i);
            if (!bean.isSeparator()){
                return bean.getFile().getAbsolutePath();
            }
        }
        return "";
    }
}
