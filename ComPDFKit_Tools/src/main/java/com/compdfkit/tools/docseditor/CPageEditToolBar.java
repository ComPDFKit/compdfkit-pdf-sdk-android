package com.compdfkit.tools.docseditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;

public class CPageEditToolBar extends FrameLayout {

    private CPDFViewCtrl pdfView;

    ConstraintLayout layoutInsert;
    ConstraintLayout layoutExtract;
    ConstraintLayout layoutReplace;

    ConstraintLayout layoutCopy;
    ConstraintLayout layoutRotate;
    ConstraintLayout layoutDelete;
    public CPageEditToolBar(@NonNull Context context) {
        this(context, null);
    }

    public CPageEditToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPageEditToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CPageEditToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_pageedit_tool_bar, this);
        CViewUtils.applyViewBackground(this);
        layoutInsert = findViewById(R.id.ll_insert);
        layoutReplace = findViewById(R.id.ll_replace);
        layoutExtract = findViewById(R.id.ll_extract);
        layoutCopy = findViewById(R.id.ll_copy);
        layoutRotate = findViewById(R.id.ll_rotate);
        layoutDelete = findViewById(R.id.ll_delete);
    }

    public void setInsertPageListener(View.OnClickListener listener) {
        layoutInsert.setOnClickListener(listener);
    }

    public void setReplacePageListener(View.OnClickListener listener) {
        layoutReplace.setOnClickListener(listener);
    }

    public void setExtractPageListener(View.OnClickListener listener) {
        layoutExtract.setOnClickListener(listener);
    }

    public void setCopyPageListener(View.OnClickListener listener) {
        layoutCopy.setOnClickListener(listener);
    }

    public void setRotatePageListener(View.OnClickListener listener) {
        layoutRotate.setOnClickListener(listener);
    }

    public void setDeletePageListener(View.OnClickListener listener) {
        layoutDelete.setOnClickListener(listener);
    }
}
