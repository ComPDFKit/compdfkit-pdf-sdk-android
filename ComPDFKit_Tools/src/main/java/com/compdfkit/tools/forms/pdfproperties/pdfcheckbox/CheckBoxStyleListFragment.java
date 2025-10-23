/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfcheckbox;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.forms.pdfproperties.pdfcheckbox.adapter.CheckStyleListAdapter;


public class CheckBoxStyleListFragment extends CBasicPropertiesFragment {

    private RecyclerView rvCheckBox;
    private CheckStyleListAdapter checkStyleListAdapter;

    private OnSelectCheckBoxStyleListener selectCheckBoxStyleListener;

    public static CheckBoxStyleListFragment newInstance() {
        Bundle args = new Bundle();
        CheckBoxStyleListFragment fragment = new CheckBoxStyleListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_checkbox_style_list_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        rvCheckBox = rootView.findViewById(R.id.rv_check_box);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkStyleListAdapter = new CheckStyleListAdapter();
        checkStyleListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            checkStyleListAdapter.setSelectCheckStyle(adapter.list.get(position));
            if (selectCheckBoxStyleListener != null) {
                selectCheckBoxStyleListener.select(adapter.list.get(position));
            }
        });
        rvCheckBox.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCheckBox.setAdapter(checkStyleListAdapter);
    }

    public void setCheckStyle(CPDFWidget.CheckStyle checkStyle){
        if (checkStyleListAdapter != null) {
            checkStyleListAdapter.setSelectCheckStyle(checkStyle);
        }
    }

    public void setSelectCheckBoxStyleListener(OnSelectCheckBoxStyleListener selectCheckBoxStyleListener) {
        this.selectCheckBoxStyleListener = selectCheckBoxStyleListener;
    }

    public interface OnSelectCheckBoxStyleListener{
        void select(CPDFWidget.CheckStyle style);
    }
}
