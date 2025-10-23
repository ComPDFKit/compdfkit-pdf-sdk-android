/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * <p>THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT
 * LAW AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */
package com.compdfkit.tools.viewer.pdfthumbnail.adpater;

import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.SparseIntArray;
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
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.docseditor.drag.CDefaultItemTouchHelpCallback;

import java.util.ArrayList;
import java.util.List;

public class CPDFEditThumbnailListAdapter
        extends RecyclerView.Adapter<CPDFEditThumbnailListAdapter.CPDFThumbnailItemViewHolder>
        implements CDefaultItemTouchHelpCallback.OnItemTouchCallbackListener {
    private CPDFDocument cPdfDocument;

    private int currentPageIndex;

    private COnSetPDFDisplayPageIndexListener displayPageIndexListener;

    private boolean isEdit = false;

    private SparseIntArray selectArr = new SparseIntArray();

    private OnPageEditListener onPageEditListener = null;

    public CPDFEditThumbnailListAdapter(CPDFDocument cPdfDocument, int currentPageIndex) {
        this.cPdfDocument = cPdfDocument;
        this.currentPageIndex = currentPageIndex;
    }

    @NonNull
    @Override
    public CPDFEditThumbnailListAdapter.CPDFThumbnailItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new CPDFEditThumbnailListAdapter.CPDFThumbnailItemViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tools_edit_thumbnail_list_item, parent, false));
    }

    public static final int SPAN = 6;

    private int[] calculateItemSize(CPDFEditThumbnailListAdapter.CPDFThumbnailItemViewHolder holder, int position) {
        boolean isLandScape = CViewUtils.isLandScape(holder.itemView.getContext());
        boolean isPad = CViewUtils.isPad(holder.itemView.getContext());
        int spanSize = isLandScape ? 1 : 2;
        int screenWidth;
        if (isPad){
            screenWidth = CDimensUtils.dp2px(holder.itemView.getContext(), 640);
        }else {
            screenWidth = CDimensUtils.getScreenWidth(holder.itemView.getContext());
        }
        if (spanSize == 1 && !isPad) {
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
    public void onBindViewHolder(
            @NonNull CPDFEditThumbnailListAdapter.CPDFThumbnailItemViewHolder holder, int position) {

        int[] size = calculateItemSize(holder, holder.getAdapterPosition());

        Glide.with(holder.itemView.getContext())
                .load(CPDFWrapper.fromDocument(cPdfDocument, holder.getAdapterPosition()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(size[0], size[1])
                .into(holder.ivThumbnailImage);

        holder.tvPageIndex.setText(String.valueOf(holder.getAdapterPosition() + 1));
        updateSelectStatus(holder);
    }

    private void updateSelectStatus(CPDFThumbnailItemViewHolder holder) {
        if (isEdit) {
            if (selectArr.get(holder.getAdapterPosition()) == 1) {
                holder.ivSelect.setSelected(true);
                holder.tvPageIndex.setSelected(true);
                holder.clThumbnail.setSelected(true);
            } else {
                holder.ivSelect.setSelected(false);
                holder.tvPageIndex.setSelected(false);
                holder.clThumbnail.setSelected(false);
            }
            holder.ivSelect.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelect.setSelected(false);
            holder.ivSelect.setVisibility(View.GONE);
            holder.tvPageIndex.setSelected(holder.getAdapterPosition() == currentPageIndex);
            holder.clThumbnail.setSelected(holder.getAdapterPosition() == currentPageIndex);
        }
    }

    @Override
    public int getItemCount() {
        return cPdfDocument == null ? 0 : cPdfDocument.getPageCount();
    }

    public void setPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener listener) {
        this.displayPageIndexListener = listener;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        if (selectArr != null) {
            selectArr.clear();
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setItemClick(int position) {
        if (isEdit) {
            if (selectArr.get(position) == 0) {
                selectArr.put(position, 1);
            } else {
                selectArr.removeAt(selectArr.indexOfKey(position));
            }
        }
        notifyItemChanged(position);
    }

    public void setAllClick(RecyclerView recyclerView) {
        if (isEdit && cPdfDocument != null) {
            for (int i = 0; i < cPdfDocument.getPageCount(); i++) {
                selectArr.put(i, 1);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    updateSelectStatus((CPDFThumbnailItemViewHolder) viewHolder);
                } else {
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void setAllUnClick(RecyclerView recyclerView) {
        if (isEdit) {
            for (int size = selectArr.size() - 1; size >= 0; size--) {
                int key = selectArr.keyAt(size);
                selectArr.removeAt(size);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(key);
                if (viewHolder != null) {
                    updateSelectStatus((CPDFThumbnailItemViewHolder) viewHolder);
                } else {
                    notifyItemChanged(key);
                }
            }
        }
    }

    public SparseIntArray getSelectArr() {
        return selectArr;
    }

    public void setSelectArr(int[] pageNum) {
        if (pageNum != null) {
            selectArr.clear();
            for (int i = 0; i < pageNum.length; i++) {
                selectArr.put(pageNum[i], 1);
            }
        }
        notifyDataSetChanged();
    }

    public void setOnPageEditListener(OnPageEditListener onPageEditListener) {
        this.onPageEditListener = onPageEditListener;
    }

    @Override
    public void onSwiped(int adapterPosition) {
    }

    @Override
    public boolean onDragging(
            RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        int sourcePosition = sourceViewHolder.getAdapterPosition();
        int targetPosition = targetViewHolder.getAdapterPosition();
        notifyItemMoved(sourcePosition, targetPosition);
        return true;
    }

    @Override
    public void onMoved(
            RecyclerView.ViewHolder sourceViewHolder, int sourcePosition, int targetPosition) {
        AsyncTask<Void, Void, Boolean> asyncTask =
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        boolean isSuccess;
                        try {
                            isSuccess = cPdfDocument.movePage(sourcePosition, targetPosition);
                            if (sourcePosition < targetPosition) { // 往后移动
                                List<Integer> selected = new ArrayList<>();
                                for (int i = sourcePosition; i <= targetPosition; i++) {
                                    if (selectArr.get(i) == 1) {
                                        selected.add(i);
                                        selectArr.removeAt(selectArr.indexOfKey(i));
                                    }
                                }
                                for (int i = 0; i < selected.size(); i++) {
                                    if (selected.get(i) == sourcePosition) {
                                        continue;
                                    }
                                    selectArr.put(selected.get(i) - 1, 1);
                                }
                                if (selected.size() > 0) {
                                    if (selected.get(0) == sourcePosition) {
                                        selectArr.put(targetPosition, 1);
                                    }
                                }
                            } else { // 往前移动
                                List<Integer> selected = new ArrayList<>();
                                for (int i = sourcePosition; i >= targetPosition; i--) {
                                    if (selectArr.get(i) == 1) {
                                        selected.add(i);
                                        selectArr.removeAt(selectArr.indexOfKey(i));
                                    }
                                }
                                for (int i = 0; i < selected.size(); i++) {
                                    if (selected.get(i) == sourcePosition) {
                                        continue;
                                    }
                                    selectArr.put(selected.get(i) + 1, 1);
                                }
                                if (selected.size() > 0) {
                                    if (selected.get(0) == sourcePosition) {
                                        selectArr.put(targetPosition, 1);
                                    }
                                }
                            }
                            if (currentPageIndex == sourcePosition) {
                                currentPageIndex = targetPosition;
                            } else if (currentPageIndex == targetPosition) {
                                if (sourcePosition < targetPosition) {
                                    currentPageIndex = targetPosition - 1;
                                } else {
                                    currentPageIndex = targetPosition + 1;
                                }
                            } else {
                                if (sourcePosition < currentPageIndex && currentPageIndex < targetPosition) {
                                    currentPageIndex--;
                                } else if (targetPosition < currentPageIndex && currentPageIndex < sourcePosition) {
                                    currentPageIndex++;
                                }
                            }
                        } catch (Exception e) {
                            return false;
                        }
                        return isSuccess;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if (aBoolean) {
                            if (onPageEditListener != null) {
                                onPageEditListener.onEdit();
                            }
                            if (displayPageIndexListener != null) {
                                displayPageIndexListener.displayPage(currentPageIndex);
                            }
                            int start = sourcePosition < targetPosition ? sourcePosition : targetPosition;
                            int count = Math.abs(sourcePosition - targetPosition) + 1;
                            notifyItemRangeChanged(start, count);
                        }
                    }
                };
        asyncTask.execute();
    }

    @Override
    public void onSwaped(
            RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        int sourcePosition = sourceViewHolder.getAdapterPosition();
        int targetPosition = targetViewHolder.getAdapterPosition();
        AsyncTask<Void, Void, Boolean> asyncTask =
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        boolean isSuccess;
                        try {
                            isSuccess = cPdfDocument.exchangePage(sourcePosition, targetPosition);
                        } catch (Exception e) {
                            return false;
                        }
                        return isSuccess;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if (aBoolean) {
                            notifyDataSetChanged();
                        } else {

                        }
                    }
                };
        asyncTask.execute();
    }

    static class CPDFThumbnailItemViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView ivThumbnailImage;
        private AppCompatTextView tvPageIndex;
        private ConstraintLayout clThumbnail;
        private AppCompatImageView ivSelect;

        private ConstraintLayout clItem;

        public CPDFThumbnailItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnailImage = itemView.findViewById(R.id.iv_thumbnail);
            tvPageIndex = itemView.findViewById(R.id.tv_thumbnail_page_index);
            clThumbnail = itemView.findViewById(R.id.cl_thumbnail);
            ivSelect = itemView.findViewById(R.id.iv_check_box);
            clItem = itemView.findViewById(R.id.cl_item);
        }
    }

    public interface OnPageEditListener {
        void onEdit();
    }
}
