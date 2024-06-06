package com.compdfkit.tools.signature.info.signlist;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.interfaces.COnDialogDismissListener;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.threadpools.SimpleBackgroundTask;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.bean.CPDFSignatureStatusInfo;
import com.compdfkit.tools.signature.info.CertDigitalSignInfoDialog;
import com.compdfkit.tools.signature.info.signlist.adapter.CertDigitalSignListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CPDFCertDigitalSignListDialog extends CBasicBottomSheetDialogFragment {

    private RecyclerView recyclerView;

    private CToolBar toolBar;

    private ProgressBar progressBar;

    private CPDFViewCtrl pdfView;

    private CertDigitalSignListAdapter signListAdapter;

    private COnDialogDismissListener dialogDismissListener;


    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    public static CPDFCertDigitalSignListDialog newInstance() {
        Bundle args = new Bundle();
        CPDFCertDigitalSignListDialog fragment = new CPDFCertDigitalSignListDialog();
        fragment.setArguments(args);
        return fragment;
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
    protected boolean draggable() {
        return false;
    }

    @Override
    protected float dimAmount() {
        return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_sign_cert_digital_sign_list_fragment;
    }

    @Override
    protected void onCreateView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        toolBar = view.findViewById(R.id.tool_bar);
        progressBar = view.findViewById(R.id.progress_bar);
        toolBar.setBackBtnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    protected void onViewCreate() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        signListAdapter = new CertDigitalSignListAdapter(pdfView.getCPdfReaderView().getPDFDocument());
        recyclerView.setAdapter(signListAdapter);
        initSignatureDatas();
        signListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            CPDFSignatureStatusInfo signature = adapter.list.get(position);
            CertDigitalSignInfoDialog infoDialog = CertDigitalSignInfoDialog.newInstance();
            infoDialog.setTrustedCertRefreshListener(this::initSignatureDatas);
            infoDialog.setPDFSignature(signature.getSignature());
            infoDialog.setDocument(pdfView.getCPdfReaderView().getPDFDocument());
            infoDialog.show(getChildFragmentManager(), "signInfoDialog");
        });
        signListAdapter.addOnItemChildClickListener(R.id.iv_delete, (adapter, view1, position) -> {
            CPDFSignatureStatusInfo signature = adapter.list.get(position);
            CAlertDialog alertDialog = CAlertDialog.newInstance(getString(R.string.tools_warning), getString(R.string.tools_are_you_sure_to_delete));
            alertDialog.setCancelClickListener(v -> {
                alertDialog.dismiss();
            });
            alertDialog.setConfirmClickListener(v -> {
                boolean result = CertificateDigitalDatas.removeDigitalSign(pdfView.getCPdfReaderView(),
                        pdfView.getCPdfReaderView().getPDFDocument(), signature.getSignature());
                if (result) {
                    signListAdapter.remove(position);
                }
                alertDialog.dismiss();
            });
            alertDialog.show(getChildFragmentManager(), "alertDialog");
        });
    }

    private void initSignatureDatas() {
        progressBar.setVisibility(View.VISIBLE);
        SimpleBackgroundTask<List<CPDFSignatureStatusInfo>> task = new SimpleBackgroundTask<List<CPDFSignatureStatusInfo>>(getContext()) {

            @Override
            protected List<CPDFSignatureStatusInfo> onRun() {
                CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
                List<CPDFSignatureStatusInfo> itemDataList = new ArrayList<>();
                List<CPDFSignature> list = CertificateDigitalDatas.getDigitalSignList(document);
                for (int i = 0; i < list.size(); i++) {
                    CPDFSignature signature = list.get(i);
                    CPDFSignatureStatusInfo statusInfo = CertificateDigitalDatas.verifyGetSignatureStatusInfo(document, signature);
                    itemDataList.add(statusInfo);
                }
                return itemDataList;
            }

            @Override
            protected void onSuccess(List<CPDFSignatureStatusInfo> result) {
                signListAdapter.setList(result);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        };
        task.execute();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dialogDismissListener != null) {
            dialogDismissListener.dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dialogDismissListener != null) {
            dialogDismissListener.dismiss();
        }
    }

    public void setDialogDismissListener(COnDialogDismissListener dialogDismissListener) {
        this.dialogDismissListener = dialogDismissListener;
    }
}
