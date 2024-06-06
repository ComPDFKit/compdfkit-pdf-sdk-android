/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.pdfviewer;

import static com.compdfkit.tools.contenteditor.CEditToolbar.SELECT_AREA_IMAGE;
import static com.compdfkit.tools.contenteditor.CEditToolbar.SELECT_AREA_TEXT;
import static com.compdfkit.ui.contextmenu.CPDFContextMenuShowHelper.AddEditImageArea;
import static com.compdfkit.ui.contextmenu.CPDFContextMenuShowHelper.ReplaceEditImageArea;

import android.Manifest;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFDocumentPermissionInfo;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.pdfviewer.databinding.PdfSampleActivityBinding;
import com.compdfkit.tools.common.basic.activity.CBasicPDFActivity;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CSelectPDFDocumentResultContract;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.task.CExtractAssetFileTask;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.compdfkit.tools.common.views.pdfbota.CPDFBOTA;
import com.compdfkit.tools.common.views.pdfbota.CPDFBotaDialogFragment;
import com.compdfkit.tools.common.views.pdfbota.CPDFBotaFragmentTabs;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.forms.pdfproperties.pdfsign.CustomSignatureWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfsign.SignatureWidgetImpl;
import com.compdfkit.tools.security.encryption.CDocumentEncryptionDialog;
import com.compdfkit.tools.security.encryption.CInputOwnerPwdDialog;
import com.compdfkit.tools.security.watermark.CWatermarkEditDialog;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.bean.CPDFDocumentSignInfo;
import com.compdfkit.tools.signature.info.signlist.CPDFCertDigitalSignListDialog;
import com.compdfkit.tools.viewer.pdfsearch.CSearchResultDialogFragment;
import com.compdfkit.ui.contextmenu.IContextMenuShowListener;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.proxy.form.CPDFComboboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFListboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFPushbuttonWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFSignatureWidgetImpl;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends CBasicPDFActivity {

    public static final String EXTRA_FILE_PATH = "file_path";

    public static final String EXTRA_FILE_PASSWORD = "file_password";

    public static final String EXTRA_CONFIGURATION = "extra_configuration";

    /**
     * assets folder pdf file
     */
    public static final String QUICK_START_GUIDE = "ComPDFKit_Sample_File_Android.pdf";

    private PdfSampleActivityBinding binding;

    CSampleScreenManager screenManager = new CSampleScreenManager();

    private CPDFConfiguration cpdfConfiguration;

    private ActivityResultLauncher<Void> selectDocumentLauncher = registerForActivityResult(new CSelectPDFDocumentResultContract(), uri -> {
        if (uri != null) {
            CPDFReaderView readerView = binding.pdfView.getCPdfReaderView();
            if (readerView != null && readerView.getEditManager() != null) {
                readerView.getEditManager().endEdit();
            }
            if (readerView.getContextMenuShowListener() != null) {
                readerView.getContextMenuShowListener().dismissContextMenu();
            }
            CFileUtils.takeUriPermission(this, uri);
            binding.pdfView.resetAnnotationType();
            binding.formToolBar.reset();
            binding.editToolBar.resetStatus();
            binding.signatureToolBar.reset();
            setPreviewMode(CPreviewMode.Viewer);
            screenManager.changeWindowStatus(CPreviewMode.Viewer);
            screenManager.constraintHide(binding.signStatusView);
            binding.pdfView.openPDF(uri, null, () -> {
                binding.editToolBar.setEditMode(false);
            });
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PdfSampleActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        screenManager.bind(binding);
        //Extract PDF files from the Android assets folder
        parseConfiguration();
        initDocument(()->{
            initPDFView();
            initToolBarView();
            initSearchBar();
            initAnnotToolbar();
            initFormToolbar();
            initEditBar();
            initSignatureToolbar();
            applyConfiguration();
            onDoNext();
        });
    }

    private void initDocument(CInitPDFRequestListener initPDFRequestListener) {
        String password = getIntent().getStringExtra(EXTRA_FILE_PASSWORD);
        if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_FILE_PATH))) {
            String path = getIntent().getStringExtra(EXTRA_FILE_PATH);
            binding.pdfView.openPDF(path, password);
            if (initPDFRequestListener != null) {
                initPDFRequestListener.success();
            }
        } else if (getIntent().getData() != null) {
            CFileUtils.takeUriPermission(this, getIntent().getData());
            binding.pdfView.openPDF(getIntent().getData(), password);
            if (initPDFRequestListener != null) {
                initPDFRequestListener.success();
            }
        } else if (getIntent().getClipData() != null &&
                getIntent().getClipData().getItemCount() > 0 &&
                getIntent().getClipData().getItemAt(0) != null) {

            Uri uri = getIntent().getClipData().getItemAt(0).getUri();
            CFileUtils.takeUriPermission(this, uri);
            binding.pdfView.openPDF(uri, password);
            if (initPDFRequestListener != null) {
                initPDFRequestListener.success();
            }
        } else {
            CExtractAssetFileTask.extract(this, QUICK_START_GUIDE, QUICK_START_GUIDE, (filePath) -> {
                        binding.pdfView.openPDF(filePath);
                        runOnUiThread(()->{
                            if (initPDFRequestListener != null) {
                                initPDFRequestListener.success();
                            }
                        });
                    }
            );
        }
    }

    private void initPDFView() {
        binding.pdfView.getCPdfReaderView().setMinScaleEnable(false);
        initAnnotationAttr(binding.pdfView);
        initFormAttr(binding.pdfView);
        registerAnnotHelper(binding.pdfView);
        registerFormHelper(binding.pdfView);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(this, R.color.tools_color_accent_50));
        binding.pdfView.getCPdfReaderView().setFormPreviewPaint(paint);
        binding.pdfView.addOnPDFFocusedTypeChangeListener(type -> {
            if (type != CPDFAnnotation.Type.INK) {
                if (binding.inkCtrlView.getVisibility() == View.VISIBLE) {
                    screenManager.changeWindowStatus(type);
                }
            }
        });
        binding.pdfView.setOnTapMainDocAreaCallback(() -> {
            //Use the CFillScreenManager.class to manage fullscreen switching.
            screenManager.fillScreenChange();
        });
        binding.pdfView.getCPdfReaderView().setPdfAddAnnotCallback((cpdfPageView, cpdfBaseAnnot) -> {
            // Annotation creation completed listener, you can use cpdfBaseAnnot.getAnnotType() to determine the type of the added annotation
            if (cpdfBaseAnnot instanceof CPDFListboxWidgetImpl) {
                // When the ListBox form is created, display an editing dialog for adding list data
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showFormListEditFragment(getSupportFragmentManager(), cpdfBaseAnnot, cpdfPageView, false);
            } else if (cpdfBaseAnnot instanceof CPDFComboboxWidgetImpl) {
                // When the ComboBox form is created, display an editing dialog for adding list data
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showFormComboBoxEditFragment(getSupportFragmentManager(), cpdfBaseAnnot, cpdfPageView, true);
            } else if (cpdfBaseAnnot instanceof CPDFPushbuttonWidgetImpl) {
                // When the PushButton form is created, display a dialog for editing the action method
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.showPushButtonActionDialog(getSupportFragmentManager(), binding.pdfView.getCPdfReaderView(),
                        cpdfBaseAnnot, cpdfPageView);
            }
        });
    }

    private void setPreviewMode(CPreviewMode mode) {
        if (binding.pdfView.getCPdfReaderView() == null) {
            return;
        }
        binding.pdfView.getCPdfReaderView().removeAllAnnotFocus();
        IContextMenuShowListener contextMenuShowListener = binding.pdfView.getCPdfReaderView().getContextMenuShowListener();
        if (contextMenuShowListener != null) {
            contextMenuShowListener.dismissContextMenu();
        }
        screenManager.changeWindowStatus(mode);
        binding.pdfToolBar.selectMode(mode);
        binding.formToolBar.reset();
        binding.signatureToolBar.reset();
        resetContextMenu(binding.pdfView, mode);
        CPDFEditManager editManager = binding.pdfView.getCPdfReaderView().getEditManager();
        if (mode == CPreviewMode.Edit) {
            binding.pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.PDFEDIT);
            if (editManager != null && !editManager.isEditMode()) {
                editManager.enable();
                editManager.beginEdit(CPDFEditPage.LoadTextImage);
            }
            if (!CPermissionUtil.hasStoragePermissions(this)) {
                requestStoragePermissions();
            }
        } else {
            if (editManager != null && editManager.isEditMode()) {
                editManager.endEdit();
            }
            switch (mode) {
                case Viewer:
                case Signature:
                    binding.pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.VIEW);
                    break;
                case Annotation:
                    binding.pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.ANNOT);
                    break;
                case Form:
                    binding.pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.FORM);
                    break;
                default:
                    break;
            }
        }
    }

    private void initToolBarView() {
        binding.pdfToolBar.addMode(CPreviewMode.Annotation);
        binding.pdfToolBar.addMode(CPreviewMode.Edit);
        binding.pdfToolBar.addMode(CPreviewMode.Form);
        binding.pdfToolBar.addMode(CPreviewMode.Signature);
        binding.pdfToolBar.setPreviewModeChangeListener(this::setPreviewMode);
        if (cpdfConfiguration != null) {
            CPDFConfiguration.ToolbarConfig toolbarConfig = cpdfConfiguration.toolbarConfig;
            for (CPDFConfiguration.ToolbarConfig.ToolbarAction androidAvailableAction : toolbarConfig.androidAvailableActions) {
                switch (androidAvailableAction) {
                    case Thumbnail:
                        binding.pdfToolBar.addAction(R.drawable.tools_ic_thumbnail, v -> {
                            showPageEdit(binding.pdfView, false, () -> {
                                if (curEditMode > CPDFEditPage.LoadNone && binding.pdfToolBar.getMode() == CPreviewMode.Edit) {
                                    CPDFEditManager editManager = binding.pdfView.getCPdfReaderView().getEditManager();
                                    if (!editManager.isEditMode()) {
                                        editManager.beginEdit(curEditMode);
                                    }
                                }
                            });
                        });
                        break;
                    case Search:
                        binding.pdfToolBar.addAction(R.drawable.tools_ic_search, v -> {
                            if (binding.pdfView.getCPdfReaderView().getEditManager().isEditMode()) {
                                curEditMode = binding.pdfView.getCPdfReaderView().getLoadType();
                            } else {
                                curEditMode = CPDFEditPage.LoadNone;
                            }
                            binding.pdfView.exitEditMode();
                            binding.pdfToolBar.setVisibility(View.GONE);
                            binding.pdfSearchToolBar.setVisibility(View.VISIBLE);
                            binding.pdfSearchToolBar.showKeyboard();
                        });
                        break;
                    case Bota:
                        binding.pdfToolBar.addAction(R.drawable.tools_ic_bookmark, v -> {
                            binding.pdfView.getCPdfReaderView().removeAllAnnotFocus();
                            binding.pdfView.exitEditMode();
                            ArrayList<CPDFBotaFragmentTabs> tabs = new ArrayList<>();
                            CPDFBotaFragmentTabs annotationTab = new CPDFBotaFragmentTabs(CPDFBOTA.ANNOTATION, getString(R.string.tools_annotations));
                            CPDFBotaFragmentTabs outlineTab = new CPDFBotaFragmentTabs(CPDFBOTA.OUTLINE, getString(R.string.tools_outlines));
                            CPDFBotaFragmentTabs bookmarkTab = new CPDFBotaFragmentTabs(CPDFBOTA.BOOKMARKS, getString(R.string.tools_bookmarks));
                            if (binding.pdfToolBar.getMode() == CPreviewMode.Viewer) {
                                tabs.add(outlineTab);
                                tabs.add(bookmarkTab);
                            } else {
                                tabs.add(outlineTab);
                                tabs.add(bookmarkTab);
                                tabs.add(annotationTab);
                            }
                            CPDFBotaDialogFragment dialogFragment = CPDFBotaDialogFragment.newInstance();
                            dialogFragment.initWithPDFView(binding.pdfView);
                            dialogFragment.setBotaDialogTabs(tabs);
                            dialogFragment.show(getSupportFragmentManager(), "annotationList");
                        });
                        break;

                    case Menu:
                        binding.pdfToolBar.addAction(R.drawable.tools_ic_more, v -> {
                            showToolbarMenuDialog(v);
                        });
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CPermissionUtil.openManageAllFileAppSettings(this);
        } else {
            multiplePermissionResultLauncher.launch(STORAGE_PERMISSIONS, result -> {
                if (CPermissionUtil.hasStoragePermissions(this)) {
                    selectDocument();
                } else {
                    if (!CPermissionUtil.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showPermissionsRequiredDialog();
                    }
                }
            });
        }
    }

    private void initAnnotToolbar() {
        binding.annotationToolBar.initWithPDFView(binding.pdfView);
        binding.annotationToolBar.setFragmentManager(getSupportFragmentManager());
        binding.annotationToolBar.setAnnotationChangeListener(type -> {
            screenManager.changeWindowStatus(type);
            //You are required to grant recording permission when selecting voice notes
            if (type == CAnnotationType.SOUND) {
                if (!hasPermission(Manifest.permission.RECORD_AUDIO)) {
                    permissionResultLauncher.launch(Manifest.permission.RECORD_AUDIO, hasRecordAudioPermission -> {
                        if (!hasRecordAudioPermission) {
                            binding.pdfView.resetAnnotationType();
                            if (!CPermissionUtil.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                                showPermissionsRequiredDialog();
                            }
                        }
                    });
                }
            }
        });
        binding.inkCtrlView.initWithPDFView(binding.pdfView);
        binding.inkCtrlView.setFragmentManager(getSupportFragmentManager());
    }

    private void initFormToolbar() {
        binding.formToolBar.initWithPDFView(binding.pdfView);
        binding.formToolBar.setFragmentManager(getSupportFragmentManager());
    }

    private void initSearchBar() {
        binding.pdfSearchToolBar.initWithPDFView(binding.pdfView);

        binding.pdfSearchToolBar.onSearchQueryResults(list -> {
            CSearchResultDialogFragment searchResultDialog = new CSearchResultDialogFragment();
            searchResultDialog.show(getSupportFragmentManager(), "searchResultDialogFragment");
            searchResultDialog.setSearchTextInfos(list);
            searchResultDialog.setOnClickSearchItemListener(clickItem -> {
                binding.pdfView.getCPdfReaderView().setDisplayPageIndex(clickItem.page);
                binding.pdfView.getCPdfReaderView().getTextSearcher().searchBegin(clickItem.page, clickItem.textRangeIndex);
                searchResultDialog.dismiss();
            });
        });
        binding.pdfSearchToolBar.setExitSearchListener(() -> {
            if (curEditMode > CPDFEditPage.LoadNone) {
                CPDFEditManager editManager = binding.pdfView.getCPdfReaderView().getEditManager();
                if (!editManager.isEditMode()) {
                    editManager.beginEdit(curEditMode);
                }
            }
            binding.pdfToolBar.setVisibility(View.VISIBLE);
            binding.pdfSearchToolBar.setVisibility(View.GONE);
        });
    }

    private void initEditBar() {
        if (binding.pdfView == null || binding.pdfView.getCPdfReaderView() == null) {
            return;
        }
        binding.editToolBar.initWithPDFView(binding.pdfView);
        binding.editToolBar.setEditMode(false);
        binding.editToolBar.setEditPropertyBtnClickListener((view) -> {
            int type = binding.pdfView.getCPdfReaderView().getSelectAreaType();
            CStyleType styleType = CStyleType.UNKNOWN;
            if (type == SELECT_AREA_TEXT) {
                styleType = CStyleType.EDIT_TEXT;
            } else if (type == SELECT_AREA_IMAGE) {
                styleType = CStyleType.EDIT_IMAGE;
            }
            if (styleType != CStyleType.UNKNOWN) {
                CPDFReaderView readerView = binding.pdfView.getCPdfReaderView();
                CPDFContextMenuHelper menuHelper = (CPDFContextMenuHelper) readerView.getContextMenuShowListener();
                if (menuHelper == null || menuHelper.getReaderView() == null) {
                    return;
                }
                CStyleManager styleManager = new CStyleManager(menuHelper.getEditSelection(), menuHelper.getPageView());
                CAnnotStyle annotStyle = styleManager.getStyle(styleType);
                CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(annotStyle);
                styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
                styleManager.setDialogHeightCallback(styleDialogFragment, binding.pdfView.getCPdfReaderView());
                styleDialogFragment.show(getSupportFragmentManager(), "textPropertyDialogFragment");
                menuHelper.dismissContextMenu();
            }
        });
        binding.pdfView.setEndScrollCallback(() -> {
            binding.editToolBar.updateUndoRedo();
        });
    }

    private void initSignatureToolbar() {
        binding.signStatusView.initWithPDFView(binding.pdfView);
        binding.signStatusView.getBtnDetails().setOnClickListener(v -> {
            CPDFCertDigitalSignListDialog signListDialog = CPDFCertDigitalSignListDialog.newInstance();
            signListDialog.initWithPDFView(binding.pdfView);
            signListDialog.setDialogDismissListener(this::verifyDocumentSignStatus);
            signListDialog.show(getSupportFragmentManager(), "signListDialog");
        });
        binding.signatureToolBar.initWithPDFView(binding.pdfView);
        binding.signatureToolBar.getVerifySignButton().setOnClickListener(v -> {
            verifyDocumentSignStatus();
        });
    }

    private void parseConfiguration() {
        if (getIntent().hasExtra(EXTRA_CONFIGURATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                cpdfConfiguration = getIntent().getSerializableExtra(EXTRA_CONFIGURATION, CPDFConfiguration.class);
            } else {
                cpdfConfiguration = (CPDFConfiguration) getIntent().getSerializableExtra(EXTRA_CONFIGURATION);
            }
        } else {
            cpdfConfiguration = CPDFConfigurationUtils.normalConfig();
        }
    }

    private void applyConfiguration() {
        if (cpdfConfiguration.readerViewConfig != null) {
            CPDFConfiguration.ReaderViewConfig readerViewConfig = cpdfConfiguration.readerViewConfig;
            binding.pdfView.getCPdfReaderView().setLinkHighlight(readerViewConfig.linkHighlight);
            binding.pdfView.getCPdfReaderView().setFormFieldHighlight(readerViewConfig.formFieldHighlight);
        }
        if (cpdfConfiguration.modeConfig != null) {
            CPDFConfiguration.ModeConfig modeConfig = cpdfConfiguration.modeConfig;
            if (modeConfig.initialViewMode != CPreviewMode.PageEdit) {
                setPreviewMode(modeConfig.initialViewMode);
                if (modeConfig.initialViewMode == CPreviewMode.Edit) {
                    binding.editToolBar.setEditMode(true);
                }
            } else {
                setPreviewMode(CPreviewMode.Viewer);
                showPageEdit(binding.pdfView, true, () -> {
                    if (curEditMode > CPDFEditPage.LoadNone && binding.pdfToolBar.getMode() == CPreviewMode.Edit) {
                        CPDFEditManager editManager = binding.pdfView.getCPdfReaderView().getEditManager();
                        if (!editManager.isEditMode()) {
                            editManager.beginEdit(curEditMode);
                        }
                    }
                });
            }
        }
    }

    private void showToolbarMenuDialog(View anchorView) {
        //Show the PDF settings dialog fragment
        CPopupMenuWindow menuWindow = new CPopupMenuWindow(this);
        if (cpdfConfiguration != null && cpdfConfiguration.toolbarConfig != null) {
            List<CPDFConfiguration.ToolbarConfig.MenuAction> menuActions = cpdfConfiguration.toolbarConfig.availableMenus;
            if (menuActions == null || menuActions.size() == 0) {
                return;
            }
            anchorView.setSelected(true);
            for (CPDFConfiguration.ToolbarConfig.MenuAction menuAction : menuActions) {
                switch (menuAction) {
                    case ViewSettings:
                        menuWindow.addItem(R.drawable.tools_ic_preview_settings, R.string.tools_view_setting, v1 -> {
                            showDisplaySettings(binding.pdfView);
                        });
                        break;
                    case DocumentEditor:
                        menuWindow.addItem(R.drawable.tools_page_edit, R.string.tools_page_edit_toolbar_title, v1 -> {
                            showPageEdit(binding.pdfView, true, () -> {
                                if (curEditMode > CPDFEditPage.LoadNone && binding.pdfToolBar.getMode() == CPreviewMode.Edit) {
                                    CPDFEditManager editManager = binding.pdfView.getCPdfReaderView().getEditManager();
                                    if (!editManager.isEditMode()) {
                                        editManager.beginEdit(curEditMode);
                                    }
                                }
                            });
                        });
                        break;
                    case Security:
                        menuWindow.addItem(R.drawable.tools_ic_add_security, R.string.tools_security, v1 -> {
                            CPDFDocument document = binding.pdfView.getCPdfReaderView().getPDFDocument();
                            if (document == null) {
                                return;
                            }
                            if (document.getPermissions() == CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsUser) {
                                CInputOwnerPwdDialog inputOwnerPwdDialog = CInputOwnerPwdDialog.newInstance();
                                inputOwnerPwdDialog.setDocument(document);
                                inputOwnerPwdDialog.setCancelClickListener(v2 -> {
                                    inputOwnerPwdDialog.dismiss();
                                });
                                inputOwnerPwdDialog.setConfirmClickListener(ownerPassword -> {
                                    document.reload(ownerPassword);
                                    showSettingEncryptionDialog();
                                    inputOwnerPwdDialog.dismiss();
                                });
                                inputOwnerPwdDialog.show(getSupportFragmentManager(), "inputPasswordDialog");
                                return;
                            }
                            showSettingEncryptionDialog();
                        });
                        break;
                    case Watermark:
                        menuWindow.addItem(R.drawable.tools_ic_add_watermark, R.string.tools_watermark, v1 -> {
                            CWatermarkEditDialog watermarkEditDialog = CWatermarkEditDialog.newInstance();
                            watermarkEditDialog.setDocument(binding.pdfView.getCPdfReaderView().getPDFDocument());
                            watermarkEditDialog.setPageIndex(binding.pdfView.currentPageIndex);
                            watermarkEditDialog.setCompleteListener((pdfFile) -> {
                                watermarkEditDialog.dismiss();
                                if (TextUtils.isEmpty(pdfFile)){
                                    CToastUtil.showLongToast(this, R.string.tools_watermark_add_failed);
                                    return;
                                }
                                binding.pdfView.openPDF(pdfFile);
                                CToastUtil.showLongToast(this, R.string.tools_watermark_add_success);
                            });
                            watermarkEditDialog.show(getSupportFragmentManager(), "watermarkEditDialog");
                        });
                        break;
                    case DocumentInfo:
                        menuWindow.addItem(R.drawable.tools_ic_document_info, R.string.tools_document_info, v1 -> {
                            showDocumentInfo(binding.pdfView);
                        });
                        break;
                    case Save:
                        menuWindow.addItem(R.drawable.tools_ic_menu_save, R.string.tools_save, v1 -> {
                            binding.pdfView.savePDF((filePath, pdfUri) -> {
                                CToastUtil.showLongToast(this, R.string.tools_save_success);
                            }, e -> {

                            });
                        });
                        break;
                    case Share:
                        menuWindow.addItem(R.drawable.tools_ic_share, R.string.tools_share, v1 -> {
                            binding.editToolBar.resetStatus();
                            sharePDF(binding.pdfView);
                        });
                        break;
                    case OpenDocument:
                        menuWindow.addItem(R.drawable.tools_ic_new_file, R.string.tools_open_document, v1 -> {
                            if (CPermissionUtil.hasStoragePermissions(this)) {
                                selectDocument();
                            } else {
                                requestStoragePermissions();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        }
        menuWindow.setOnDismissListener(() -> anchorView.setSelected(false));
        menuWindow.showAsDropDown(anchorView);
    }

    /**
     *
     */
    private void verifyDocumentSignStatus() {
        CPDFDocument document = binding.pdfView.getCPdfReaderView().getPDFDocument();
        if (CertificateDigitalDatas.hasDigitalSignature(document)) {
            CThreadPoolUtils.getInstance().executeIO(() -> {
                CPDFDocumentSignInfo status = CertificateDigitalDatas.verifyDocumentSignStatus(document);
                runOnUiThread(() -> {
                    if (status != null) {
                        binding.signStatusView.setStatus(status);
                        screenManager.fillScreenManager.bindTopToolView(binding.signStatusView);
                        screenManager.constraintShow(binding.signStatusView);
                    }
                });
            });
        } else {
            if (binding.signStatusView.getVisibility() == View.VISIBLE) {
                screenManager.fillScreenManager.removeAndHideToolView(binding.signStatusView);
                screenManager.constraintHide(binding.signStatusView);
            }
        }
    }

    private void selectDocument() {
        if (binding.pdfToolBar.getMode() == CPreviewMode.Edit) {
            binding.pdfView.exitEditMode();
        }

        if (binding.pdfView.getCPdfReaderView().getPDFDocument() == null) {
            selectDocumentLauncher.launch(null);
            return;
        }
        if (!binding.pdfView.getCPdfReaderView().getPDFDocument().hasChanges()) {
            selectDocumentLauncher.launch(null);
            return;
        }
        CAlertDialog alertDialog = CAlertDialog.newInstance(getString(R.string.tools_save_title), getString(R.string.tools_save_message));
        alertDialog.setConfirmClickListener(v -> {
            //save pdf document
            binding.pdfView.savePDF((filePath, pdfUri) -> {
                alertDialog.dismiss();
                selectDocumentLauncher.launch(null);
            }, e -> {
                alertDialog.dismiss();
                selectDocumentLauncher.launch(null);
            });
        });
        alertDialog.setCancelClickListener(v -> {
            alertDialog.dismiss();
            selectDocumentLauncher.launch(null);
        });
        alertDialog.show(getSupportFragmentManager(), "alertDialog");
    }

    private void onDoNext() {
        multiplePermissionResultLauncher.launch(STORAGE_PERMISSIONS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReplaceEditImageArea) {
            if (binding.pdfView == null || binding.pdfView.getCPdfReaderView() == null) {
                return;
            }
            for (int i = 0; i < binding.pdfView.getCPdfReaderView().getChildCount(); i++) {
                CPDFPageView pageView = (CPDFPageView) binding.pdfView.getCPdfReaderView().getChildAt(i);
                if (pageView == null) {
                    continue;
                }
                if (data == null) {
                    return;
                }
                if (pageView.getPageNum() == binding.pdfView.getCPdfReaderView().getPageNum()) {
                    boolean ret = pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.REPLACE, data.getData());
                    if (ret == false) {
                        Toast.makeText(getApplicationContext(), "replace fail", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
        } else if (requestCode == AddEditImageArea) {
            if (binding.pdfView == null || binding.pdfView.getCPdfReaderView() == null) {
                return;
            }
            if (data == null) {
                return;
            }
            for (int i = 0; i < binding.pdfView.getCPdfReaderView().getChildCount(); i++) {
                CPDFPageView pageView = (CPDFPageView) binding.pdfView.getCPdfReaderView().getChildAt(i);
                if (pageView == null) {
                    continue;
                }
                if (pageView.getPageNum() == binding.pdfView.getCPdfReaderView().getAddImagePage()) {
                    boolean ret = pageView.addEditImageArea(binding.pdfView.getCPdfReaderView().getAddImagePoint(), data.getData());
                    if (ret == false) {
                        Toast.makeText(getApplicationContext(), "add fail", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
        }
    }

    private void showSettingEncryptionDialog() {
        CDocumentEncryptionDialog documentEncryptionDialog = CDocumentEncryptionDialog.newInstance();
        documentEncryptionDialog.setDocument(binding.pdfView.getCPdfReaderView().getPDFDocument());
        documentEncryptionDialog.setEncryptionResultListener((isRemoveSecurity, result, filePath, passowrd) -> {
            binding.pdfView.openPDF(filePath);
            documentEncryptionDialog.dismiss();
            int msgResId;
            if (isRemoveSecurity) {
                msgResId = result ? R.string.tools_password_remove_success : R.string.tools_password_remove_fail;
            } else {
                msgResId = result ? R.string.tools_set_password_successfully : R.string.tools_set_password_failures;
            }
            CToastUtil.showLongToast(this, msgResId);
        });
        documentEncryptionDialog.show(getSupportFragmentManager(), "documentEncryption");
    }

    @Override
    protected void registerFormHelper(CPDFViewCtrl pdfView) {
        super.registerFormHelper(pdfView);
        pdfView.getCPdfReaderView().getAnnotImplRegistry()
                // Register the CustomSignatureWidgetImpl.class to implement a custom dropdown options popup.
                .registImpl(CPDFSignatureWidget.class, CSignatureWidgetImpl.class);
    }

    @Override
    public void onBackPressed() {
        if (binding.pdfView != null) {
            binding.pdfView.savePDF((filePath, pdfUri) -> super.onBackPressed(), e -> super.onBackPressed());
        } else {
            super.onBackPressed();
        }
    }

    public static class CSignatureWidgetImpl extends SignatureWidgetImpl {
        @Override
        protected CPreviewMode getCurrentMode() {
            if (readerView.getContext() instanceof MainActivity) {
                return ((MainActivity) readerView.getContext()).binding.pdfToolBar.getMode();
            } else {
                return CPreviewMode.Viewer;
            }
        }
    }

    interface CInitPDFRequestListener {
        void success();
    }
}