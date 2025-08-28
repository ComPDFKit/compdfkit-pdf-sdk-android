/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationlist.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationlist.bean.CPDFAnnotListItem;
import com.compdfkit.tools.annotation.pdfannotationlist.dialog.CMarkedTipsWindow;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.google.android.material.color.MaterialColors;

import java.util.HashMap;
import java.util.List;


public class CPDFAnnotListAdapter  extends CBaseQuickAdapter<CPDFAnnotListItem, CBaseQuickViewHolder> {

    private static final String REFRESH_MARKED_STATUS = "refresh_marked_status";

    public static final String REFRESH_REVIEW_STATUS = "refresh_review_status";

    private static final int ITEM_HEADER = 1;

    private static final int ITEM_CONTENT = 2;

    private static final HashMap<CPDFAnnotation.Type, Integer> ICON_RES_IDS = new HashMap<>();
    private static final HashMap<CPDFAnnotation.Type, Integer> ICON_RES_IDS_DARK = new HashMap<>();

    private boolean showMoreMenu = true;

    static {
        ICON_RES_IDS.put(CPDFAnnotation.Type.TEXT, R.drawable.tools_ic_annotation_note);
        ICON_RES_IDS.put(CPDFAnnotation.Type.HIGHLIGHT, R.drawable.tools_ic_annotation_highlight_normal);
        ICON_RES_IDS.put(CPDFAnnotation.Type.UNDERLINE, R.drawable.tools_ic_annotation_underline_normal);
        ICON_RES_IDS.put(CPDFAnnotation.Type.SQUIGGLY, R.drawable.tools_ic_annotation_squiggly_normal);
        ICON_RES_IDS.put(CPDFAnnotation.Type.STRIKEOUT, R.drawable.tools_ic_annotation_strikeout_normal);
        ICON_RES_IDS.put(CPDFAnnotation.Type.FREETEXT, R.drawable.tools_ic_annotation_freetext);
        ICON_RES_IDS.put(CPDFAnnotation.Type.INK, R.drawable.tools_ic_annotation_ink_normal);
        ICON_RES_IDS.put(CPDFAnnotation.Type.LINE, R.drawable.tools_ic_annotation_shape_line);
        ICON_RES_IDS.put(CPDFAnnotation.Type.SQUARE, R.drawable.tools_ic_annotation_shape_rectangle);
        ICON_RES_IDS.put(CPDFAnnotation.Type.CIRCLE, R.drawable.tools_ic_annotation_shape_circular);
        ICON_RES_IDS.put(CPDFAnnotation.Type.STAMP, R.drawable.tools_ic_annotation_stamp);
        ICON_RES_IDS.put(CPDFAnnotation.Type.LINK, R.drawable.tools_ic_annotation_link);
        ICON_RES_IDS.put(CPDFAnnotation.Type.SOUND, R.drawable.tools_ic_annotation_sound);


        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.TEXT, R.drawable.tools_ic_annotation_note);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.HIGHLIGHT, R.drawable.tools_ic_annotation_highlight_normal_dark_1);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.UNDERLINE, R.drawable.tools_ic_annotation_underline_normal_dark_1);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.SQUIGGLY, R.drawable.tools_ic_annotation_squiggly_normal_dark_1);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.STRIKEOUT, R.drawable.tools_ic_annotation_strikeout_normal_dark_1);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.FREETEXT, R.drawable.tools_ic_annotation_freetext);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.INK, R.drawable.tools_ic_annotation_ink_normal_dark_1);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.LINE, R.drawable.tools_ic_annotation_shape_line);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.SQUARE, R.drawable.tools_ic_annotation_shape_rectangle);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.CIRCLE, R.drawable.tools_ic_annotation_shape_circular);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.STAMP, R.drawable.tools_ic_annotation_stamp);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.LINK, R.drawable.tools_ic_annotation_link);
        ICON_RES_IDS_DARK.put(CPDFAnnotation.Type.SOUND, R.drawable.tools_ic_annotation_sound);
    }


    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        if (viewType == ITEM_HEADER){
            return new CBaseQuickViewHolder(R.layout.tools_bota_annotation_list_item_header, parent);
        }else {
            return new CBaseQuickViewHolder(R.layout.tools_bota_annotation_list_item_content, parent);
        }
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CPDFAnnotListItem item) {
        if (item.isHeader()){
            holder.setText(R.id.id_item_annot_head_page, String.valueOf(item.getPage() + 1));
            holder.setText(R.id.id_item_annot_count, String.valueOf(item.getAnnotationCount()));
        }else {
            int isLightThemeValue = CViewUtils.getThemeAttrData(holder.itemView.getContext().getTheme(),  com.google.android.material.R.attr.isLightTheme);
            boolean isDarkTheme = isLightThemeValue == 0;
            Integer annotIcon = ICON_RES_IDS.get(item.getAnnotType());
            if (isDarkTheme){
                annotIcon = ICON_RES_IDS_DARK.get(item.getAnnotType());
            }
            if (annotIcon != null) {
                if (item.getAnnotType() == CPDFAnnotation.Type.LINE){
                    if (item.isArrowLine()){
                        holder.setImageResource(R.id.iv_annot_icon, R.drawable.tools_ic_annotation_shape_arrow);
                    }else {
                        holder.setImageResource(R.id.iv_annot_icon, annotIcon);
                    }
                }else {
                    holder.setImageResource(R.id.iv_annot_icon, annotIcon);
                }
            }
            switch (item.getAnnotType()){
                case TEXT:
                case SQUARE:
                case CIRCLE:
                case LINE:
                    holder.setImageTintList(R.id.iv_annot_icon, ColorStateList.valueOf(item.getColor()));
                    holder.setBackgroundColor(R.id.view_icon_bg, Color.TRANSPARENT);
                    break;
                case HIGHLIGHT:
                case UNDERLINE:
                case SQUIGGLY:
                case STRIKEOUT:
                case INK:
                    holder.setBackgroundColor(R.id.view_icon_bg, item.getColor());
                    holder.setImageTintList(R.id.iv_annot_icon, null);
                    break;
                case FREETEXT:
                case STAMP:
                case SOUND:
                    Context context = holder.itemView.getContext();
                    holder.setBackgroundColor(R.id.view_icon_bg, 0);
                    holder.setImageTintList(R.id.iv_annot_icon,
                            ColorStateList.valueOf(MaterialColors.getColor(
                                    context, android.R.attr.textColorPrimary,
                                    ContextCompat.getColor(
                                            context, R.color.tools_text_color_primary
                                    ))));
                    break;
                default:
                    holder.setBackgroundColor(R.id.view_icon_bg, 0);
                    holder.setImageTintList(R.id.iv_annot_icon, null);
                    break;
            }
            holder.setVisible(R.id.tv_annot_content, !TextUtils.isEmpty(item.getContent()));
            holder.setText(R.id.tv_annot_content, item.getContent());
            holder.setText(R.id.tv_annot_date, item.getModifyDate());
            holder.setText(R.id.tv_author, item.getAttr().getTitle());
            holder.setVisible(R.id.iv_more, showMoreMenu);
            refreshReviewStatus(holder, item.getAttr().getReviewAnnotState());
            refreshMarkedStatus(holder, item.getAttr().getMarkedAnnotState());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads);
        }else {
            CPDFAnnotListItem item = list.get(position);
            for (Object payload : payloads) {
                if(payload == REFRESH_REVIEW_STATUS){
          refreshReviewStatus((CBaseQuickViewHolder) holder, item.getAttr().getReviewAnnotState());
                } else if(payload == REFRESH_MARKED_STATUS){
          refreshMarkedStatus((CBaseQuickViewHolder) holder, item.getAttr().getMarkedAnnotState());
                }
            }
        }
    }

    private void refreshReviewStatus(CBaseQuickViewHolder adapter, CPDFAnnotation.ReviewState reviewState){
        switch (reviewState){
            case REVIEW_NONE:
                adapter.setImageResource(R.id.iv_review_status, R.drawable.tools_annot_review_status_none);
                break;
            case REVIEW_ACCEPTED:
                adapter.setImageResource(R.id.iv_review_status, R.drawable.tools_annot_review_status_accepted);

                break;
            case REVIEW_REJECTED:
                adapter.setImageResource(R.id.iv_review_status, R.drawable.tools_annot_review_status_rejected);

                break;
            case REVIEW_COMPLETED:
                adapter.setImageResource(R.id.iv_review_status, R.drawable.tools_annot_review_status_completed);

                break;
            case REVIEW_CANCELLED:
                adapter.setImageResource(R.id.iv_review_status, R.drawable.tools_annot_review_status_cancelled);
                break;
            case REVIEW_ERROR:
                adapter.setImageResource(R.id.iv_review_status, R.drawable.tools_annot_review_status_none);
                break;
            default:break;
        }
    }

    private void refreshMarkedStatus(CBaseQuickViewHolder adapter, CPDFAnnotation.MarkState markState){
        adapter.setChecked(R.id.cb_marked_status, markState == CPDFAnnotation.MarkState.MARKED);
    }

    public void setReviewStatus(int position, CPDFAnnotation.ReviewState reviewState){
        CPDFAnnotation annotation = list.get(position).getAttr();
        annotation.setReviewAnnotState(reviewState);
        annotation.updateAp();
        notifyItemChanged(position, REFRESH_REVIEW_STATUS);
    }

    public void checkedMarkedStatus(int position){
        CPDFAnnotListItem item = list.get(position);
        if (item.getAttr().getMarkedAnnotState() == CPDFAnnotation.MarkState.MARKED) {
            item.getAttr().setMarkedAnnotState(CPDFAnnotation.MarkState.UNMARKED);
        }else {
            item.getAttr().setMarkedAnnotState(CPDFAnnotation.MarkState.MARKED);
        }
        item.getAttr().updateAp();
        notifyItemChanged(position, REFRESH_MARKED_STATUS);
    }

    public void setShowMoreMenu(boolean showMoreMenu) {
        this.showMoreMenu = showMoreMenu;
    }

    @Override
    public int getItemViewType(int position) {
       return list.get(position).isHeader() ? ITEM_HEADER : ITEM_CONTENT;
    }

    /**
     * Display the comment reply status menu
     * @param position The subscript of the selected annotation in the list
     * @param anchorView The position anchor view to be popped up, displayed above or below it
     */
    public void showReviewStatusMenu(Context context, int position, View anchorView){
        anchorView.setSelected(true);
        CPopupMenuWindow reviewStatusMenuWindow = new CPopupMenuWindow(context);
        reviewStatusMenuWindow.setOutsideTouchable(false);
        reviewStatusMenuWindow.addItem(R.drawable.tools_annot_review_status_accepted, R.string.tools_accepted, v -> {
            setReviewStatus(position, CPDFAnnotation.ReviewState.REVIEW_ACCEPTED);
        });
        reviewStatusMenuWindow.addItem(R.drawable.tools_annot_review_status_rejected, R.string.tools_rejected, v -> {
            setReviewStatus(position, CPDFAnnotation.ReviewState.REVIEW_REJECTED);
        });
        reviewStatusMenuWindow.addItem(R.drawable.tools_annot_review_status_cancelled, R.string.tools_cancelled, v -> {
            setReviewStatus(position, CPDFAnnotation.ReviewState.REVIEW_CANCELLED);
        });
        reviewStatusMenuWindow.addItem(R.drawable.tools_annot_review_status_completed, R.string.tools_completed, v -> {
            setReviewStatus(position, CPDFAnnotation.ReviewState.REVIEW_COMPLETED);
        });
        reviewStatusMenuWindow.addItem(R.drawable.tools_annot_review_status_none, R.string.tools_none, v -> {
            setReviewStatus(position, CPDFAnnotation.ReviewState.REVIEW_NONE);
        });
        reviewStatusMenuWindow.setOnDismissListener(() -> anchorView.setSelected(false));
        //Calculate the position of the pop-up.
        // If there is enough space at the bottom,
        // it will be displayed below the anchor view. If not, it will be displayed above it.
        int[] windowPos = CDimensUtils.calculatePopWindowPos(anchorView, reviewStatusMenuWindow.getContentView());
        reviewStatusMenuWindow.setAnimationStyle(R.style.PopupAnimation);
        reviewStatusMenuWindow.showAtLocation(anchorView, Gravity.START | Gravity.TOP, windowPos[0], windowPos[1]);
    }

    /**
     * Show annotation markup status menu
     * @param position The subscript of the selected annotation in the list
     * @param anchorView The position anchor view to be popped up, displayed above or below it
     */
    public void showMarkedStatusMenu(Context context, int position, View anchorView){
        checkedMarkedStatus(position);
        CMarkedTipsWindow tipsWindow = new CMarkedTipsWindow(context);
        tipsWindow.setMarkState(list.get(position).getAttr().getMarkedAnnotState());
        tipsWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int winWidth = tipsWindow.getContentView().getMeasuredWidth();
        int winHeight = tipsWindow.getContentView().getMeasuredHeight();
        int offsetX = (anchorView.getWidth() - winWidth) / 2;
        int offsetY =  anchorView.getHeight() + winHeight;
        tipsWindow.showAsDropDown(anchorView, offsetX, -offsetY);
    }

}
