/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */

package com.compdfkit.tools.compress;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.document.CPDFDocument.CPDFOptimizeOptions;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

/**
 * Custom compression options dialog for configuring CPDFOptimizeOptions
 */
public class CPDFCustomCompressOptionsDialog extends CBasicBottomSheetDialogFragment implements View.OnClickListener {

  @Override
  public void onStart() {
    super.onStart();
    if (getDialog() != null) {
      View bottomSheet = getDialog().findViewById(
          com.google.android.material.R.id.design_bottom_sheet);
      if (bottomSheet != null) {
        bottomSheet.setBackground(null);
      }
      if (getDialog().getWindow() != null) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      }
    }
  }

  private AppCompatSpinner spinnerImageQuality;
  private AppCompatEditText etImageExceedsPpi;
  private AppCompatEditText etDownsampleToPpi;

  // Font Settings
  private CompoundButton switchRemoveFontEmbedding;

  // Discard Objects switches
  private CompoundButton switchDiscardFormActions;
  private CompoundButton switchDiscardJavascript;
  private CompoundButton switchDiscardPageThumbnails;
  private CompoundButton switchDiscardDocumentTags;
  private CompoundButton switchDiscardBookmarks;
  private CompoundButton switchDiscardUserData;
  private CompoundButton switchDiscardCommentsForms;
  private CompoundButton switchDiscardDocumentInfo;
  private CompoundButton switchDiscardObjectData;
  private CompoundButton switchDiscardFileAttachments;
  private CompoundButton switchDiscardLayerContent;
  private CompoundButton switchDiscardOtherData;
  private CompoundButton switchDiscardInvalidBookmarks;
  private CompoundButton switchDiscardInvalidLinks;

  private AppCompatTextView btnSave;
  private AppCompatTextView btnCancel;

  private CPDFOptimizeOptions optimizeOptions;
  private COnOptionsConfiguredListener listener;

  public static CPDFCustomCompressOptionsDialog newInstance(CPDFOptimizeOptions options) {
    CPDFCustomCompressOptionsDialog dialog = new CPDFCustomCompressOptionsDialog();
    dialog.optimizeOptions = new CPDFOptimizeOptions(options);
    return dialog;
  }

  @Override
  protected float dimAmount() {
    return CViewUtils.isLandScape(getContext()) ? 0.2F : 0.7F;
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
  protected int themeResId() {
    return R.attr.compdfkit_BottomSheetDialog_Transparent_Theme;
  }

  @Override
  protected int layoutId() {
    return R.layout.tools_custom_compress_options_dialog;
  }

  @Override
  protected void onCreateView(View rootView) {
    View closeButton = rootView.findViewById(R.id.btn_close);
    closeButton.setOnClickListener(v -> dismiss());

    spinnerImageQuality = rootView.findViewById(R.id.spinner_image_quality);
    etImageExceedsPpi = rootView.findViewById(R.id.et_image_exceeds_ppi);
    etDownsampleToPpi = rootView.findViewById(R.id.et_downsample_to_ppi);

    switchRemoveFontEmbedding = rootView.findViewById(R.id.switch_remove_font_embedding);

    switchDiscardFormActions = rootView.findViewById(R.id.switch_discard_form_actions);
    switchDiscardJavascript = rootView.findViewById(R.id.switch_discard_javascript);
    switchDiscardPageThumbnails = rootView.findViewById(R.id.switch_discard_page_thumbnails);
    switchDiscardDocumentTags = rootView.findViewById(R.id.switch_discard_document_tags);
    switchDiscardBookmarks = rootView.findViewById(R.id.switch_discard_bookmarks);
    switchDiscardUserData = rootView.findViewById(R.id.switch_discard_user_data);
    switchDiscardCommentsForms = rootView.findViewById(R.id.switch_discard_comments_forms);
    switchDiscardDocumentInfo = rootView.findViewById(R.id.switch_discard_document_info);
    switchDiscardObjectData = rootView.findViewById(R.id.switch_discard_object_data);
    switchDiscardFileAttachments = rootView.findViewById(R.id.switch_discard_file_attachments);
    switchDiscardLayerContent = rootView.findViewById(R.id.switch_discard_layer_content);
    switchDiscardOtherData = rootView.findViewById(R.id.switch_discard_other_data);
    switchDiscardInvalidBookmarks = rootView.findViewById(R.id.switch_discard_invalid_bookmarks);
    switchDiscardInvalidLinks = rootView.findViewById(R.id.switch_discard_invalid_links);

    btnSave = rootView.findViewById(R.id.btn_save);
    btnCancel = rootView.findViewById(R.id.btn_cancel);

    btnSave.setOnClickListener(this);
    btnCancel.setOnClickListener(this);

    if (CViewUtils.isLandScape(getContext())) {
      View actions = rootView.findViewById(R.id.compress_actions);
      android.view.ViewGroup.LayoutParams actionsParams = actions.getLayoutParams();
      actionsParams.height = getResources().getDimensionPixelSize(
          R.dimen.tools_compress_action_container_land_height);
      actions.setLayoutParams(actionsParams);
      configureLandscapeButtons();
    }

    setupCheckboxRow(switchRemoveFontEmbedding);
    setupCheckboxRow(switchDiscardFormActions);
    setupCheckboxRow(switchDiscardJavascript);
    setupCheckboxRow(switchDiscardPageThumbnails);
    setupCheckboxRow(switchDiscardDocumentTags);
    setupCheckboxRow(switchDiscardBookmarks);
    setupCheckboxRow(switchDiscardCommentsForms);
    setupCheckboxRow(switchDiscardDocumentInfo);
    setupCheckboxRow(switchDiscardObjectData);
    setupCheckboxRow(switchDiscardFileAttachments);
    setupCheckboxRow(switchDiscardLayerContent);
    setupCheckboxRow(switchDiscardInvalidBookmarks);
    setupCheckboxRow(switchDiscardInvalidLinks);
  }

  private void configureLandscapeButtons() {
    ConstraintLayout.LayoutParams saveParams =
        (ConstraintLayout.LayoutParams) btnSave.getLayoutParams();
    saveParams.width = 0;
    saveParams.height = getResources().getDimensionPixelSize(R.dimen.tools_compress_action_height);
    clearHorizontalConstraints(saveParams);
    saveParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    saveParams.endToStart = R.id.btn_cancel;
    saveParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    saveParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.tools_compress_action_bottom);
    saveParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.tools_compress_action_outer_margin));
    saveParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.tools_compress_action_gap));
    btnSave.setLayoutParams(saveParams);

    ConstraintLayout.LayoutParams cancelParams =
        (ConstraintLayout.LayoutParams) btnCancel.getLayoutParams();
    cancelParams.width = 0;
    cancelParams.height = getResources().getDimensionPixelSize(R.dimen.tools_compress_action_height);
    clearHorizontalConstraints(cancelParams);
    cancelParams.startToEnd = R.id.btn_save;
    cancelParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    cancelParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    cancelParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.tools_compress_action_bottom);
    cancelParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.tools_compress_action_gap));
    cancelParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.tools_compress_action_outer_margin));
    btnCancel.setLayoutParams(cancelParams);

    View scrollView = getView();
    if (scrollView != null) {
      scrollView.requestLayout();
    }
  }

  private void clearHorizontalConstraints(ConstraintLayout.LayoutParams params) {
    params.startToStart = ConstraintLayout.LayoutParams.UNSET;
    params.startToEnd = ConstraintLayout.LayoutParams.UNSET;
    params.endToStart = ConstraintLayout.LayoutParams.UNSET;
    params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
    params.leftToLeft = ConstraintLayout.LayoutParams.UNSET;
    params.leftToRight = ConstraintLayout.LayoutParams.UNSET;
    params.rightToLeft = ConstraintLayout.LayoutParams.UNSET;
    params.rightToRight = ConstraintLayout.LayoutParams.UNSET;
  }

  private void setupCheckboxRow(CompoundButton checkbox) {
    View parent = (View) checkbox.getParent();
    parent.setOnClickListener(v -> {
      if (checkbox.isEnabled()) {
        checkbox.toggle();
      }
    });
  }

  @Override
  protected void onViewCreate() {
    setupImageQualitySpinner();
    loadCurrentOptions();
  }

  private void setupImageQualitySpinner() {
    String[] qualities = new String[]{
        getString(R.string.tools_image_quality_low),
        getString(R.string.tools_image_quality_medium),
        getString(R.string.tools_image_quality_high)
    };
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        requireContext(),
        R.layout.tools_custom_compress_spinner_item,
        qualities
    );
    adapter.setDropDownViewResource(R.layout.tools_custom_compress_spinner_item);
    spinnerImageQuality.setAdapter(adapter);
  }

  private void loadCurrentOptions() {
    if (optimizeOptions == null) {
      return;
    }

    // Load image quality (map 0-100 to Low/Medium/High)
    float quality = optimizeOptions.imageQuality;
    if (quality <= 33) {
      spinnerImageQuality.setSelection(0); // Low
    } else if (quality <= 66) {
      spinnerImageQuality.setSelection(1); // Medium
    } else {
      spinnerImageQuality.setSelection(2); // High
    }

    // Load PPI values
    if (optimizeOptions.colorImageOptions != null) {
      etImageExceedsPpi.setText(String.valueOf(optimizeOptions.colorImageOptions.upperPpi));
      etDownsampleToPpi.setText(String.valueOf(optimizeOptions.colorImageOptions.targetPpi));
    }

    // Load font settings
    switchRemoveFontEmbedding.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_EMBEDDED_FONTS) != 0
    );

    // Load discard object flags
    switchDiscardFormActions.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_FORM_SUBMIT_IMPORT_RESET_ACTIONS) != 0
    );
    switchDiscardJavascript.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_JAVASCRIPT_ACTIONS) != 0
    );
    switchDiscardPageThumbnails.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_PAGE_THUMBNAILS) != 0
    );
    switchDiscardDocumentTags.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_METADATA) != 0
    );
    switchDiscardBookmarks.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_BOOKMARKS) != 0
    );
    switchDiscardUserData.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_DOCUMENT_INFO) != 0
    );
    switchDiscardCommentsForms.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_ANNOTATIONS) != 0
        || (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_FORMS) != 0
        || (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_MULTIMEDIA) != 0
    );
    switchDiscardDocumentInfo.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_DOCUMENT_INFO) != 0
        && (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_METADATA) != 0
    );
    switchDiscardObjectData.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_OBJECT_DATA) != 0
    );
    switchDiscardFileAttachments.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_FILE_ATTACHMENTS) != 0
    );
    switchDiscardLayerContent.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_HIDDEN_LAYERS) != 0
        || (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.MERGE_VISIBLE_LAYERS) != 0
    );
    switchDiscardOtherData.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_OTHER_APP_DATA) != 0
    );
    switchDiscardInvalidBookmarks.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_INVALID_BOOKMARKS) != 0
    );
    switchDiscardInvalidLinks.setChecked(
        (optimizeOptions.optimizeFlags & CPDFOptimizeOptions.REMOVE_INVALID_LINKS) != 0
    );
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_save) {
      saveOptions();
    } else if (v.getId() == R.id.btn_cancel) {
      dismiss();
    }
  }

  private void saveOptions() {
    if (optimizeOptions == null) {
      optimizeOptions = new CPDFOptimizeOptions();
    }

    // Save image quality
    int qualityIndex = spinnerImageQuality.getSelectedItemPosition();
    float quality;
    switch (qualityIndex) {
      case 0: // Low
        quality = 25.0f;
        break;
      case 1: // Medium
        quality = 50.0f;
        break;
      case 2: // High
        quality = 75.0f;
        break;
      default:
        quality = 40.0f;
    }
    optimizeOptions.imageQuality = quality;

    // Save PPI values with validation
    String exceedsPpiStr = etImageExceedsPpi.getText().toString();
    String downsamplePpiStr = etDownsampleToPpi.getText().toString();

    if (!TextUtils.isEmpty(exceedsPpiStr) && !TextUtils.isEmpty(downsamplePpiStr)) {
      try {
        int exceedsPpi = Integer.parseInt(exceedsPpiStr);
        int downsamplePpi = Integer.parseInt(downsamplePpiStr);

        // Validate that exceedsPpi must be greater than downsamplePpi
        if (exceedsPpi <= downsamplePpi) {
          CToastUtil.showLongToast(getContext(), R.string.tools_error_exceeds_ppi_must_be_greater);
          return; // Don't dismiss dialog, let user correct the input
        }

        if (optimizeOptions.colorImageOptions != null) {
          optimizeOptions.colorImageOptions.upperPpi = exceedsPpi;
          optimizeOptions.colorImageOptions.targetPpi = downsamplePpi;
        }
        if (optimizeOptions.grayscaleImageOptions != null) {
          optimizeOptions.grayscaleImageOptions.upperPpi = exceedsPpi;
          optimizeOptions.grayscaleImageOptions.targetPpi = downsamplePpi;
        }
        if (optimizeOptions.monochromeImageOptions != null) {
          // Monochrome images use 2x PPI - industry standard for 1-bit color depth to maintain clarity
          optimizeOptions.monochromeImageOptions.upperPpi = exceedsPpi * 2;
          optimizeOptions.monochromeImageOptions.targetPpi = downsamplePpi * 2;
        }
      } catch (NumberFormatException e) {
        // Show error message to user instead of silent failure
        CToastUtil.showLongToast(getContext(), R.string.tools_error_invalid_ppi_value);
        return; // Don't dismiss dialog, let user correct the input
      }
    }

    // Build optimize flags based on switch states
    int flags = CPDFOptimizeOptions.NONE;

    // Always enable image compression
    flags |= CPDFOptimizeOptions.COMPRESS_IMAGES;

    // Font settings
    if (switchRemoveFontEmbedding.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_EMBEDDED_FONTS;
    }

    // Discard objects
    if (switchDiscardFormActions.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_FORM_SUBMIT_IMPORT_RESET_ACTIONS;
    }
    if (switchDiscardJavascript.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_JAVASCRIPT_ACTIONS;
    }
    if (switchDiscardPageThumbnails.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_PAGE_THUMBNAILS;
    }
    if (switchDiscardDocumentTags.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_METADATA;
    }
    if (switchDiscardBookmarks.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_BOOKMARKS;
    }
    if (switchDiscardUserData.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_DOCUMENT_INFO;
    }
    if (switchDiscardCommentsForms.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_ANNOTATIONS;
      flags |= CPDFOptimizeOptions.REMOVE_FORMS;
      flags |= CPDFOptimizeOptions.REMOVE_MULTIMEDIA;
    }
    if (switchDiscardDocumentInfo.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_DOCUMENT_INFO;
      flags |= CPDFOptimizeOptions.REMOVE_METADATA;
    }
    if (switchDiscardObjectData.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_OBJECT_DATA;
    }
    if (switchDiscardFileAttachments.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_FILE_ATTACHMENTS;
    }
    if (switchDiscardLayerContent.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_HIDDEN_LAYERS;
      flags |= CPDFOptimizeOptions.MERGE_VISIBLE_LAYERS;
    }
    if (switchDiscardOtherData.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_OTHER_APP_DATA;
    }
    if (switchDiscardInvalidBookmarks.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_INVALID_BOOKMARKS;
    }
    if (switchDiscardInvalidLinks.isChecked()) {
      flags |= CPDFOptimizeOptions.REMOVE_INVALID_LINKS;
    }

    optimizeOptions.optimizeFlags = flags;

    // Notify listener and dismiss
    if (listener != null) {
      listener.onOptionsConfigured(optimizeOptions);
    }
    dismiss();
  }

  public void setOnOptionsConfiguredListener(COnOptionsConfiguredListener listener) {
    this.listener = listener;
  }

  public interface COnOptionsConfiguredListener {
    void onOptionsConfigured(CPDFOptimizeOptions options);
  }
}
