/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.info;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.interfaces.COnDialogDismissListener;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.bean.CPDFOwnerItemData;

import java.util.ArrayList;
import java.util.List;

public class CertDetailsDialog extends CBasicBottomSheetDialogFragment {

    private CToolBar toolBar;

    private RecyclerView recyclerView;

    private CPDFSignature signature;

    private CertDetailsListAdapter detailsListAdapter;

    private COnDialogDismissListener dialogDismissListener;

    public static CertDetailsDialog newInstance(CPDFSignature signature) {
        Bundle args = new Bundle();
        CertDetailsDialog fragment = new CertDetailsDialog();
        fragment.setSignature(signature);
        fragment.setArguments(args);
        return fragment;
    }

    public void setSignature(CPDFSignature signature) {
        this.signature = signature;
    }


    @Override
    protected int getStyle() {
        return  CViewUtils.getThemeAttrResourceId(getContext().getTheme(), R.attr.compdfkit_BottomSheetDialog_Theme);
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
    protected boolean draggable() {
        return false;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_sign_certificate_details_dialog;
    }

    @Override
    protected void onCreateView(View view) {
        toolBar = view.findViewById(R.id.tool_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        toolBar.setBackBtnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    protected void onViewCreate() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        detailsListAdapter = new CertDetailsListAdapter();
        recyclerView.setAdapter(detailsListAdapter);
        detailsListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            CPDFOwnerItemData item = detailsListAdapter.list.get(position);
            CertDigitalSignAttributesDialog dialog = CertDigitalSignAttributesDialog.newInstance(signature, item.getCpdfx509());
            dialog.setTrustedCertRefreshListener(dialogDismissListener);
            dialog.show(getChildFragmentManager(), "certAttrDialog");
        });
        initData();
    }

    private void initData(){
        List<CPDFX509> list = CertificateDigitalDatas.getCertDetails(getContext(), signature);
        List<CPDFOwnerItemData> dataList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CPDFX509 cpdfx509 = list.get(i);
            CPDFOwnerItemData data = new CPDFOwnerItemData(cpdfx509);
            if (i != list.size() -1){
                data.setHasParent(true);
            }
            dataList.add(data);
        }
        detailsListAdapter.setList(dataList);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dialogDismissListener != null) {
            dialogDismissListener.dismiss();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dialogDismissListener != null) {
            dialogDismissListener.dismiss();
        }
    }


    public void setTrustedCertRefreshListener(COnDialogDismissListener dialogDismissListener) {
        this.dialogDismissListener = dialogDismissListener;
    }
}
