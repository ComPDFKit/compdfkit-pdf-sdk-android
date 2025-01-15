package com.compdfkit.tools.viewer.pdfinfo;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFDocumentPermissionInfo;
import com.compdfkit.core.document.CPDFInfo;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;


public class CPDFDocumentInfoDialogFragment extends CBasicBottomSheetDialogFragment {

    private CToolBar toolBar;

    private AppCompatTextView tvPDFFileName;

    private AppCompatTextView tvPDFSize;

    private AppCompatTextView tvPDFTitle;

    private AppCompatTextView tvPDFAuthor;

    private AppCompatTextView tvPDFSubject;

    private AppCompatTextView tvPDFKeywords;

    private AppCompatTextView tvPDFVersion;

    private AppCompatTextView tvPDFPageNum;

    private AppCompatTextView tvPDFCreator;

    private AppCompatTextView tvPDFCreationDate;

    private AppCompatTextView tvPDFModificationDate;

    private AppCompatTextView tvPDFAllowPrint;

    private AppCompatTextView tvPDFAllowCopy;

    private AppCompatTextView tvPDFAllowDocumentChanges;

    private AppCompatTextView tvPDFAllowDocumentAssembly;

    private AppCompatTextView tvPDFAllowDocumentCommenting;

    private AppCompatTextView tvPDFAllowDocumentFormFieldEntry;

    private CPDFViewCtrl pdfView;

    private CDocumentInfoBean documentInfoBean;

    public static CPDFDocumentInfoDialogFragment newInstance() {
        return new CPDFDocumentInfoDialogFragment();
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    @Override
    protected float dimAmount() {
        return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_pdf_document_info_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        toolBar = rootView.findViewById(R.id.tool_bar);
        toolBar.setBackBtnClickListener(v -> {
            dismiss();
        });
        tvPDFFileName = rootView.findViewById(R.id.id_pdf_info_fileName);
        tvPDFSize = rootView.findViewById(R.id.id_pdf_info_size);
        tvPDFTitle = rootView.findViewById(R.id.id_pdf_info_title);
        tvPDFAuthor = rootView.findViewById(R.id.id_pdf_info_author);
        tvPDFSubject = rootView.findViewById(R.id.id_pdf_info_subject);
        tvPDFKeywords = rootView.findViewById(R.id.id_pdf_info_keywords);
        tvPDFVersion = rootView.findViewById(R.id.id_pdf_info_version);
        tvPDFPageNum = rootView.findViewById(R.id.id_pdf_info_pages);
        tvPDFCreator = rootView.findViewById(R.id.id_pdf_info_creator);
        tvPDFCreationDate = rootView.findViewById(R.id.id_pdf_info_creationDate);
        tvPDFModificationDate = rootView.findViewById(R.id.id_pdf_info_modification);
        tvPDFAllowPrint = rootView.findViewById(R.id.id_pdf_info_allowPrint);
        tvPDFAllowCopy = rootView.findViewById(R.id.id_pdf_info_allowCopy);
        tvPDFAllowDocumentChanges = rootView.findViewById(R.id.id_pdf_info_document_changes);
        tvPDFAllowDocumentAssembly = rootView.findViewById(R.id.id_pdf_info_document_assembly);
        tvPDFAllowDocumentCommenting = rootView.findViewById(R.id.id_pdf_info_document_commenting);
        tvPDFAllowDocumentFormFieldEntry = rootView.findViewById(R.id.id_pdf_info_document_form_field_entry);
    }

    @Override
    protected void onViewCreate() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("document_info")){
            documentInfoBean = (CDocumentInfoBean) savedInstanceState.getSerializable("document_info");
        }
        if (documentInfoBean == null){
            documentInfoBean = getCDocumentInfo(pdfView);
        }
        if (documentInfoBean != null) {
            tvPDFFileName.setText(documentInfoBean.getFileName());
            tvPDFSize.setText(documentInfoBean.getFileSize());
            tvPDFTitle.setText(documentInfoBean.getTitle());
            tvPDFAuthor.setText(documentInfoBean.getAuthor());
            tvPDFSubject.setText(documentInfoBean.getSubject());
            tvPDFKeywords.setText(documentInfoBean.getKeywords());

            tvPDFVersion.setText(documentInfoBean.getVersion());
            tvPDFPageNum.setText(String.valueOf(documentInfoBean.getPageCount()));
            tvPDFCreator.setText(documentInfoBean.getCreator());
            tvPDFCreationDate.setText(documentInfoBean.getCreationDate());
            tvPDFModificationDate.setText(documentInfoBean.getModificationDate());

            tvPDFAllowPrint.setText(allowStr(documentInfoBean.isAllowsPrinting()));
            tvPDFAllowCopy.setText(allowStr(documentInfoBean.isAllowsCopying()));
            tvPDFAllowDocumentChanges.setText(allowStr(documentInfoBean.isAllowsDocumentChanges()));
            tvPDFAllowDocumentAssembly.setText(allowStr(documentInfoBean.isAllowsDocumentAssembly()));
            tvPDFAllowDocumentCommenting.setText(allowStr(documentInfoBean.isAllowsCommenting()));
            tvPDFAllowDocumentFormFieldEntry.setText(allowStr(documentInfoBean.isAllowsFormFieldEntry()));
        }
    }

    private CDocumentInfoBean getCDocumentInfo(CPDFViewCtrl pdfView){
        CDocumentInfoBean infoBean = new CDocumentInfoBean();
        if (pdfView == null){
            return infoBean;
        }
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        if (document == null){
            return infoBean;
        }
        CPDFDocumentPermissionInfo permissionInfo = document.getPermissionsInfo();
        infoBean.setFileName(document.getFileName());
        infoBean.setFileSize(getDocumentSize(document));
        infoBean.setVersion(String.valueOf(document.getMajorVersion()));
        infoBean.setPageCount(document.getPageCount());
        CPDFInfo cpdfInfo = document.getInfo();
        if (cpdfInfo != null) {
            infoBean.setTitle(cpdfInfo.getTitle());
            infoBean.setAuthor(cpdfInfo.getAuthor());
            infoBean.setSubject(cpdfInfo.getSubject());
            infoBean.setKeywords(cpdfInfo.getKeywords());
            infoBean.setCreator(cpdfInfo.getCreator());
            infoBean.setCreationDate(CDateUtil.transformPDFDate(cpdfInfo.getCreationDate()));
            infoBean.setModificationDate(CDateUtil.transformPDFDate(cpdfInfo.getModificationDate()));
        }
        if (permissionInfo != null) {
            infoBean.setAllowsPrinting(permissionInfo.isAllowsPrinting());
            infoBean.setAllowsCopying(permissionInfo.isAllowsCopying());
            infoBean.setAllowsDocumentChanges(permissionInfo.isAllowsDocumentChanges());
            infoBean.setAllowsDocumentAssembly(permissionInfo.isAllowsDocumentAssembly());
            infoBean.setAllowsCommenting(permissionInfo.isAllowsCommenting());
            infoBean.setAllowsFormFieldEntry(permissionInfo.isAllowsFormFieldEntry());
        }
        return infoBean;
    }

    private String allowStr(boolean allow) {
        return getString(allow ? R.string.tools_allowed : R.string.tools_not_allowed);
    }


    private String getDocumentSize(CPDFDocument document) {
        final long MB = 1024 * 1024;
        final int KB = 1024;
        long fileSize = 0L;
        try {
            ParcelFileDescriptor p = document.getContext().getContentResolver().openFileDescriptor(document.getUri(), "r");
            fileSize = p.getStatSize();
            p.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        float size;
        String unit = " M";
        if (fileSize > MB) {
            size = ((float) fileSize / (1024 * 1024));
        } else if (fileSize > KB) {
            size = ((float) fileSize / 1024);
            unit = " KB";
        } else {
            size = fileSize;
            unit = " B";
        }
        return String.format("%.2f", size) + unit;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (documentInfoBean != null) {
            outState.putSerializable("document_info", documentInfoBean);
        }
        super.onSaveInstanceState(outState);
    }
}
