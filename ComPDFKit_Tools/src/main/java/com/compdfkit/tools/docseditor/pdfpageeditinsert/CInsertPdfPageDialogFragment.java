package com.compdfkit.tools.docseditor.pdfpageeditinsert;

import static com.compdfkit.core.document.CPDFDocument.PDFDocumentError.PDFDocumentErrorPassword;
import static com.compdfkit.core.document.CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.docseditor.CPageEditBar;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CInsertPdfPageDialogFragment extends CBasicBottomSheetDialogFragment implements View.OnClickListener {
    private CPageEditBar toolBar;
    private CPDFViewCtrl pdfView;
    private AppCompatTextView tvFilename;

    private AppCompatRadioButton rbFromPageLocationAll;
    private AppCompatRadioButton rbFromPageLocationOdd;
    private AppCompatRadioButton rbFromPageLocationEven;
    private AppCompatRadioButton rbFromPageLocationSpecify;
    private AppCompatEditText etFromInputPageIndex;

    private AppCompatRadioButton rbToPageLocationHome;
    private AppCompatRadioButton rbToPageLocationLast;
    private AppCompatRadioButton rbToPageLocationBefore;
    private AppCompatRadioButton rbToPageLocationAfter;

    private AppCompatEditText etToInputPageIndex;

    private CPageEditBar.OnEditDoneCallback onEditDoneCallback;
    private CPDFDocument selectDocument = null;
    private int[] insertPages = null;
    private final String selectPageRegex = "^(\\d+)(-\\d+)?([,，](\\d+)(-\\d+)?)*$";
    private ActivityResultLauncher<Intent> selectDocumentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null && result.getData().getData() != null) {
            Uri fileUri = result.getData().getData();
            CFileUtils.takeUriPermission(getContext(), fileUri);

            CPDFDocument tempDocument = new CPDFDocument(getContext());
            CPDFDocument.PDFDocumentError pdfDocumentError = tempDocument.open(fileUri);
            if (pdfDocumentError == PDFDocumentErrorSuccess) {
                String name = CUriUtil.getUriFileName(getContext(), fileUri);
                if (!TextUtils.isEmpty(name)) {
                    tvFilename.setText(name);
                }
                selectDocument = tempDocument;
            } else if (pdfDocumentError == PDFDocumentErrorPassword) {
                CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
                verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(tempDocument, fileUri);
                verifyPasswordDialogFragment.setVerifyCompleteListener(document -> {
                    String name = CUriUtil.getUriFileName(getContext(), fileUri);
                    if (!TextUtils.isEmpty(name)) {
                        tvFilename.setText(name);
                    }
                    selectDocument = document;
                });
                verifyPasswordDialogFragment.show(getChildFragmentManager(), "verifyPwdDialog");
            }
        }
    });

    public static CInsertPdfPageDialogFragment newInstance() {
        return new CInsertPdfPageDialogFragment();
    }

    public void setInsertDocument(CPDFDocument selectDocument) {
        this.selectDocument = selectDocument;
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
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
    protected int layoutId() {
        return R.layout.tools_pageedit_insert_pdf_page_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        toolBar = rootView.findViewById(R.id.page_edit_tool_bar);
        tvFilename = rootView.findViewById(R.id.iv_tool_insert_page_filename);
        AppCompatImageView ivFileNameRight = rootView.findViewById(R.id.iv_tool_insert_page_right);
        RadioGroup rgPageRange = rootView.findViewById(R.id.rp_tools_edit_page_insertpage_from_location);
        rbFromPageLocationAll = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_from_all);
        rbFromPageLocationOdd = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_from_odd);
        rbFromPageLocationEven = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_from_even);
        rbFromPageLocationSpecify = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_from_specify);
        etFromInputPageIndex = rootView.findViewById(R.id.et_tool_edit_page_from_enterpage);
        rbToPageLocationHome = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_homepage);
        rbToPageLocationLast = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_lastpage);
        rbToPageLocationBefore = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_before);
        rbToPageLocationAfter = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_after);
        etToInputPageIndex = rootView.findViewById(R.id.et_tool_edit_page_enterpage);
        RadioGroup rgInsertTo = rootView.findViewById(R.id.rp_tools_edit_page_insertpage_location);

        etFromInputPageIndex.setOnFocusChangeListener((view, b) -> {
            if (b) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (!rbFromPageLocationSpecify.isChecked()) {
                        rbFromPageLocationSpecify.setChecked(true);
                        CViewUtils.showKeyboard(etFromInputPageIndex);
                    }
                }
            } else {
                CViewUtils.hideKeyboard(etFromInputPageIndex);
            }
        });
        etFromInputPageIndex.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (!rbFromPageLocationSpecify.isChecked()) {
                    rbFromPageLocationSpecify.setChecked(true);
                    CViewUtils.showKeyboard(etFromInputPageIndex);
                }
            }
        });
        rgPageRange.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_tools_edit_page_insert_location_from_all) {
                etFromInputPageIndex.setFocusableInTouchMode(false);
                etFromInputPageIndex.clearFocus();
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_from_odd) {
                etFromInputPageIndex.setFocusableInTouchMode(false);
                etFromInputPageIndex.clearFocus();
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_from_even) {
                etFromInputPageIndex.setFocusableInTouchMode(false);
                etFromInputPageIndex.clearFocus();
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_from_specify) {
                if (!etFromInputPageIndex.isFocused()) {
                    etFromInputPageIndex.requestFocus();
                    CViewUtils.showKeyboard(etFromInputPageIndex);
                }
            }
            updateDoneBtnStatus();
        });
        etFromInputPageIndex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateDoneBtnStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etToInputPageIndex.setOnFocusChangeListener((view, b) -> {
            if (b) {
                if (rbToPageLocationHome.isChecked() || rbToPageLocationLast.isChecked()) {
                    rbToPageLocationBefore.setChecked(true);
                    CViewUtils.showKeyboard(etToInputPageIndex);
                }
            } else {
                CViewUtils.hideKeyboard(etToInputPageIndex);
            }
        });
        rgInsertTo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_tools_edit_page_insert_location_homepage) {
                etToInputPageIndex.setFocusableInTouchMode(false);
                etToInputPageIndex.clearFocus();
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_lastpage) {
                etToInputPageIndex.setFocusableInTouchMode(false);
                etToInputPageIndex.clearFocus();
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_before) {
                if (!etToInputPageIndex.isFocused()) {
                    etToInputPageIndex.requestFocus();
                    CViewUtils.showKeyboard(etToInputPageIndex);
                }
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_after) {
                if (!etToInputPageIndex.isFocused()) {
                    etToInputPageIndex.requestFocus();
                    CViewUtils.showKeyboard(etToInputPageIndex);
                }
            }
            updateDoneBtnStatus();
        });
        etToInputPageIndex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateDoneBtnStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toolBar.setBackBtnClickListener(v -> dismiss());
        toolBar.setOnDoneClickCallback(() -> {
            if (selectDocument == null) {
                Toast.makeText(getContext(), R.string.tools_page_edit_select_file_tips, Toast.LENGTH_LONG).show();
            } else {
                boolean format = true;
                if (rbFromPageLocationSpecify.isChecked()) {
                    Pattern p = Pattern.compile(selectPageRegex);
                    Matcher m = p.matcher(etFromInputPageIndex.getText().toString());
                    format = m.matches();
                }
                if (!format) {
                    Toast.makeText(getContext(), R.string.tools_page_choose_skip_input_error, Toast.LENGTH_SHORT).show();
                } else {
                    insertPdfPage();
                }
            }
        });
        toolBar.setTitle(R.string.tools_page_edit_insert_page_pdf);
        toolBar.showEditButton(false);
        toolBar.showSelectButton(false);
        toolBar.showDoneButton(true);
        ivFileNameRight.setOnClickListener(this);

        if (selectDocument != null) {
            if (selectDocument.getUri() != null) {
                tvFilename.setText(CUriUtil.getUriFileName(getContext(), selectDocument.getUri()));
            } else if (selectDocument.getAbsolutePath() != null) {
                String[] arr = selectDocument.getAbsolutePath().split("/");
                if (arr != null && arr.length > 0) {
                    tvFilename.setText(arr[arr.length - 1]);
                }
            }
        }
    }

    @Override
    protected void onViewCreate() {

    }

    private void updateDoneBtnStatus() {
        boolean enable = true;
        if (rbFromPageLocationSpecify.isChecked()) {
            enable = !TextUtils.isEmpty(etFromInputPageIndex.getText()) && !TextUtils.isEmpty(etFromInputPageIndex.getText().toString().trim());
        }
        if (rbToPageLocationAfter.isChecked() || rbToPageLocationBefore.isChecked()) {
            enable = !TextUtils.isEmpty(etToInputPageIndex.getText()) && !TextUtils.isEmpty(etToInputPageIndex.getText().toString().trim());
        }
        toolBar.enableDone(enable);
    }

    private int[] getPageArr(String pageStr) {
        if (TextUtils.isEmpty(pageStr)) {
            return null;
        }
        List<Integer> pageList = new ArrayList<>();
        int[] pageNum = null;
        String[] dotSplit = pageStr.split(",|，");
        if (dotSplit != null && dotSplit.length > 0) {
            try {
                for (int i = 0; i < dotSplit.length; i++) {
                    String[] lineSplit = dotSplit[i].split("-");
                    if (lineSplit == null) {
                        continue;
                    }
                    if (lineSplit.length == 1) {
                        int page = -1;
                        try {
                            page = Integer.parseInt(lineSplit[0]);
                            pageList.add(page);
                        } catch (Exception e) {

                        }
                    } else if (lineSplit.length == 2) {
                        int pageStart = -1;
                        int pageEnd = -1;
                        try {
                            pageStart = Integer.parseInt(lineSplit[0]);
                            pageEnd = Integer.parseInt(lineSplit[1]);
                            for (int j = pageStart; j <= pageEnd; j++) {
                                pageList.add(j);
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            } catch (Exception e) {

            }
            if (pageList.size() > 0) {
                pageNum = new int[pageList.size()];
                for (int i = 0; i < pageList.size(); i++) {
                    pageNum[i] = pageList.get(i) - 1;
                }
            }
        }
        return pageNum;
    }

    private void insertPdfPage() {
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return;
        }
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        if (document == null) {
            return;
        }
        if (selectDocument == null) {
            return;
        }
        int[] pageNums = null;
        if (rbFromPageLocationAll.isChecked()) {
            pageNums = new int[selectDocument.getPageCount()];
            for (int i = 0; i < pageNums.length; i++) {
                pageNums[i] = i;
            }
        } else if (rbFromPageLocationOdd.isChecked()) {
            pageNums = new int[(selectDocument.getPageCount() + 1) / 2];
            for (int i = 0, j = 0; i < selectDocument.getPageCount(); i += 2) {
                pageNums[j++] = i;
            }
        } else if (rbFromPageLocationEven.isChecked()) {
            pageNums = new int[selectDocument.getPageCount() / 2];
            for (int i = 1, j = 0; i < selectDocument.getPageCount(); i += 2) {
                pageNums[j++] = i;
            }
        } else {
            pageNums = getPageArr(etFromInputPageIndex.getText().toString());
        }
        for (int pageNum : pageNums) {
            if (pageNum >= selectDocument.getPageCount()) {
                CToastUtil.showLongToast(getContext(), R.string.tools_page_choose_skip_input_error);
                return;
            }
        }

        int insertPos = -1;
        if (rbToPageLocationHome.isChecked()) {
            insertPos = 0;
        } else if (rbToPageLocationLast.isChecked()) {
            insertPos = document.getPageCount();
        } else {
            String toStr = etToInputPageIndex.getText().toString();
            try {
                insertPos = Integer.parseInt(toStr);
            } catch (Exception e) {

            }
            if (rbToPageLocationBefore.isChecked()) {
                insertPos -= 1;
            }
        }
        int pageCount = document.getPageCount();
        if (insertPos > pageCount || insertPos < 0) {
            CToastUtil.showLongToast(getContext(), R.string.tools_page_choose_skip_input_error);
            return;
        }
        if (pageNums != null && pageNums.length > 0 && insertPos >= 0) {
            document.importPages(selectDocument, pageNums, insertPos);
            insertPages = new int[pageNums.length];
            for (int i = insertPos; i <= pageNums.length + insertPos - 1; i++) {
                insertPages[i - insertPos] = i;
            }
            dismiss();
            if (onEditDoneCallback != null) {
                onEditDoneCallback.onEditDone();
            }
        }
    }

    public int[] getInsertPagesArr() {
        return insertPages;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_tool_insert_page_right) {
            selectDocumentLauncher.launch(CFileUtils.getContentIntent());
        }
    }

    public void setOnEditDoneCallback(CPageEditBar.OnEditDoneCallback callback) {
        onEditDoneCallback = callback;
    }
}
