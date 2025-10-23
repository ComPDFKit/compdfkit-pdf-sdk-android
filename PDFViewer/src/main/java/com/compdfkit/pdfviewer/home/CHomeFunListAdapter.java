/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home;

import static com.compdfkit.tools.common.utils.date.CDateUtil.NORMAL_DATE_FORMAT_1;

import android.content.Context;
import android.view.ViewGroup;

import com.compdfkit.pdfviewer.R;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.date.CDateUtil;

import java.io.File;


public class CHomeFunListAdapter extends CBaseQuickAdapter<HomeFunBean, CBaseQuickViewHolder> {

    public static final int ITEM_TITLE = 0;

    public static final int ITEM_CONTENT = 1;

    @Override
    public CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        if (viewType == ITEM_TITLE){
            return new CBaseQuickViewHolder(R.layout.rv_item_home_fun_item_title, parent);
        }else {
            return new CBaseQuickViewHolder(R.layout.rv_item_home_fun_item_content, parent);
        }
    }

    @Override
    public void onBindViewHolder(CBaseQuickViewHolder holder, int position, HomeFunBean item) {
        if (item.isHead()) {
            holder.setText(R.id.tv_title, item.getTitle());
        } else {
            holder.setText(R.id.tv_title, item.getTitle());

            if (item.getType() == HomeFunBean.FunType.SamplePDF){
                File file = new File(item.getFilePath());
                String lastModifyTime = CDateUtil.format(file.lastModified(), NORMAL_DATE_FORMAT_1);
                String fileSize = CFileUtils.fileSizeFormat(file.length()).replace(" ", "");

                holder.setText(R.id.tv_desc, lastModifyTime + "  " + fileSize);
                holder.setVisible(R.id.iv_pdf_sample_icon, true);
                holder.setVisible(R.id.iv_icon, false);
                holder.setVisible(R.id.tv_new, false);

            }else {
                holder.setText(R.id.tv_desc, item.getDescription());
                holder.setImageResource(R.id.iv_icon, item.getIconResId());
                holder.setVisible(R.id.iv_pdf_sample_icon, false);
                holder.setVisible(R.id.iv_icon, true);
                holder.setVisible(R.id.tv_new, item.isNew());

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isHead()) {
            return ITEM_TITLE;
        }else {
            return ITEM_CONTENT;
        }
    }
}
