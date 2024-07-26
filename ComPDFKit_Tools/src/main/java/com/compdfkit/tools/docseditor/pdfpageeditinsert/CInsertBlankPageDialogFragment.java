package com.compdfkit.tools.docseditor.pdfpageeditinsert;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.docseditor.CPageEditBar;

public class CInsertBlankPageDialogFragment extends CBasicBottomSheetDialogFragment implements View.OnClickListener {

    public static final String EXTRA_SHOW_INSERT_LOCATION = "extra_show_insert_location";

    public static final String EXTRA_DEFAULT_VISIBLE_PAGE_SIZE = "extra_default_visible_page_size";

    public static final String EXTRA_TITLE = "extra_title";

    public static final String EXTRA_SAVE_BUTTON_TEXT = "extra_save_button_text";

    private CPageEditBar toolBar;
    private CPDFDocument document;
    private ConstraintLayout clPagesize;
    private AppCompatTextView tvCurPageSize;
    private RadioGroup rgPageSize;
    private AppCompatImageView ivDirectionV;
    private AppCompatImageView ivDirectionH;
    private AppCompatImageView ivRightArrow;
    private AppCompatRadioButton rbHomePage;
    private AppCompatRadioButton rbLastPage;
    private AppCompatRadioButton rbToPageLocationBefore;
    private AppCompatRadioButton rbToPageLocationAfter;
    private AppCompatEditText etInputPageIndex;

    private ConstraintLayout clInsertLocation;

    private RadioGroup rgInsertTo;

    private CPageEditBar.OnEditDoneCallback onEditDoneCallback;

    private int pageIndex;

    public static CInsertBlankPageDialogFragment newInstance() {
        return new CInsertBlankPageDialogFragment();
    }

    public void setDocument(CPDFDocument document) {
        this.document = document;
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
        return R.layout.tools_pageedit_insert_blank_page_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        toolBar = rootView.findViewById(R.id.page_edit_tool_bar);
        ivDirectionV = rootView.findViewById(R.id.iv_tool_insert_page_verticel);
        ivDirectionH = rootView.findViewById(R.id.iv_tool_insert_page_horizontal);
        ivRightArrow = rootView.findViewById(R.id.iv_tool_insert_page_right);
        tvCurPageSize = rootView.findViewById(R.id.iv_tool_insert_page_cur_size);
        clPagesize = rootView.findViewById(R.id.cl_pagesize);
        rgPageSize = rootView.findViewById(R.id.rg_tools_edit_page_insertpage_pagesize);
        rbHomePage = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_homepage);
        rbLastPage = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_lastpage);
        rbToPageLocationBefore = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_before);
        rbToPageLocationAfter = rootView.findViewById(R.id.rb_tools_edit_page_insert_location_after);
        etInputPageIndex = rootView.findViewById(R.id.et_tool_edit_page_enterpage);
        rgInsertTo = rootView.findViewById(R.id.rp_tools_edit_page_insertpage_location);
        clInsertLocation = rootView.findViewById(R.id.cl_insert_location);
        etInputPageIndex.setOnFocusChangeListener((view, focus) -> {
            if (!focus) {
                CViewUtils.hideKeyboard(etInputPageIndex);
            }else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
                    if (rbHomePage.isChecked() || rbLastPage.isChecked()) {
                        rbToPageLocationBefore.setChecked(true);
                        CViewUtils.showKeyboard(etInputPageIndex);
                    }
                }
            }
        });
        etInputPageIndex.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (rbHomePage.isChecked() || rbLastPage.isChecked()) {
                    rbToPageLocationBefore.setChecked(true);
                    CViewUtils.showKeyboard(etInputPageIndex);
                }
            }
        });
        etInputPageIndex.addTextChangedListener(new TextWatcher() {
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
        toolBar.setOnDoneClickCallback(this::insertBlankPage);
        toolBar.setTitle(R.string.tools_page_edit_insert_page_blank);
        toolBar.showEditButton(false);
        toolBar.showSelectButton(false);
        toolBar.showDoneButton(true);
        ivDirectionV.setSelected(true);
        ivDirectionV.setOnClickListener(this);
        ivDirectionH.setOnClickListener(this);
        ivRightArrow.setOnClickListener(this);

        rgInsertTo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_tools_edit_page_insert_location_homepage) {
                etInputPageIndex.setFocusableInTouchMode(false);
                etInputPageIndex.clearFocus();
                updateDoneBtnStatus();
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_lastpage) {
                etInputPageIndex.setFocusableInTouchMode(false);
                etInputPageIndex.clearFocus();
                updateDoneBtnStatus();
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_before) {
                if (!etInputPageIndex.isFocused()) {
                    etInputPageIndex.requestFocus();
                    CViewUtils.showKeyboard(etInputPageIndex);
                }
                updateDoneBtnStatus();
            } else if (checkedId == R.id.rb_tools_edit_page_insert_location_after) {
                if (!etInputPageIndex.isFocused()) {
                    etInputPageIndex.requestFocus();
                    CViewUtils.showKeyboard(etInputPageIndex);
                }
                updateDoneBtnStatus();
            }
        });

        if (getArguments() != null) {
            boolean showInsertLocation =  getArguments().getBoolean(EXTRA_SHOW_INSERT_LOCATION, true);
            boolean visiblePageSize = getArguments().getBoolean(EXTRA_DEFAULT_VISIBLE_PAGE_SIZE, false);
            clInsertLocation.setVisibility(showInsertLocation ?View.VISIBLE : View.GONE);

            if (visiblePageSize) {
                ivRightArrow.performClick();
            }

            String title = getArguments().getString(EXTRA_TITLE, getString(R.string.tools_document_info) );
            toolBar.setTitle(title);

            String doneBtnText = getArguments().getString(EXTRA_SAVE_BUTTON_TEXT, getString(R.string.tools_save_title));
            toolBar.setDoneButtonText(doneBtnText);
        }
    }

    public int getInsertPageIndex() {
        return pageIndex;
    }

    private void insertBlankPage() {
        if (document == null) {
            return;
        }
        String pageStr = etInputPageIndex.getText().toString();
        pageIndex = 1;
        if (rbHomePage.isChecked()) {
            pageIndex = 0;
        } else if (rbLastPage.isChecked()) {
            pageIndex = document.getPageCount();
        } else {
            if (!TextUtils.isEmpty(pageStr)) {
                try {
                    pageIndex = Integer.parseInt(pageStr);
                } catch (Exception e) {

                }
            }
            if (pageIndex >= 1) {
                if (rbToPageLocationBefore.isChecked()) {
                    pageIndex -= 1;
                }
            }
        }
        int pageCount = document.getPageCount();
        if (pageIndex > pageCount) {
            CToastUtil.showLongToast(getContext(), R.string.tools_page_choose_skip_input_error);
            return;
        }
        CViewUtils.hideKeyboard(etInputPageIndex);
        String pageSize = "";
        for (int i = 0; i < rgPageSize.getChildCount(); i++) {
            if (((RadioButton) rgPageSize.getChildAt(i)).isChecked()) {
                pageSize = (String) rgPageSize.getChildAt(i).getTag();
                break;
            }
        }
        if (!TextUtils.isEmpty(pageSize)) {
            String[] size = pageSize.split("\\*");
            if (size != null && size.length == 2) {
                int width = Integer.parseInt(size[0]);
                int height = Integer.parseInt(size[1]);
                if (ivDirectionH.isSelected()) {
                    document.insertBlankPage(pageIndex, height, width);
                } else {
                    document.insertBlankPage(pageIndex, width, height);
                }
                dismiss();
                if (onEditDoneCallback != null) {
                    onEditDoneCallback.onEditDone();
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private void initPageSizeGroup() {
        String[] sizeArr = getContext().getResources().getStringArray(R.array.tools_page_edit_pagesize_name);
        String[] sizeValueArr = getContext().getResources().getStringArray(R.array.tools_page_edit_pagesize_value);
        if (sizeArr == null) {
            return;
        }
        for (int i = 0; i < sizeArr.length; i++) {
            AppCompatRadioButton radio = (AppCompatRadioButton) LayoutInflater.from(getContext()).inflate(R.layout.tools_pageedit_pagesize_item, null);
            RadioGroup.LayoutParams layoutParams =
                    new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CDimensUtils.dp2px(getContext(), 50));
            radio.setLayoutParams(layoutParams);
            radio.setId(View.generateViewId());
            if (i == 1) {
                radio.setChecked(true);
                tvCurPageSize.setText(sizeArr[i]);
            }
            radio.setText(sizeArr[i]);
            radio.setTag(sizeValueArr[i]);
            radio.setOnClickListener(view -> {
                tvCurPageSize.setText(((RadioButton) view).getText());
            });
            rgPageSize.addView(radio, i);
        }
    }

    @Override
    protected void onViewCreate() {
        initPageSizeGroup();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_tool_insert_page_verticel) {
            boolean select = ivDirectionV.isSelected();
            if (!select) {
                ivDirectionV.setSelected(true);
                ivDirectionH.setSelected(false);
            }
        } else if (id == R.id.iv_tool_insert_page_horizontal) {
            boolean select = ivDirectionH.isSelected();
            if (!select) {
                ivDirectionH.setSelected(true);
                ivDirectionV.setSelected(false);
            }
        } else if (id == R.id.iv_tool_insert_page_right) {
            boolean select = ivRightArrow.isSelected();
            ivRightArrow.setSelected(!select);
            if (!select == true) {
                clPagesize.setVisibility(View.VISIBLE);
            } else {
                clPagesize.setVisibility(View.GONE);
            }
        }
    }

    private void updateDoneBtnStatus() {
        boolean enable = true;
        if (rbToPageLocationAfter.isChecked() || rbToPageLocationBefore.isChecked()) {
            enable = !TextUtils.isEmpty(etInputPageIndex.getText()) && !TextUtils.isEmpty(etInputPageIndex.getText().toString().trim());
        }
        toolBar.enableDone(enable);
    }

    public void setOnEditDoneCallback(CPageEditBar.OnEditDoneCallback callback) {
        onEditDoneCallback = callback;
    }
}
