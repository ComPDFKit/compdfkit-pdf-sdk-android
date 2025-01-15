/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfbookmark;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.common.CPDFDate;
import com.compdfkit.core.document.CPDFBookmark;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.utils.TTimeUtil;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeFragment;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.dialog.CEditDialog;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfbookmark.adapter.CPDFBookmarkListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CPDFBookmarkFragment extends CBasicThemeFragment {

    private RecyclerView rvPdfBookmark;

    private FloatingActionButton btnAddBookmark;

    private CPDFViewCtrl pdfView;

    private ConstraintLayout clEmptyView;

    private COnSetPDFDisplayPageIndexListener displayPageIndexListener;

    private CPDFBookmarkListAdapter bookmarkListAdapter;

    public static CPDFBookmarkFragment newInstance() {
        return new CPDFBookmarkFragment();
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_bota_bookmark_list_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        rvPdfBookmark = rootView.findViewById(R.id.rv_pdf_bookmark);
        btnAddBookmark = rootView.findViewById(R.id.flbtn_add_bookmark);
        clEmptyView = rootView.findViewById(R.id.cl_bookmarks_empty_view);
    }

    public void setPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.displayPageIndexListener = displayPageIndexListener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookmarkListAdapter = new CPDFBookmarkListAdapter();
        updateBookmarkList();
        bookmarkListAdapter.setDisplayPageIndexListener(displayPageIndexListener);
        bookmarkListAdapter.setDeleteBookmarkClickListener((bookmark, index) -> {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().getPDFDocument().removeBookmark(bookmark.getPageIndex());
            }
            updateBookmarkList();
        });
        bookmarkListAdapter.setEditBookmarkClickListener((bookmark, index) -> {
            CEditDialog editBookmarkDialog = CEditDialog.newInstance(getString(R.string.tools_edit_bookmark_title), bookmark.getTitle());
            editBookmarkDialog.setHint(getString(R.string.tools_bookmark_et_hint));
            editBookmarkDialog.setEditListener(title -> {
                if (pdfView != null) {
                    pdfView.getCPdfReaderView().getPDFDocument().getBookmarks()
                            .get(index)
                            .setTitle(title);
                    bookmarkListAdapter.updateBookmarkItem(index);
                }
                editBookmarkDialog.dismiss();
            });
            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
            if (fragmentActivity != null){
                editBookmarkDialog.show(fragmentActivity.getSupportFragmentManager(), "editBookmarkDialog");
            }
        });
        rvPdfBookmark.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPdfBookmark.setAdapter(bookmarkListAdapter);
        btnAddBookmark.setOnClickListener(v -> {
            if (bookmarkListAdapter.hasBookmark(pdfView.currentPageIndex)) {
                Toast.makeText(getContext(), getString(R.string.tools_has_bookmark), Toast.LENGTH_SHORT).show();
                return;
            }
            CEditDialog editBookmarkDialog = CEditDialog.newInstance(getString(R.string.tools_add_bookmarks), "");
            editBookmarkDialog.setHint(getString(R.string.tools_bookmark_et_hint));
            editBookmarkDialog.setEditListener(title -> {
                if (pdfView != null) {
                    int pageIndex = pdfView.currentPageIndex;
                    CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
                    if (document == null){
                        editBookmarkDialog.dismiss();
                        return;
                    }
                    document.addBookmark(new CPDFBookmark(
                            pageIndex, title, CPDFDate.toStandardDate(TTimeUtil.getCurrentDate())
                    ));
                    updateBookmarkList();
                }
                editBookmarkDialog.dismiss();
            });
            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
            if (fragmentActivity != null){
                editBookmarkDialog.show(fragmentActivity.getSupportFragmentManager(), "editBookmarkDialog");
            }
        });
    }

    private void updateBookmarkList(){
        CThreadPoolUtils.getInstance().executeIO(()->{
            List<CPDFBookmark> list = getBookmarkList();
            if (getActivity() != null) {
                getActivity().runOnUiThread(()->{
                    bookmarkListAdapter.submitList(list);
                    checkBookmarkList(list);
                });
            }
        });

    }

    private List<CPDFBookmark> getBookmarkList() {
        if (pdfView != null) {
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            if (document == null){
                return new ArrayList<>();
            }
            List<CPDFBookmark> list = pdfView.getCPdfReaderView().getPDFDocument().getBookmarks();
            if (list != null){
                return new ArrayList<>(list);
            }else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    private void checkBookmarkList(List<CPDFBookmark> list) {
        if (list.size() == 0) {
            clEmptyView.setVisibility(View.VISIBLE);
        } else {
            clEmptyView.setVisibility(View.GONE);
        }
    }
}
