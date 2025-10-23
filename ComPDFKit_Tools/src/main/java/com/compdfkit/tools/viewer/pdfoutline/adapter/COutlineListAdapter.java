/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfoutline.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfoutline.bean.COutlineData;
import com.compdfkit.tools.viewer.pdfoutline.data.COutlineDatas;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.List;


/**
 * PDF outline data list. When the outline has sub-levels,<br/>
 * the sub-level outline list can be expanded or collapsed by clicking.<br/>
 * Clicking on a non-collapsible outline will jump to the corresponding PDF page.<br/>
 * <p>
 * <p/>
 * If you do not like this list style and want to obtain PDF outline data for display on your own,<br/>
 * you can get the PDF outline data by calling "cpdfView.getCPdfReaderView().getPDFDocument().getOutlineRoot()". <br/>
 * Alternatively, you can use the preprocessed data that we have provided for you.<br/>
 * {@link COutlineDatas#getOutlineList(CPDFViewCtrl cpdfView, int maxLevel)}
 *
 * @see CPDFViewCtrl
 * @see CPDFReaderView#getPDFDocument()
 * @see COutlineDatas
 */
public class COutlineListAdapter extends CBaseQuickAdapter<COutlineData, CBaseQuickViewHolder> {

    private static final String REFRESH_ARROW = "refresh_arrow";

    private COnSetPDFDisplayPageIndexListener outlineClickListener;

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_bota_outline_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, COutlineData item) {
        holder.setText(R.id.tv_outline_item_title, item.getTitle());
        if (item.getPageIndex() < 0) {
            holder.setText(R.id.tv_outline_item_page, item.getTitle());
        } else {
            holder.setText(R.id.tv_outline_item_page, String.valueOf(item.getPageIndex() + 1));
        }
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.getView(R.id.iv_outline_item_arrow).getLayoutParams();
        layoutParams.setMarginStart(item.getLevelMargin(holder.itemView.getContext()));
        holder.getView(R.id.iv_outline_item_arrow).setLayoutParams(layoutParams);
        holder.getView(R.id.iv_outline_item_arrow).setVisibility( item.childOutlineIsEmpty() ? View.INVISIBLE : View.VISIBLE);
        holder.setImageResource(R.id.iv_outline_item_arrow, item.isExpand() ? R.drawable.tools_ic_arrow_down : R.drawable.tools_ic_right);
        holder.setBackgroundColor(R.id.cl_root, CViewUtils.getThemeAttrData(holder.itemView.getContext().getTheme(),
                item.getLevel() == 1 ? R.attr.compdfkit_HeadItem_BackgroundColor : android.R.attr.colorBackground));
        holder.itemView.setOnClickListener(v -> {
            // Check if the child outline of the item is empty
            // If the child outline is empty and an outlineClickListener is set, display the page
            if (outlineClickListener != null) {
                outlineClickListener.displayPage(item.getPageIndex());
            }
        });
        holder.setOnClickListener(R.id.iv_outline_item_arrow, v -> {
            // If the child outline is not empty, check if the item is already expanded
            if (item.isExpand()) {
                // If the item is already expanded, collapse it
                collapseItem(holder.getAdapterPosition(), item);
            } else {
                // If the item is not expanded, expand it
                expandItem(holder.getAdapterPosition(), item);
            }
        });
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, COutlineData item, List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, item, payloads);
        } else {
            for (Object payload : payloads) {
                if (payload == REFRESH_ARROW) {
                    holder.setImageResource(R.id.iv_outline_item_arrow, item.isExpand() ? R.drawable.tools_ic_arrow_down : R.drawable.tools_ic_right);
                }
            }
        }
    }

    /**
     * Expand the given item and add its child items to the list.
     *
     * @param positionStart the starting position of the given item in the list
     * @param item          the item to be expanded
     */
    private void expandItem(int positionStart, COutlineData item) {
        // Set expand flag of the item to true
        item.setExpand(true);
        // Add child items of the expanded item to the list
        list.addAll(positionStart + 1, item.getChildOutline());
        // Notify the adapter that the arrow status of the clicked item has changed
        notifyItemChanged(positionStart, REFRESH_ARROW);
        // Notify the adapter that data has been inserted
        notifyItemRangeInserted(positionStart + 1, item.getChildOutline().size());
    }

    /**
     * Collapse the given item and its child items.
     *
     * @param positionStart The position of the item in the adapter.
     * @param item          The item to collapse.
     */
    private void collapseItem(int positionStart, COutlineData item) {
        int removeCount = loopCollapseItem(item);
        notifyItemChanged(positionStart, REFRESH_ARROW);
        notifyItemRangeRemoved(positionStart + 1, removeCount);
    }

    /**
     * Recursively collapse the given item and its child items.
     *
     * @param outlineData The item to collapse.
     * @return The number of items removed from the adapter.
     */
    private int loopCollapseItem(COutlineData outlineData) {
        outlineData.setExpand(false);
        int removeCount = 0;
        for (COutlineData cOutlineData : outlineData.getChildOutline()) {
            if (!cOutlineData.childOutlineIsEmpty()) {
                removeCount += loopCollapseItem(cOutlineData);
            }
            boolean success = list.remove(cOutlineData);
            if (success) {
                removeCount++;
            }
        }
        return removeCount;
    }

    public void setOutlineClickListener(COnSetPDFDisplayPageIndexListener outlineClickListener) {
        this.outlineClickListener = outlineClickListener;
    }
}
