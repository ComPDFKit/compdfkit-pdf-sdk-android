/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfsignature;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfsignature.adapter.CSignatureListAdapter;
import com.compdfkit.tools.annotation.pdfproperties.pdfsignature.data.CSignatureDatas;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class CSignatureStyleFragment extends CBasicPropertiesFragment {

    private RecyclerView rvSignature;

    private FloatingActionButton fabAddSignature;

    private ConstraintLayout clEmptyView;

    private ActivityResultLauncher<Intent> addSignatureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null && result.getData().getStringExtra("file_path") != null) {
            refreshSignatureList();
        }
    });
    private CSignatureListAdapter signatureListAdapter;

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_signature_style_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        rvSignature = rootView.findViewById(R.id.rv_signature);
        fabAddSignature = rootView.findViewById(R.id.fab_add_signature);
        clEmptyView = rootView.findViewById(R.id.cl_empty_view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabAddSignature.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CAddSignatureActivity.class);
            intent.putExtra(CAddSignatureActivity.EXTRA_SCREEN_ORIENTATION, CViewUtils.isLandScape(getContext())?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            intent.putExtra(CAddSignatureActivity.EXTRA_THEME_ID, CPDFApplyConfigUtil.getInstance().getGlobalThemeId());
            addSignatureLauncher.launch(intent);
        });
        signatureListAdapter = new CSignatureListAdapter();
        signatureListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (viewModel != null) {
                viewModel.getStyle().setImagePath(adapter.list.get(position));
                dismissStyleDialog();
            }
        });
        signatureListAdapter.addOnItemChildClickListener(R.id.iv_delete, (adapter, view1, position) -> {
            CAlertDialog alertDialog = CAlertDialog.newInstance(getString(R.string.tools_warning), getString(R.string.tools_are_you_sure_to_delete));
            alertDialog.setCancelClickListener(v -> alertDialog.dismiss());
            alertDialog.setConfirmClickListener(v -> {
                CSignatureDatas.removeSignature(adapter.list.get(position));
                signatureListAdapter.remove(position);
                alertDialog.dismiss();
                checkList();
            });
            alertDialog.show(getChildFragmentManager(), "alertDialog");
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                boolean isPortrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                return isPortrait ? 2 : 1;
            }
        });
        rvSignature.setLayoutManager(gridLayoutManager);
        rvSignature.setAdapter(signatureListAdapter);
        refreshSignatureList();
    }

    private void refreshSignatureList(){
        if (signatureListAdapter != null && getContext() != null) {
            List<String> signatures = CSignatureDatas.getSignatures(getContext());
            signatureListAdapter.setList(signatures);
            checkList();
        }
    }

    private void checkList(){
        if (signatureListAdapter.list == null || signatureListAdapter.list.size() == 0){
            clEmptyView.setVisibility(View.VISIBLE);
        }else {
            clEmptyView.setVisibility(View.GONE);
        }
    }
}
