/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
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
import com.compdfkit.tools.security.watermark.view.CWatermarkView;


class CWatermarkPageFragmentAdapter extends FragmentStateAdapter {

    private CPDFDocument document;

    private int pageIndex = 0;

    public CWatermarkPageFragmentAdapter(@NonNull Fragment fragment, CPDFDocument document, int pageIndex) {
        super(fragment);
        this.document = document;
        this.pageIndex = pageIndex;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        CWatermarkPageFragment pageFragment = CWatermarkPageFragment.newInstance(
                position == 0 ? CWatermarkView.EditType.TXT : CWatermarkView.EditType.Image);
        pageFragment.setDocument(document);
        pageFragment.setPageIndex(pageIndex);
        return pageFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
