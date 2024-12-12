/**
 * Copyright © 2014-2024 PDF Technologies, Inc. All Rights Reserved.
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
import com.compdfkit.core.document.CPDFDocument.CompressListener;
import com.compdfkit.tools.R;


public class CPDFCompressLoadingDialog extends DialogFragment {

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
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (getDialog() != null){
      getDialog().getWindow().setBackgroundDrawableResource(R.drawable.tools_dialog_background);
    }
    View rootView = LayoutInflater.from(getContext()).inflate(R.layout.tools_compress_loading_dialog, container, false);
    tvProgress = rootView.findViewById(R.id.tv_progress);
    return rootView;
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
