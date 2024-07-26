/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.preview;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.activity.ComponentDialog;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.signature.CPDFDigitalSigConfig;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfsignature.data.CSignatureDatas;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.signature.pdfproperties.pdfsign.CDigitalSignStylePreviewView;
import com.compdfkit.tools.signature.preview.view.CSignStylePositionView;
import com.compdfkit.tools.signature.preview.view.CSignStyleReasonView;


public class CDigitalSignStylePreviewDialog extends CBasicBottomSheetDialogFragment implements View.OnClickListener {

    public static final String EXTRA_SIGN_IMAGE_PATH = "extra_sign_image_path";
    public static final String EXTRA_COMMON_NAME = "extra_common_name";
    public static final String EXTRA_DIGTINGUISHABLE_NAME = "extra_dn";

    private CDigitalSignStylePreviewView previewView;

    private ConstraintLayout clPosition;

    private ConstraintLayout clReason;

    private AppCompatTextView tvPositionDesc;

    private AppCompatTextView tvReasonDesc;

    private CSignStylePositionView positionView;

    private CSignStyleReasonView reasonView;

    private AppCompatButton btnSave;

    private CToolBar toolBar;

    private ScrollView slMain;

    private AppCompatCheckBox cbName;

    private AppCompatCheckBox cbDate;

    private AppCompatCheckBox cbLogo;

    private AppCompatCheckBox cbDistinguishableName;

    private AppCompatCheckBox cbSDKVersion;

    private AppCompatCheckBox cbTab;

    private OnBackPressedCallback callback;

    private LinearLayout llAlignment;

    private AppCompatTextView tvAlignment;

    private AppCompatImageView ivAlignmentLeft;

    private AppCompatImageView ivAlignmentRight;

    private COnResultDigitalSignListener resultDigitalSignListener;

    protected CMultiplePermissionResultLauncher multiplePermissionResultLauncher = new CMultiplePermissionResultLauncher(this);

    public static CDigitalSignStylePreviewDialog newInstance(String signImagePath, String commonName, String dn) {
        Bundle args = new Bundle();
        args.putString(EXTRA_SIGN_IMAGE_PATH, signImagePath);
        args.putString(EXTRA_COMMON_NAME, commonName);
        args.putString(EXTRA_DIGTINGUISHABLE_NAME, dn);
        CDigitalSignStylePreviewDialog fragment = new CDigitalSignStylePreviewDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog instanceof ComponentDialog) {
            ComponentDialog componentDialog = (ComponentDialog) dialog;
            callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    toolBar.getIvToolBarBackBtn().performClick();
                }
            };
            componentDialog.getOnBackPressedDispatcher().addCallback(callback);
        }
        return dialog;
    }

    @Override
    protected int getStyle() {
        return CViewUtils.getThemeAttrResourceId(getContext().getTheme(), R.attr.compdfkit_BottomSheetDialog_Theme);
    }

    @Override
    protected boolean fullScreen() {
        return true;
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
    protected int layoutId() {
        return R.layout.tools_sign_style_preview_dialog;
    }

    @Override
    protected void onCreateView(View view) {
        previewView = view.findViewById(R.id.digital_sign_preview);
        clPosition = view.findViewById(R.id.cl_position);
        clReason = view.findViewById(R.id.cl_reason);
        tvPositionDesc = view.findViewById(R.id.tv_position_desc);
        tvReasonDesc = view.findViewById(R.id.tv_reason_desc);
        toolBar = view.findViewById(R.id.tool_bar);
        positionView = view.findViewById(R.id.view_position);
        reasonView = view.findViewById(R.id.view_reason);
        slMain = view.findViewById(R.id.sl_main);
        btnSave = view.findViewById(R.id.btn_save);
        cbName = view.findViewById(R.id.cb_name);
        cbDate = view.findViewById(R.id.cb_date);
        cbLogo = view.findViewById(R.id.cb_logo);
        cbDistinguishableName = view.findViewById(R.id.cb_distinguishable_name);
        cbSDKVersion = view.findViewById(R.id.cb_compdfkit_version);
        cbTab = view.findViewById(R.id.cb_tab);
        ivAlignmentLeft = view.findViewById(R.id.iv_alignment_left);
        ivAlignmentRight = view.findViewById(R.id.iv_alignment_right);
        llAlignment = view.findViewById(R.id.ll_alignment_type);
        tvAlignment = view.findViewById(R.id.tv_alignment);
    }

    @Override
    protected void onViewCreate() {
        initListener();
        initData();
    }

    private void initListener() {
        clPosition.setOnClickListener(this);
        clReason.setOnClickListener(this);
        toolBar.setBackBtnClickListener(this);
        btnSave.setOnClickListener(this);
        ivAlignmentLeft.setOnClickListener(this);
        ivAlignmentRight.setOnClickListener(this);
        positionView.setCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvPositionDesc.setText(positionView.getPosition());
            } else {
                previewView.setLocation("");
                tvPositionDesc.setText(R.string.tools_close);
            }
            previewView.setShowLocation(isChecked);
        });
        positionView.setTextChangedListener((s, start, before, count) -> {
            if (positionView.isEnablePosition()) {
                tvPositionDesc.setText(s);
                previewView.setLocation(TextUtils.isEmpty(s) ? "" : s.toString());
            }
        });
        reasonView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvReasonDesc.setText(reasonView.getReason());
            } else {
                tvReasonDesc.setText(R.string.tools_close);
            }
            previewView.setShowReason(isChecked);
            previewView.setReason(reasonView.getReason());
        });
        reasonView.setSelectReasonListener(reason -> {
            if (reasonView.isEnableReason()) {
                tvReasonDesc.setText(reason);
                previewView.setReason(reason);
            }
        });
        cbTab.setOnCheckedChangeListener((buttonView, isChecked) -> previewView.setShowTab(isChecked));
        cbName.setOnCheckedChangeListener((buttonView, isChecked) -> previewView.setShowName(isChecked));
        cbDate.setOnCheckedChangeListener((buttonView, isChecked) -> previewView.setShowDate(isChecked));
        cbDistinguishableName.setOnCheckedChangeListener((buttonView, isChecked) -> previewView.setShowDistinguishableName(isChecked));
        cbSDKVersion.setOnCheckedChangeListener((buttonView, isChecked) -> previewView.setShowSdkVersion(isChecked));
        cbLogo.setOnCheckedChangeListener((buttonView, isChecked) -> previewView.setShowLogo(isChecked));
    }

    private void initData() {
        if (getArguments() != null) {
            String signImagePath = getArguments().getString(EXTRA_SIGN_IMAGE_PATH, "");
            tvAlignment.setVisibility(TextUtils.isEmpty(signImagePath) ? View.GONE : View.VISIBLE);
            llAlignment.setVisibility(TextUtils.isEmpty(signImagePath) ? View.GONE : View.VISIBLE);
            previewView.setSignImage(signImagePath);
            if (!TextUtils.isEmpty(getArguments().getString(EXTRA_COMMON_NAME))) {
                String commonName = getArguments().getString(EXTRA_COMMON_NAME);
                previewView.setCommonName(commonName);
            }
            if (!TextUtils.isEmpty(getArguments().getString(EXTRA_DIGTINGUISHABLE_NAME))) {
                previewView.setDistinguishableName(getArguments().getString(EXTRA_DIGTINGUISHABLE_NAME));
            }
        }
        previewView.setShowName(cbName.isChecked());
        previewView.setShowDate(cbDate.isChecked());
        previewView.setShowLogo(cbLogo.isChecked());
        previewView.setShowDistinguishableName(cbDistinguishableName.isChecked());
        previewView.setShowSdkVersion(cbSDKVersion.isChecked());
        previewView.setShowTab(cbTab.isChecked());
        ivAlignmentLeft.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cl_position) {
            positionView.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            slMain.setVisibility(View.GONE);
        } else if (v.getId() == R.id.cl_reason) {
            reasonView.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            slMain.setVisibility(View.GONE);
        } else if (v.getId() == toolBar.getIvToolBarBackBtn().getId()) {
            CViewUtils.hideKeyboard(getDialog());
            if (positionView.getVisibility() == View.VISIBLE) {
                positionView.setVisibility(View.GONE);
                slMain.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                positionView.hideKeyboard();
                return;
            }
            if (reasonView.getVisibility() == View.VISIBLE) {
                reasonView.setVisibility(View.GONE);
                slMain.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                return;
            }
            dismiss();
        } else if (v.getId() == R.id.iv_alignment_left) {
            ivAlignmentLeft.setSelected(true);
            ivAlignmentRight.setSelected(false);
            previewView.setContentAlignLeft(false);
        } else if (v.getId() == R.id.iv_alignment_right) {
            ivAlignmentLeft.setSelected(false);
            ivAlignmentRight.setSelected(true);
            previewView.setContentAlignLeft(true);
        } else if (v.getId() == R.id.btn_save) {
            CViewUtils.hideKeyboard(getDialog());
            if (Build.VERSION.SDK_INT < CPermissionUtil.VERSION_R) {
                multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
                    if (CPermissionUtil.hasStoragePermissions(getContext())) {
                        save();
                    } else {
                        if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
                        }
                    }
                });
            } else {
                save();
            }

        }
    }

    private void save() {
        if (positionView.getVisibility() == View.VISIBLE) {
            positionView.setVisibility(View.GONE);
            slMain.setVisibility(View.VISIBLE);
            return;
        }
        if (reasonView.getVisibility() == View.VISIBLE) {
            reasonView.setVisibility(View.GONE);
            slMain.setVisibility(View.VISIBLE);
            return;
        }
        Bitmap bitmap = previewView.getBitmap();
        String imagePath = CSignatureDatas.saveDigitalSignatureBitmap(getContext(), bitmap);
        if (resultDigitalSignListener != null) {
            resultDigitalSignListener.sign(imagePath, previewView.getConfig(), previewView.getLocation(), previewView.getReason());
        }
        dismiss();
    }

    public void setResultDigitalSignListener(COnResultDigitalSignListener resultDigitalSignListener) {
        this.resultDigitalSignListener = resultDigitalSignListener;
    }

    public interface COnResultDigitalSignListener {
        void sign(String signPreviewImagePath, CPDFDigitalSigConfig config, String location, String reason);
    }
}
