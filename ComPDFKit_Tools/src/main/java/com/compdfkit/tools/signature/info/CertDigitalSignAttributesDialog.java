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
import com.compdfkit.tools.signature.bean.CPDFCertAttrDataItem;

import java.util.ArrayList;
import java.util.List;

public class CertDigitalSignAttributesDialog extends CBasicBottomSheetDialogFragment {

    private CToolBar toolBar;

    private RecyclerView recyclerView;

    private CPDFX509 cpdfx509;

    private CPDFSignature signature;

    private CertAttrListAdapter attrListAdapter;

    private COnDialogDismissListener dialogDismissListener;

    private boolean isTrusted = false;

    public static CertDigitalSignAttributesDialog newInstance(CPDFSignature signature, CPDFX509 cpdfx509) {
        Bundle args = new Bundle();
        CertDigitalSignAttributesDialog fragment = new CertDigitalSignAttributesDialog();
        fragment.setCpdfx509(cpdfx509);
        fragment.setSignature(signature);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCpdfx509(CPDFX509 cpdfx509) {
        this.cpdfx509 = cpdfx509;
    }

    public void setSignature(CPDFSignature signature) {
        this.signature = signature;
    }

    @Override
    protected int getStyle() {
        return R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle;
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
        return R.layout.tools_sign_certificate_attributes_fragment;
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
        attrListAdapter = new CertAttrListAdapter();
        recyclerView.setAdapter(attrListAdapter);
        attrListAdapter.addOnItemChildClickListener(R.id.btn_trusted, (adapter, view, position) -> {
            boolean result = cpdfx509.addToTrustedCertificates(getContext());
            CPDFCertAttrDataItem item = attrListAdapter.list.get(position);
            if (result){
                isTrusted = true;
                item.setCertIsTrusted(true);
                attrListAdapter.notifyDataSetChanged();
            }
        });
        initData();
    }

    private void initData() {
        if (cpdfx509 != null) {
            List<CPDFCertAttrDataItem> list = new ArrayList<>();
            list.add(new CPDFCertAttrDataItem(getString(R.string.tools_summary)));
            list.addAll(CertAttrDatas.getAbstractInfo(getContext(), cpdfx509));
            list.add(new CPDFCertAttrDataItem(getString(R.string.tools_details)));
            list.addAll(CertAttrDatas.getCertAttrDetailInfoList(getContext(), cpdfx509));
            list.add(new CPDFCertAttrDataItem(getString(R.string.tools_trust)));
            CPDFCertAttrDataItem item = new CPDFCertAttrDataItem();
            item.setCertTrustedType(true);
            item.setCertIsTrusted(CertificateDigitalDatas.certIsTrusted(getContext(), signature, cpdfx509));
            list.add(item);
            attrListAdapter.setList(list);
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dialogDismissListener != null && isTrusted) {
            dialogDismissListener.dismiss();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dialogDismissListener != null && isTrusted) {
            dialogDismissListener.dismiss();
        }
    }


    public void setTrustedCertRefreshListener(COnDialogDismissListener dialogDismissListener) {
        this.dialogDismissListener = dialogDismissListener;
    }
}
