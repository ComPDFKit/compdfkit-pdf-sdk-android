/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SampleListAdapter extends RecyclerView.Adapter<SampleListAdapter.SampleListViewHolder> {

    private List<PDFSamples> list = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public SampleListAdapter(){
        list.addAll(SampleApplication.getInstance().samplesList);
    }

    @NonNull
    @Override
    public SampleListAdapter.SampleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SampleListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sample_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SampleListAdapter.SampleListViewHolder holder, int position) {
        holder.tvSampleTitle.setText(list.get(holder.getAdapterPosition()).getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class SampleListViewHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView tvSampleTitle;

        public SampleListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSampleTitle = itemView.findViewById(R.id.tv_sample_title);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
