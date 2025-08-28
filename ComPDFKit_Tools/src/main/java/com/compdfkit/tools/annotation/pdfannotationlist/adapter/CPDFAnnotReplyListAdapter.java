/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.annotation.pdfannotationlist.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFReplyAnnotation;
import com.compdfkit.core.common.CPDFDate;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationlist.dialog.CMarkedTipsWindow;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;

import java.util.List;

public class CPDFAnnotReplyListAdapter extends CBaseQuickAdapter<CPDFReplyAnnotation, CBaseQuickViewHolder> {

  private static final String REFRESH_MARKED_STATUS = "refresh_marked_status";

  public static final String REFRESH_REVIEW_STATUS = "refresh_review_status";

  @Override
  protected CBaseQuickViewHolder onCreateViewHolder(
      Context context, ViewGroup parent, int viewType) {
    return new CBaseQuickViewHolder(R.layout.tools_annot_reply_list_item_content, parent);
  }

  @Override
  protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CPDFReplyAnnotation item) {
    holder.setText(R.id.tv_author, item.getTitle());
    String date = "";
    if (item.getRecentlyModifyDate() != null){
      date = CPDFDate.toStandardDate(item.getRecentlyModifyDate());
    }
    holder.setText(R.id.tv_annot_date, CDateUtil.transformPDFDate(date));
    refreshMarkedStatus(holder, item.getMarkedAnnotState());
    refreshReviewStatus(holder, item.getReviewAnnotState());
    holder.setText(R.id.tv_annot_content, item.getContent());
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
    if (payloads.isEmpty()){
      super.onBindViewHolder(holder, position, payloads);
    }else {
      CPDFReplyAnnotation item = list.get(position);
      for (Object payload : payloads) {
        if(payload == REFRESH_REVIEW_STATUS){
          refreshReviewStatus((CBaseQuickViewHolder) holder, item.getReviewAnnotState());
        } else if(payload == REFRESH_MARKED_STATUS){
          refreshMarkedStatus((CBaseQuickViewHolder) holder, item.getMarkedAnnotState());
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
    CPDFReplyAnnotation replyAnnotation = list.get(position);
    replyAnnotation.setReviewAnnotState(reviewState);
    replyAnnotation.updateAp();
    notifyItemChanged(position, REFRESH_REVIEW_STATUS);
  }

  public void checkedMarkedStatus(int position){
    CPDFReplyAnnotation replyAnnotation = list.get(position);
    if (replyAnnotation.getMarkedAnnotState() == CPDFAnnotation.MarkState.MARKED) {
      replyAnnotation.setMarkedAnnotState(CPDFAnnotation.MarkState.UNMARKED);
    }else {
      replyAnnotation.setMarkedAnnotState(CPDFAnnotation.MarkState.MARKED);
    }
    replyAnnotation.updateAp();
    notifyItemChanged(position, REFRESH_MARKED_STATUS);
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
    tipsWindow.setMarkState(list.get(position).getMarkedAnnotState());
    tipsWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    int winWidth = tipsWindow.getContentView().getMeasuredWidth();
    int winHeight = tipsWindow.getContentView().getMeasuredHeight();
    int offsetX = (anchorView.getWidth() - winWidth) / 2;
    int offsetY =  anchorView.getHeight() + winHeight;
    tipsWindow.showAsDropDown(anchorView, offsetX, -offsetY);
  }

}
