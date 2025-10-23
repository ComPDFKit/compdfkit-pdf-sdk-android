/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.option.edit;


import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.form.CPDFWidgetItem;
import com.compdfkit.core.annotation.form.CPDFWidgetItems;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CStringUtils;
import com.compdfkit.tools.common.utils.dialog.CEditDialog;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.forms.pdfproperties.option.CWidgetItemBean;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CFormOptionEditFragment extends CBasicBottomSheetDialogFragment {

    private static final String EXTRA_TOOL_BAR_TITLE = "extra_tool_bar_title";

    private static final String EXTRA_ADD_DIALOG_TITLE = "extra_add_dialog_title";

    private static final String EXTRA_EDIT_DIALOG_TITLE = "extra_edit_dialog_title";

    private CToolBar toolBar;

    private RecyclerView rvList;

    private FloatingActionButton fabAdd;

    private CPDFWidgetItems pdfWidgetItems;

    private CFormEditItemsAdapter itemsAdapter;

    private OnEditListListener editListListener;

    private boolean needDefaultSelect;

    public static CFormOptionEditFragment newInstance(@StringRes int title,
                                                      @StringRes int addDialogTitle,
                                                      @StringRes int editDialogTitle) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_TOOL_BAR_TITLE, title);
        args.putInt(EXTRA_ADD_DIALOG_TITLE, addDialogTitle);
        args.putInt(EXTRA_EDIT_DIALOG_TITLE, editDialogTitle);
        CFormOptionEditFragment fragment = new CFormOptionEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setPdfWidgetItems(CPDFWidgetItems pdfWidgetItems) {
        this.pdfWidgetItems = pdfWidgetItems;
    }


    public void setNeedDefaultSelect(boolean needDefaultSelect){
        this.needDefaultSelect = needDefaultSelect;
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    protected float dimAmount() {
        return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_form_option_edit_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        rvList = rootView.findViewById(R.id.rv_list_box);
        fabAdd = rootView.findViewById(R.id.fab_add);
        toolBar = rootView.findViewById(R.id.tool_bar);
    }

    @Override
    protected void onViewCreate() {
        if (getArguments() != null) {
            toolBar.setTitle(getArguments().getInt(EXTRA_TOOL_BAR_TITLE,-1));
        }
        itemsAdapter = new CFormEditItemsAdapter();
        itemsAdapter.setList(getWidgetItems());
        itemsAdapter.addOnItemChildClickListener(R.id.iv_more, (adapter, view1, position) -> {
            CWidgetItemBean item = adapter.list.get(position);
            CPopupMenuWindow menuWindow = new CPopupMenuWindow(getContext());
            menuWindow.addItem(R.string.tools_edit, v1 -> {
                int titleRes = R.string.tools_edit;
                if (getArguments() != null) {
                    titleRes = getArguments().getInt(EXTRA_EDIT_DIALOG_TITLE, R.string.tools_edit);
                }
                CEditDialog editDialog = CEditDialog.newInstance(getString(titleRes), item.getText());
                editDialog.setEditListener(text -> {
                    item.setText(CStringUtils.filterEmoji(text));
                    itemsAdapter.notifyItemChanged(position, "REFRESH");
                    updateCallback();
                    editDialog.dismiss();
                });
                editDialog.show(getChildFragmentManager(), "editDialog");
            });
            menuWindow.addItem(R.string.tools_delete, v1 -> {
                itemsAdapter.remove(position);
                updateCallback();
                menuWindow.dismiss();
            });
            menuWindow.showAsDropDown(view1);
        });

        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(itemsAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback(){

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.DOWN | ItemTouchHelper.UP, ACTION_STATE_IDLE);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                if (itemsAdapter != null){
                    itemsAdapter.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != 0){
                    viewHolder.itemView.setAlpha(0.9F);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0F);
                if (itemsAdapter != null){
                    itemsAdapter.notifyDataSetChanged();
                }
                updateCallback();
            }
        };
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rvList);
        toolBar.setBackBtnClickListener(v -> {
            dismiss();
        });
        fabAdd.setOnClickListener(v -> {
            int titleRes = R.string.tools_edit;
            if (getArguments() != null) {
                titleRes = getArguments().getInt(EXTRA_ADD_DIALOG_TITLE, R.string.tools_edit);
            }
            CEditDialog editDialog = CEditDialog.newInstance(getString(titleRes), "");
            editDialog.setEditListener(text -> {
                String filteredText = CStringUtils.filterEmoji(text);
                if (itemsAdapter.list == null || itemsAdapter.list.size() == 0){
                    itemsAdapter.addItem(new CWidgetItemBean(filteredText, needDefaultSelect));
                }else {
                    itemsAdapter.addItem(new CWidgetItemBean(filteredText, false));
                }
                updateCallback();
                editDialog.dismiss();
            });
            editDialog.show(getChildFragmentManager(), "editDialog");
        });
    }

    private List<CWidgetItemBean> getWidgetItems(){
        if (pdfWidgetItems == null || pdfWidgetItems.getOptions() == null){
            return new ArrayList<>();
        }
        CPDFWidgetItem selectItem = getSelectedItem();
        List<CWidgetItemBean> list = new ArrayList<>();
        for (CPDFWidgetItem option : pdfWidgetItems.getOptions()) {
            boolean isSelect = false;
            if (selectItem != null && selectItem.text.equals(option.text)){
                isSelect = true;
            }
            list.add(new CWidgetItemBean(option.text, isSelect));
        }
        return list;
    }

    private CPDFWidgetItem getSelectedItem(){
        int[] selectedIndexs = pdfWidgetItems.getSelectedIndexes();
        CPDFWidgetItem selectedItem = null;
        if (selectedIndexs != null && selectedIndexs.length > 0){
            selectedItem = pdfWidgetItems.getOptionByIndex(selectedIndexs[0]);
        }
        return selectedItem;
    }

    private void updateCallback(){
        if (editListListener != null) {
            List<CWidgetItemBean> list = itemsAdapter.list;
            List<CPDFWidgetItem> items = new ArrayList<>();
            int[] selectedIndexs = null;
            for (int i = 0; i < list.size(); i++) {
                CWidgetItemBean bean = list.get(i);
                items.add(new CPDFWidgetItem(bean.getText(), bean.getText()));
                if (bean.isSelect()) {
                    selectedIndexs = new int[1];
                    selectedIndexs[0] = i;
                }
            }
            CPDFWidgetItem[] widgetItems = new CPDFWidgetItem[items.size()];
            if (needDefaultSelect && selectedIndexs == null){
                selectedIndexs = new int[]{0};
            }
            items.toArray(widgetItems);
            editListListener.widgets(widgetItems, selectedIndexs);
        }
    }

    public void setEditListListener(OnEditListListener editListListener) {
        this.editListListener = editListListener;
    }

    public interface OnEditListListener {
        void widgets(CPDFWidgetItem[] widgetItems, int[] selectedIndexs);
    }
}
