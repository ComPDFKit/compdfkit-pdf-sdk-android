/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.reader.CPDFReaderView;

public class CSignatureToolBar extends FrameLayout {

    ConstraintLayout clAddSignatures;

    ConstraintLayout clVerifySign;

    private CPDFViewCtrl pdfView;

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    public CSignatureToolBar(@NonNull Context context) {
        this(context, null);
    }

    public CSignatureToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CSignatureToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_signature_tool_bar, this);
        clAddSignatures = findViewById(R.id.cl_add_digital_signature);
        clVerifySign = findViewById(R.id.cl_verify_digital_signature);
        clAddSignatures.setOnClickListener(v -> {
            if (pdfView != null) {
                clAddSignatures.setSelected(!clAddSignatures.isSelected());
                if (clAddSignatures.isSelected()) {
                    pdfView.setViewMode(CPDFReaderView.ViewMode.FORM);
                    pdfView.changeFormType(CPDFWidget.WidgetType.Widget_SignatureFields);
                } else {
                    pdfView.setViewMode(CPDFReaderView.ViewMode.VIEW);
                    pdfView.changeFormType(CPDFWidget.WidgetType.Widget_Unknown);
                }
            }
        });

        clVerifySign.setOnClickListener(v -> {

        });
    }

    public View getVerifySignButton() {
        return clVerifySign;
    }

    public void reset() {
        clAddSignatures.setSelected(false);
    }

}
