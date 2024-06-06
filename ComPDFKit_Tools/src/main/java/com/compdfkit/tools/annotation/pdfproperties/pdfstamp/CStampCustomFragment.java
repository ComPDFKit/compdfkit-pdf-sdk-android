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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.adapter.CImageStampListAdapter;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.adapter.CTextStampAdapter;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CTextStampBean;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.data.CStampDatas;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.dialog.CImportImageDialogFragment;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class CStampCustomFragment extends CBasicPropertiesFragment {

    private RecyclerView rvTextStamp;

    private AppCompatTextView tvTextStampTitle;

    private RecyclerView rvImageStamp;

    private AppCompatTextView tvImageStampTitle;

    private CTextStampAdapter textStampAdapter;

    private CImageStampListAdapter imageStampListAdapter;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_properties_stamp_custom_list_fragment, container, false);
        FloatingActionButton fabAdd = rootView.findViewById(R.id.fab_add_custom_stamp);
        rvTextStamp = rootView.findViewById(R.id.rv_custom_text_stamp);
        tvTextStampTitle = rootView.findViewById(R.id.tv_custom_text_stamp_title);
        rvImageStamp = rootView.findViewById(R.id.rv_custom_image_stamp);
        tvImageStampTitle = rootView.findViewById(R.id.tv_custom_image_stamp_title);
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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTextStampList();
        initImageStampList();
        refreshStampList();
    }

    private void initTextStampList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                boolean isPortrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                return isPortrait ? 2 : 1;
            }
        });
        rvTextStamp.setLayoutManager(gridLayoutManager);
        rvTextStamp.setNestedScrollingEnabled(false);
        textStampAdapter = new CTextStampAdapter();
        textStampAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (viewModel != null) {
                CTextStampBean bean = adapter.list.get(position);
                CPDFStampAnnotation.TextStamp textStamp = new CPDFStampAnnotation.TextStamp(
                        bean.getTextContent(),
                        bean.getDateStr(),
                        bean.getTextStampShapeId(),
                        bean.getTextStampColorId());
                viewModel.getStyle().setTextStamp(textStamp);
                dismissStyleDialog();
            }
        });
        textStampAdapter.addOnItemChildClickListener(R.id.iv_delete, (adapter, view, position) -> {
            CAlertDialog alertDialog = CAlertDialog.newInstance(getString(R.string.tools_warning), getString(R.string.tools_are_you_sure_to_delete));
            alertDialog.setCancelClickListener(v -> alertDialog.dismiss());
            alertDialog.setConfirmClickListener(v -> {
                CTextStampBean bean = adapter.list.get(position);
                CStampDatas.removeStamp(bean.getFilePath());
                textStampAdapter.remove(position);
                if (textStampAdapter.list.size() == 0){
                    tvTextStampTitle.setVisibility(View.GONE);
                }
                alertDialog.dismiss();
            });
            alertDialog.show(getChildFragmentManager(), "alertDialog");
        });
        rvTextStamp.setAdapter(textStampAdapter);
    }

    private void initImageStampList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                boolean isPortrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                return isPortrait ? 2 : 1;
            }
        });
        rvImageStamp.setLayoutManager(gridLayoutManager);
        rvImageStamp.setNestedScrollingEnabled(false);
        imageStampListAdapter = new CImageStampListAdapter();
        imageStampListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (viewModel != null) {
                String imagePath = adapter.list.get(position);
                viewModel.getStyle().setImagePath(imagePath);
                dismissStyleDialog();
            }
        });
        imageStampListAdapter.addOnItemChildClickListener(R.id.iv_delete, (adapter, view, position) -> {
            CAlertDialog alertDialog = CAlertDialog.newInstance(getString(R.string.tools_warning), getString(R.string.tools_are_you_sure_to_delete));
            alertDialog.setCancelClickListener(v -> alertDialog.dismiss());
            alertDialog.setConfirmClickListener(v -> {
                String imagePath = adapter.list.get(position);
                CStampDatas.removeStamp(imagePath);
                imageStampListAdapter.remove(position);
                if (imageStampListAdapter.list.size() == 0){
                    tvImageStampTitle.setVisibility(View.GONE);
                }
                alertDialog.dismiss();
            });
            alertDialog.show(getChildFragmentManager(), "alertDialog");
        });
        rvImageStamp.setAdapter(imageStampListAdapter);
    }

    private void refreshStampList() {
        CThreadPoolUtils.getInstance().executeIO(() -> {
            try {
                if (getContext() != null) {
                    List<CTextStampBean> list = CStampDatas.getTextStampList(getContext());
                    if (textStampAdapter != null) {
                        textStampAdapter.setList(list);
                    }
                    checkShowStampTitle();
                }
            }catch (Exception e){

            }
        });
        CThreadPoolUtils.getInstance().executeIO(() -> {
            try {
                if (getContext() != null) {
                    List<String> list = CStampDatas.getImageStampList(getContext());
                    if (imageStampListAdapter != null) {
                        imageStampListAdapter.setList(list);
                    }
                    checkShowStampTitle();
                }
            }catch (Exception e){

            }
        });
    }

    private void checkShowStampTitle() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(()->{
                if (tvTextStampTitle != null) {
                    boolean show = textStampAdapter != null && textStampAdapter.list.size() > 0;
                    tvTextStampTitle.setVisibility(show ? View.VISIBLE : View.GONE);
                }
                if (tvImageStampTitle != null) {
                    boolean show = imageStampListAdapter != null && imageStampListAdapter.list.size() > 0;
                    tvImageStampTitle.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }
}
