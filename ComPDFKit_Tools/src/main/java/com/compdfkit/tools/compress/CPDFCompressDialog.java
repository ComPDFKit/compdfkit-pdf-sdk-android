/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */

package com.compdfkit.tools.compress;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.activitycontracts.CSelectPDFDocumentResultContract;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.viewutils.EditTextUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CPDFCompressDialog extends CBasicBottomSheetDialogFragment implements
    View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

  private AppCompatTextView tvFileName;

  private AppCompatTextView tvFilePath;

  private CToolBar toolBar;

  private AppCompatTextView tvSwap;

  private AppCompatRadioButton rbQualityLow;

  private AppCompatRadioButton rbQualityStandard;

  private AppCompatRadioButton rbQualityHigh;

  private AppCompatRadioButton rbQualityCustom;

  private AppCompatEditText etCustomQuality;

  private View savePathView;

  private AppCompatButton btnCancel;

  private AppCompatButton btnCompress;

  private AppCompatTextView tvSavePath;

  private CPDFDocument document;

  private List<AppCompatRadioButton> radioButtons = new ArrayList<>();
  protected CMultiplePermissionResultLauncher multiplePermissionResultLauncher = new CMultiplePermissionResultLauncher(this);

  private String savePath = Environment.getExternalStoragePublicDirectory(
      Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

  private COnCompressDocumentListener compressDocumentListener;

  private ActivityResultLauncher<Void> selectDocumentLauncher = registerForActivityResult(
      new CSelectPDFDocumentResultContract(), uri -> {
        if (uri != null) {
          verifyDocument(uri, document -> {
            this.document = document;
            setDocumentInfo();
          });
        }
      });
  private CPDFCompressLoadingDialog loadingDialog;

  public void setDocument(CPDFDocument document) {
    this.document = document;
  }


  @Override
  protected float dimAmount() {
    return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
  }

  @Override
  protected boolean draggable() {
    return false;
  }

  @Override
  protected boolean fullScreen() {
    return true;
  }


  @Override
  protected int layoutId() {
    return R.layout.tools_compress_dialog;
  }

  @Override
  protected void onCreateView(View rootView) {
    toolBar = rootView.findViewById(R.id.tool_bar);
    tvSwap = rootView.findViewById(R.id.tv_swap);
    tvFileName = rootView.findViewById(R.id.tv_title);
    tvFilePath = rootView.findViewById(R.id.tv_desc);
    rbQualityLow = rootView.findViewById(R.id.rb_quality_low);
    rbQualityStandard = rootView.findViewById(R.id.rb_quality_standard);
    rbQualityHigh = rootView.findViewById(R.id.rb_quality_high);
    rbQualityCustom = rootView.findViewById(R.id.rb_quality_custom);
    etCustomQuality = rootView.findViewById(R.id.et_custom_quality);
    savePathView = rootView.findViewById(R.id.view_save_path);
    btnCancel = rootView.findViewById(R.id.btn_cancel);
    btnCompress = rootView.findViewById(R.id.btn_compress);
    tvSavePath = rootView.findViewById(R.id.tv_save_path);
    tvSwap.setOnClickListener(this);
    btnCompress.setOnClickListener(this);
    btnCancel.setOnClickListener(this);
    savePathView.setOnClickListener(this);
    toolBar.setBackBtnClickListener(v -> {
      CViewUtils.hideKeyboard(etCustomQuality);
      dismiss();
    });
    rbQualityLow.setOnCheckedChangeListener(this);
    rbQualityStandard.setOnCheckedChangeListener(this);
    rbQualityHigh.setOnCheckedChangeListener(this);
    rbQualityCustom.setOnCheckedChangeListener(this);
    radioButtons.addAll(
        Arrays.asList(rbQualityLow, rbQualityStandard, rbQualityHigh, rbQualityCustom));
    etCustomQuality.setFilters(new InputFilter[]{EditTextUtils.inputRangeFilter(1, 120)});
  }

  @Override
  protected void onViewCreate() {
    setDocumentInfo();
    setSavePath(savePath);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.tv_swap) {
      selectDocumentLauncher.launch(null);
    } else if (v.getId() == R.id.btn_cancel) {
      dismiss();
    } else if (v.getId() == R.id.btn_compress) {
      if (Build.VERSION.SDK_INT < CPermissionUtil.VERSION_R) {
        multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
          if (CPermissionUtil.hasStoragePermissions(getContext())) {
            startCompress();
          } else {
            if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
              CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
            }
          }
        });
      }else {
        startCompress();
      }

    } else if (v.getId() == R.id.view_save_path) {

      if (Build.VERSION.SDK_INT < CPermissionUtil.VERSION_R) {
        multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
          if (CPermissionUtil.hasStoragePermissions(getContext())) {
            showSelectDirDialog();
          } else {
            if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
              CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
            }
          }
        });
      }else {
        showSelectDirDialog();
      }

    }
  }

  private void startCompress(){
    CThreadPoolUtils.getInstance().executeIO(() -> {
      compressPDF((result, path) -> {
        CThreadPoolUtils.getInstance().executeMain(() -> {
          if (compressDocumentListener != null) {
            compressDocumentListener.compress(result, path);
          }
        });
      });
    });
  }

  private void showSelectDirDialog(){
    String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    CFileDirectoryDialog dialog = CFileDirectoryDialog.newInstance(rootDir,
        getContext().getString(R.string.tools_select_folder),
        getContext().getString(R.string.tools_save_to_this_directory));
    dialog.setSelectFolderListener(this::setSavePath);
    dialog.show(getChildFragmentManager(), "fileDirectoryDialog");
  }
  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      updateRadioButtonStatus(buttonView);
      if (buttonView.getId() == R.id.rb_quality_custom) {
        etCustomQuality.setEnabled(true);
        etCustomQuality.setFocusableInTouchMode(true);
        etCustomQuality.requestFocus();
        CViewUtils.showKeyboard(etCustomQuality);
      } else {
        etCustomQuality.setFocusableInTouchMode(false);
        etCustomQuality.setEnabled(false);
        etCustomQuality.setText("");
      }
    }
  }

  private void updateRadioButtonStatus(CompoundButton checkButton) {
    checkButton.setChecked(true);
    for (AppCompatRadioButton radioButton : radioButtons) {
      if (radioButton.getId() != checkButton.getId()) {
        radioButton.setChecked(false);
      }
    }
  }

  private void setDocumentInfo() {
    if (document == null) {
      return;
    }
    tvFileName.setText(document.getFileName());
    String filePath = document.getAbsolutePath();
    if (TextUtils.isEmpty(filePath)) {
      if (document.getUri() != null) {
        filePath = CUriUtil.getUriData(getContext(), document.getUri());
      }
    }
    tvFilePath.setText(filePath);
  }

  private void setSavePath(String path) {
    this.savePath = path;
    tvSavePath.setText(savePath);
  }

  private void compressPDF(COnCompressDocumentListener listener) {
    String newSavePath = CFileUtils.renameNameSuffix(
            new File(savePath + File.separator + document.getFileName()))
        .getAbsolutePath();

    if (rbQualityCustom.isChecked()) {
      if (TextUtils.isEmpty(etCustomQuality.getText())) {
        CToastUtil.showLongToast(getContext(), R.string.tools_please_enter_percentage);
        listener.compress(false, null);
        return;
      }
      int quality = Integer.parseInt(etCustomQuality.getText().toString());
      loadingDialog = CPDFCompressLoadingDialog.newInstance(
          document.getPageCount());
      loadingDialog.show(getParentFragmentManager(), "compressLoadingDialog");
      try {
        CLog.e("ComPDFKit", "Compress:quality:" + quality);
        boolean result = document.saveAsCompressOptimize(newSavePath, quality, pageIndex -> {
            if (loadingDialog != null) {
              loadingDialog.setCompressProgress(pageIndex);
            }
        });
        loadingDialog.dismiss();
        listener.compress(result, newSavePath);
      } catch (Exception e) {
        loadingDialog.dismiss();
        listener.compress(false, null);
      }
    } else {
      CPDFDocument.PDFDocumentCompressLevel level;
      if (rbQualityLow.isChecked()) {
        level = CPDFDocument.PDFDocumentCompressLevel.MICRO;
      } else if (rbQualityStandard.isChecked()) {
        level = CPDFDocument.PDFDocumentCompressLevel.STANDARD;
      } else if (rbQualityHigh.isChecked()) {
        level = CPDFDocument.PDFDocumentCompressLevel.HIGH;
      } else {
        level = CPDFDocument.PDFDocumentCompressLevel.STANDARD;
      }
      CLog.e("ComPDFKit", "Compress:level:" + level.name());
      loadingDialog = CPDFCompressLoadingDialog.newInstance(
          document.getPageCount());
      loadingDialog.show(getParentFragmentManager(), "compressLoadingDialog");
      try {

        boolean result = document.saveAsCompressOptimize(newSavePath, level,
            pageIndex -> {
                if (loadingDialog != null) {
                  loadingDialog.setCompressProgress(pageIndex);
                }
            });
        loadingDialog.dismiss();
        listener.compress(result, newSavePath);
      } catch (Exception e) {
        loadingDialog.dismiss();
        listener.compress(false, null);
      }
    }
  }


  private void verifyDocument(Uri uri,
      CVerifyPasswordDialogFragment.CVerifyCompleteListener listener) {
    CPDFDocument document = new CPDFDocument(getContext());
    CPDFDocument.PDFDocumentError error = document.open(uri);
    if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
      if (listener != null) {
        listener.complete(document);
      }
    } else if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorPassword) {
      showVerifyPasswordDialog(document, uri, listener);
    }
  }

  private void showVerifyPasswordDialog(CPDFDocument document, Uri uri,
      CVerifyPasswordDialogFragment.CVerifyCompleteListener documentListener) {
    CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
    verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(document, "", uri);
    verifyPasswordDialogFragment.setVerifyCompleteListener(document1 -> {
      if (documentListener != null) {
        documentListener.complete(document1);
      }
    });
    verifyPasswordDialogFragment.show(getChildFragmentManager(), "verifyPasswordDialog");
  }

  @Override
  public void onDestroy() {
    loadingDialog = null;
    if (document != null) {
      document.close();
      document = null;
    }
    compressDocumentListener = null;
    super.onDestroy();
  }

  public interface COnCompressDocumentListener {

    void compress(boolean result, String path);
  }

  public void setCompressDocumentListener(COnCompressDocumentListener compressDocumentListener) {
    this.compressDocumentListener = compressDocumentListener;
  }
}
