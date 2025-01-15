/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.adapter.CStandardStampListAdapter;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.data.CStampDatas;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;


public class CStampStandardFragment extends CBasicPropertiesFragment {

    private RecyclerView rvStandardStamp;

    public static CStampStandardFragment newInstance() {
        Bundle args = new Bundle();
        CStampStandardFragment fragment = new CStampStandardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_stamp_standard_list_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        rvStandardStamp = rootView.findViewById(R.id.rv_standard_stamp);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CStandardStampListAdapter standardStampListAdapter = new CStandardStampListAdapter();
        standardStampListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (viewModel != null) {
                viewModel.getStyle().setStandardStamp(adapter.list.get(position).getStandardStamp());
                dismissStyleDialog();
            }
        });
        standardStampListAdapter.setList(CStampDatas.getStandardStampList());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 8);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                boolean isPortrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                return isPortrait ? 4 : 2;
            }
        });
        rvStandardStamp.setLayoutManager(gridLayoutManager);
        rvStandardStamp.setAdapter(standardStampListAdapter);
    }
}
