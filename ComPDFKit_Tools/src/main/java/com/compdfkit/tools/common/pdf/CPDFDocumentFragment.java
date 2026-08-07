/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationbar.CAnnotationToolbar;
import com.compdfkit.tools.common.basic.fragment.CBasicPDFFragment;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.CPDFWatermarkConfig;
import com.compdfkit.tools.common.pdf.controller.CBOTAController;
import com.compdfkit.tools.common.pdf.controller.CDocumentIOController;
import com.compdfkit.tools.common.pdf.controller.CPDFDocumentController;
import com.compdfkit.tools.common.pdf.controller.CPreviewModeController;
import com.compdfkit.tools.common.pdf.controller.CSearchController;
import com.compdfkit.tools.common.pdf.controller.CSecurityController;
import com.compdfkit.tools.common.pdf.controller.CSignStatusController;
import com.compdfkit.tools.common.pdf.controller.CSnippetController;
import com.compdfkit.tools.common.pdf.controller.CToolbarController;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultLauncher;
import com.compdfkit.tools.common.utils.activitycontracts.CSelectPDFDocumentResultContract;
import com.compdfkit.tools.common.utils.dialog.CExitTipsDialog;
import com.compdfkit.tools.common.utils.glide.CPDFGlideInitializer;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.compdfkit.tools.common.views.CPDFToolBar;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.contenteditor.CEditToolbar;
import com.compdfkit.tools.forms.pdfformbar.CFormToolbar;
import com.compdfkit.tools.signature.CSignatureToolBar;
import com.compdfkit.tools.signature.verify.CVerifySignStatusView;
import com.compdfkit.tools.viewer.pdfsearch.CSearchReplaceToolbar;
import com.compdfkit.ui.reader.CPDFAddAnnotCallback;
import com.compdfkit.ui.reader.CPDFReaderView;

/**
 * Main PDF document fragment.
 *
 * <p>This class is a <b>facade</b>: it assembles the PDF viewing experience by
 * delegating to a set of single-responsibility controllers under
 * {@code com.compdfkit.tools.common.pdf.controller}. The fragment itself handles
 * lifecycle, back-press, public field exposure (required by Flutter/RN SDKs),
 * and delegates all business logic.
 *
 * <p>Public method signatures are part of the cross-platform SDK contract
 * (Flutter {@code ViewerPreviewOps}/{@code ViewerUtilityOps}, React Native
 * {@code RnPdfView}) and must not change.
 */
public class CPDFDocumentFragment extends CBasicPDFFragment {

    private static final String TAG = "CPDFDocumentFragment";

    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_FILE_URI = "file_uri";
    public static final String EXTRA_FILE_PASSWORD = "file_password";
    public static final String EXTRA_CONFIGURATION = "extra_configuration";
    public static final String EXTRA_PAGE_INDEX = "extra_page_index";

    public CSampleScreenManager screenManager = new CSampleScreenManager();

    public ConstraintLayout clRoot;
    public CPDFViewCtrl pdfView;
    public FrameLayout flTool;
    public CPDFToolBar pdfToolBar;
    public CSearchReplaceToolbar pdfSearchToolBarView;
    public CVerifySignStatusView signStatusView;
    public FrameLayout flBottomToolBar;
    public CAnnotationToolbar annotationToolbar;
    public CEditToolbar editToolBar;
    public CFormToolbar formToolBar;
    public CSignatureToolBar signatureToolBar;

    // Fields accessed by controllers (package-visible across sub-packages via public)
    public View blockView;
    public AppCompatImageView ivTouchBrowse;
    public CardView cardTouchBrowse;
    public OnBackPressedCallback onBackPressedCallback;
    public CPopupMenuWindow menuWindow;
    public CPDFAddAnnotCallback addAnnotCallback;
    public CFillScreenChangeListener fillScreenChangeListener;

    private CPDFDocumentFragmentInitListener initListener;

    private final ActivityResultLauncher<Void> selectDocumentLauncher = registerForActivityResult(
            new CSelectPDFDocumentResultContract(), uri -> {
                if (uri != null) {
                    handleDocumentSelected(uri);
                }
            });

    private final CImageResultLauncher imageResultLauncher = new CImageResultLauncher(this);

    // === Controllers ===
    private CPDFDocumentController documentContext;
    private CPreviewModeController previewModeController;
    private CToolbarController toolbarController;
    private CDocumentIOController documentIOController;
    private CSearchController searchController;
    private CBOTAController botaController;
    private CSecurityController securityController;
    private CSignStatusController signStatusController;
    private CSnippetController snippetController;

    public static CPDFDocumentFragment newInstance(String filePath, String password, CPDFConfiguration configuration) {
        Bundle args = new Bundle();
        args.putString(EXTRA_FILE_PATH, filePath);
        args.putString(EXTRA_FILE_PASSWORD, password);
        args.putSerializable(EXTRA_CONFIGURATION, configuration);
        CPDFDocumentFragment fragment = new CPDFDocumentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CPDFDocumentFragment newInstance(Uri uri, String password, CPDFConfiguration configuration) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FILE_URI, uri);
        args.putString(EXTRA_FILE_PASSWORD, password);
        args.putSerializable(EXTRA_CONFIGURATION, configuration);
        CPDFDocumentFragment fragment = new CPDFDocumentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CPDFDocumentFragment newInstance(Bundle bundle) {
        CPDFDocumentFragment fragment = new CPDFDocumentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (pdfSearchToolBarView.getVisibility() == VISIBLE) {
                    pdfSearchToolBarView.exitSearch();
                    onBackPressedCallback.setEnabled(false);
                    return;
                }
               if (pdfView.getCPdfReaderView().getTouchMode() == CPDFReaderView.TouchMode.SCREENSHOT) {
                   onBackPressedCallback.setEnabled(false);
                   if (snippetController != null) {
                       snippetController.exitSnippetMode();
                   }
                   return;
               }
                pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
                pdfView.getCPdfReaderView().pauseAllRenderProcess();
                pdfView.getCPdfReaderView().removeAllAnnotFocus();

                CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
                boolean hasChanges = document != null && document.hasChanges();
                boolean enableExitSaveTips = cpdfConfiguration.globalConfig.enableExitSaveTips;

               if (!hasChanges) {
                   onBackPressedCallback.setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                   return;
               }
              if (!enableExitSaveTips) {
                  onBackPressedCallback.setEnabled(false);
                  if (documentIOController != null) {
                      documentIOController.saveAndExit();
                  } else {
                       requireActivity().getOnBackPressedDispatcher().onBackPressed();
                  }
                  return;
              }
               CExitTipsDialog exitTipsDialog = CExitTipsDialog.newInstance();
               exitTipsDialog.setCancelClickListener(v -> {
                   onBackPressedCallback.setEnabled(false);
                   exitTipsDialog.dismiss();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
               });
               exitTipsDialog.setCancelable(false);
              exitTipsDialog.setConfirmClickListener(v -> {
                  exitTipsDialog.dismiss();
                  if (documentIOController != null) {
                      documentIOController.saveAndExit();
                  } else {
                       requireActivity().getOnBackPressedDispatcher().onBackPressed();
                  }
              });
                exitTipsDialog.setContinueClickListener(v -> {
                    onBackPressedCallback.setEnabled(true);
                    exitTipsDialog.dismiss();
                });
                exitTipsDialog.show(getChildFragmentManager(), "exitTipsDialog");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parseConfiguration();
        int themeId = CPDFApplyConfigUtil.getInstance().getGlobalThemeId(getContext(), cpdfConfiguration);
        Context wrapper = new ContextThemeWrapper(getContext(), themeId);
        LayoutInflater themedInflater = inflater.cloneInContext(wrapper);
        View rootView = themedInflater.inflate(R.layout.tools_pdf_document_fragment, container, false);
        CPDFGlideInitializer.register(getContext());
        clRoot = rootView.findViewById(R.id.cl_root);
        pdfView = rootView.findViewById(R.id.pdf_view);
        flTool = rootView.findViewById(R.id.fl_tool);
        pdfToolBar = rootView.findViewById(R.id.pdf_tool_bar);
        pdfSearchToolBarView = rootView.findViewById(R.id.search_toolbar_view);
        signStatusView = rootView.findViewById(R.id.sign_status_view);
        flBottomToolBar = rootView.findViewById(R.id.fl_bottom_tool_bar);
        annotationToolbar = rootView.findViewById(R.id.annotation_tool_bar);
        editToolBar = rootView.findViewById(R.id.edit_tool_bar);
        formToolBar = rootView.findViewById(R.id.form_tool_bar);
        signatureToolBar = rootView.findViewById(R.id.signature_tool_bar);
        blockView = rootView.findViewById(R.id.block_view);
        ivTouchBrowse = rootView.findViewById(R.id.iv_touch_browse);
        cardTouchBrowse = rootView.findViewById(R.id.card_ink_touch_browse);
        CPDFApplyConfigUtil.getInstance().appleUiConfig(this, cpdfConfiguration);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControllers();
        screenManager.bind(this);
        pdfView.setCPDFConfiguration(cpdfConfiguration);
        documentIOController.initDocument(() -> {
            // Opening a document completes asynchronously on the main thread. The fragment
            // can be detached or have its view destroyed before this callback is delivered.
            if (!isAdded() || getView() == null || getContext() == null) {
                return;
            }
            toolbarController.initAll();
            searchController.init();
            applyConfiguration();
            if (initListener != null) {
                initListener.onInit(pdfView);
            }
        });
    }

    private void initControllers() {
        documentContext = new CPDFDocumentController(this, pdfView, cpdfConfiguration, screenManager);
        previewModeController = new CPreviewModeController(documentContext);
        signStatusController = new CSignStatusController(documentContext);
        toolbarController = new CToolbarController(documentContext, previewModeController, signStatusController, imageResultLauncher);
        documentIOController = new CDocumentIOController(documentContext, previewModeController, selectDocumentLauncher);
        searchController = new CSearchController(documentContext, previewModeController);
        botaController = new CBOTAController(documentContext, previewModeController);
        securityController = new CSecurityController(documentContext, documentIOController);
        snippetController = new CSnippetController(documentContext);
    }

    private void handleDocumentSelected(Uri uri) {
        if (documentIOController != null) {
            documentIOController.onDocumentSelected(uri);
        }
    }

    protected void parseConfiguration() {
        if (getArguments() != null && getArguments().containsKey(EXTRA_CONFIGURATION)) {
            if (Build.VERSION.SDK_INT >= CPermissionUtil.VERSION_TIRAMISU) {
                cpdfConfiguration = getArguments().getSerializable(EXTRA_CONFIGURATION, CPDFConfiguration.class);
            } else {
                cpdfConfiguration = (CPDFConfiguration) getArguments().getSerializable(EXTRA_CONFIGURATION);
            }
        }
        if (cpdfConfiguration == null) {
            cpdfConfiguration = CPDFConfigurationUtils.normalConfig(getContext(), "tools_default_configuration.json");
        }
    }

    protected void applyConfiguration() {
        CPDFApplyConfigUtil.getInstance().applyConfiguration(this, cpdfConfiguration);
        CPDFApplyConfigUtil.getInstance().applyModeConfig(this, cpdfConfiguration);
        CPDFApplyConfigUtil.getInstance().applyReaderViewUiStyle(pdfView.getCPdfReaderView(), cpdfConfiguration.readerViewConfig.uiStyle);
    }

    @Override
   public void onConfigurationChanged(@NonNull Configuration newConfig) {
       super.onConfigurationChanged(newConfig);
        // Both landscape and portrait refresh the search-replace context menu the same way.
       pdfSearchToolBarView.showSearchReplaceContextMenu();
   }

    // ========================================================================
    // Public API — delegates to controllers.
    // Signatures are part of the cross-platform SDK contract (Flutter / RN).
    // ========================================================================

    public void setPreviewMode(CPreviewMode mode) {
        previewModeController.setPreviewMode(mode);
    }

    public void showBOTA() {
        botaController.showBOTA();
    }

    public void showSecurityDialog() {
        securityController.showSecurityDialog();
    }

    public void showAddWatermarkDialog() {
        securityController.showAddWatermarkDialog();
    }

    public void showAddWatermarkDialog(CPDFWatermarkConfig watermarkConfig) {
        securityController.showAddWatermarkDialog(watermarkConfig);
    }

    public void showFlattenedDialog() {
        securityController.showFlattenedDialog();
    }

    public void showPageEdit(boolean enterEditMode, boolean enableEditMode) {
        botaController.showPageEdit(enterEditMode, enableEditMode);
    }

    public void selectDocument() {
        documentIOController.selectDocument();
    }

    public void enterSnipMode() {
        snippetController.enterSnippetMode();
    }

    public void exitScreenShot() {
        snippetController.exitSnippetMode();
    }

    public void exitSnipMode() {
        snippetController.exitSnippetMode();
    }

    public void showTextSearchView() {
        searchController.showTextSearchView();
    }

    public void hideTextSearchView() {
        searchController.hideTextSearchView();
    }

    public void verifyDocumentSignStatus() {
        signStatusController.verify();
    }

    public void hideDigitalSignStatusView() {
        signStatusController.hide();
    }

    public void showToolbarMenuDialog(View anchorView) {
        toolbarController.showMenuDialog(anchorView);
    }

    public void showDisplaySettings(CPDFViewCtrl pdfView) {
        botaController.showDisplaySettings(pdfView);
    }

    public void showDocumentInfo(CPDFViewCtrl pdfView) {
        botaController.showDocumentInfo(pdfView);
    }

    public void sharePDF(CPDFViewCtrl pdfView) {
        documentIOController.sharePDF(pdfView);
    }

    // ========================================================================
    // Lifecycle cleanup
    // ========================================================================

    @Override
    public void onDestroy() {
        try {
            CLog.e(TAG, "onDestroy() document close()");
            CViewUtils.hideKeyboard(getActivity().getWindow().getDecorView());
            onBackPressedCallback = null;
            initListener = null;
            fillScreenChangeListener = null;
            addAnnotCallback = null;
            dismissLoadingDialog();
            if (annotationToolbar != null) {
                annotationToolbar.release();
            }
            pdfView.close();
            if (menuWindow != null) {
                menuWindow.dismiss();
            }
        } catch (Exception e) {
            CLog.e(TAG, "onDestroy cleanup failed: " + e.getMessage());
        }
        super.onDestroy();
    }

    // ========================================================================
    // Setters / listeners
    // ========================================================================

    public void setInitListener(CPDFDocumentFragmentInitListener initListener) {
        this.initListener = initListener;
    }

    public void setFillScreenChangeListener(CFillScreenChangeListener fillScreenChangeListener) {
        this.fillScreenChangeListener = fillScreenChangeListener;
    }

    public void setAddAnnotCallback(CPDFAddAnnotCallback addAnnotCallback) {
        this.addAnnotCallback = addAnnotCallback;
    }

    public interface CPDFDocumentFragmentInitListener {
        void onInit(CPDFViewCtrl pdfView);
    }

    public interface CFillScreenChangeListener {
        void fillScreenChange(boolean fillScreen);
    }
}
