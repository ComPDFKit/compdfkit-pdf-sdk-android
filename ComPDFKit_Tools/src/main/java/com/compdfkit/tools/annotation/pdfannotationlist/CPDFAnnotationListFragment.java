/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationlist;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.CPDFReplyAnnotation;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationlist.adapter.CPDFAnnotListAdapter;
import com.compdfkit.tools.annotation.pdfannotationlist.bean.CPDFAnnotListItem;
import com.compdfkit.tools.annotation.pdfannotationlist.data.CPDFAnnotDatas;
import com.compdfkit.tools.annotation.pdfannotationlist.dialog.CPDFEditReplyDialogFragment;
import com.compdfkit.tools.annotation.pdfannotationlist.dialog.CPDFReplyDetailsDialogFragment;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeFragment;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CPDFAnnotationListFragment extends CBasicThemeFragment {

    private RecyclerView rvAnnotation;

    private CPDFViewCtrl pdfView;

    private ConstraintLayout clEmptyView;

    private COnSetPDFDisplayPageIndexListener displayPageIndexListener;
    private CPDFAnnotListAdapter listAdapter;

    private ProgressBar progressBar;


    public static CPDFAnnotationListFragment newInstance() {
        return new CPDFAnnotationListFragment();
    }

    /**
     * Used to select `xfdf` format annotation files from the system file manager
     * and import them into PDF documents
     * sample:<br/><br/>
     * importAnnotFileLauncher.launch(CFileUtils.getIntent("application/octet-stream")
     */
    private ActivityResultLauncher<Intent> importAnnotFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri uri = result.getData().getData();
            // get xfdf format file name
            String fileName = CUriUtil.getUriFileName(getContext(), uri);
            //Determine whether the file is in xfdf format
            if (fileName.toLowerCase().endsWith(".xfdf")) {
                // Save the file to the app's internal storage cache directory
                String dir = new File(getContext().getCacheDir(), CFileUtils.CACHE_FOLDER + File.separator + "xfdfFile").getAbsolutePath();
                // Get the saved file path
                String importFilePath = CFileUtils.copyFileToInternalDirectory(getContext(), uri, dir, fileName);
                CLog.e("ComPDFKit-Tools", "importFilePath:" + importFilePath);

                if (!TextUtils.isEmpty(importFilePath)) {
                    boolean importResult = CPDFAnnotDatas.importAnnotations(pdfView.getCPdfReaderView().getPDFDocument(), importFilePath);
                    if (importResult) {
                        CToastUtil.showLongToast(getContext(), R.string.tools_import_success);
                        pdfView.getCPdfReaderView().reloadPages();
                        pdfView.postDelayed(this::updateAnnotationList, 400);
                    }
                    CLog.e("ComPDFKit-Tools", "import Annotation xfdf format file " + (importResult ? "success" : "fail"));
                }
            } else {
                CLog.e("ComPDFKit-Tools", "Please select xfdf format file");
                CToastUtil.showLongToast(getContext(), R.string.tools_please_select_xfdf_format_file);
            }
        }
    });

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }


    @Override
    protected int layoutId() {
        return R.layout.tools_bota_annotation_list_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        rvAnnotation = rootView.findViewById(R.id.rv_annotation);
        clEmptyView = rootView.findViewById(R.id.cl_annot_empty_view);
        progressBar = rootView.findViewById(R.id.progress_bar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listAdapter = new CPDFAnnotListAdapter();
        updateAnnotationList();
        listAdapter.addOnItemChildClickListener((adapter, view1, position) -> {
            CPDFAnnotListItem item = adapter.list.get(position);
            if (view1.getId() == R.id.cl_root) {
                if (!item.isHeader()) {
                    // Close the dialog and jump to the corresponding page number
                    if (displayPageIndexListener != null) {
                        displayPageIndexListener.displayPage(item.getPage());
                    }
                }
            } else if (view1.getId() == R.id.iv_review_status) {
                listAdapter.showReviewStatusMenu(getContext(), position, view1);
            } else if (view1.getId() == R.id.cb_marked_status) {
                listAdapter.showMarkedStatusMenu(getContext(), position, view1);
            } else if (view1.getId() == R.id.iv_more) {
                showAnnotationMoreMenu(item, position, view1);
            }
        }, R.id.iv_review_status, R.id.cb_marked_status, R.id.iv_more, R.id.cl_root);
        rvAnnotation.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAnnotation.setAdapter(listAdapter);
    }


    public void updateAnnotationList() {
        updateAnnotationList(true);
    }

    /**
     * Reload the annotation list data
     *
     * @param showProgressBar Whether to display the loading status when loading
     */
    public void updateAnnotationList(boolean showProgressBar) {
        if (showProgressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
        CThreadPoolUtils.getInstance().executeIO(() -> {
            List<CPDFAnnotListItem> list = CPDFAnnotDatas.getAnnotationList(pdfView);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (list.isEmpty()) {
                        clEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        clEmptyView.setVisibility(View.GONE);
                    }
                    listAdapter.setList(list);
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    /**
     * show toolbar annotation menu, in CPDFBOTAFragment
     *
     */
    public void showAnnotationMenu(View anchorView) {
        CPopupMenuWindow menuWindow = new CPopupMenuWindow(getContext());
        boolean hasAnnotations = !listAdapter.list.isEmpty();
        menuWindow.addItem(R.string.tools_import_annotations, v -> importAnnotFileLauncher.launch(CFileUtils.getIntent("application/octet-stream")));
        menuWindow.addItem(R.string.tools_export_annotations,hasAnnotations,  v -> {
            // Select the directory to export the annotation files
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(dirPath, getString(R.string.tools_saving_path), getString(R.string.tools_okay));
            directoryDialog.setSelectFolderListener(dir -> {
                CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
                boolean result = CPDFAnnotDatas.exportAnnotations(document, dir, CFileUtils.getFileNameNoExtension(document.getFileName()));
                if (result) {
                    CToastUtil.showLongToast(getContext(), R.string.tools_export_success);
                }
            });
            directoryDialog.show(getChildFragmentManager(), "dirDialog");
        });
        menuWindow.addItem(R.string.tools_delete_all_annotations, hasAnnotations, v -> deleteAllAnnotations());
        menuWindow.addItem(R.string.tools_delete_all_replies,hasAnnotations,  v -> {
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            CPDFAnnotDatas.removeAllAnnotationReply(document);
            updateAnnotationList();
        });
        menuWindow.showAsDropDown(anchorView);
    }

    /**
     * delete pdf document all annotations
     */
    public void deleteAllAnnotations() {
        boolean result = CPDFAnnotDatas.removeAllAnnotations(listAdapter.list);
        if (result) {
            pdfView.getCPdfReaderView().reloadPages();
            updateAnnotationList();
        }
    }

    /**
     * Displays the more options menu for the selected annotation, including:
     * adding an annotation reply, showing the annotation reply list, and deleting the annotation.
     *
     * @param item       The annotation data currently selected from the list
     * @param position   The position of the selected item in the list
     * @param anchorView The view to anchor the menu to
     */
    private void showAnnotationMoreMenu(CPDFAnnotListItem item, int position, View anchorView) {
        CPopupMenuWindow moreMenu = new CPopupMenuWindow(getContext());
        moreMenu.addItem(R.string.tools_add_a_new_reply, v -> {
            CPDFEditReplyDialogFragment editReplyDialogFragment = CPDFEditReplyDialogFragment.addReply();
            editReplyDialogFragment.setReplyContentListener(content -> {
                CPDFReplyAnnotation replyAnnotation = item.getAttr().createReplyAnnotation();
                replyAnnotation.setTitle(pdfView.getCPDFConfiguration().annotationsConfig.annotationAuthor);
                replyAnnotation.setContent(content);
                showReplyDetailsFragment(item, position);
            });
            editReplyDialogFragment.show(getChildFragmentManager(), "addReplyDialogFragment");
        });
        moreMenu.addItem(R.string.tools_view_replies, v -> showReplyDetailsFragment(item, position));
        moreMenu.addItem(R.string.tools_delete_annotation, v -> {
            boolean result = item.getAttr().removeFromPage();
            if (result) {
                listAdapter.remove(position);
                ArrayList<Integer> pages = new ArrayList<>();
                pages.add(item.getPage());
                pdfView.getCPdfReaderView().reloadPages(pages);
                pdfView.getCPdfReaderView().postDelayed(() -> updateAnnotationList(false), 450);
            }
        });
        int[] windowPos = CDimensUtils.calculatePopWindowPos(anchorView, moreMenu.getContentView());
        moreMenu.setAnimationStyle(R.style.PopupAnimation);
        moreMenu.showAtLocation(anchorView, Gravity.START | Gravity.TOP, windowPos[0], windowPos[1]);
    }

    private void showReplyDetailsFragment(CPDFAnnotListItem item, int position) {
        CPDFReplyDetailsDialogFragment replyDetailsDialogFragment = CPDFReplyDetailsDialogFragment.newInstance();
        replyDetailsDialogFragment.setCPDFAnnotation(item.getAttr());
        replyDetailsDialogFragment.setAnnotAuthor(pdfView.getCPDFConfiguration().annotationsConfig.annotationAuthor);
        replyDetailsDialogFragment.setUpdateAnnotationListListener(() -> {
            listAdapter.remove(position);
            ArrayList<Integer> pages = new ArrayList<>();
            pages.add(item.getPage());
            pdfView.getCPdfReaderView().reloadPages(pages);
            pdfView.getCPdfReaderView().postDelayed(() -> updateAnnotationList(false), 450);
        });
        replyDetailsDialogFragment.setDismissListener(() -> listAdapter.notifyItemChanged(position));
        replyDetailsDialogFragment.show(getChildFragmentManager(), "replyDetailsDialogFragment");
    }

    public void setPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.displayPageIndexListener = displayPageIndexListener;
    }
}
