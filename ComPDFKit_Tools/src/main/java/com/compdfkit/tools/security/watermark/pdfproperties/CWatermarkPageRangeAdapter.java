/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.watermark.pdfproperties;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.adapter.CBasicSpinnerAdapter;

import java.util.List;


class CWatermarkPageRangeAdapter extends CBasicSpinnerAdapter<CPageRange> {

    public CWatermarkPageRangeAdapter(@NonNull Context context, List<CPageRange> list, CPageRange selectItem) {
        super(context, list, selectItem);
    }

    @Override
    public int getLayoutResIds() {
        return R.layout.tools_spinner_list_item;
    }

    @Override
    public void onConvert(View convertView, int position, CPageRange item) {
        AppCompatTextView titleView = convertView.findViewById(R.id.tv_menu_title);
        if (item == CPageRange.CurrentPage) {
            titleView.setText(R.string.tools_current_page);
        } else {
            titleView.setText(R.string.tools_all_page);
        }
    }
}
