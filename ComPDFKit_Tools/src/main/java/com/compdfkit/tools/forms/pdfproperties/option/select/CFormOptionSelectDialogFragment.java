/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.option.select;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.form.CPDFWidgetItem;
import com.compdfkit.core.annotation.form.CPDFWidgetItems;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.forms.pdfproperties.option.CWidgetItemBean;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;


public class CFormOptionSelectDialogFragment extends CBasicBottomSheetDialogFragment {

    private AppCompatTextView tvTitle;

    private AppCompatImageView ivClose;

    private RecyclerView rvOptions;

    private CFormOptionListAdapter optionListAdapter;

    private CPDFWidgetItems pdfWidgetItems;

    private OnSelectOptionListener selectOptionListener;

    public static CFormOptionSelectDialogFragment newInstance() {
        Bundle args = new Bundle();
        CFormOptionSelectDialogFragment fragment = new CFormOptionSelectDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setPdfWidgetItems(CPDFWidgetItems pdfWidgetItems) {
        this.pdfWidgetItems = pdfWidgetItems;
    }

    @Override
    protected int getStyle() {
        return CViewUtils.getThemeAttrResourceId(getContext().getTheme(), R.attr.compdfkit_BottomSheetDialog_Transparent_Theme);
    }


    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        if (CViewUtils.isLandScape(getContext())) {
            behavior.setSkipCollapsed(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_form_option_select_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        tvTitle = rootView.findViewById(R.id.tv_tool_bar_title);
        ivClose = rootView.findViewById(R.id.iv_tool_bar_close);
        rvOptions = rootView.findViewById(R.id.rv_options);
    }

    @Override
    protected void onViewCreate() {
        tvTitle.setText(R.string.tools_context_menu_select);
        ivClose.setOnClickListener(v -> dismiss());
        optionListAdapter = new CFormOptionListAdapter();
        optionListAdapter.setList(getWidgetItems());
        optionListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (optionListAdapter != null) {
                optionListAdapter.setSelect(position);
                List<CWidgetItemBean> list = optionListAdapter.list;
                int[] selectedIndexs = null;
                for (int i = 0; i < list.size(); i++) {
                    CWidgetItemBean item = list.get(i);
                    if (item.isSelect()) {
                        selectedIndexs = new int[1];
                        selectedIndexs[0] = i;
                    }
                }
                if (selectOptionListener != null) {
                    selectOptionListener.select(selectedIndexs);
                }
            }
            dismiss();
        });
        rvOptions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOptions.setAdapter(optionListAdapter);
    }

    private List<CWidgetItemBean> getWidgetItems() {
        if (pdfWidgetItems == null || pdfWidgetItems.getOptions() == null) {
            return new ArrayList<>();
        }
        CPDFWidgetItem selectItem = getSelectedItem();
        List<CWidgetItemBean> list = new ArrayList<>();
        for (CPDFWidgetItem option : pdfWidgetItems.getOptions()) {
            boolean isSelect = false;
            if (selectItem != null && selectItem.text.equals(option.text)) {
                isSelect = true;
            }
            list.add(new CWidgetItemBean(option.text, isSelect));
        }
        return list;
    }

    private CPDFWidgetItem getSelectedItem() {
        int[] selectedIndexs = pdfWidgetItems.getSelectedIndexes();
        CPDFWidgetItem selectedItem = null;
        if (selectedIndexs != null && selectedIndexs.length > 0) {
            selectedItem = pdfWidgetItems.getOptionByIndex(selectedIndexs[0]);
        }
        return selectedItem;
    }

    public void setSelectOptionListener(OnSelectOptionListener selectOptionListener) {
        this.selectOptionListener = selectOptionListener;
    }

    public interface OnSelectOptionListener {
        void select(int[] selectedIndexs);
    }
}
