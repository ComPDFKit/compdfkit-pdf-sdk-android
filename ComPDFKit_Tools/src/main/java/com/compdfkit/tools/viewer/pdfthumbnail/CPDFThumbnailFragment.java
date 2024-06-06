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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.glide.CPDFWrapper;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfthumbnail.adpater.CPDFThumbnailListAdapter;

/**
 * PDF thumbnail list Fragment.
 *
 * <p/>
 * If you want to obtain the PDF thumbnail list to implement your own UI style,
 * you can refer to the code in "CPDFThumbnailListAdapter" for implementation.
 * <p/>
 * You can use Glide SDK to load the thumbnail of a PDF document.<br/>
 * GlideApp.with(context) <br/>
 *      .load(CPDFWrapper.fromDocument(CPDFDocument document, int pageIndex) <br/>
 *      .diskCacheStrategy(DiskCacheStrategy.NONE) <br/>
 *      .into(imageView) <br/>
 * <p/>
 * 如果你想加载pdf 的封面图，你可以使用以下方式 <br/>
 * - fromFile <br/>
 * GlideApp.with(context) <br/>
 *      .load(CPDFWrapper.fromFile(String pdfFilePath)) <br/>
 *      .diskCacheStrategy(DiskCacheStrategy.NONE) <br/>
 *      .into(imageView) <br/>
 * <p/>
 * - fromUri <br/>
 * GlideApp.with(context) <br/>
 *      .load(CPDFWrapper.fromUri(Uri pdfFileUri)) <br/>
 *      .diskCacheStrategy(DiskCacheStrategy.NONE) <br/>
 *      .into(imageview) <br/>
 *
 * @see CPDFThumbnailListAdapter
 * @see CPDFWrapper#fromDocument(CPDFDocument, int)
 * @see CPDFWrapper#fromFile(String)
 * @see CPDFWrapper#fromUri(Uri)
 */
public class CPDFThumbnailFragment extends Fragment {

    private RecyclerView rvThumbnailRecyclerView;

    private COnSetPDFDisplayPageIndexListener displayPageIndexListener;

    private CPDFViewCtrl pdfView;

    /**
     * Creates a new instance of CPDFThumbnailFragment.
     *
     * @return A new instance of CPDFThumbnailFragment.
     */
    public static CPDFThumbnailFragment newInstance() {
        return new CPDFThumbnailFragment();
    }

    /**
     * Sets the CPDFView instance for this fragment.
     *
     * @param pdfView The CPDFView instance.
     */
    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
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
        if (pdfView != null) {
            CPDFThumbnailListAdapter thumbnailListAdapter = new CPDFThumbnailListAdapter(pdfView.getCPdfReaderView().getPDFDocument(),  pdfView.currentPageIndex);
            thumbnailListAdapter.setPDFDisplayPageIndexListener(displayPageIndexListener);
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
        }
    }

    public void setPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.displayPageIndexListener = displayPageIndexListener;
    }
}
