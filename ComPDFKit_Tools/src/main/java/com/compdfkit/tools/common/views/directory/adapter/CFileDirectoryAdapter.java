package com.compdfkit.tools.common.views.directory.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;

import java.io.File;


public class CFileDirectoryAdapter extends CBaseQuickAdapter<File, CBaseQuickViewHolder> {
    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_file_directory_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, File item) {
        holder.setText(R.id.tv_name, item.getName());
    }
}
