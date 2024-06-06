/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfshape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfshape.adapter.CLineTypeListAdapter;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;


public class CLineArrowTypeListFragment extends CBasicPropertiesFragment {

    private RecyclerView rvArrowType;

    private CLineTypeListAdapter lineTypeListAdapter;

    private OnLineTypeListener lineTypeListener;

    public static CLineArrowTypeListFragment newInstance() {
        Bundle args = new Bundle();
        CLineArrowTypeListFragment fragment = new CLineArrowTypeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_properties_arrow_list_fragment, container, false);
        rvArrowType = rootView.findViewById(R.id.rv_arrow_type);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineTypeListAdapter = new CLineTypeListAdapter();
        lineTypeListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            lineTypeListAdapter.setSelectLineType(adapter.list.get(position));
            if (lineTypeListener != null) {
                lineTypeListener.select(adapter.list.get(position));
            }
        });
        rvArrowType.setLayoutManager(new GridLayoutManager(getContext(), 6));
        rvArrowType.setAdapter(lineTypeListAdapter);
    }

    public void setLineType(CPDFLineAnnotation.LineType lineType, boolean isStart){
        if (lineTypeListAdapter != null) {
            lineTypeListAdapter.setSelectLineType(lineType, isStart);
        }
    }

    public void setLineTypeListener(OnLineTypeListener lineTypeListener) {
        this.lineTypeListener = lineTypeListener;
    }

    public interface OnLineTypeListener{
        void select(CPDFLineAnnotation.LineType lineType);
    }
}
