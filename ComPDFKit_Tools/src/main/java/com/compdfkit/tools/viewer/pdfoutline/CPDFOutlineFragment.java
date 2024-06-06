/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfoutline;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfoutline.adapter.COutlineListAdapter;
import com.compdfkit.tools.viewer.pdfoutline.bean.COutlineData;
import com.compdfkit.tools.viewer.pdfoutline.data.COutlineDatas;

import java.util.ArrayList;
import java.util.List;

public class CPDFOutlineFragment extends Fragment {

    /**
     * Outline maximum expansion level
     */
    public static final int OUTLINE_MAX_LEVEL = 10;

    private RecyclerView rvOutlineRecyclerView;

    private ConstraintLayout clEmptyView;

    private COnSetPDFDisplayPageIndexListener outlineCallback;

    private CPDFViewCtrl pdfView;

    COutlineListAdapter outlineListAdapter = null;

    /**
     * Creates a new instance of CPDFOutlineFragment.
     *
     * @return A new instance of CPDFOutlineFragment.
     */
    public static CPDFOutlineFragment newInstance() {
        return new CPDFOutlineFragment();
    }

    /**
     * Sets the CPDFView instance for this fragment.
     *
     * @param pdfView The CPDFView instance.
     */
    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View rootView = inflater.inflate(R.layout.tools_bota_outline_list_fragment, container, false);
        rvOutlineRecyclerView = rootView.findViewById(R.id.rv_outline);
        clEmptyView = rootView.findViewById(R.id.cl_outline_empty_view);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Set up the adapter and the RecyclerView.
        outlineListAdapter = new COutlineListAdapter();
        outlineListAdapter.setOutlineClickListener(outlineCallback);
        rvOutlineRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOutlineRecyclerView.setAdapter(outlineListAdapter);
        // Set the list of outline data for the adapter to display.
        List<COutlineData> list = getOutlineData();
        if (list == null || list.size() <=0){
            clEmptyView.setVisibility(View.VISIBLE);
        }
        outlineListAdapter.setList(getOutlineData());
    }

    /**
     * Returns the list of outline data for the PDF document.
     *
     * @return The list of outline data.
     */
    private ArrayList<COutlineData> getOutlineData() {
        if (pdfView != null) {
            return COutlineDatas.getOutlineList(pdfView, OUTLINE_MAX_LEVEL);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Sets the listener for outline item click events.
     *
     * @param outlineClickListener The listener to set.
     */
    public void setOutlineClickListener(COnSetPDFDisplayPageIndexListener outlineClickListener) {
        this.outlineCallback = outlineClickListener;
    }
}
