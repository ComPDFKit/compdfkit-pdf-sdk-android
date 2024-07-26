/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfthumbnail;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.adapter.COnRecyclerItemClickListener;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.docseditor.drag.CProItemTouchHelper;
import com.compdfkit.tools.docseditor.pdfpageedit.CPDFPageEditDialogFragment;
import com.compdfkit.tools.viewer.pdfthumbnail.adpater.CPDFEditThumbnailListAdapter;

public class CPDFEditThumbnailFragment extends Fragment {
    private RecyclerView rvThumbnailRecyclerView;

    private COnSetPDFDisplayPageIndexListener displayPageIndexListener;

    private CPDFViewCtrl pdfView;

    private CPDFEditThumbnailListAdapter thumbnailListAdapter = null;
    private boolean isEdit = false;

    private CPDFPageEditDialogFragment pageEditDialogFragment = null;

    public static final int UPDATE_TYPE_ROTATE = 1;

    public static final int UPDATE_TYPE_DELETE = 2;

    /**
     * Creates a new instance of CPDFThumbnailFragment.
     *
     * @return A new instance of CPDFThumbnailFragment.
     */
    public static CPDFEditThumbnailFragment newInstance() {
        return new CPDFEditThumbnailFragment();
    }

    /**
     * Sets the CPDFView instance for this fragment.
     *
     * @param pdfView The CPDFView instance.
     */
    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    public void setCPDFPageEditDialogFragment(CPDFPageEditDialogFragment editDialogFragment) {
        pageEditDialogFragment = editDialogFragment;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        if (thumbnailListAdapter != null) {
            thumbnailListAdapter.setEdit(edit);
            thumbnailListAdapter.setItemClick(pdfView.currentPageIndex);
            thumbnailListAdapter.notifyDataSetChanged();
        }
    }

    public boolean isEdit() {
        return this.isEdit;
    }

    public void setSelectAll(boolean select) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(()->{
                if (isEdit == true && thumbnailListAdapter != null) {
                    if (select) {
                        thumbnailListAdapter.setAllClick(rvThumbnailRecyclerView);
                    } else {
                        thumbnailListAdapter.setAllUnClick(rvThumbnailRecyclerView);
                    }
                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View rootView = inflater.inflate(R.layout.tools_bota_thumbnail_list_fragment, container, false);
        rvThumbnailRecyclerView = rootView.findViewById(R.id.rv_thumbnail);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void initFragment() {
        if (pdfView != null) {
            thumbnailListAdapter = new CPDFEditThumbnailListAdapter(pdfView.getCPdfReaderView().getPDFDocument(),  pdfView.currentPageIndex);
            thumbnailListAdapter.setPDFDisplayPageIndexListener(displayPageIndexListener);
            setEdit(isEdit);
            thumbnailListAdapter.setOnPageEditListener(() -> {
                pageEditDialogFragment.setHasEdit(true);
            });
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 6);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isPortrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                    return isPortrait ? 2 : 1;
                }
            });
            rvThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
            rvThumbnailRecyclerView.setAdapter(thumbnailListAdapter);
            rvThumbnailRecyclerView.scrollToPosition(pdfView.currentPageIndex);

            rvThumbnailRecyclerView.addOnItemTouchListener(new COnRecyclerItemClickListener(rvThumbnailRecyclerView) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder vh) {
                    int position = vh.getAdapterPosition();
                    if (thumbnailListAdapter.isEdit()) {
                        thumbnailListAdapter.setItemClick(position);
                    } else {
                        if (pageEditDialogFragment != null) {
                            pageEditDialogFragment.dismiss();
                        }
                        if (displayPageIndexListener != null) {
                            displayPageIndexListener.displayPage(position);
                        }
                    }
                }
            });
            CProItemTouchHelper itemTouchHelper = new CProItemTouchHelper(thumbnailListAdapter);
            itemTouchHelper.attachToRecyclerView(rvThumbnailRecyclerView);
            itemTouchHelper.setDragEnable(true);
            itemTouchHelper.setSwapEnable(false);
            itemTouchHelper.setSwipeEnable(false);
        }
    }

    public void setPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.displayPageIndexListener = displayPageIndexListener;
    }

    public void updatePagesArr(int[] pages, int updateType) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(()->{
                if (thumbnailListAdapter != null && pages != null) {
                    if (updateType == UPDATE_TYPE_ROTATE) {
                        for (int page : pages) {
                            thumbnailListAdapter.notifyItemChanged(page);
                        }
                    } else if (updateType == UPDATE_TYPE_DELETE) {
                        for (int length = pages.length - 1; length >= 0; length--) {
                            thumbnailListAdapter.notifyItemRemoved(pages[length]);
                        }
                        int middleIndex = pages.length /2;
                        int min = pages[middleIndex];
                        for (int page : pages) {
                            if (page < min) {
                                min = page;
                            }
                        }
                        thumbnailListAdapter.notifyItemRangeChanged(min, thumbnailListAdapter.getItemCount() - min);
                    }
                }
            });
        }

    }

    public SparseIntArray getSelectPages() {
        if (thumbnailListAdapter != null) {
            return thumbnailListAdapter.getSelectArr();
        }
        return null;
    }

    public void setSelectPages(int[] pages) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(()->{
                if (thumbnailListAdapter != null) {
                    thumbnailListAdapter.setSelectArr(pages);
                }
            });
        }

    }

    public void scrollToPosition(int position){
        if (rvThumbnailRecyclerView != null) {
            rvThumbnailRecyclerView.scrollToPosition(position);
        }
    }
}
