/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.watermark;

import android.Manifest;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.threadpools.SimpleBackgroundTask;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;

/**
 * watermark edit dialog fragment
 * <blockquote><pre>
 *
 *      CWatermarkEditDialog watermarkEditDialog = CWatermarkEditDialog.newInstance();
 *      watermarkEditDialog.setDocument(binding.pdfView.getCPdfReaderView().getPDFDocument());
 *      watermarkEditDialog.setPageIndex(binding.pdfView.currentPageIndex);
 *      watermarkEditDialog.setCompleteListener((pdfFile)->{
 *           // Get the saving path of the saved pdf document and open the display
 *           binding.pdfView.openPDF(pdfFile);
 *           watermarkEditDialog.dismiss();
 *      });
 *      watermarkEditDialog.show(getSupportFragmentManager(), "watermarkEditDialog");
 *
 * </pre></blockquote><p>
 */
public class CWatermarkEditDialog extends CBasicBottomSheetDialogFragment implements View.OnClickListener {

    private CToolBar toolBar;

    private AppCompatButton btnDone;

    private ViewPager2 viewPager2;

    private CPDFDocument document;

    private TabLayout tabLayout;

    private CWatermarkPageFragmentAdapter watermarkPageFragmentAdapter;

    private CEditCompleteListener completeListener;

    private int pageIndex = 0;

    private String pdfFilePath;

    private Uri pdfUri;

    private String password;

    protected CMultiplePermissionResultLauncher multiplePermissionResultLauncher = new CMultiplePermissionResultLauncher(this);

    public static CWatermarkEditDialog newInstance() {
        Bundle args = new Bundle();
        CWatermarkEditDialog fragment = new CWatermarkEditDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void setDocument(CPDFDocument document) {
        this.document = document;
    }

    public void setDocument(@Nullable String pdfFilePath, @Nullable Uri uri) {
        this.pdfFilePath = pdfFilePath;
        this.pdfUri = uri;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
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
    protected int getStyle() {
        return R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle_FillScreen;
    }

    @Override
    protected float dimAmount() {
        return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_cpdf_security_watermark_edit_dialog;
    }

    @Override
    protected void onCreateView(View rootView) {
        toolBar = rootView.findViewById(R.id.tool_bar);
        btnDone = rootView.findViewById(R.id.btn_done);
        viewPager2 = rootView.findViewById(R.id.view_pager2);
        tabLayout = rootView.findViewById(R.id.tab_layout);
        rootView.findViewById(R.id.iv_watermark_setting).setOnClickListener(this);
    }

    @Override
    protected void onViewCreate() {
        toolBar.setBackBtnClickListener(this::onClick);
        btnDone.setOnClickListener(this::onClick);
        if (document != null) {
            initWatermarkContent();
        } else if (!TextUtils.isEmpty(pdfFilePath) || pdfUri != null) {
            initDocument(pdfFilePath, pdfUri);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == toolBar.getIvToolBarBackBtn().getId()) {
            dismiss();
        } else if (v.getId() == R.id.btn_done) {
            if (document == null) {
                return;
            }
            Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + tabLayout.getSelectedTabPosition());
            if (fragment != null && fragment instanceof CWatermarkPageFragment) {
                if (!((CWatermarkPageFragment) fragment).hasWatermark()) {
                    return;
                }
            }

            // Check the storage permissions to ensure that
            // you can select the directory and save to the corresponding directory normally.
            if (CPermissionUtil.hasStoragePermissions(getContext())) {
                save();
            } else {
                if (CPermissionUtil.checkManifestPermission(getContext(), Manifest.permission.MANAGE_EXTERNAL_STORAGE) && Build.VERSION.SDK_INT >= CPermissionUtil.VERSION_R) {
                    CPermissionUtil.openManageAllFileAppSettings(getContext());
                } else {
                    multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
                        if (CPermissionUtil.hasStoragePermissions(getContext())) {
                            save();
                        } else {
                            if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getContext());
                            }
                        }
                    });
                }
            }
        } else if (v.getId() == R.id.iv_watermark_setting) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + tabLayout.getSelectedTabPosition());
            if (fragment != null && fragment instanceof CWatermarkPageFragment) {
                ((CWatermarkPageFragment) fragment).showWatermarkStyleDialog();
            }
        }
    }

    private void initWatermarkContent() {
        int[] tabs = new int[]{R.string.tools_custom_stamp_text, R.string.tools_image};
        watermarkPageFragmentAdapter = new CWatermarkPageFragmentAdapter(this, document, pageIndex);
        viewPager2.setAdapter(watermarkPageFragmentAdapter);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setOffscreenPageLimit(2);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(tabs[position]);
        });
        tabLayoutMediator.attach();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                try {
                    if (position == 1) {
                        viewPager2.postDelayed(() -> {
                            Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + position);
                            if (fragment != null && fragment instanceof CWatermarkPageFragment) {
                                CWatermarkPageFragment watermarkPageFragment = ((CWatermarkPageFragment) fragment);
                                Fragment styleFragment = watermarkPageFragment.getChildFragmentManager().findFragmentByTag("styleFragment");
                                if (styleFragment == null && !watermarkPageFragment.hasWatermark()) {
                                    watermarkPageFragment.showWatermarkStyleDialog();
                                }
                            }
                        }, 150);
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void save() {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(dirPath, getString(R.string.tools_saving_path), getString(R.string.tools_okay));
        directoryDialog.setSelectFolderListener(dir -> {
            File file = new File(dir, CFileUtils.getFileNameNoExtension(document.getFileName()) + getString(R.string.tools_watermark_suffix));
            File pdfFile = CFileUtils.renameNameSuffix(file);
            Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + tabLayout.getSelectedTabPosition());
            if (fragment != null && fragment instanceof CWatermarkPageFragment) {
                new SimpleBackgroundTask<String>(getContext()) {

                    @Override
                    protected String onRun() {
                        try {
                            boolean success = ((CWatermarkPageFragment) fragment).applyWatermark();
                            if (!success){
                                return null;
                            }
                            boolean result = document.saveAs(pdfFile.getAbsolutePath(), false);
                            return result ? pdfFile.getAbsolutePath() : null;
                        } catch (Exception e) {

                        }
                        return null;
                    }

                    @Override
                    protected void onSuccess(String result) {
                        if (completeListener != null) {
                            completeListener.complete(result);
                        }
                    }
                }.execute();
            }
        });
        directoryDialog.show(getChildFragmentManager(), "dirDialog");
    }


    private void initDocument(String filePath, Uri uri) {
        CPDFDocument document = new CPDFDocument(getContext());
        CPDFDocument.PDFDocumentError error;
        if (!TextUtils.isEmpty(filePath)) {
            error = document.open(filePath, password);
        } else {
            error = document.open(uri, password);
        }
        if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
            this.document = document;
            initWatermarkContent();
        } else if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorPassword) {
            CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
            verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(document, filePath, uri);
            verifyPasswordDialogFragment.setVerifyCompleteListener(document1 -> {
                this.document = document1;
                this.password = verifyPasswordDialogFragment.getPassword();
                initWatermarkContent();
            });
            verifyPasswordDialogFragment.show(getChildFragmentManager(), "verifyPasswordDialog");
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ||
                this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewPager2.postDelayed(() -> {
                if (viewPager2.getCurrentItem() == 1) {
                    viewPager2.setCurrentItem(0);
                    viewPager2.setCurrentItem(1);
                }
            }, 100);
        }
    }

    public void setCompleteListener(CEditCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public interface CEditCompleteListener {
        void complete(String pdfFile);
    }
}
