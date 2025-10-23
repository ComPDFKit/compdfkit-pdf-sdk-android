/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.adapter.CStampFragmentAdapter;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class CStampStyleFragment extends CBasicPropertiesFragment {

    private TabLayout tabLayout;

    private ViewPager2 viewPager2;

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_stamp_style_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        tabLayout = rootView.findViewById(R.id.tab_layout);
        viewPager2 = rootView.findViewById(R.id.view_pager);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CStampFragmentAdapter stampFragmentAdapter = new CStampFragmentAdapter(this);
        stampFragmentAdapter.setListener(switchFragmentListener);
        viewPager2.setAdapter(stampFragmentAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(stampFragmentAdapter.getTitle(position));
        });
        tabLayoutMediator.attach();
    }
}
