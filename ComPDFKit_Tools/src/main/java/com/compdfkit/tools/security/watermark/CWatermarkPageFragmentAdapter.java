/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.watermark;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.CPDFWatermarkConfig;
import com.compdfkit.tools.security.watermark.view.CWatermarkView;


class CWatermarkPageFragmentAdapter extends FragmentStateAdapter {

    private CPDFDocument document;

    private int pageIndex = 0;

    private String defaultImagePath;

    private CPDFWatermarkConfig watermarkConfig;

    public CWatermarkPageFragmentAdapter(@NonNull Fragment fragment, CPDFDocument document, int pageIndex) {
        super(fragment);
        this.document = document;
        this.pageIndex = pageIndex;
    }

    public void setWatermarkConfig(CPDFWatermarkConfig watermarkConfig) {
        this.watermarkConfig = watermarkConfig;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        CWatermarkPageFragment pageFragment = CWatermarkPageFragment.newInstance(getEditType(position));
        pageFragment.setDocument(document);
        pageFragment.setPageIndex(pageIndex);
        pageFragment.setWatermarkConfig(watermarkConfig);
        return pageFragment;
    }

    @Override
    public int getItemCount() {
        return watermarkConfig.types.size();
    }

    public CWatermarkView.EditType getEditType(int position) {
        if (watermarkConfig.types.get(position).equals("text")) {
            return CWatermarkView.EditType.TXT;
        } else {
            return CWatermarkView.EditType.Image;
        }
    }

    public int getTitleRes(int position) {
        if (watermarkConfig.types.get(position).equals("text")) {
            return R.string.tools_custom_stamp_text;
        } else {
            return R.string.tools_image;
        }

    }
}
