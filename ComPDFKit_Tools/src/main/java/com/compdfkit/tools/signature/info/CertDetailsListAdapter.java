/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.info;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.signature.bean.CPDFOwnerItemData;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.List;


public class CertDetailsListAdapter extends CBaseQuickAdapter<CPDFOwnerItemData, CBaseQuickViewHolder> {

    private static final String REFRESH_ARROW = "refresh_arrow";

    private List<CPDFOwnerItemData> backList = new ArrayList<>();

    @Override
    public void setList(List<CPDFOwnerItemData> list) {
        super.setList(list);
        backList.addAll(list);
    }

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_sign_certificate_details_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CPDFOwnerItemData item) {
        holder.setText(R.id.tv_item_title, item.getContent());
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.getView(R.id.iv_item_arrow).getLayoutParams();
        layoutParams.setMarginStart(getLevelMargin(holder.itemView.getContext(), position +1));
        holder.setBackgroundColor(R.id.cl_root, MaterialColors.getColor(holder.itemView.getContext(), position == 0 ?
                R.attr.compdfkit_HeadItem_BackgroundColor : android.R.attr.colorBackground,
                ContextCompat.getColor(holder.itemView.getContext(), R.color.tools_color_background)));

        AppCompatTextView title = holder.getView(R.id.tv_item_title);
        title.setTypeface(position == 0 ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        holder.setImageResource(R.id.iv_item_arrow, item.isExpanded() ? R.drawable.tools_ic_arrow_down : R.drawable.tools_ic_right);
        View view = holder.getView(R.id.iv_item_arrow);
        view.setVisibility(item.isHasParent() ? View.VISIBLE : View.INVISIBLE);
        holder.setOnClickListener(R.id.iv_item_arrow, v -> {
            if (item.isExpanded()) {
                collapseItem(position, item);
            }else {
                expandItem(position, item);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        CPDFOwnerItemData item = list.get(position);
        ((CBaseQuickViewHolder) holder)
                .setImageResource(R.id.iv_item_arrow, item.isExpanded() ? R.drawable.tools_ic_arrow_down : R.drawable.tools_ic_right);
    }

    private int getLevelMargin(Context context, int position) {
        return CDimensUtils.dp2px(context, (position - 1) * 15);
    }

    private void expandItem(int positionStart, CPDFOwnerItemData item) {
        item.setExpanded(true);
        List<CPDFOwnerItemData> tempList = new ArrayList<>(backList.subList(positionStart +1, backList.size()));
        list.addAll(positionStart + 1, tempList);
        // Notify the adapter that the arrow status of the clicked item has changed
        notifyItemChanged(positionStart, REFRESH_ARROW);
        // Notify the adapter that data has been inserted
        notifyItemRangeInserted(positionStart + 1, tempList.size());
    }


    private void collapseItem(int positionStart, CPDFOwnerItemData item) {
        item.setExpanded(false);
        int removeCount = list.size() - positionStart -1;
        for (int i = list.size() -1; i > positionStart; i--) {
            list.remove(i);
        }
        notifyItemChanged(positionStart, REFRESH_ARROW);
        notifyItemRangeRemoved(positionStart + 1, removeCount);
    }
}
