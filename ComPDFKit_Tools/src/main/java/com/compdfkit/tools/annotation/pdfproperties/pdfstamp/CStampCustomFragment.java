/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.adapter.CustomStampAdapter;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CCustomStampBean;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CTextStampBean;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.data.CStampDatas;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.dialog.CImportImageDialogFragment;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class CStampCustomFragment extends CBasicPropertiesFragment {

    private RecyclerView rvCustomStamp;

    private CustomStampAdapter customStampAdapter;

    private ActivityResultLauncher<Intent> addStampLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null && result.getData().getStringExtra("file_path") != null) {
            refreshStampList();
        }
    });

    public static CStampCustomFragment newInstance() {
        Bundle args = new Bundle();
        CStampCustomFragment fragment = new CStampCustomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_stamp_custom_list_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        FloatingActionButton fabAdd = rootView.findViewById(R.id.fab_add_custom_stamp);
        rvCustomStamp = rootView.findViewById(R.id.rv_custom_stamp);
        fabAdd.setOnClickListener(v -> {
            CAddStampSwichDialogFragment addStampDialog = CAddStampSwichDialogFragment.newInstance();
            addStampDialog.setAddTextStampClickListener(v1 -> {
                addStampLauncher.launch(new Intent(getContext(), CAddCustomStampActivity.class));
                addStampDialog.dismiss();
            });
            addStampDialog.setAddImageStampClickListener(v1 -> {
                CImportImageDialogFragment importImageDialogFragment = CImportImageDialogFragment.newInstance();
                importImageDialogFragment.setImportImageListener(imageUri -> {
                    if (imageUri != null) {
                        CStampDatas.saveStampImage(getContext(), imageUri);

                        refreshStampList();
                    }
                    importImageDialogFragment.dismiss();
                });
                importImageDialogFragment.show(getChildFragmentManager(), "import");
                addStampDialog.dismiss();
            });
            addStampDialog.show(getChildFragmentManager(), "addStampDialog");
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTextStampList();
        refreshStampList();
    }

    private void initTextStampList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemType = customStampAdapter.getItemViewType(position);
                switch (itemType){
                    case CCustomStampBean.ITEM_TEXT_STAMP_TITLE:
                    case CCustomStampBean.ITEM_IMAGE_STAMP_TITLE:
                        return 2;
                    default:
                        boolean isPortrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                        return isPortrait ? 2 : 1;
                }
            }
        });
        rvCustomStamp.setLayoutManager(gridLayoutManager);
        customStampAdapter = new CustomStampAdapter();
        customStampAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (viewModel != null) {
                CCustomStampBean bean = adapter.list.get(position);
                switch (bean.getItemType()) {
                    case CCustomStampBean.ITEM_TEXT_STAMP:
                        CTextStampBean textStampBean = bean.getTextStampBean();
                        CPDFStampAnnotation.TextStamp textStamp = new CPDFStampAnnotation.TextStamp(
                                textStampBean.getTextContent(),
                                textStampBean.getDateStr(),
                                textStampBean.getTextStampShapeId(),
                                textStampBean.getTextStampColorId());
                        viewModel.getStyle().setTextStamp(textStamp);
                        dismissStyleDialog();
                        break;
                    case CCustomStampBean.ITEM_IMAGE_STAMP:
                        String imagePath = adapter.list.get(position).getImageStampPath();
                        viewModel.getStyle().setImagePath(imagePath);
                        dismissStyleDialog();
                        break;
                    default:
                        break;
                }
            }
        });
        customStampAdapter.addOnItemChildClickListener(R.id.iv_delete, (adapter, view, position) -> {
            CAlertDialog alertDialog = CAlertDialog.newInstance(getString(R.string.tools_warning), getString(R.string.tools_are_you_sure_to_delete));
            alertDialog.setCancelClickListener(v -> alertDialog.dismiss());
            alertDialog.setConfirmClickListener(v -> {
                CCustomStampBean bean = adapter.list.get(position);
                switch (bean.getItemType()) {
                    case CCustomStampBean.ITEM_TEXT_STAMP:
                        CStampDatas.removeStamp(bean.getTextStampBean().getFilePath());
                        customStampAdapter.remove(position);
                        customStampAdapter.checkRemoveTextStampTitle();
                        alertDialog.dismiss();
                        break;
                    case CCustomStampBean.ITEM_IMAGE_STAMP:
                        String imagePath = adapter.list.get(position).getImageStampPath();
                        CStampDatas.removeStamp(imagePath);
                        customStampAdapter.remove(position);
                        customStampAdapter.checkRemoveImageStampTitle();
                        alertDialog.dismiss();
                        break;
                    default:
                        break;
                }
            });
            alertDialog.show(getChildFragmentManager(), "alertDialog");
        });
        rvCustomStamp.setAdapter(customStampAdapter);
    }

    private void refreshStampList() {
        CThreadPoolUtils.getInstance().executeIO(() -> {
            try {
                if (getContext() != null) {
                    List<CCustomStampBean> lists = new ArrayList<>();
                    List<CTextStampBean> textStampList = CStampDatas.getTextStampList(getContext());
                    for (int i = 0; i < textStampList.size(); i++) {
                        if (i == 0){
                            lists.add(CCustomStampBean.headItem(getString(R.string.tools_text_stamp), CCustomStampBean.ITEM_TEXT_STAMP_TITLE));
                        }
                        lists.add(new CCustomStampBean(textStampList.get(i)));
                    }
                    List<String> imageStampList = CStampDatas.getImageStampList(getContext());
                    for (int i = 0; i < imageStampList.size(); i++) {
                        if (i == 0){
                            lists.add(CCustomStampBean.headItem(getString(R.string.tools_image_stamp), CCustomStampBean.ITEM_IMAGE_STAMP_TITLE));
                        }
                        lists.add(new CCustomStampBean(imageStampList.get(i)));
                    }
                    CThreadPoolUtils.getInstance().executeMain(()->{
                        customStampAdapter.setList(lists);
                    });
                }
            } catch (Exception e) {
            }
        });
    }
}
