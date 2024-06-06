package com.compdfkit.tools.docseditor.pdfpageeditinsert;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CSelectInsertPageTypeDialogFragment extends CBasicBottomSheetDialogFragment {

    public static final String EXTRA_CREATE_BLANK_PAGE_ITEM_TITLE = "extra_create_blank_page_item_title";

    public static final String EXTRA_INSERT_PDF_PAGE_ITEM_TITLE = "extra_insert_pdf_page_item_title";

    private View.OnClickListener blankClickListener;

    private View.OnClickListener pdfClickListener;

    public static CSelectInsertPageTypeDialogFragment newInstance() {
        return new CSelectInsertPageTypeDialogFragment();
    }

    public static CSelectInsertPageTypeDialogFragment newInstance(Bundle args) {
        CSelectInsertPageTypeDialogFragment fragment = new CSelectInsertPageTypeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_pageedit_insert_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        ConstraintLayout clInsertBlankPage = rootView.findViewById(R.id.cl_create_blank_page);
        ConstraintLayout clInsertPdfPage = rootView.findViewById(R.id.cl_open_document);

        AppCompatTextView tvBlankPage = rootView.findViewById(R.id.tv_create_blank_page);
        AppCompatTextView tvOpenDocument = rootView.findViewById(R.id.tv_open_document);

        clInsertBlankPage.setOnClickListener(blankClickListener);
        clInsertPdfPage.setOnClickListener(pdfClickListener);

        if (getArguments() != null) {
            String blankPageTitle = getArguments().getString(EXTRA_CREATE_BLANK_PAGE_ITEM_TITLE, getString(R.string.tools_blank_page));
            String pdfPageTitle = getArguments().getString(EXTRA_INSERT_PDF_PAGE_ITEM_TITLE, getString(R.string.tools_pdf_page));

            tvBlankPage.setText(blankPageTitle);
            tvOpenDocument.setText(pdfPageTitle);

        }

    }

    @Override
    protected void onViewCreate() {

    }

    public void setInsertBlankPageClickListener(View.OnClickListener blankClickListener) {
        this.blankClickListener = blankClickListener;
    }

    public void setInsertPdfPageClickListener(View.OnClickListener pdfClickListener) {
        this.pdfClickListener = pdfClickListener;
    }
}
