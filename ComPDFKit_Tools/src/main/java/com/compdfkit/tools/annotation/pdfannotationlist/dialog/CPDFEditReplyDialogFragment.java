/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationlist.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;


public class CPDFEditReplyDialogFragment extends CBasicBottomSheetDialogFragment {

  public static final String EXTRA_TITLE = "extra_title_res_id";

  public static final String EXTRA_CONTENT = "extra_content";

  private CToolBar toolBar;

  private AppCompatEditText editText;

  private AppCompatButton btnDone;

  private CEditReplyContentListener replyContentListener;

  public static CPDFEditReplyDialogFragment addReply() {
    Bundle args = new Bundle();
    args.putInt(EXTRA_TITLE, R.string.tools_add_a_new_reply);
    CPDFEditReplyDialogFragment fragment = new CPDFEditReplyDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static CPDFEditReplyDialogFragment editReply(String replyContent) {
    Bundle args = new Bundle();
    args.putInt(EXTRA_TITLE, R.string.tools_edit_reply);
    args.putString(EXTRA_CONTENT, replyContent);
    CPDFEditReplyDialogFragment fragment = new CPDFEditReplyDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected boolean fullScreen() {
    return true;
  }

  @Override
  protected boolean draggable() {
    return false;
  }

  @Override
  protected float dimAmount() {
    return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
  }

  @Override
  protected int layoutId() {
    return R.layout.tools_annot_edit_reply_dialog_fragment;
  }

  @Override
  protected void onCreateView(View rootView) {
    toolBar = rootView.findViewById(R.id.tool_bar);
    editText = rootView.findViewById(R.id.tv_reply_content);
    btnDone = rootView.findViewById(R.id.btn_save);
    toolBar.setBackBtnClickListener(v -> {
      CViewUtils.hideKeyboard(editText);
      dismiss();
    });
    btnDone.setOnClickListener(v -> {
      CViewUtils.hideKeyboard(editText);
      editText.postDelayed(()->{
        if (replyContentListener != null) {
          String content = TextUtils.isEmpty(editText.getText()) ? "" : editText.getText().toString();
          if (TextUtils.isEmpty(content)){
            return;
          }
          replyContentListener.reply(content);
        }
        dismiss();
      }, 400);
    });
    btnDone.setEnabled(false);
    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        btnDone.setEnabled(!TextUtils.isEmpty(charSequence));
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });
  }

  @Override
  protected void onViewCreate() {
    if (getArguments() != null) {
      int titleResId = getArguments().getInt(EXTRA_TITLE, -1);
      if (titleResId != -1) {
        toolBar.setTitle(titleResId);
      } else {
        toolBar.setTitle("");
      }
      String replyContent = getArguments().getString(EXTRA_CONTENT);
      if (!TextUtils.isEmpty(replyContent)){
        editText.setText(replyContent);
        editText.setSelection(replyContent.length());
      }
      CViewUtils.showKeyboard(editText);
    }
  }

  public void setReplyContentListener(CEditReplyContentListener replyContentListener) {
    this.replyContentListener = replyContentListener;
  }

  public interface CEditReplyContentListener{
    void reply(String content);
  }
}
