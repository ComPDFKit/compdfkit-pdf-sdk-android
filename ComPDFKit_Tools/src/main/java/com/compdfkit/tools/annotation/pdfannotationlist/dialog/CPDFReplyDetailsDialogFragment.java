/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * <p>THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT
 * LAW AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */
package com.compdfkit.tools.annotation.pdfannotationlist.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFReplyAnnotation;
import com.compdfkit.core.common.CPDFDate;
import com.compdfkit.core.utils.TTimeUtil;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationlist.adapter.CPDFAnnotListAdapter;
import com.compdfkit.tools.annotation.pdfannotationlist.adapter.CPDFAnnotReplyListAdapter;
import com.compdfkit.tools.annotation.pdfannotationlist.bean.CPDFAnnotListItem;
import com.compdfkit.tools.annotation.pdfannotationlist.data.CPDFAnnotDatas;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.pdf.config.bota.CPDFBotaAnnotationMenu;
import com.compdfkit.tools.common.pdf.config.bota.CPDFBotaItemMenu;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;
import com.compdfkit.tools.common.views.CToolBar;

import java.util.ArrayList;
import java.util.Arrays;

public class CPDFReplyDetailsDialogFragment extends CBasicBottomSheetDialogFragment {

    private CToolBar toolBar;

    private RecyclerView rvAnnot;

    private RecyclerView rvReplyList;

    private AppCompatTextView tvTotal;

    private AppCompatTextView tvDeleteAnnotation;

    private AppCompatTextView tvAddNewReply;

    private CPDFAnnotListAdapter annotListAdapter;

    private CPDFAnnotReplyListAdapter annotReplyListAdapter;

    private CPDFAnnotation cpdfAnnotation;

    private String annotAuthor;

    private CUpdateAnnotationListListener updateAnnotationListListener;

    @Override
    protected float dimAmount() {
        return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
    }

    @Override
    protected boolean draggable() {
        return false;
    }

    public static CPDFReplyDetailsDialogFragment newInstance() {
        Bundle args = new Bundle();
        CPDFReplyDetailsDialogFragment fragment = new CPDFReplyDetailsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCPDFAnnotation(CPDFAnnotation cpdfAnnotation) {
        this.cpdfAnnotation = cpdfAnnotation;
    }

    public void setAnnotAuthor(String annotAuthor) {
        this.annotAuthor = annotAuthor;
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_annot_reply_details_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        toolBar = rootView.findViewById(R.id.tool_bar);
        rvAnnot = rootView.findViewById(R.id.rv_annotation);
        rvReplyList = rootView.findViewById(R.id.rv_reply);
        tvTotal = rootView.findViewById(R.id.tv_total);
        tvDeleteAnnotation = rootView.findViewById(R.id.tv_delete_annotation);
        tvAddNewReply = rootView.findViewById(R.id.tv_add_reply);
        toolBar.setBackBtnClickListener(v -> dismiss());
        tvDeleteAnnotation.setOnClickListener(v -> {
            if (cpdfAnnotation != null) {
                boolean result = cpdfAnnotation.removeFromPage();
                if (result) {
                    if (updateAnnotationListListener != null) {
                        updateAnnotationListListener.delete();
                    }
                    dismiss();
                }
            }else {
                dismiss();
            }
        });
        tvAddNewReply.setOnClickListener(v -> {
            CPDFEditReplyDialogFragment addReplyDialog = CPDFEditReplyDialogFragment.addReply();
            addReplyDialog.setReplyContentListener(content -> {
                if (cpdfAnnotation != null) {
                    CPDFReplyAnnotation replyAnnotation = cpdfAnnotation.createReplyAnnotation();
                    replyAnnotation.setContent(content);
                    replyAnnotation.setTitle(annotAuthor);
                    updateReplyList();
                } else {
                    dismiss();
                }
            });
            addReplyDialog.show(getChildFragmentManager(), "addReplyDialogFragment");
        });
    }

    @Override
    protected void onViewCreate() {
        initAnnotItem();
        initReplyList();
    }

    private void initAnnotItem() {
        if (cpdfAnnotation != null) {
            annotListAdapter = new CPDFAnnotListAdapter();
            annotListAdapter.setShowMoreMenu(false);
            CPDFAnnotListItem item = CPDFAnnotDatas.convertToListItem(cpdfAnnotation);
            annotListAdapter.setList(Arrays.asList(item));
            rvAnnot.setLayoutManager(new LinearLayoutManager(getContext()));
            rvAnnot.setAdapter(annotListAdapter);

            annotListAdapter.addOnItemChildClickListener((adapter, view, position) -> {
                if (view.getId() == R.id.iv_review_status) {
                    annotListAdapter.showReviewStatusMenu(getContext(), position, view);
                } else if (view.getId() == R.id.cb_marked_status) {
                    annotListAdapter.showMarkedStatusMenu(getContext(), position, view);
                }
            }, R.id.iv_review_status, R.id.cb_marked_status);
        }
    }

    private void initReplyList() {
        if (cpdfAnnotation == null) {
            return;
        }
        updateTotalCount();
        CPDFReplyAnnotation[] replyAnnotations = cpdfAnnotation.getAllReplyAnnotations();
        annotReplyListAdapter = new CPDFAnnotReplyListAdapter();
        annotReplyListAdapter.setList(new ArrayList<>(Arrays.asList(replyAnnotations)));
        rvReplyList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReplyList.setAdapter(annotReplyListAdapter);
        annotReplyListAdapter.setOnItemClickListener((adapter, view, position) -> {
        });
        annotReplyListAdapter.addOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_review_status) {
                annotReplyListAdapter.showReviewStatusMenu(getContext(), position, view);
            } else if (view.getId() == R.id.cb_marked_status) {
                annotReplyListAdapter.showMarkedStatusMenu(getContext(), position, view);
            } else if (view.getId() == R.id.iv_more) {
                CPopupMenuWindow moreMenu = new CPopupMenuWindow(getContext());
                moreMenu.addItem(R.string.tools_edit, v -> {
                    CPDFReplyAnnotation replyAnnotation = adapter.list.get(position);
                    CPDFEditReplyDialogFragment replyDialogFragment = CPDFEditReplyDialogFragment.editReply(replyAnnotation.getContent());
                    replyDialogFragment.setReplyContentListener(content -> {
                        replyAnnotation.setContent(content);
                        CPDFDate.currentTime2LocalDate();
                        replyAnnotation.setRecentlyModifyDate(TTimeUtil.getCurrentDate());
                        replyAnnotation.updateAp();
                        annotReplyListAdapter.notifyDataSetChanged();
                    });
                    replyDialogFragment.show(getChildFragmentManager(), "replyDialogFragment");
                });
                moreMenu.addItem(R.string.tools_delete, v -> {
                    CPDFReplyAnnotation replyAnnotation = adapter.list.get(position);
                    boolean result = replyAnnotation.removeFromPageIncludeReplyAnnot();
                    if (result) {
                        annotReplyListAdapter.remove(position);
                        updateTotalCount();
                    }
                });
                //Calculate the position of the pop-up.
                // If there is enough space at the bottom,
                // it will be displayed below the anchor view. If not, it will be displayed above it.
                int[] windowPos = CDimensUtils.calculatePopWindowPos(view, moreMenu.getContentView());
                moreMenu.setAnimationStyle(R.style.ToolsPopupAnimation);
                moreMenu.showAtLocation(view, Gravity.START | Gravity.TOP, windowPos[0], windowPos[1]);
            }
        }, R.id.iv_review_status, R.id.cb_marked_status, R.id.iv_more);
    }

    private void updateReplyList() {
        CPDFReplyAnnotation[] replyAnnotations = cpdfAnnotation.getAllReplyAnnotations();
        annotReplyListAdapter.setList(new ArrayList<>(Arrays.asList(replyAnnotations)));
        updateTotalCount();
    }

    private void updateTotalCount() {
        CPDFReplyAnnotation[] replyAnnotations = cpdfAnnotation.getAllReplyAnnotations();
        tvTotal.setVisibility(replyAnnotations.length > 0 ? View.VISIBLE : View.INVISIBLE);
        tvTotal.setText(getString(R.string.tools_total, replyAnnotations.length));
    }

    public void setUpdateAnnotationListListener(CUpdateAnnotationListListener updateAnnotationListListener) {
        this.updateAnnotationListListener = updateAnnotationListListener;
    }

    public interface CUpdateAnnotationListListener {
        void delete();
    }

}
