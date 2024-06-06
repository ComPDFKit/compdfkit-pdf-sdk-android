/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.encryption;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFDocumentPermissionInfo;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;

import java.io.File;

/**
 * Document encryption related settings dialog
 * 1. Set file opening password
 * 2. Set file permission password
 *
 * Before encrypting or decrypting, make sure you have document owner permissions
 * <blockquote><pre>
 *     CPDFDocument.PDFDocumentPermissions permission = document.getPermissions()
 *     //only has user permission
 *     if (permission = CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsUser) {
 *         // You can enter the owner permissions password and reload the pdf to gain owner permissions
 *         document.reload(ownerPassword);
 *         return;
 *     }
 *
 *     // has owner permission
 *     CDocumentEncryptionDialog documentEncryptionDialog = CDocumentEncryptionDialog.newInstance();
 *     documentEncryptionDialog.setDocument(binding.pdfView.getCPdfReaderView().getPDFDocument());
 *     documentEncryptionDialog.setEncryptionResultListener((isRemoveSecurity, result, filePath, passowrd) -> {
 *         // Open encrypted or password-removed documents
 *         binding.pdfView.openPDF(filePath, passowrd);
 *         documentEncryptionDialog.dismiss();
 *     });
 *     documentEncryptionDialog.show(getSupportFragmentManager(), "documentEncryptionDialog");
 * </pre></blockquote><p>
 *
 */
public class CDocumentEncryptionDialog extends CBasicBottomSheetDialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CToolBar toolBar;

    private AppCompatButton btnDone;

    private Switch swUserPassword;

    private AppCompatEditText etUserPassword;

    private Switch swOwnerPassword;

    private AppCompatEditText etOwnerPassword;


    private AppCompatCheckBox cbRestrictPrint;

    private AppCompatCheckBox cbRestrictCopy;

    private AppCompatImageView ivUserPwdVisible;

    private AppCompatImageView ivOwnerPwdVisible;

    private Spinner levelSpinner;

    private AppCompatTextView tvEncryptionLevel;

    private CEncryptAlgorithmSpinnerAdapter algorithmSpinnerAdapter;

    private CPDFDocument document;

    private CEncryptionResultListener encryptionResultListener;

    protected CMultiplePermissionResultLauncher multiplePermissionResultLauncher = new CMultiplePermissionResultLauncher(this);

    public void setDocument(CPDFDocument document) {
        this.document = document;
    }


    @Override
    protected int getStyle() {
        return R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle_FillScreen;
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

    public static CDocumentEncryptionDialog newInstance() {
        Bundle args = new Bundle();
        CDocumentEncryptionDialog fragment = new CDocumentEncryptionDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_cpdf_security_document_encryption_dialog;
    }

    @Override
    protected void onCreateView(View rootView) {
        toolBar = rootView.findViewById(R.id.tool_bar);
        btnDone = rootView.findViewById(R.id.btn_done);
        swUserPassword = rootView.findViewById(R.id.sw_setting_password);
        etUserPassword = rootView.findViewById(R.id.et_user_password);
        swOwnerPassword = rootView.findViewById(R.id.sw_setting_permission_password);
        etOwnerPassword = rootView.findViewById(R.id.et_owner_password);
        cbRestrictPrint = rootView.findViewById(R.id.cb_print);
        cbRestrictCopy = rootView.findViewById(R.id.cb_copy);
        levelSpinner = rootView.findViewById(R.id.spinner_cryptographic_level);
        ivUserPwdVisible = rootView.findViewById(R.id.iv_user_pwd_show);
        ivOwnerPwdVisible = rootView.findViewById(R.id.iv_owner_pwd_show);
        tvEncryptionLevel = rootView.findViewById(R.id.tv_encryption_level);
    }

    @Override
    protected void onViewCreate() {
        toolBar.setBackBtnClickListener(this);
        btnDone.setOnClickListener(this);
        ivUserPwdVisible.setOnClickListener(this);
        ivOwnerPwdVisible.setOnClickListener(this);
        swUserPassword.setOnCheckedChangeListener(this);
        swOwnerPassword.setOnCheckedChangeListener(this);
        etOwnerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateEnablePermissionInfo();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        algorithmSpinnerAdapter = new CEncryptAlgorithmSpinnerAdapter(getContext(), document.getEncryptAlgorithm());
        levelSpinner.setAdapter(algorithmSpinnerAdapter);
        levelSpinner.setSelection(algorithmSpinnerAdapter.getSelectEncryptAlgoIndex());
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                algorithmSpinnerAdapter.setSelectEncrypt(algorithmSpinnerAdapter.list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initPermissionInfo();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == swUserPassword.getId()) {
            setTextViewEnable(etUserPassword, isChecked);
            ivUserPwdVisible.setEnabled(isChecked);
            if (isChecked) {
                CViewUtils.showKeyboard(etUserPassword);
            } else {
                etUserPassword.setText("");
            }
            updateEncryptionLevelStatus();
        } else if (buttonView.getId() == swOwnerPassword.getId()) {
            setTextViewEnable(etOwnerPassword, isChecked);
            ivOwnerPwdVisible.setEnabled(isChecked);
            updateEnablePermissionInfo();
            updateEncryptionLevelStatus();
            if (isChecked) {
                CViewUtils.showKeyboard(etOwnerPassword);
            } else {
                etOwnerPassword.setText("");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == toolBar.getIvToolBarBackBtn().getId()) {
            dismiss();
        } else if (v.getId() == R.id.btn_done) {
            Editable userPassword = etUserPassword.getText();
            Editable ownerPassword = etOwnerPassword.getText();
            if (swUserPassword.isChecked() && TextUtils.isEmpty(userPassword)){
                CToastUtil.showLongToast(getContext(), R.string.tools_enter_password_can_not_be_empty);
                return;
            }
            if (swOwnerPassword.isChecked() && TextUtils.isEmpty(ownerPassword)){
                CToastUtil.showLongToast(getContext(), R.string.tools_enter_password_can_not_be_empty);
                return;
            }
            if (!TextUtils.isEmpty(userPassword) && !TextUtils.isEmpty(ownerPassword)
                && userPassword.toString().equals(ownerPassword.toString())){
                CToastUtil.showLongToast(getContext(), R.string.tools_password_must_be_different);
                return;
            }

            // Check the storage permissions to ensure that
            // you can select the directory and save to the corresponding directory normally.
            if (CPermissionUtil.hasStoragePermissions(getContext())) {
                showSelectDirDialog();
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    CPermissionUtil.openManageAllFileAppSettings(getContext());
                } else {
                    multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
                        if (CPermissionUtil.hasStoragePermissions(getContext())) {
                            showSelectDirDialog();
                        } else {
                            if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
                            }
                        }
                    });
                }
            }
        } else if (v.getId() == R.id.iv_user_pwd_show) {
            ivUserPwdVisible.setSelected(!ivUserPwdVisible.isSelected());
            etUserPassword.setInputType(ivUserPwdVisible.isSelected() ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etUserPassword.setSelection(etUserPassword.getText().length());
        } else if (v.getId() == R.id.iv_owner_pwd_show) {
            ivOwnerPwdVisible.setSelected(!ivOwnerPwdVisible.isSelected());
            etOwnerPassword.setInputType(ivOwnerPwdVisible.isSelected() ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etOwnerPassword.setSelection(etOwnerPassword.getText().length());
        }
    }

    private void initPermissionInfo() {
        if (document == null){
            dismiss();
            return;
        }
        if (document.isEncrypted()){
            String password = document.getPassword();
            boolean isOwnerPassword = document.checkOwnerPassword(password);
            if (isOwnerPassword){
                etOwnerPassword.setText(password);
                swOwnerPassword.setChecked(true);
                etOwnerPassword.setEnabled(true);
                swUserPassword.setChecked(true);
                etUserPassword.setEnabled(true);
            }else {
                etUserPassword.setText(password);
                swUserPassword.setChecked(true);
                etUserPassword.setEnabled(true);
            }
        }
        CPDFDocument tempDocument = new CPDFDocument(getContext());
        CPDFDocument.PDFDocumentError error;
        if (!TextUtils.isEmpty(document.getAbsolutePath())){
            error = tempDocument.open(document.getAbsolutePath());
        }else {
            error = tempDocument.open(document.getUri());
        }
        if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess  && tempDocument.isEncrypted()){
            swUserPassword.setChecked(false);
            etUserPassword.setEnabled(false);
        }
        tempDocument.close();
        updateEncryptionLevelStatus();
    }

    private void setTextViewEnable(TextView textView, boolean enable) {
        textView.setTextColor(ContextCompat.getColor(getContext(), enable ? R.color.tools_text_color_primary : R.color.tools_text_color_disable));
        textView.setEnabled(enable);
    }

    private void updateEnablePermissionInfo() {
        boolean enable = swOwnerPassword.isChecked() && !TextUtils.isEmpty(etOwnerPassword.getText());
        cbRestrictCopy.setEnabled(enable);
        cbRestrictPrint.setEnabled(enable);
        if (!enable) {
            cbRestrictCopy.setChecked(false);
            cbRestrictPrint.setChecked(false);
        }
        setTextViewEnable(cbRestrictCopy, enable);
        setTextViewEnable(cbRestrictPrint, enable);
    }

    private void updateEncryptionLevelStatus(){
        boolean show = swOwnerPassword.isChecked() || swUserPassword.isChecked();
        levelSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        tvEncryptionLevel.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showSelectDirDialog(){
        // Display the system folder directory and select a directory to save.
        String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(rootDir,
                getString(R.string.tools_saving_path), getString(R.string.tools_okay));
        directoryDialog.setSelectFolderListener(dir -> encryptionDocument(dir));
        directoryDialog.show(getParentFragmentManager(), "dirDialog");
    }


    private void encryptionDocument(String dir) {
        if (document == null) {
            return;
        }
        CThreadPoolUtils.getInstance().executeIO(() -> {
            boolean removeUserPassword = false;
            boolean removeOwnerPassword = false;
            String userPassword = etUserPassword.getText().toString();
            String ownerPassword = etOwnerPassword.getText().toString();
            try {
                CPDFDocument.PDFDocumentPermissions permissions = document.getPermissions();
                if (swUserPassword.isChecked()) {
                    // add user password
                    if (!TextUtils.isEmpty(userPassword)) {
                        document.setUserPassword(userPassword);
                    }else {
                        removeUserPassword = true;
                        document.setUserPassword("");
                    }
                } else {
                    if (permissions == CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsUser ||
                            !TextUtils.isEmpty(document.getPassword())) {
                        // remove user password
                        removeUserPassword = true;
                        document.setUserPassword("");
                    }
                }
                if (swOwnerPassword.isChecked() && !TextUtils.isEmpty(ownerPassword)) {
                    CPDFDocumentPermissionInfo permissionInfo = document.getPermissionsInfo();
                    permissionInfo.setAllowsPrinting(!cbRestrictPrint.isChecked());
                    permissionInfo.setAllowsCopying(!cbRestrictCopy.isChecked());
                    document.setPermissionsInfo(permissionInfo);
                    document.setOwnerPassword(ownerPassword);
                } else {
                    if (permissions == CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsOwner) {
                        removeOwnerPassword = true;
                        document.setOwnerPassword("");
                    }
                }
                document.setEncryptAlgorithm(algorithmSpinnerAdapter.getSelectEncryptAlgo());
                boolean result;
                boolean isRemoveSecurity = removeUserPassword && removeOwnerPassword;
                File file;
                if (isRemoveSecurity){
                    file = new File(dir, CFileUtils.getFileNameNoExtension(document.getFileName()) + getString(R.string.tools_document_encryption_remove_suffix));
                }else {
                    file = new File(dir, CFileUtils.getFileNameNoExtension(document.getFileName()) + getString(R.string.tools_document_encryption_suffix));
                }
                String filePath = CFileUtils.renameNameSuffix(file).getAbsolutePath();
                if (isRemoveSecurity) {
                    // remove security
                    result = document.saveAs(filePath, true);
                } else {
                    // Save to specified directory
                    result = document.saveAs(filePath, false);
                }
                document.close();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (encryptionResultListener != null) {
                            encryptionResultListener.result(isRemoveSecurity,
                                    result,
                                    filePath,
                                    !TextUtils.isEmpty(ownerPassword) ? ownerPassword : userPassword);
                        }
                    });
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }


    public void setEncryptionResultListener(CEncryptionResultListener encryptionResultListener) {
        this.encryptionResultListener = encryptionResultListener;
    }

    public interface CEncryptionResultListener {
        void result(boolean isRemoveSecurity, boolean result, String filePath, String password);
    }
}
