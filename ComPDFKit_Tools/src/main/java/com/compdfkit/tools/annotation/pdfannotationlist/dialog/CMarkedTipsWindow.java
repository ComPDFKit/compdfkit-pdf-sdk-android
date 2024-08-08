/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * <p>THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT
 * LAW AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */
package com.compdfkit.tools.annotation.pdfannotationlist.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.window.CBasePopupWindow;

public class CMarkedTipsWindow extends CBasePopupWindow {

  private CPDFAnnotation.MarkState markState = CPDFAnnotation.MarkState.MARKED;

  private AppCompatTextView tvMarked;

  public CMarkedTipsWindow(Context context) {
    super(context);
  }

  @Override
  protected View setLayout(LayoutInflater inflater) {
    return LayoutInflater.from(mContext).inflate(R.layout.tools_annot_marked_tips, null);
  }

  @Override
  protected void initResource() {}

  @Override
  protected void initListener() {}

  @Override
  protected void initView() {
    tvMarked = mContentView.findViewById(R.id.tv_marked_status);
  }

  public void setMarkState(CPDFAnnotation.MarkState markState) {
    this.markState = markState;
    if (markState == CPDFAnnotation.MarkState.MARKED){
      tvMarked.setText(R.string.tools_marked);
    }else {
      tvMarked.setText(R.string.tools_unmarked);
    }
  }

  @Override
  public View getContentView() {
    return super.getContentView();
  }

  @Override
  protected void onClickListener(View view) {}

  @Override
  public void showAsDropDown(View anchor, int xoff, int yoff) {
    super.showAsDropDown(anchor, xoff, yoff);
    try{
      anchor.postDelayed(this::dismiss, 500);
    }catch (Exception e){

    }
  }
}
