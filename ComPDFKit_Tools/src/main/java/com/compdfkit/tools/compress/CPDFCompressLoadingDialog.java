/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.compress;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeDialogFragment;


public class CPDFCompressLoadingDialog extends CBasicThemeDialogFragment {

  private AppCompatTextView tvProgress;

  private int currentPageIndex;

  private int totalPage;

  public static CPDFCompressLoadingDialog newInstance(int totalPage) {

    Bundle args = new Bundle();
    args.putInt("extra_total_page", totalPage);
    CPDFCompressLoadingDialog fragment = new CPDFCompressLoadingDialog();
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public void onStart() {
    super.onStart();
    if (getDialog() != null) {
      getDialog().setCanceledOnTouchOutside(false);
      getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
  }

  @Override
  protected int layoutId() {
    return R.layout.tools_compress_loading_dialog;
  }

  @Override
  protected void onCreateView(View rootView) {
    tvProgress = rootView.findViewById(R.id.tv_progress);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    totalPage = getArguments().getInt("extra_total_page", 0);
    tvProgress.setText((currentPageIndex + 1) + "/" + totalPage);
  }

  public void setCompressProgress(int currentPageIndex) {
    if (tvProgress != null) {
      this.currentPageIndex = currentPageIndex + 1;
      tvProgress.post(()->{
        tvProgress.setText(this.currentPageIndex + "/" + totalPage);
      });
    }
  }


}
