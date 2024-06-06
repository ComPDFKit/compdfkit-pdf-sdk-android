/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.viewer.pdfsearch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * PDF text search result list adapter. The search results will be displayed in groups according to the page numbers.
 *
 *
 */
public class CSearchPDFTextRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * head item type
     */
    private final static int ITEM_TYPE_HEAD = 0x100;
    /**
     * search result data item type
     */
    private final static int ITEM_TYPE_CONTENT = 0x200;

    private final List<CSearchTextInfo> searchTextInfoList = new ArrayList<>();

    private OnClickSearchItemListener onClickSearchItemListener;

    public CSearchPDFTextRecyclerviewAdapter() {
    }
    
    public void addList(List<CSearchTextInfo> list) {
        searchTextInfoList.addAll(list);
    }

    public void clearList() {
        searchTextInfoList.clear();
    }

    public void setOnClickSearchItemListener(OnClickSearchItemListener onClickSearchItemListener) {
        this.onClickSearchItemListener = onClickSearchItemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (ITEM_TYPE_HEAD == viewType) {
            return new SearchTextHeadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tools_search_keywords_list_item_header, parent, false));
        }
        return new SearchTextContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tools_search_keywords_list_item, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if ( searchTextInfoList.size() > 0) {
            if (searchTextInfoList.get(position).isHeader) {
                return ITEM_TYPE_HEAD;
            }
        }
        return ITEM_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();
        if (searchTextInfoList.size() > 0) {
            if (holder instanceof SearchTextHeadViewHolder) {
                ((SearchTextHeadViewHolder) holder).idItemSearchHeadPage.setText(String.valueOf(searchTextInfoList.get(adapterPosition).page + 1));
            } else {
                ((SearchTextContentViewHolder) holder).idItemSearchContentText.setText(searchTextInfoList.get(adapterPosition).stringBuilder);
                holder.itemView.setOnClickListener(v -> {
                    CSearchTextInfo clickItem = searchTextInfoList.get(adapterPosition);
                    if (onClickSearchItemListener != null) {
                        onClickSearchItemListener.onClick(clickItem);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return searchTextInfoList.size();
    }

    public static class SearchTextHeadViewHolder extends RecyclerView.ViewHolder {
        private TextView idItemSearchHeadPage;

        public SearchTextHeadViewHolder(View itemView) {
            super(itemView);
            idItemSearchHeadPage = itemView.findViewById(R.id.id_item_search_head_page);
        }
    }

    public static class SearchTextContentViewHolder extends RecyclerView.ViewHolder {
        private TextView idItemSearchContentText;

        public SearchTextContentViewHolder(View itemView) {
            super(itemView);
            idItemSearchContentText = itemView.findViewById(R.id.id_item_search_content_text);
        }
    }

    public interface OnClickSearchItemListener {
        void onClick(CSearchTextInfo clickItem);
    }
}
