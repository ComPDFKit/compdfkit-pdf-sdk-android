/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.CStampCustomFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.CStampStandardFragment;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;


public class CStampFragmentAdapter extends FragmentStateAdapter {

    private CBasicPropertiesFragment.OnSwitchFragmentListener listener;

    public CStampFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            CStampStandardFragment stampStandardFragment = CStampStandardFragment.newInstance();
            stampStandardFragment.setSwitchFragmentListener(listener);
            return stampStandardFragment;
        }else {
            CStampCustomFragment customFragment = CStampCustomFragment.newInstance();
            customFragment.setSwitchFragmentListener(listener);
            return customFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public int getTitle(int position){
        if (position == 0){
            return R.string.tools_standard_stamp;
        }else {
            return R.string.tools_custom_stamp;
        }
    }

    public void setListener(CBasicPropertiesFragment.OnSwitchFragmentListener listener) {
        this.listener = listener;
    }
}
