/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfthumbnail.adpater;

import android.content.res.Configuration;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.glide.CPDFWrapper;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;

/**
 * PDF thumbnail list adapter, displays the thumbnail of each page of a PDF file,
 * and clicking on it will navigate to the corresponding PDF page.
 * <p/>
 */
public class CPDFThumbnailListAdapter extends RecyclerView.Adapter<CPDFThumbnailListAdapter.CPDFThumbnailItemViewHolder> {

    private CPDFDocument cPdfDocument;

    private int currentPageIndex;

    private COnSetPDFDisplayPageIndexListener displayPageIndexListener;

    public CPDFThumbnailListAdapter(CPDFDocument cDdfDocument, int currentPageIndex) {
        this.cPdfDocument = cDdfDocument;
        this.currentPageIndex = currentPageIndex;
    }

    @NonNull
    @Override
    public CPDFThumbnailItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CPDFThumbnailItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tools_bota_thumbnail_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(
            @NonNull CPDFThumbnailItemViewHolder holder, int position) {

        int[] size = calculateItemSize(holder, holder.getAdapterPosition());

        Glide.with(holder.itemView.getContext())
                .load(CPDFWrapper.fromDocument(cPdfDocument, holder.getAdapterPosition()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(size[0], size[1])
                .into(holder.ivThumbnailImage);

        holder.tvPageIndex.setText(String.valueOf(holder.getAdapterPosition() + 1));
        holder.tvPageIndex.setSelected(holder.getAdapterPosition() == currentPageIndex);
        holder.clThumbnail.setSelected(holder.getAdapterPosition() == currentPageIndex);
        holder.clItem.setOnClickListener(v -> {
            if (displayPageIndexListener != null) {
                displayPageIndexListener.displayPage(holder.getAdapterPosition());
            }
        });

    }

    public static final int SPAN = 6;

    private int[] calculateItemSize(CPDFThumbnailItemViewHolder holder, int position) {
        boolean isPortrait = holder.itemView.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int spanSize = isPortrait ? 2 : 1;
        int screenWidth = CDimensUtils.getScreenWidth(holder.itemView.getContext());
        if (spanSize == 1) {
            screenWidth = CDimensUtils.dp2px(holder.itemView.getContext(), 600);
        }
        int itemWidth = screenWidth / (SPAN / spanSize);
        int itemMarginHorizontal = CDimensUtils.dp2px(holder.itemView.getContext(), 4) * 2;
        itemWidth -= itemMarginHorizontal;

        RectF rectF = cPdfDocument.pageAtIndex(position).getSize();
        float itemHeight = itemWidth * (134F / 104F);

        float imageWidth = itemWidth;
        float imageHeight = ((float) imageWidth / (float) rectF.width()) * rectF.height();

        if (imageHeight > itemHeight) {
            imageHeight = itemHeight;
            imageWidth = (imageHeight / rectF.height()) * rectF.width();
        }
        ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) holder.clThumbnail.getLayoutParams();
        layoutParams.width = (int) imageWidth;
        layoutParams.height = (int) imageHeight;
        holder.clThumbnail.setLayoutParams(layoutParams);
        return new int[]{(int) imageWidth, (int) imageHeight};
    }


    @Override
    public int getItemCount() {
        return cPdfDocument == null ? 0 : cPdfDocument.getPageCount();
    }

    public void setPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener listener) {
        this.displayPageIndexListener = listener;
    }

    static class CPDFThumbnailItemViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView ivThumbnailImage;
        private AppCompatTextView tvPageIndex;

        private ConstraintLayout clItem;

        private ConstraintLayout clThumbnail;

        public CPDFThumbnailItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnailImage = itemView.findViewById(R.id.iv_thumbnail);
            tvPageIndex = itemView.findViewById(R.id.tv_thumbnail_page_index);
            clItem = itemView.findViewById(R.id.cl_item);
            clThumbnail = itemView.findViewById(R.id.cl_thumbnail);
        }
    }
}
