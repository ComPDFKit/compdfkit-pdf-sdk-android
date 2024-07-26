package com.compdfkit.tools.common.utils.adapter;


import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class CBaseQuickAdapter<T,VH extends CBaseQuickViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<T> list = new ArrayList<>();

    private OnItemClickListener<T> onItemClickListener;

    private OnItemLongClickListener<T> onItemLongClickListener;

    private SparseArray<OnItemChildClickListener<T>> childClickArray = new SparseArray<OnItemChildClickListener<T>>(3);

    private SparseArray<OnItemChildLongClickListener<T>> childLongClickArray = new SparseArray<OnItemChildLongClickListener<T>>(3);


    public void setList(List<T> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<T> list){
        this.list.addAll(list);
        notifyItemRangeInserted(this.list.size() - list.size(), list.size());
    }

    public void addItem(T item){
        this.list.add(item);
        notifyItemInserted(list.size() -1);
    }

    public void addItem(@IntRange(from = 0) int index, T item){
        this.list.add(index, item);
        notifyItemInserted(index);
    }

    public void remove(@IntRange(from = 0) int index){
        this.list.remove(index);
        notifyItemRemoved(index);
    }

    protected abstract VH onCreateViewHolder(
            Context context,  ViewGroup parent, int viewType
    );

    protected abstract void onBindViewHolder(VH holder, int position, T item);

    protected void onBindViewHolder(VH holder,int position,  T item, List<Object> payloads) {
        onBindViewHolder(holder, position, item);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VH viewHolder =  onCreateViewHolder(parent.getContext(), parent, viewType);
        bindViewClickListener(viewHolder, viewType);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((VH) holder, holder.getAdapterPosition(), list.get(holder.getAdapterPosition()));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        onBindViewHolder((VH) holder, position, list.get(holder.getAdapterPosition()), payloads);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected void bindViewClickListener(VH viewHolder, int viewType){
        if (onItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(v -> {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION){
                    return;
                }
                onItemClick(v, position);
            });
        }

        if (onItemLongClickListener != null) {
            viewHolder.itemView.setOnClickListener(v -> {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION){
                    return;
                }
                onItemLongClick(v, position);
            });
        }

        if (onItemLongClickListener != null){
            viewHolder.itemView.setOnLongClickListener(v -> {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION){
                    return false;
                }
                return onItemLongClickListener.onLongClick((CBaseQuickAdapter<T, CBaseQuickViewHolder>) this, v, position);
            });
        }

        for (int i = 0; i < childClickArray.size(); i++) {
            int id = childClickArray.keyAt(i);
            View childView = viewHolder.itemView.findViewById(id);
            if (childView != null) {
                childView.setOnClickListener(v -> {
                    int position = viewHolder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION){
                        return;
                    }
                    onItemChildClick(v, position);
                });
            }
        }

        for (int i = 0; i < childLongClickArray.size(); i++) {
            int id = childLongClickArray.keyAt(i);
            View childView = viewHolder.itemView.findViewById(id);
            if (childView != null) {
                childView.setOnLongClickListener(v -> {
                    int position = viewHolder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION){
                        return false;
                    }
                    return onItemChildLongClick(v, position);
                });
            }
        }
    }

    protected void onItemClick(View view, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onClick((CBaseQuickAdapter<T, CBaseQuickViewHolder>) this, view, position);
        }
    }

    protected boolean onItemLongClick(View view, int position) {
        if (onItemLongClickListener != null) {
            return onItemLongClickListener.onLongClick((CBaseQuickAdapter<T, CBaseQuickViewHolder>) this, view, position);
        }else {
            return false;
        }
    }

    protected void onItemChildClick(View view, int position){
        OnItemChildClickListener<T> listener = childClickArray.get(view.getId());
        if (listener != null){
            listener.onItemClick((CBaseQuickAdapter<T, CBaseQuickViewHolder>) this, view, position);
        }
    }

    protected boolean onItemChildLongClick(View view, int position){
        OnItemChildLongClickListener<T> listener = childLongClickArray.get(view.getId());
        if (listener != null){
            return listener.onItemLongClick((CBaseQuickAdapter<T, CBaseQuickViewHolder>) this, view, position);
        }else {
            return false;
        }
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void addOnItemChildClickListener(@IdRes int viewId, OnItemChildClickListener<T> listener){
        childClickArray.put(viewId, listener);
    }

    public void addOnItemChildClickListener(OnItemChildClickListener<T> listener, @IdRes int... viewIds){
        for (int viewId : viewIds) {
            childClickArray.put(viewId, listener);
        }
    }

    public void addOnItemChildLongClickListener(@IdRes int viewId, OnItemChildLongClickListener<T> listener){
        childLongClickArray.put(viewId, listener);
    }

    public interface OnItemClickListener<T> {
        void onClick(CBaseQuickAdapter<T, CBaseQuickViewHolder> adapter, View view, int position);
    }

    public interface OnItemLongClickListener<T>{
        boolean onLongClick(CBaseQuickAdapter<T, CBaseQuickViewHolder> adapter, View view, int position);
    }

    public interface OnItemChildClickListener<T>{
        void onItemClick(CBaseQuickAdapter<T, CBaseQuickViewHolder> adapter, View view, int position);
    }

    public interface OnItemChildLongClickListener<T>{
        boolean onItemLongClick(CBaseQuickAdapter<T, CBaseQuickViewHolder> adapter, View view, int position);
    }
}
