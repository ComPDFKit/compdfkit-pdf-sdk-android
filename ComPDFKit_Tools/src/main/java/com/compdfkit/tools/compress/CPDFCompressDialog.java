/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */

package com.compdfkit.tools.compress;

import android.Manifest;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFDocument.CPDFOptimizeOptions;
import com.compdfkit.core.document.CPDFDocument.CPDFOptimizeResult;
import com.compdfkit.core.document.CPDFDocument.OptimizeListener;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.activitycontracts.CSelectPDFDocumentResultContract;
import com.compdfkit.tools.common.utils.storage.CPDFPublicFileSaver;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;

import java.util.Arrays;
import java.util.List;


public class CPDFCompressDialog extends CBasicBottomSheetDialogFragment implements
    View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

  private static final int LOW_UPPER_PPI = 120;
  private static final int LOW_TARGET_PPI = 96;
  private static final int LOW_QUALITY = 30;
  private static final int STANDARD_UPPER_PPI = 180;
  private static final int STANDARD_TARGET_PPI = 150;
  private static final int STANDARD_QUALITY = 45;
  private static final int HIGH_UPPER_PPI = 220;
  private static final int HIGH_TARGET_PPI = 150;
  private static final int HIGH_QUALITY = 75;

  private static final String FILE_DIRECTORY_DIALOG_TAG = "fileDirectoryDialog";
  private static final String CUSTOM_OPTIONS_DIALOG_TAG = "customCompressOptionsDialog";
  private static final String LOADING_DIALOG_TAG = "compressLoadingDialog";

  private AppCompatTextView tvFileName;

  private AppCompatTextView tvFilePath;

  private CToolBar toolBar;

  private AppCompatTextView tvSwap;

  private AppCompatRadioButton rbQualityLow;

  private AppCompatRadioButton rbQualityStandard;

  private AppCompatRadioButton rbQualityHigh;

  private AppCompatRadioButton rbQualityCustom;

  private View savePathView;

  private AppCompatButton btnCancel;

  private AppCompatButton btnCompress;

  private AppCompatTextView tvSavePath;

  private AppCompatTextView tvCustomCompressOptions;

  private CPDFOptimizeOptions optimizeOptions = new CPDFOptimizeOptions();

  private CPDFDocument document;

  private volatile boolean isCancellationRequested = false;

  private List<AppCompatRadioButton> radioButtons;
  protected CMultiplePermissionResultLauncher multiplePermissionResultLauncher =
      new CMultiplePermissionResultLauncher(this);

  private String savePath = CPDFStorageManager.getDefaultDirectoryDialogPath();

  private COnCompressDocumentListener compressDocumentListener;

  private ActivityResultLauncher<Void> selectDocumentLauncher = registerForActivityResult(
      new CSelectPDFDocumentResultContract(), uri -> {
        if (uri != null) {
          verifyDocument(uri, document -> {
            replaceDocument(document);
            setDocumentInfo();
          });
        }
      });
  private volatile CPDFCompressLoadingDialog loadingDialog;

  public void setDocument(CPDFDocument document) {
    replaceDocument(document);
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
    bindViews(rootView);
    bindListeners();
    updateCustomOptionsButtonState();
  }

  private void bindViews(View rootView) {
    toolBar = rootView.findViewById(R.id.tool_bar);
    tvSwap = rootView.findViewById(R.id.tv_swap);
    tvFileName = rootView.findViewById(R.id.tv_title);
    tvFilePath = rootView.findViewById(R.id.tv_desc);
    rbQualityLow = rootView.findViewById(R.id.rb_quality_low);
    rbQualityStandard = rootView.findViewById(R.id.rb_quality_standard);
    rbQualityHigh = rootView.findViewById(R.id.rb_quality_high);
    rbQualityCustom = rootView.findViewById(R.id.rb_quality_custom);
    savePathView = rootView.findViewById(R.id.view_save_path);
    btnCancel = rootView.findViewById(R.id.btn_cancel);
    btnCompress = rootView.findViewById(R.id.btn_compress);
    tvSavePath = rootView.findViewById(R.id.tv_save_path);
    tvCustomCompressOptions = rootView.findViewById(R.id.tv_custom_compress_options);
    radioButtons = Arrays.asList(
        rbQualityLow, rbQualityStandard, rbQualityHigh, rbQualityCustom);
  }

  private void bindListeners() {
    tvSwap.setOnClickListener(this);
    btnCompress.setOnClickListener(this);
    btnCancel.setOnClickListener(this);
    savePathView.setOnClickListener(this);
    tvCustomCompressOptions.setOnClickListener(this);
    toolBar.setBackBtnClickListener(v -> {
      dismiss();
    });
    rbQualityLow.setOnCheckedChangeListener(this);
    rbQualityStandard.setOnCheckedChangeListener(this);
    rbQualityHigh.setOnCheckedChangeListener(this);
    rbQualityCustom.setOnCheckedChangeListener(this);
  }

  @Override
  protected void onViewCreate() {
    setDocumentInfo();
    setSavePath(savePath);
  }

  @Override
  public void onClick(View v) {
    int viewId = v.getId();
    if (viewId == R.id.tv_swap) {
      selectDocumentLauncher.launch(null);
    } else if (viewId == R.id.btn_cancel) {
      dismiss();
    } else if (viewId == R.id.btn_compress) {
      requestStoragePermissionIfNeeded();
    } else if (viewId == R.id.view_save_path) {
      showSelectDirDialog();
    } else if (viewId == R.id.tv_custom_compress_options && rbQualityCustom.isChecked()) {
      showCustomCompressOptionsDialog();
    }
  }

  private void requestStoragePermissionIfNeeded() {
    if (!CPDFStorageManager.shouldRequestLegacyWritePermission()) {
      startCompress();
      return;
    }

    multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
      if (CPermissionUtil.hasStoragePermissions(getContext())) {
        startCompress();
      } else if (!CPermissionUtil.shouldShowRequestPermissionRationale(
          requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
      }
    });
  }

  private void startCompress() {
    showLoadingDialog();
    CThreadPoolUtils.getInstance().executeIO(() -> {
      compressPDF((result, path, uri) -> {
        CThreadPoolUtils.getInstance().executeMain(() -> {
          dismissLoadingDialog();
          if (compressDocumentListener != null) {
            compressDocumentListener.compress(result, path, uri);
          }
        });
      });
    });
  }

  private void showSelectDirDialog() {
    String rootDir = CPDFStorageManager.getDefaultDirectoryDialogPath();
    CFileDirectoryDialog dialog = CFileDirectoryDialog.newInstance(rootDir,
        getContext().getString(R.string.tools_select_folder),
        getContext().getString(R.string.tools_save_to_this_directory));
    dialog.setSelectFolderListener(this::setSavePath);
    dialog.show(getChildFragmentManager(), FILE_DIRECTORY_DIALOG_TAG);
  }

  private void showCustomCompressOptionsDialog() {
    CPDFCustomCompressOptionsDialog dialog = CPDFCustomCompressOptionsDialog.newInstance(optimizeOptions);
    dialog.setOnOptionsConfiguredListener(options -> {
      optimizeOptions = options;
    });
    dialog.show(getChildFragmentManager(), CUSTOM_OPTIONS_DIALOG_TAG);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      updateRadioButtonStatus(buttonView);
      updateCustomOptionsButtonState();
    }
  }

  private void updateCustomOptionsButtonState() {
    boolean isCustomChecked = rbQualityCustom.isChecked();
    tvCustomCompressOptions.setEnabled(isCustomChecked);
    tvCustomCompressOptions.setAlpha(isCustomChecked ? 1.0f : 0.5f);
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

  private void showLoadingDialog() {
    loadingDialog = CPDFCompressLoadingDialog.newInstance(document.getPageCount());
    loadingDialog.show(getParentFragmentManager(), LOADING_DIALOG_TAG);
  }

  private void dismissLoadingDialog() {
    if (loadingDialog != null) {
      loadingDialog.dismiss();
      loadingDialog = null;
    }
  }

  private void compressPDF(COnCompressDocumentListener listener) {
    CPDFOptimizeOptions options = getOptimizeOptions();
    String fileName = document.getFileName();
    CPDFPublicFileSaver.SaveResult saveResult = CPDFPublicFileSaver.savePdfToSelectedDirectory(
        getContext(), savePath, fileName, false,
        tempPath -> optimizeDocument(tempPath, options));
    listener.compress(saveResult.isSuccess(), saveResult.getOpenPath(), saveResult.getPublicUri());
  }

  private boolean optimizeDocument(String tempPath, CPDFOptimizeOptions options) {
    try {
      CPDFOptimizeResult result = document.optimizeSaveAsCopy(
          tempPath, options, new OptimizeListener() {
            @Override
            public boolean isCancelled() {
              return isCancellationRequested;
            }

            @Override
            public void onUpdate(int pageIndex) {
              if (loadingDialog != null) {
                loadingDialog.setCompressProgress(pageIndex);
              }
            }
          });
      return result.isSuccess();
    } catch (CPDFDocumentException e) {
      CLog.e("ComPDFKit", "Compress failed: " + e.getMessage());
      return false;
    }
  }

  private CPDFOptimizeOptions getOptimizeOptions() {
    if (rbQualityCustom.isChecked()) {
      return optimizeOptions;
    }

    CPDFOptimizeOptions options = new CPDFOptimizeOptions();
    options.monochromeImageOptions = getImageOptimizeOptions();
    return options;
  }

  private CPDFDocument.CPDFImageOptimizeOptions getImageOptimizeOptions() {
    CPDFDocument.CPDFImageOptimizeOptions imageOptimizeOptions = new CPDFDocument.CPDFImageOptimizeOptions();
    if (rbQualityLow.isChecked()) {
      imageOptimizeOptions.upperPpi = LOW_UPPER_PPI;
      imageOptimizeOptions.targetPpi = LOW_TARGET_PPI;
      imageOptimizeOptions.quality = LOW_QUALITY;
    } else if (rbQualityStandard.isChecked()) {
      imageOptimizeOptions.upperPpi = STANDARD_UPPER_PPI;
      imageOptimizeOptions.targetPpi = STANDARD_TARGET_PPI;
      imageOptimizeOptions.quality = STANDARD_QUALITY;
    } else if (rbQualityHigh.isChecked()) {
      imageOptimizeOptions.upperPpi = HIGH_UPPER_PPI;
      imageOptimizeOptions.targetPpi = HIGH_TARGET_PPI;
      imageOptimizeOptions.quality = HIGH_QUALITY;
    }
    return imageOptimizeOptions;
  }

  private void replaceDocument(CPDFDocument newDocument) {
    if (document != null && document != newDocument) {
      document.close();
    }
    document = newDocument;
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
    } else {
      document.close();
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
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    isCancellationRequested = true;
    dismissLoadingDialog();
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

    void compress(boolean result, String path, Uri uri);
  }

  public void setCompressDocumentListener(COnCompressDocumentListener compressDocumentListener) {
    this.compressDocumentListener = compressDocumentListener;
  }
}
