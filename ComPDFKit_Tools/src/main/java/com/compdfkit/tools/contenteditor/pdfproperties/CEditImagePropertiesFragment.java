/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.contenteditor.pdfproperties;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.preview.CStylePreviewView;


public class CEditImagePropertiesFragment extends CBasicPropertiesFragment implements View.OnClickListener {
    private CStylePreviewView stylePreviewView;
    private ConstraintLayout rlLeftRotate;
    private ConstraintLayout rlRightRotate;
    private AppCompatImageView ivHorizontalMirror;
    private AppCompatImageView ivVerticalMirror;

    private CSliderBar opacitySliderBar;
    private ConstraintLayout rlReplece;
    private ConstraintLayout rlExport;
    private ConstraintLayout rlCrop;

    private ActivityResultLauncher<CImageResultContracts.RequestType> imageLauncher = registerForActivityResult(new CImageResultContracts(), result -> {
        if (result != null) {
            if (viewModel != null) {
                viewModel.getStyle().setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.ReplaceImage);
                viewModel.getStyle().setImageUri(result);
                dismissStyleDialog();
            }
        }
    });

    @Override
    protected int layoutId() {
        return R.layout.tools_edit_image_property_dialog;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onCreateView(View mContentView) {
        stylePreviewView = mContentView.findViewById(R.id.style_preview);
        rlLeftRotate = mContentView.findViewById(R.id.rl_left_rotate);
        rlRightRotate = mContentView.findViewById(R.id.rl_right_rotate);
        ivHorizontalMirror = mContentView.findViewById(R.id.iv_horizontal_mirror);
        ivVerticalMirror = mContentView.findViewById(R.id.iv_verticle_mirror);
        opacitySliderBar = mContentView.findViewById(R.id.opacity_slider_bar);
        rlReplece = mContentView.findViewById(R.id.rl_replace);
        rlExport = mContentView.findViewById(R.id.rl_export);
        rlCrop = mContentView.findViewById(R.id.rl_crop);
        ivHorizontalMirror.setSelected(false);
        ivVerticalMirror.setSelected(false);
        rlLeftRotate.setOnClickListener(this);
        rlRightRotate.setOnClickListener(this);
        ivHorizontalMirror.setOnClickListener(this);
        ivVerticalMirror.setOnClickListener(this);
        rlReplece.setOnClickListener(this);
        rlCrop.setOnClickListener(this);
        rlExport.setOnClickListener(this);
        stylePreviewView.setAnnotType(CStyleType.EDIT_IMAGE);
        stylePreviewView.setImage(viewModel.getStyle().getEditImageBitmap());
        opacitySliderBar.setProgress(viewModel.getStyle().getOpacity());
        opacitySliderBar.setChangeListener((progress, percentageValue, isStopTouch) -> {
            if (isStopTouch) {
                if (viewModel != null) {
                    CAnnotStyle style = viewModel.getStyle();
                    style.setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
                    style.setOpacity(progress);
                }
            }
            stylePreviewView.setColorOpacity(progress);
        });
        viewModel.addStyleChangeListener(this);
    }

    @Override
    public void onChangeOpacity(int opacity) {
        super.onChangeOpacity(opacity);
        stylePreviewView.setColorOpacity(opacity);
    }

    @Override
    public void onChangeRotation(float rotation) {
        if (stylePreviewView != null) {
            stylePreviewView.setRotationAngle(rotation);
        }
    }

    @Override
    public void onChangeMirror(CAnnotStyle.Mirror mirror) {
        if (stylePreviewView != null) {
            stylePreviewView.setMirror(mirror);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_left_rotate) {
            if (viewModel != null) {
                viewModel.getStyle().setRotationAngle(90);
            }
        } else if (id == R.id.rl_right_rotate) {
            if (viewModel != null) {
                viewModel.getStyle().setRotationAngle(-90);
            }
        } else if (id == R.id.iv_horizontal_mirror) {
            if (viewModel != null) {
                viewModel.getStyle().setMirror(CAnnotStyle.Mirror.Horizontal);
            }
        } else if (id == R.id.iv_verticle_mirror) {
            if (viewModel != null) {
                viewModel.getStyle().setMirror(CAnnotStyle.Mirror.Vertical);
            }
        } else if (id == R.id.rl_replace) {
            imageLauncher.launch(CImageResultContracts.RequestType.PHOTO_ALBUM);
        } else if (id == R.id.rl_crop) {
            if (viewModel != null) {
                viewModel.getStyle().setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.Crop, true);
            }
            dismissStyleDialog();
        } else if (id == R.id.rl_export) {
            if (viewModel != null) {
                viewModel.getStyle().setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.Export, true);
                Toast.makeText(getContext(), getString(R.string.tools_export_success), Toast.LENGTH_SHORT).show();
            }
            dismissStyleDialog();
        }
    }
}