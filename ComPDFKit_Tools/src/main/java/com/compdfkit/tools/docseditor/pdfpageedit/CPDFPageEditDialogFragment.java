/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
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
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.CPDFThumbnailConfig;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
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
import com.compdfkit.ui.reader.CPDFReaderView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CPDFPageEditDialogFragment extends CBasicBottomSheetDialogFragment {

    private CPageEditBar toolBar;

    private CPDFViewCtrl pdfView;

    private CPageEditToolBar editToolBar;

    private OnBackLisener onBackLisener = null;

    private COnEnterBackPressedListener onEnterBackPressedListener;

    private CPDFEditThumbnailFragment editThumbnailFragment;

    private boolean hasEdit = false;

    private boolean enterEdit = false;
    private boolean enableEditMode = true;
    private List<Integer> refreshHQApList = new ArrayList<>();

    private ActivityResultLauncher<Intent> replaceSelectDocumentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null && result.getData().getData() != null) {
            Uri selectUri = result.getData().getData();
            CFileUtils.takeUriPermission(getContext(), selectUri);
            CPDFDocument selectDocument = new CPDFDocument(getContext());
            CPDFDocument.PDFDocumentError pdfDocumentError = selectDocument.open(selectUri);
            if (pdfDocumentError == PDFDocumentErrorSuccess) {
                CThreadPoolUtils.getInstance().executeIO(() -> {
                    replacePage(selectDocument);
                });
            } else if (pdfDocumentError == PDFDocumentErrorPassword) {
                CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
                verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(selectDocument, selectUri);
                verifyPasswordDialogFragment.setVerifyCompleteListener(document -> {
                    replacePage(selectDocument);
                });
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
                verifyPasswordDialogFragment.setVerifyCompleteListener(document -> {
                    showInsertPDFPageDialog(tempDocument);
                });
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
    protected int themeResId() {
        return R.attr.compdfkit_BottomSheetDialog_Theme;
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
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if (onBackLisener != null) {
                            onBackLisener.onBack();
                        }
                        break;
                    default:
                        break;
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
                    if (editThumbnailFragment.isEdit()) {
                        editThumbnailFragment.setEdit(false);
                        toolBar.showEditButton(true);
                        toolBar.showSelectButton(false);
                        toolBar.showDoneButton(false);
                        editToolBar.setVisibility(View.GONE);
                        return true;
                    } else {
                        if (onEnterBackPressedListener != null) {
                            onEnterBackPressedListener.onEnterBackPressed();
                        }
                        dismiss();
                        return false;
                    }
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
        }catch (Exception e){

        }
        this.editThumbnailFragment = (CPDFEditThumbnailFragment) getChildFragmentManager().findFragmentById(R.id.id_edit_thumbnail_fragment);
        if (this.editThumbnailFragment != null) {
            editThumbnailFragment.setCPDFPageEditDialogFragment(this);
            editThumbnailFragment.setEdit(enableEditMode && enterEdit);
            editThumbnailFragment.setEnableEditMode(enableEditMode);
        }

        editThumbnailFragment.setPDFDisplayPageIndexListener(pageIndex -> {
            if (pdfView != null) {
                pdfView.currentPageIndex = pageIndex;
                pdfView.getCPdfReaderView().setDisplayPageIndex(pageIndex);
            }
        });
        editThumbnailFragment.initWithPDFView(pdfView);
        refreshHQApList.clear();

        editToolBar.initWithPDFView(pdfView);
        toolBar.setBackBtnClickListener(v -> {
            if (editThumbnailFragment.isEdit()) {
                editThumbnailFragment.setEdit(false);
                toolBar.showEditButton(true);
                toolBar.showSelectButton(false);
                toolBar.showDoneButton(false);
                editToolBar.setVisibility(View.GONE);
            } else {
                if (onEnterBackPressedListener != null) {
                    onEnterBackPressedListener.onEnterBackPressed();
                }
                dismiss();
            }
        });
        toolBar.setOnDoneClickCallback(() -> {
            if (editThumbnailFragment.isEdit()) {
                editThumbnailFragment.setEdit(false);
                toolBar.showEditButton(true);
                toolBar.showSelectButton(false);
                toolBar.showDoneButton(false);
                editToolBar.setVisibility(View.GONE);
            }
        });
        toolBar.setOnEnableEditPageCallback((enable) -> {
            if (enable) {
                editToolBar.setVisibility(View.VISIBLE);
                editThumbnailFragment.setEdit(true);
            }
        });
        toolBar.setOnSelectAllCallback((select) -> {
            editThumbnailFragment.setSelectAll(select);
        });
        toolBar.showEditButton(enableEditMode);
        if (enableEditMode && enterEdit) {
            toolBar.enterEditMode();
        }
        editToolBar.setInsertPageListener(v -> {
            insertPage();
        });
        editToolBar.setReplacePageListener(view -> {
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (pages.size() == 0) {
                CAlertDialog dialog = CAlertDialog.newInstance(getContext().getResources().getString(R.string.tools_page_edit_alert_title),
                        getContext().getResources().getString(R.string.tools_page_edit_alert_content_nopage));
                dialog.setConfirmClickListener((v) -> {
                    dialog.dismiss();
                });
                dialog.show(getChildFragmentManager(), "dialog");
            } else {
                replaceSelectDocumentLauncher.launch(CFileUtils.getContentIntent());
            }
        });
        editToolBar.setExtractPageListener(view -> {
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (pages.size() == 0) {
                CAlertDialog dialog = CAlertDialog.newInstance(getContext().getResources().getString(R.string.tools_page_edit_alert_title),
                        getContext().getResources().getString(R.string.tools_page_edit_alert_content_nopage));
                dialog.setConfirmClickListener((v) -> {
                    dialog.dismiss();
                });
                dialog.show(getChildFragmentManager(), "dialog");
            } else {
                CThreadPoolUtils.getInstance().executeIO(() -> {
                    String dir = Environment.DIRECTORY_DOWNLOADS + File.separator + CFileUtils.EXTRACT_FOLDER;
                    Uri extractPDFUri = extractPage(dir);
                    String fileName = CUriUtil.getUriFileName(getContext(), extractPDFUri);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (extractPDFUri != null) {
                                CFileUtils.shareFile(getContext(), getString(R.string.tools_share_to), "application/pdf", extractPDFUri);
                            }
                            String msg = extractPDFUri == null ? getString(R.string.tools_page_edit_extract_fail) : (getString(R.string.tools_page_edit_extract_ok) + " : " + dir + File.separator + fileName);
                            CToastUtil.showToast(getContext(), msg);
                        });
                    }
                });
            }
        });
        editToolBar.setCopyPageListener(view -> {
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (pages.size() == 0) {
                CAlertDialog dialog = CAlertDialog.newInstance(getContext().getResources().getString(R.string.tools_page_edit_alert_title),
                        getContext().getResources().getString(R.string.tools_page_edit_alert_content_nopage));
                dialog.setConfirmClickListener((v) -> {
                    dialog.dismiss();
                });
                dialog.show(getChildFragmentManager(), "dialog");
            } else {
                CThreadPoolUtils.getInstance().executeIO(() -> {
                    copyPage();
                });
            }
        });
        editToolBar.setRotatePageListener(view -> {
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (pages.size() == 0) {
                CAlertDialog dialog = CAlertDialog.newInstance(getContext().getResources().getString(R.string.tools_page_edit_alert_title),
                        getContext().getResources().getString(R.string.tools_page_edit_alert_content_nopage));
                dialog.setConfirmClickListener((v) -> {
                    dialog.dismiss();
                });
                dialog.show(getChildFragmentManager(), "dialog");
            } else {
                CThreadPoolUtils.getInstance().executeIO(this::rotatePage);
            }
        });
        editToolBar.setDeletePageListener(view -> {
            SparseIntArray pages = editThumbnailFragment.getSelectPages();
            if (pages.size() == 0) {
                CAlertDialog dialog = CAlertDialog.newInstance(getContext().getResources().getString(R.string.tools_page_edit_alert_title),
                        getContext().getResources().getString(R.string.tools_page_edit_alert_content_nopage));
                dialog.setConfirmClickListener((v) -> {
                    dialog.dismiss();
                });
                dialog.show(getChildFragmentManager(), "dialog");
            } else {
                if (checkPdfView() == false) {
                    return;
                }
                CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
                if (pages.size() == document.getPageCount()) {
                    CAlertDialog dialog = CAlertDialog.newInstance(getContext().getString(R.string.tools_warning),
                            getContext().getResources().getString(R.string.tools_page_edit_alert_content_allpage));
                    dialog.setConfirmClickListener((v) -> {
                        dialog.dismiss();
                    });
                    dialog.show(getChildFragmentManager(), "dialog");
                } else {
                    CThreadPoolUtils.getInstance().executeIO(this::deletePage);
                }
            }
        });
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
                return true;
            }

            return false;
        });
    }

    private void insertPage() {
        CSelectInsertPageTypeDialogFragment insertDialogFragment = CSelectInsertPageTypeDialogFragment.newInstance();
        insertDialogFragment.setInsertBlankPageClickListener(view -> {
            CInsertBlankPageDialogFragment blankPageDialogFragment = CInsertBlankPageDialogFragment.newInstance();
            blankPageDialogFragment.setDocument(pdfView.getCPdfReaderView().getPDFDocument());
            blankPageDialogFragment.setOnEditDoneCallback(() -> {
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

    private void showInsertPDFPageDialog(CPDFDocument document) {
        CInsertPdfPageDialogFragment pdfPageDialogFragment = CInsertPdfPageDialogFragment.newInstance();
        pdfPageDialogFragment.initWithPDFView(pdfView);
        pdfPageDialogFragment.setInsertDocument(document);
        pdfPageDialogFragment.setOnEditDoneCallback(() -> {
            int[] pageNum = pdfPageDialogFragment.getInsertPagesArr();
            if (pageNum != null && pageNum.length > 0) {
                editThumbnailFragment.setSelectPages(pageNum);
                editThumbnailFragment.scrollToPosition(pageNum[0]);
            }
            hasEdit = true;
        });
        pdfPageDialogFragment.show(getChildFragmentManager(), "pdf page");
    }

    private boolean replacePage(CPDFDocument selectDocument) {
        if (selectDocument == null) {
            return false;
        }
        if (checkPdfView() == false) {
            return false;
        }

        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        SparseIntArray pagesArr = editThumbnailFragment.getSelectPages();
        if (pagesArr == null || pagesArr.size() == 0) {
            return false;
        }
        boolean res = false;

        int[] insertPageNum = new int[selectDocument.getPageCount()];
        for (int i = 0; i < insertPageNum.length; i++) {
            insertPageNum[i] = i;
        }

        int[] pageNum = new int[pagesArr.size()];
        for (int i = 0; i < pagesArr.size(); i++) {
            pageNum[i] = pagesArr.keyAt(i);
        }
        res = document.importPages(selectDocument, insertPageNum, pageNum[pageNum.length - 1] + 1);
        if (res == false) {
            return false;
        }
        hasEdit = true;
        res = document.removePages(pageNum);
        if (res == false) {
            return false;
        }

        int[] insertPages = new int[insertPageNum.length];
        for (int i = pageNum[pageNum.length - 1] + 1 - pageNum.length; i <= insertPageNum.length + (pageNum[pageNum.length - 1] + 1) - 1 - pageNum.length; i++) {
            insertPages[i - (pageNum[pageNum.length - 1] + 1 - pageNum.length)] = i;
        }
        if (insertPages.length > 0) {
            editThumbnailFragment.setSelectPages(insertPages);
        }
        return true;
    }

    private Uri extractPage(String publicDirectory) {
        if (checkPdfView() == false) {
            return null;
        }
        SparseIntArray pagesArr = editThumbnailFragment.getSelectPages();
        if (pagesArr == null || pagesArr.size() == 0) {
            return null;
        }
        boolean res = false;
        int[] pageNum = new int[pagesArr.size()];
        for (int i = 0; i < pagesArr.size(); i++) {
            pageNum[i] = pagesArr.keyAt(i);
        }
        // Downloads/compdfkit/extract/

        Uri saveUri = CUriUtil.createFileUri(getContext(),
                publicDirectory, getNewFileName(pageNum), "application/pdf");
        if (saveUri == null) {
            return null;
        }
        CPDFDocument newDocument = CPDFDocument.createDocument(getContext());
        res = newDocument.importPages(pdfView.getCPdfReaderView().getPDFDocument(), pageNum, 0);
        try {
            res &= newDocument.saveAs(saveUri, false, pdfView.isSaveFileExtraFontSubset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res ? saveUri : null;
    }

    private boolean copyPage() {
        if (checkPdfView() == false) {
            return false;
        }
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        SparseIntArray pagesArr = editThumbnailFragment.getSelectPages();
        if (pagesArr == null || pagesArr.size() == 0) {
            return false;
        }

        int[] pageNum = new int[pagesArr.size()];
        for (int i = 0; i < pagesArr.size(); i++) {
            pageNum[i] = pagesArr.keyAt(i);
        }

        List<CPDFPage> pageList = new ArrayList<>();
        for (int i = 0; i < pagesArr.size(); i++) {
            CPDFPage page = document.copyPage(pagesArr.keyAt(i));
            if (page != null) {
                pageList.add(page);
            }
        }

        if (pageList.size() > 0) {
            for (int i = pageList.size() - 1; i >= 0; i--) {
                document.addPage(pageList.get(i), pagesArr.keyAt(pagesArr.size() - 1) + 1);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    pdfView.getCPdfReaderView().reloadPages();
                });
            }
            int[] updatePags = new int[pagesArr.size()];
            for (int i = 0; i < updatePags.length; i++) {
                updatePags[i] = i + pagesArr.keyAt(pagesArr.size() - 1) + 1;
            }
            editThumbnailFragment.setSelectPages(updatePags);
            hasEdit = true;
        }
        return true;
    }

    private boolean rotatePage() {
        if (checkPdfView() == false) {
            return false;
        }
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        SparseIntArray pagesArr = editThumbnailFragment.getSelectPages();
        if (pagesArr == null || pagesArr.size() == 0) {
            return false;
        }

        int[] pageNum = new int[pagesArr.size()];
        for (int i = 0; i < pagesArr.size(); i++) {
            pageNum[i] = pagesArr.keyAt(i);
        }
        for (int page : pageNum) {
            CPDFPage tpdfPage = document.pageAtIndex(page);
            if (tpdfPage == null) {
                return false;
            }
            int rotation = tpdfPage.getRotation();
            if (!tpdfPage.setRotation(rotation + 90)) {
                return false;
            }
            if (!refreshHQApList.contains(page)) {
                refreshHQApList.add(page);
            }
            hasEdit = true;
        }
        editThumbnailFragment.updatePagesArr(pageNum, CPDFEditThumbnailFragment.UPDATE_TYPE_ROTATE);
        return true;
    }

    private boolean deletePage() {
        if (checkPdfView() == false) {
            return false;
        }
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        SparseIntArray pagesArr = editThumbnailFragment.getSelectPages();
        if (pagesArr == null || pagesArr.size() == 0) {
            return false;
        }

        int[] pageNum = new int[pagesArr.size()];
        for (int i = 0; i < pagesArr.size(); i++) {
            pageNum[i] = pagesArr.keyAt(i);
        }
        boolean res = document.removePages(pageNum);
        editThumbnailFragment.setSelectAll(false);
        editThumbnailFragment.updatePagesArr(pageNum, CPDFEditThumbnailFragment.UPDATE_TYPE_DELETE);
        hasEdit = true;
        return res;
    }

    private boolean checkPdfView() {
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return false;
        }
        if (pdfView.getCPdfReaderView().getPDFDocument() == null) {
            return false;
        }
        return true;
    }

    private String getNewFileName(int[] exportPages) {
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        String fileName = document.getFileName();
        StringBuilder newName = new StringBuilder(fileName.substring(0, fileName.indexOf(".pdf")));
        newName.append("_Page");
        for (int i = 0; i < exportPages.length; i++) {
            if (i != 0) {
                newName.append(",").append(exportPages[i] + 1);
            } else {
                newName.append(exportPages[i] + 1);
            }
        }
        newName.append(".pdf");
        return newName.toString();
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
        if (checkPdfView()) {
            CPDFReaderView readerView = pdfView.getCPdfReaderView();
            if (hasEdit) {
                int pageCount = readerView.getPDFDocument().getPageCount();
                int jumpIndex = pdfView.currentPageIndex >= pageCount ? pageCount - 1 : pdfView.currentPageIndex;
                readerView.reloadPages(refreshHQApList);
                readerView.setDisplayPageIndex(jumpIndex);
                pdfView.slideBar.setPageCount(pageCount);
                pdfView.slideBar.requestLayout();
                pdfView.indicatorView.setTotalPage(pageCount);
            }
        }
        super.dismiss();
        if (onBackLisener != null) {
            onBackLisener.onBack();
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (checkPdfView()) {
            CPDFReaderView readerView = pdfView.getCPdfReaderView();
            if (hasEdit) {
                int pageCount = readerView.getPDFDocument().getPageCount();
                int jumpIndex = pdfView.currentPageIndex >= pageCount ? pageCount - 1 : pdfView.currentPageIndex;
                readerView.reloadPages(refreshHQApList);
                readerView.setDisplayPageIndex(jumpIndex);
                pdfView.slideBar.setPageCount(pageCount);
                pdfView.slideBar.requestLayout();
                pdfView.indicatorView.setTotalPage(pageCount);
            }
        }
        if (onEnterBackPressedListener != null) {
            onEnterBackPressedListener.onEnterBackPressed();
        }
    }

    public void setHasEdit(boolean edit) {
        hasEdit = edit;
    }

    public void setOnBackListener(OnBackLisener listener) {
        this.onBackLisener = listener;
    }

    public void setOnEnterBackPressedListener(COnEnterBackPressedListener onEnterBackPressedListener) {
        this.onEnterBackPressedListener = onEnterBackPressedListener;
    }

    public interface OnBackLisener {
        void onBack();
    }

    public interface COnEnterBackPressedListener {
        void onEnterBackPressed();
    }
}
