/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.verify;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.signature.SignatureStatus;
import com.compdfkit.tools.signature.bean.CPDFDocumentSignInfo;

public class CVerifySignStatusView extends FrameLayout {

    private AppCompatImageView ivStatus;

    private AppCompatTextView tvStatus;

    private AppCompatButton btnDetails;

    private ProgressBar progressBar;

    private SignatureStatus status;

    private String statusInfo = "";

    private CPDFViewCtrl pdfView;

    private boolean loading = false;

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    public CVerifySignStatusView(@NonNull Context context) {
        this(context, null);
    }

    public CVerifySignStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CVerifySignStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView(context);
        updateStatus();
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_sign_verify_sign_status_view, this);
        ivStatus = findViewById(R.id.iv_status);
        tvStatus = findViewById(R.id.tv_status);
        btnDetails = findViewById(R.id.btn_details);
        progressBar = findViewById(R.id.progress_bar);
    }

    public AppCompatButton getBtnDetails() {
        return btnDetails;
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CVerifySignStatusView);
        if (typedArray != null) {
            int statusFlag = typedArray.getInt(R.styleable.CVerifySignStatusView_status, SignatureStatus.SUCCESS.ordinal());
            switch (statusFlag) {
                case 1:
                    status = SignatureStatus.FAILURE;
                    break;
                case 2:
                    status = SignatureStatus.UNKNOWN;
                    break;
                default:
                    status = SignatureStatus.SUCCESS;
                    break;
            }
            typedArray.recycle();
        }
    }

    public void showLoading(boolean show){
        loading = show;
        updateStatus();
    }

    public void setStatus(CPDFDocumentSignInfo signInfo) {
        this.loading = false;
        this.status = signInfo.getSignatureStatus();
        this.statusInfo = signInfo.getInfo();
        updateStatus();
    }

    private void updateStatus() {
        if (loading){
            progressBar.setVisibility(VISIBLE);
            ivStatus.setVisibility(INVISIBLE);
            tvStatus.setVisibility(GONE);
            btnDetails.setVisibility(GONE);
            return;
        }
        progressBar.setVisibility(GONE);
        ivStatus.setVisibility(VISIBLE);
        tvStatus.setVisibility(VISIBLE);
        btnDetails.setVisibility(VISIBLE);

        tvStatus.setText(statusInfo);
        switch (status) {
            case SUCCESS:
                ivStatus.setImageResource(R.drawable.tools_ic_digital_sign_is_valid);
                break;
            case FAILURE:
                ivStatus.setImageResource(R.drawable.tools_ic_digital_sign_is_failures);
                break;
            case UNKNOWN:
                ivStatus.setImageResource(R.drawable.tools_ic_digital_sign_is_wrong);
                break;
            default:
                break;
        }
    }
}
