package com.compdfkit.tools.viewer.pdfbookmark.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.compdfkit.core.document.CPDFBookmark;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;

import java.util.List;

public class CPDFBookmarkListAdapter extends ListAdapter<CPDFBookmark, CBaseQuickViewHolder> {

    public static final String REFRESH_ITEM = "refresh_item";

    private COnSetPDFDisplayPageIndexListener displayPageIndexListener;

    private OnBookmarkCallback editBookmarkClickListener;

    private OnBookmarkCallback deleteBookmarkClickListener;

    public CPDFBookmarkListAdapter() {
        super(new BookmarkDiffCallback());
    }

    @NonNull
    @Override
    public CBaseQuickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_bota_bookmark_list_item, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CBaseQuickViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object payload : payloads) {
                if (payload == REFRESH_ITEM) {
                    CPDFBookmark item = getCurrentList().get(position);
                    holder.setText(R.id.tv_bookmark_title, item.getTitle());
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CBaseQuickViewHolder holder, int position) {
        CPDFBookmark bookmark = getItem(holder.getAdapterPosition());
        holder.setText(R.id.tv_bookmark_title, bookmark.getTitle());
        holder.setText(R.id.tv_bookmark_page, holder.itemView.getContext().getString(R.string.tools_page) + " " + (bookmark.getPageIndex() + 1));
        holder.setOnClickListener(R.id.iv_bookmark_more,v -> {
            CPopupMenuWindow menuWindow = new CPopupMenuWindow(v.getContext());
            menuWindow.addItem(R.string.tools_edit, v1 -> {
                if (editBookmarkClickListener != null) {
                    editBookmarkClickListener.bookmark(bookmark, holder.getAdapterPosition());
                }
            });
            menuWindow.addItem(R.string.tools_delete, v1 -> {
                if (deleteBookmarkClickListener != null) {
                    deleteBookmarkClickListener.bookmark(bookmark, holder.getAdapterPosition());
                }
            });
            menuWindow.showAsDropDown(v);
        });
        holder.itemView.setOnClickListener(v -> {
            if (displayPageIndexListener != null) {
                displayPageIndexListener.displayPage(bookmark.getPageIndex());
            }
        });
    }

    public boolean hasBookmark(int pageIndex){
        for (CPDFBookmark bookmark : getCurrentList()) {
            if (bookmark.getPageIndex() == pageIndex) {
                return true;
            }
        }
        return false;
    }

    public void setDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.displayPageIndexListener = displayPageIndexListener;
    }

    public void setDeleteBookmarkClickListener(OnBookmarkCallback deleteBookmarkClickListener) {
        this.deleteBookmarkClickListener = deleteBookmarkClickListener;
    }

    public void setEditBookmarkClickListener(OnBookmarkCallback bookmarkClickListener) {
        this.editBookmarkClickListener = bookmarkClickListener;
    }

    public interface OnBookmarkCallback {
        void bookmark(CPDFBookmark bookmark, int index);
    }

    static class BookmarkDiffCallback extends DiffUtil.ItemCallback<CPDFBookmark> {
        @Override
        public boolean areItemsTheSame(@NonNull CPDFBookmark oldItem, @NonNull CPDFBookmark newItem) {
            return oldItem.toString().equals(newItem.toString());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CPDFBookmark oldItem, @NonNull CPDFBookmark newItem) {
            return oldItem == newItem;
        }
    }

    public void updateBookmarkItem(int index) {
        notifyItemChanged(index, REFRESH_ITEM);
    }
}
