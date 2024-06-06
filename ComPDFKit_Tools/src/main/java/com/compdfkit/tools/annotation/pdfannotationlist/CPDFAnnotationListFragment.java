/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationlist.adapter.CPDFAnnotListAdapter;
import com.compdfkit.tools.annotation.pdfannotationlist.bean.CPDFAnnotListItem;
import com.compdfkit.tools.annotation.pdfannotationlist.data.CPDFAnnotDatas;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;

import java.util.List;


public class CPDFAnnotationListFragment extends Fragment {

    private RecyclerView rvAnnotation;

    private CPDFViewCtrl pdfView;

    private ConstraintLayout clEmptyView;

    private COnSetPDFDisplayPageIndexListener displayPageIndexListener;
    private CPDFAnnotListAdapter listAdapter;

    private ProgressBar progressBar;

    public static CPDFAnnotationListFragment newInstance() {
        return new CPDFAnnotationListFragment();
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_bota_annotation_list_fragment, container, false);
        rvAnnotation = rootView.findViewById(R.id.rv_annotation);
        clEmptyView = rootView.findViewById(R.id.cl_annot_empty_view);
        progressBar = rootView.findViewById(R.id.progress_bar);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listAdapter = new CPDFAnnotListAdapter();
        updateAnnotationList();
        listAdapter.addOnItemChildClickListener(R.id.cl_root, (adapter, view1, position) -> {
            CPDFAnnotListItem item = adapter.list.get(position);
            if (!item.isHeader()) {
                if (displayPageIndexListener != null) {
                    displayPageIndexListener.displayPage(item.getPage());
                }
            }
        });
        rvAnnotation.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAnnotation.setAdapter(listAdapter);
    }


    private void updateAnnotationList() {
        progressBar.setVisibility(View.VISIBLE);
        CThreadPoolUtils.getInstance().executeIO(() -> {
            List<CPDFAnnotListItem> list = CPDFAnnotDatas.getAnnotationList(pdfView);
            if (getActivity() != null) {
                getActivity().runOnUiThread(()->{
                    if (list == null || list.size() <= 0) {
                        clEmptyView.setVisibility(View.VISIBLE);
                    }
                    listAdapter.setList(list);
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    public void setPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.displayPageIndexListener = displayPageIndexListener;
    }
}
