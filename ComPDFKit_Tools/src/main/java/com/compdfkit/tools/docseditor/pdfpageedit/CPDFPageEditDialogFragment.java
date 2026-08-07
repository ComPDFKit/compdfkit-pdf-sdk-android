/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.docseditor.pdfpageedit;

import static com.compdfkit.core.document.CPDFDocument.PDFDocumentError.PDFDocumentErrorPassword;
import static com.compdfkit.core.document.CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.CPDFThumbnailConfig;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.glide.CPDFThumbnailCacheRevisionManager;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.docseditor.CPageEditBar;
import com.compdfkit.tools.docseditor.CPageEditToolBar;
import com.compdfkit.tools.docseditor.pdfpageeditinsert.CInsertBlankPageDialogFragment;
import com.compdfkit.tools.docseditor.pdfpageeditinsert.CInsertPdfPageDialogFragment;
import com.compdfkit.tools.docseditor.pdfpageeditinsert.CSelectInsertPageTypeDialogFragment;
import com.compdfkit.tools.viewer.pdfthumbnail.CPDFEditThumbnailFragment;
import com.compdfkit.tools.viewer.pdfthumbnail.adpater.CPDFEditThumbnailListAdapter;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CPDFPageEditDialogFragment extends CBasicBottomSheetDialogFragment {

    private static final String TAG = "CPDFPageEdit";

    private CPageEditBar toolBar;

    private CPDFViewCtrl pdfView;

    private CPageEditToolBar editToolBar;

    private OnBackListener onBackListener = null;

    private COnEnterBackPressedListener onEnterBackPressedListener;

    private CPDFEditThumbnailFragment editThumbnailFragment;

    private boolean hasEdit = false;

    private boolean enterEdit = false;
    private boolean enableEditMode = true;
    private List<Integer> refreshHQApList = new ArrayList<>();
    private volatile boolean pageOperationRunning = false;
    private int[] pendingReplacePages = null;
    private final CPDFPageEditOperationManager pageEditOperationManager = new CPDFPageEditOperationManager();

    private ActivityResultLauncher<Intent> replaceSelectDocumentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null && result.getData().getData() != null) {
            Uri selectUri = result.getData().getData();
            CFileUtils.takeUriPermission(getContext(), selectUri);
            CPDFDocument selectDocument = new CPDFDocument(getContext());
            CPDFDocument.PDFDocumentError pdfDocumentError = selectDocument.open(selectUri);
            if (pdfDocumentError == PDFDocumentErrorSuccess) {
                int[] replacePages = pendingReplacePages;
                CThreadPoolUtils.getInstance().executeIO(() -> replacePage(selectDocument, replacePages));
            } else if (pdfDocumentError == PDFDocumentErrorPassword) {
                CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
                verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(selectDocument, selectUri);
                int[] replacePages = pendingReplacePages;
                verifyPasswordDialogFragment.setVerifyCompleteListener(document ->
                        CThreadPoolUtils.getInstance().executeIO(() -> replacePage(selectDocument, replacePages)));
                verifyPasswordDialogFragment.show(getChildFragmentManager(), "verifyPwdDialog");
            }
        }
    });


    private ActivityResultLauncher<Intent> insertDocumentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null && result.getData().getData() != null) {
            Uri fileUri = result.getData().getData();
            CFileUtils.takeUriPermission(getContext(), fileUri);
            CPDFDocument tempDocument = new CPDFDocument(getContext());
            CPDFDocument.PDFDocumentError pdfDocumentError = tempDocument.open(fileUri);
            if (pdfDocumentError == PDFDocumentErrorSuccess) {
                showInsertPDFPageDialog(tempDocument);
            } else if (pdfDocumentError == PDFDocumentErrorPassword) {
                CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
                verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(tempDocument, fileUri);
                verifyPasswordDialogFragment.setVerifyCompleteListener(document -> showInsertPDFPageDialog(tempDocument));
                verifyPasswordDialogFragment.show(getChildFragmentManager(), "verifyPwdDialog");
            }
        }
    });

    public static CPDFPageEditDialogFragment newInstance() {
        return new CPDFPageEditDialogFragment();
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    public void setEnterEdit(boolean enterEdit) {
        this.enterEdit = enterEdit;
    }

    public void setEnableEditMode(boolean enableEditMode) {
        this.enableEditMode = enableEditMode;
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
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (onBackListener != null) {
                        onBackListener.onBack();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    return handleBackPressed();
                }
                return false;
            });
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_page_edit_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        toolBar = rootView.findViewById(R.id.tool_bar);
        editToolBar = rootView.findViewById(R.id.tool_page_edit_bar);
        try {
            CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
            if (configuration != null){
                CPDFThumbnailConfig thumbnailConfig = configuration.globalConfig.thumbnail;
                if (!TextUtils.isEmpty(thumbnailConfig.backgroundColor)){
                    int bgColor = Color.parseColor(thumbnailConfig.backgroundColor);
                    rootView.setBackgroundColor(bgColor);
                }
                if (!TextUtils.isEmpty(thumbnailConfig.title)) {
                    toolBar.setTitle(thumbnailConfig.title);
                }
            }
        } catch (Exception ignored){

        }
        editThumbnailFragment = (CPDFEditThumbnailFragment) getChildFragmentManager().findFragmentById(R.id.id_edit_thumbnail_fragment);
        if (editThumbnailFragment != null) {
            editThumbnailFragment.setCPDFPageEditDialogFragment(this);
            editThumbnailFragment.setEdit(enableEditMode && enterEdit);
            editThumbnailFragment.setEnableEditMode(enableEditMode);
            editThumbnailFragment.setPDFDisplayPageIndexListener(pageIndex -> {
                if (pdfView != null) {
                    pdfView.currentPageIndex = pageIndex;
                    pdfView.getCPdfReaderView().setDisplayPageIndex(pageIndex);
                }
            });
            editThumbnailFragment.initWithPDFView(pdfView);
        }

        refreshHQApList.clear();

        editToolBar.initWithPDFView(pdfView);
        toolBar.setBackBtnClickListener(v -> {
            handleBackPressed();
        });
        toolBar.setOnDoneClickCallback(() -> {
            if (editThumbnailFragment.isEdit()) {
                exitEditMode();
            }
        });
        toolBar.setOnEnableEditPageCallback((enable) -> {
            if (enable) {
                editToolBar.setVisibility(View.VISIBLE);
                editThumbnailFragment.setEdit(true);
            }
        });
        toolBar.setOnSelectAllCallback((select) -> editThumbnailFragment.setSelectAll(select));
        toolBar.showEditButton(enableEditMode);
        if (enableEditMode && enterEdit) {
            toolBar.enterEditMode();
        }
        editToolBar.setInsertPageListener(v -> {
            if (isPageStructureEditing()) {
                return;
            }
            insertPage();
        });
        editToolBar.setReplacePageListener(view -> {
            if (isPageStructureEditing()) {
                return;
            }
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (!hasSelectedPages(pages)) {
                return;
            }
            pendingReplacePages = snapshotSelectedPages(pages);
            replaceSelectDocumentLauncher.launch(CFileUtils.getContentIntent());
        });
        editToolBar.setExtractPageListener(view -> {
            if (isPageStructureEditing()) {
                return;
            }
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (!hasSelectedPages(pages)) {
                return;
            }
            int[] selectedPages = snapshotSelectedPages(pages);
            setPageOperationRunning(true);
            editThumbnailFragment.setRecyclerViewTouchable(false);
            CThreadPoolUtils.getInstance().executeIO(() -> {
                String dir = Environment.DIRECTORY_DOWNLOADS + File.separator + CFileUtils.EXTRACT_FOLDER;
                Uri extractPDFUri = extractPages(dir, selectedPages);
                String fileName = CUriUtil.getUriFileName(getContext(), extractPDFUri);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (extractPDFUri != null) {
                            CFileUtils.shareFile(requireContext(), getString(R.string.tools_share_to), "application/pdf", extractPDFUri);
                        }
                        String msg = extractPDFUri == null ? getString(R.string.tools_page_edit_extract_fail) : (getString(R.string.tools_page_edit_extract_ok) + " : " + dir + File.separator + fileName);
                        CToastUtil.showToast(getContext(), msg);
                        finishPageOperation();
                    });
                } else {
                    finishPageOperationOnMain();
                }
            });
        });
        editToolBar.setCopyPageListener(view -> {
            if (isPageStructureEditing()) {
                return;
            }
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (!hasSelectedPages(pages)) {
                return;
            }
            int[] selectedPages = snapshotSelectedPages(pages);
            setPageOperationRunning(true);
            editThumbnailFragment.setRecyclerViewTouchable(false);
            CThreadPoolUtils.getInstance().executeIO(() -> copyPages(selectedPages));
        });
        editToolBar.setRotatePageListener(view -> {
            if (isPageStructureEditing()) {
                return;
            }
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (!hasSelectedPages(pages)) {
                return;
            }
            int[] selectedPages = snapshotSelectedPages(pages);
            setPageOperationRunning(true);
            editThumbnailFragment.setRecyclerViewTouchable(false);
            CThreadPoolUtils.getInstance().executeIO(() -> rotatePages(selectedPages));
        });
        editToolBar.setDeletePageListener(view -> {
            if (isPageStructureEditing()) {
                return;
            }
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (!hasSelectedPages(pages) || !checkPdfView()) {
                return;
            }
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            if (pages.size() == document.getPageCount()) {
                CAlertDialog dialog = CAlertDialog.newInstance(getString(R.string.tools_warning),
                        getString(R.string.tools_page_edit_alert_content_allpage));
                dialog.setConfirmClickListener((v) -> dialog.dismiss());
                dialog.show(getChildFragmentManager(), "dialog");
                return;
            }
            int[] selectedPages = snapshotSelectedPages(pages);
            setPageOperationRunning(true);
            editThumbnailFragment.setRecyclerViewTouchable(false);
            CThreadPoolUtils.getInstance().executeIO(() -> deletePages(selectedPages));
        });
    }

    /**
     * Returns true while a page operation is mutating the PDF document.
     */
    private boolean isPageStructureEditing() {
        return pageOperationRunning;
    }

    /**
     * Updates the operation guard used to prevent concurrent page mutations.
     */
    private void setPageOperationRunning(boolean running) {
        pageOperationRunning = running;
    }

    /**
     * Checks whether at least one page is selected and shows a warning when none is selected.
     */
    private boolean hasSelectedPages(SparseIntArray pages) {
        if (pages != null && pages.size() > 0) {
            return true;
        }
        CAlertDialog dialog = CAlertDialog.newInstance(getString(R.string.tools_page_edit_alert_title),
                getString(R.string.tools_page_edit_alert_content_nopage));
        dialog.setConfirmClickListener((v) -> dialog.dismiss());
        dialog.show(getChildFragmentManager(), "dialog");
        return false;
    }

    /**
     * Handles toolbar and system back actions with the same edit-mode behavior.
     */
    private boolean handleBackPressed() {
        if (isPageStructureEditing()) {
            return true;
        }
        if (editThumbnailFragment != null && editThumbnailFragment.isEdit()) {
            exitEditMode();
            return true;
        }
        if (onEnterBackPressedListener != null) {
            onEnterBackPressedListener.onEnterBackPressed();
        }
        dismiss();
        return true;
    }

    /**
     * Leaves edit mode and restores the toolbar to browse mode.
     */
    private void exitEditMode() {
        if (editThumbnailFragment != null) {
            editThumbnailFragment.setEdit(false);
        }
        toolBar.showEditButton(true);
        toolBar.showSelectButton(false);
        toolBar.showDoneButton(false);
        editToolBar.setVisibility(View.GONE);
    }

    /**
     * Displays the insert-page type picker and delegates the selected insert flow.
     */
    private void insertPage() {
        CSelectInsertPageTypeDialogFragment insertDialogFragment = CSelectInsertPageTypeDialogFragment.newInstance();
        insertDialogFragment.setInsertBlankPageClickListener(view -> {
            CInsertBlankPageDialogFragment blankPageDialogFragment = CInsertBlankPageDialogFragment.newInstance();
            blankPageDialogFragment.setDocument(pdfView.getCPdfReaderView().getPDFDocument());
            blankPageDialogFragment.setOnEditDoneCallback(() -> {
                CPDFThumbnailCacheRevisionManager.bumpRevision(pdfView.getCPdfReaderView().getPDFDocument());
                int[] pageNum = new int[1];
                pageNum[0] = blankPageDialogFragment.getInsertPageIndex();
                editThumbnailFragment.setSelectPages(pageNum);
                editThumbnailFragment.scrollToPosition(blankPageDialogFragment.getInsertPageIndex());
                hasEdit = true;
            });
            blankPageDialogFragment.show(getChildFragmentManager(), "blank page");
            insertDialogFragment.dismiss();
        });
        insertDialogFragment.setInsertPdfPageClickListener(view -> {
            insertDocumentLauncher.launch(CFileUtils.getContentIntent());
            insertDialogFragment.dismiss();
        });
        insertDialogFragment.show(getChildFragmentManager(), "insert page");
    }

    /**
     * Shows the PDF insert dialog for a successfully opened source document.
     */
    private void showInsertPDFPageDialog(CPDFDocument document) {
        CInsertPdfPageDialogFragment pdfPageDialogFragment = CInsertPdfPageDialogFragment.newInstance();
        pdfPageDialogFragment.initWithPDFView(pdfView);
        pdfPageDialogFragment.setInsertDocument(document);
        pdfPageDialogFragment.setOnEditDoneCallback(() -> {
            CPDFThumbnailCacheRevisionManager.bumpRevision(pdfView.getCPdfReaderView().getPDFDocument());
            int[] pageNum = pdfPageDialogFragment.getInsertPagesArr();
            if (pageNum != null && pageNum.length > 0) {
                editThumbnailFragment.setSelectPages(pageNum);
                editThumbnailFragment.scrollToPosition(pageNum[0]);
            }
            hasEdit = true;
        });
        pdfPageDialogFragment.show(getChildFragmentManager(), "pdf page");
    }

    /**
     * Replaces selected pages with pages from the selected source document.
     */
    private boolean replacePage(CPDFDocument selectDocument, int[] pageNum) {
        setPageOperationRunning(true);
        CThreadPoolUtils.getInstance().executeMain(() -> {
            if (editThumbnailFragment != null) {
                editThumbnailFragment.setRecyclerViewTouchable(false);
            }
        });
        try {
            if (selectDocument == null || pageNum == null || pageNum.length == 0) {
                return false;
            }
            if (!checkPdfView()) {
                return false;
            }

            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            int[] insertedPages = pageEditOperationManager.replacePages(document, selectDocument, pageNum);
            if (insertedPages == null) {
                return false;
            }

            hasEdit = true;
            if (insertedPages.length > 0) {
                editThumbnailFragment.setSelectPages(insertedPages);
            }
            return true;
        } finally {
            pendingReplacePages = null;
            finishPageOperationOnMain();
        }
    }

    /**
     * Exports selected pages into a new PDF file.
     */
    private Uri extractPages(String publicDirectory, int[] pageNum) {
        if (!checkPdfView()) {
            return null;
        }
        return pageEditOperationManager.extractPages(
                getContext(),
                pdfView.getCPdfReaderView().getPDFDocument(),
                pageNum,
                publicDirectory,
                pdfView.isSaveFileExtraFontSubset());
    }

    /**
     * Creates an immutable snapshot of the selected page indexes.
     */
    private int[] snapshotSelectedPages(SparseIntArray pagesArr) {
        int[] selectedPages = new int[pagesArr.size()];
        for (int i = 0; i < pagesArr.size(); i++) {
            selectedPages[i] = pagesArr.keyAt(i);
        }
        return selectedPages;
    }

    /**
     * Copies selected pages and selects the newly inserted copies.
     */
    private void copyPages(int[] selectedPages) {
        boolean finishOnMain = true;
        try {
            if (!checkPdfView()) {
                return;
            }
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            int[] copiedPages = pageEditOperationManager.copyPages(document, selectedPages);
            if (copiedPages == null) {
                return;
            }

            hasEdit = true;
            editThumbnailFragment.setSelectPages(copiedPages, this::finishPageOperation);
            finishOnMain = false;
        } catch (Exception e) {
            Log.e(TAG, "copyPage failed", e);
        } finally {
            if (finishOnMain) {
                finishPageOperationOnMain();
            }
        }
    }

    /**
     * Restores interaction after a page operation finishes or fails.
     */
    private void finishPageOperation() {
        if (editThumbnailFragment != null) {
            editThumbnailFragment.setRecyclerViewTouchable(true);
        }
        setPageOperationRunning(false);
    }

    /**
     * Restores interaction on the Android main thread.
     */
    private void finishPageOperationOnMain() {
        CThreadPoolUtils.getInstance().executeMain(this::finishPageOperation);
    }

    /**
     * Rotates selected pages and refreshes their thumbnails.
     */
    private boolean rotatePages(int[] pageNum) {
        boolean finishOnMain = true;
        try {
            if (!checkPdfView()) {
                return false;
            }
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            if (pageNum == null || pageNum.length == 0) {
                return false;
            }

            if (!pageEditOperationManager.rotatePages(document, pageNum)) {
                return false;
            }
            for (int page : pageNum) {
                if (!refreshHQApList.contains(page)) {
                    refreshHQApList.add(page);
                }
            }
            hasEdit = true;
            editThumbnailFragment.updatePagesArr(pageNum, CPDFEditThumbnailFragment.UPDATE_TYPE_ROTATE);
            finishPageOperationOnMain();
            finishOnMain = false;
            return true;
        } finally {
            if (finishOnMain) {
                finishPageOperationOnMain();
            }
        }
    }

    /**
     * Deletes selected pages and refreshes the thumbnail list.
     */
    private boolean deletePages(int[] pageNum) {
        boolean finishOnMain = true;
        try {
            if (!checkPdfView()) {
                return false;
            }
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            if (pageNum == null || pageNum.length == 0) {
                return false;
            }
            boolean removed = pageEditOperationManager.deletePages(document, pageNum);
            CThreadPoolUtils.getInstance().executeMain(()->{
                editThumbnailFragment.setSelectAll(false);
                if (removed) {
                    editThumbnailFragment.updatePagesArr(pageNum, CPDFEditThumbnailFragment.UPDATE_TYPE_DELETE);
                }
                hasEdit = removed || hasEdit;
                finishPageOperation();
            });
            finishOnMain = false;
        } catch (Exception e) {
            Log.e(TAG, "deletePage failed", e);
            finishPageOperationOnMain();
            finishOnMain = false;
        } finally {
            if (finishOnMain) {
                finishPageOperationOnMain();
            }
        }
        return true;
    }

    /**
     * Moves a page after a thumbnail drag request has been accepted.
     */
    public void movePage(int sourcePosition, int targetPosition,
                         CPDFEditThumbnailListAdapter.MoveResultCallback callback) {
        if (isPageStructureEditing() || !checkPdfView()) {
            if (callback != null) {
                callback.onResult(false);
            }
            return;
        }
        setPageOperationRunning(true);
        if (editThumbnailFragment != null) {
            editThumbnailFragment.setRecyclerViewTouchable(false);
        }
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        CThreadPoolUtils.getInstance().executeIO(() -> {
            boolean moved = false;
            try {
                moved = pageEditOperationManager.movePage(document, sourcePosition, targetPosition);
                hasEdit = moved || hasEdit;
            } catch (Exception e) {
                Log.e(TAG, "movePage failed", e);
            }
            boolean result = moved;
            CThreadPoolUtils.getInstance().executeMain(() -> {
                finishPageOperation();
                if (callback != null) {
                    callback.onResult(result);
                }
            });
        });
    }

    /**
     * Checks whether the current PDF view and document are available.
     */
    private boolean checkPdfView() {
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return false;
        }
        return pdfView.getCPdfReaderView().getPDFDocument() != null;
    }

    @Override
    protected void onViewCreate() {
        intEditThumbnailFragment();
    }

    private void intEditThumbnailFragment() {
        editThumbnailFragment.initFragment();
    }

    @Override
    public void dismiss() {
        refreshPDFView();
        super.dismiss();
        if (onBackListener != null) {
            onBackListener.onBack();
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        refreshPDFView();
        if (onBackListener != null) {
            onBackListener.onBack();
        }
        if (onEnterBackPressedListener != null) {
            onEnterBackPressedListener.onEnterBackPressed();
        }
    }

    private void refreshPDFView(){
        if (checkPdfView()) {
            CPDFReaderView readerView = pdfView.getCPdfReaderView();
            if (hasEdit) {
                int pageCount = readerView.getPDFDocument().getPageCount();
                int jumpIndex = pdfView.currentPageIndex >= pageCount ? pageCount - 1 : pdfView.currentPageIndex;
                readerView.reloadPages(refreshHQApList);
                pdfView.currentPageIndex = jumpIndex;
                readerView.setDisplayPageIndex(jumpIndex);
                pdfView.refreshSlideBarDocumentState();
                pdfView.indicatorView.setTotalPage(pageCount);
            }
        }
    }

    public void setHasEdit(boolean edit) {
        hasEdit = edit;
    }

    public void setOnBackListener(OnBackListener listener) {
        this.onBackListener = listener;
    }

    public void setOnEnterBackPressedListener(COnEnterBackPressedListener onEnterBackPressedListener) {
        this.onEnterBackPressedListener = onEnterBackPressedListener;
    }

    public interface OnBackListener {
        void onBack();
    }

    public interface COnEnterBackPressedListener {
        void onEnterBackPressed();
    }
}
