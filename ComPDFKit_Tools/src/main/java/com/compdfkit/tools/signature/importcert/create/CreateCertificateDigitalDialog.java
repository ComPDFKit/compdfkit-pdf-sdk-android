/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.importcert.create;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.ViewSwitcher;

import androidx.activity.ComponentDialog;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.signature.CPDFOwnerInfo;
import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.storage.CPDFPublicFileSaver;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.view.CEditText;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.importcert.create.adapter.CountryReginSpinnerAdapter;
import com.compdfkit.tools.signature.importcert.create.adapter.PurposeSpinnerAdapter;
import com.compdfkit.tools.signature.interfaces.COnSelectCertFileListener;

import java.io.File;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class CreateCertificateDigitalDialog extends CBasicBottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "CPDFStorage";

    private static final String CERTIFICATE_MIME_TYPE = "application/x-pkcs12";

    private AppCompatTextView tvTitle;

    private CEditText etName;

    private CEditText etOrganizationUnit;

    private CEditText etOrganizationName;

    private CEditText etEmailAddress;

    private AppCompatSpinner spinnerCountry;

    private AppCompatSpinner spinnerPurpose;

    private Switch swSaveToFile;

    private AppCompatButton btnOk;

    private ViewSwitcher switcher;

    private AppCompatTextView tvSaveAddress;

    private LinearLayout llSaveAddress;

    private CEditText etPassword;

    private CEditText etConfirmPassword;

    private boolean showSaveStatus = false;

    private OnBackPressedCallback callback;

    private String customSavePath;

    private AppCompatTextView tvPasswordError;

    private AppCompatTextView tvEmailError;

    private COnSelectCertFileListener selectCertFileListener;

    private CountryReginSpinnerAdapter countryReginSpinnerAdapter;

    private PurposeSpinnerAdapter purposeSpinnerAdapter;

    private String fileName = "";

    protected CMultiplePermissionResultLauncher multiplePermissionResultLauncher = new CMultiplePermissionResultLauncher(this);

    public static CreateCertificateDigitalDialog newInstance() {
        Bundle args = new Bundle();
        CreateCertificateDigitalDialog fragment = new CreateCertificateDigitalDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int themeResId() {
        return R.attr.compdfkit_BottomSheetDialog_Transparent_Theme;
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
                    if (showSaveStatus) {
                        showInfoStatusView();
                        showSaveStatus = false;
                        callback.setEnabled(false);
                    } else {
                        dismiss();
                    }
                }
            };
            componentDialog.getOnBackPressedDispatcher().addCallback(callback);
        }
        return dialog;
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
        return R.layout.tools_sign_create_cert_digital_id_fragment;
    }

    @Override
    protected void onCreateView(View view) {
        tvTitle = view.findViewById(R.id.tv_tool_bar_title);
        AppCompatImageView ivClose = view.findViewById(R.id.iv_tool_bar_close);
        etName = view.findViewById(R.id.et_name);
        etOrganizationUnit = view.findViewById(R.id.et_organization_unit);
        etOrganizationName = view.findViewById(R.id.et_organization_name);
        etEmailAddress = view.findViewById(R.id.et_email_address);
        spinnerCountry = view.findViewById(R.id.spinner_country_regin);
        spinnerPurpose = view.findViewById(R.id.spinner_purpose);
        swSaveToFile = view.findViewById(R.id.sw_save_to_file);
        btnOk = view.findViewById(R.id.btn_ok);
        switcher = view.findViewById(R.id.view_switcher);
        tvSaveAddress = view.findViewById(R.id.tv_save_address);
        llSaveAddress = view.findViewById(R.id.ll_save_address);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        AppCompatButton btnConfirmCreate = view.findViewById(R.id.btn_save);
        tvPasswordError = view.findViewById(R.id.tv_password_error);
        tvEmailError = view.findViewById(R.id.tv_email_error);
        btnOk.setOnClickListener(this);
        tvSaveAddress.setOnClickListener(this);
        btnConfirmCreate.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }

    @Override
    protected void onViewCreate() {
        String uuid = UUID.randomUUID().toString();
        fileName = "new_cert_" + uuid.substring(uuid.length() - 4, uuid.length()) + ".pfx";
        tvTitle.setText(R.string.tools_create_a_self_signed_digital_id);
        initCountryReginData();
        initPurpose();
        etName.addTextChangedListener((s, start, before, count) -> enableConfirmButton());
        etOrganizationUnit.addTextChangedListener((s, start, before, count) -> enableConfirmButton());
        etEmailAddress.addTextChangedListener((s, start, before, count) -> {
            enableConfirmButton();
            clearEmailError();
        });
        etOrganizationName.addTextChangedListener((s, start, before, count) -> enableConfirmButton());
        swSaveToFile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llSaveAddress.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            tvSaveAddress.setText(getSaveAddress() + File.separator + fileName);
        });
        tvSaveAddress.setText(getSaveAddress() + File.separator + fileName);
        enableConfirmButton();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            if (!validateEmail(true)) {
                return;
            }
            callback.setEnabled(true);
            showSaveStatusView();
            showSaveStatus = true;
        } else if (v.getId() == R.id.iv_tool_bar_close) {
            dismiss();
        } else if (v.getId() == R.id.tv_save_address) {
            if (CPDFStorageManager.shouldRequestLegacyWritePermission()) {
                multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
                    if (CPermissionUtil.hasStoragePermissions(getContext())) {
                        showDirectoryDialog();
                    } else {
                        if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
                        }
                    }
                });
            }else {
                showDirectoryDialog();
            }

        } else if (v.getId() == R.id.btn_save) {

            if (CPDFStorageManager.shouldRequestLegacyWritePermission()) {
                multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
                    if (CPermissionUtil.hasStoragePermissions(getContext())) {
                        createCertificate();
                    } else {
                        if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
                        }
                    }
                });
            }else {
                createCertificate();
            }
        }
    }

    private void createCertificate(){
        String name = etName.getText();
        String grantor = etOrganizationUnit.getText();
        String sectoral = etOrganizationName.getText();
        String email = etEmailAddress.getText().trim();
        String countryArea = countryReginSpinnerAdapter.getSelectCountryRegin();
        CPDFSignature.CertUsage certUsage = purposeSpinnerAdapter.getSelectUsage();

        String password = etPassword.getText();
        String verifyPassword = etConfirmPassword.getText();

        if (!validateEmail(true)) {
            showInfoStatusView();
            showSaveStatus = false;
            callback.setEnabled(false);
            return;
        }

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(verifyPassword) || !password.equals(verifyPassword)) {
            etPassword.setError(true);
            etConfirmPassword.setError(true);
            tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }
        etPassword.setError(false);
        etConfirmPassword.setError(false);
        tvPasswordError.setVisibility(View.GONE);

        CPDFOwnerInfo ownerInfo = new CPDFOwnerInfo();
        ownerInfo.setCommonName(name);
        ownerInfo.setOrgnizeUnit(grantor);
        ownerInfo.setOrgnize(sectoral);
        ownerInfo.setEmail(email);
        ownerInfo.setCountry(countryArea);
        File certFile = CPDFStorageManager.createTempFile(requireContext(), fileName);
        String saveDir = certFile.getParent();
        CLog.d(TAG, "CreateCertificateDigitalDialog create certificate temp start file="
                + certFile.getAbsolutePath()
                + ", saveToFile=" + swSaveToFile.isChecked()
                + ", customSavePath=" + customSavePath);
        boolean success = CertificateDigitalDatas.generatePKCS12Cert(
                ownerInfo,
                etPassword.getText(),
                saveDir,
                fileName,
                certUsage);
        boolean exists = certFile.exists();
        if (success && exists) {
            File workFile = copyCertificateToWorkFile(certFile);
            if (workFile == null || !workFile.exists()) {
                CLog.e(TAG, "CreateCertificateDigitalDialog copy work file failed source="
                        + certFile.getAbsolutePath());
                CToastUtil.showToast(getContext(), R.string.tools_digital_create_error);
                deleteTempCertificate(certFile);
                return;
            }
            if (swSaveToFile.isChecked()) {
                boolean published = publishCertificate(certFile);
                if (!published) {
                    CToastUtil.showToast(getContext(), R.string.tools_digital_create_error);
                    deleteTempCertificate(certFile);
                    return;
                }
            } else {
                deleteTempCertificate(certFile);
            }
            if (selectCertFileListener != null) {
                selectCertFileListener.certificateFile(workFile.getAbsolutePath(), etPassword.getText());
            }
        } else {
            CLog.e(TAG, "CreateCertificateDigitalDialog create certificate failed file="
                    + certFile.getAbsolutePath()
                    + ", success=" + success
                    + ", exists=" + exists
                    + ", size=" + (exists ? certFile.length() : -1));
            CToastUtil.showToast(getContext(), R.string.tools_digital_create_error);
            deleteTempCertificate(certFile);
        }
    }

    private File copyCertificateToWorkFile(File sourceFile) {
        File workDir = new File(requireContext().getCacheDir(), CPDFStorageManager.getStorageConfig().getCacheFolderName() + File.separator + "certFile");
        if (!workDir.exists()) {
            boolean created = workDir.mkdirs();
            CLog.d(TAG, "CreateCertificateDigitalDialog create work dir=" + workDir.getAbsolutePath()
                    + ", created=" + created);
        }
        File workFile = CFileUtils.renameNameSuffix(new File(workDir, sourceFile.getName()));
        try {
            boolean copied = CFileUtils.writeFile(
                    new FileInputStream(sourceFile),
                    new FileOutputStream(workFile)
            );
            CLog.d(TAG, "CreateCertificateDigitalDialog copy work file source="
                    + sourceFile.getAbsolutePath()
                    + ", target=" + workFile.getAbsolutePath()
                    + ", copied=" + copied
                    + ", size=" + (workFile.exists() ? workFile.length() : -1));
            return copied ? workFile : null;
        } catch (Exception e) {
            CLog.e(TAG, "CreateCertificateDigitalDialog copy work file exception source="
                    + sourceFile.getAbsolutePath()
                    + ", error=" + e.getMessage());
            return null;
        }
    }

    private boolean publishCertificate(File sourceFile) {
        String publicDirectory = getCertificatePublicDirectory();
        if (TextUtils.isEmpty(publicDirectory)) {
            return copyCertificateToCustomPath(sourceFile);
        }
        CUriUtil.MediaStoreSaveResult result = CUriUtil.publishFileToMediaStore(
                requireContext(),
                sourceFile,
                publicDirectory,
                sourceFile.getName(),
                CERTIFICATE_MIME_TYPE,
                true
        );
        CLog.d(TAG, "CreateCertificateDigitalDialog publish certificate result success="
                + result.isSuccess()
                + ", uri=" + result.getUri()
                + ", path=" + result.getPath()
                + ", error=" + result.getErrorMessage());
        return result.isSuccess();
    }

    private String getCertificatePublicDirectory() {
        if (TextUtils.isEmpty(customSavePath)) {
            return CPDFStorageManager.getCertificateRelativePath();
        }
        String relativePath = CPDFStorageManager.toPublicRelativePath(customSavePath);
        CLog.d(TAG, "CreateCertificateDigitalDialog custom certificate path="
                + customSavePath
                + ", relativePath=" + relativePath);
        return relativePath;
    }

    private boolean copyCertificateToCustomPath(File sourceFile) {
        CPDFPublicFileSaver.SaveResult saveResult = CPDFPublicFileSaver.saveToSelectedDirectory(
                requireContext(),
                customSavePath,
                null,
                sourceFile.getName(),
                CERTIFICATE_MIME_TYPE,
                false,
                tempPath -> {
                    try {
                        return CFileUtils.writeFile(
                                new FileInputStream(sourceFile),
                                new FileOutputStream(tempPath)
                        );
                    } catch (Exception e) {
                        CLog.e(TAG, "copyCertificateToCustomPath exception=" + e.getMessage());
                        return false;
                    }
                });
        boolean success = saveResult.isSuccess();
        if (success) {
            deleteTempCertificate(sourceFile);
        }
        return success;
    }

    private void deleteTempCertificate(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        boolean deleted = file.delete();
        CLog.d(TAG, "CreateCertificateDigitalDialog delete temp certificate file="
                + file.getAbsolutePath()
                + ", deleted=" + deleted);
    }

    private void initCountryReginData() {
        CThreadPoolUtils.getInstance().executeIO(() -> {
            List<Locale> datas = getLocals();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    try {
                        countryReginSpinnerAdapter = new CountryReginSpinnerAdapter(getContext(), datas);
                        spinnerCountry.setAdapter(countryReginSpinnerAdapter);
                        spinnerCountry.setSelection(countryReginSpinnerAdapter.getSelectPosition());
                    } catch (Exception ignored) {
                    }
                });
            }
        });
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (countryReginSpinnerAdapter != null) {
                    countryReginSpinnerAdapter.setSelectLocal(countryReginSpinnerAdapter.list.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initPurpose() {
        List<CPDFSignature.CertUsage> certUsages = new ArrayList<>();
        certUsages.add(CPDFSignature.CertUsage.PDFDigSig);
        certUsages.add(CPDFSignature.CertUsage.PDFDataEnc);
        certUsages.add(CPDFSignature.CertUsage.PDFAll);
        purposeSpinnerAdapter = new PurposeSpinnerAdapter(getContext(), certUsages);
        spinnerPurpose.setAdapter(purposeSpinnerAdapter);
        spinnerPurpose.setSelection(purposeSpinnerAdapter.getSelectPosition());
        spinnerPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (purposeSpinnerAdapter != null) {
                    purposeSpinnerAdapter.setSelectUseAge(purposeSpinnerAdapter.list.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showDirectoryDialog(){
        CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(
                CPDFStorageManager.getDefaultDirectoryDialogPath(),
                getString(R.string.tools_select_folder),
                getString(R.string.tools_save_to_this_directory)
        );
        directoryDialog.setSelectFolderListener(dir -> {
            customSavePath = dir;
            tvSaveAddress.setText(getSaveAddress() + File.separator + fileName);
        });
        directoryDialog.show(getChildFragmentManager(), "directoryDialog");
    }

    private void enableConfirmButton() {
        String name = etName.getText();
        String email = etEmailAddress.getText().trim();
        boolean enable = !TextUtils.isEmpty(name) && !TextUtils.isEmpty(email);
        btnOk.setEnabled(enable);
    }

    private boolean validateEmail(boolean showError) {
        String email = etEmailAddress.getText().trim();
        boolean valid = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (valid || !showError) {
            clearEmailError();
        } else {
            etEmailAddress.getEditText().setBackgroundResource(R.drawable.tools_bg_import_certificate_digital_id_email_error_item);
            tvEmailError.setVisibility(View.VISIBLE);
        }
        return valid;
    }

    private void clearEmailError() {
        etEmailAddress.setError(false);
        tvEmailError.setVisibility(View.GONE);
    }

    private void showSaveStatusView() {
        switcher.setInAnimation(getContext(), R.anim.tools_slide_in_right);
        switcher.setOutAnimation(getContext(), R.anim.tools_slide_out_left);
        switcher.showNext();
        tvTitle.setText(swSaveToFile.isChecked() ? R.string.tools_save_a_self_signed_digital_id : R.string.tools_create_a_self_signed_digital_id);
    }

    private void showInfoStatusView() {
        switcher.setInAnimation(getContext(), R.anim.tools_slide_in_left);
        switcher.setOutAnimation(getContext(), R.anim.tools_slide_out_right);
        switcher.showPrevious();
        tvTitle.setText(R.string.tools_create_a_self_signed_digital_id);

    }

    private String getSaveAddress() {
        if (!TextUtils.isEmpty(customSavePath)) {
            return customSavePath;
        }
        File file;
        if (swSaveToFile.isChecked()) {
            file = new File(CPDFStorageManager.getLegacyPublicDirectoryPath(CPDFStorageManager.getCertificateRelativePath()));
        } else {
            file = new File(getContext().getCacheDir(), CPDFStorageManager.getStorageConfig().getCacheFolderName());
        }
        return file.getAbsolutePath();
    }

    private List<Locale> getLocals() {
        String[] isos = Locale.getISOCountries();
        List<Locale> list = new ArrayList<>();
        for (String iso : isos) {
            list.add(new Locale("", iso));
        }
        return list;
    }

    public void setSelectCertFileListener(COnSelectCertFileListener selectCertFileListener) {
        this.selectCertFileListener = selectCertFileListener;
    }
}
