/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.action;


import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.google.android.material.tabs.TabLayout;

public class CActionEditDialogFragment extends CBasicBottomSheetDialogFragment implements View.OnClickListener {

    public static final String EXTRA_PAGE_COUNT = "extra_page_count";

    public static final String EXTRA_CURRENT_PAGE = "extra_current_page";

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_EMAIL = "extra_email";

    private static final String EXTRA_SELECT_INDEX = "select_index";

    private int selectIndex = 0;

    private TabLayout tabLayout;

    private AppCompatEditText etText;

    private AppCompatButton btnSave;

    private int pageCount = 1;

    private int currentPage = -1;

    private String url = "";

    private String email = "";

    private COnActionInfoChangeListener cOnActionInfoChangeListener;

    private boolean isCreate;

    private boolean showEmail = true;

    public static CActionEditDialogFragment newInstance(int pageCount) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_PAGE_COUNT, pageCount);
        args.putInt(EXTRA_SELECT_INDEX, 0);
        CActionEditDialogFragment fragment = new CActionEditDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CActionEditDialogFragment newInstanceWithUrl(int pageCount, String url) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_PAGE_COUNT, pageCount);
        args.putString(EXTRA_URL, url);
        args.putInt(EXTRA_SELECT_INDEX, 0);
        CActionEditDialogFragment fragment = new CActionEditDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CActionEditDialogFragment newInstanceWithPage(int pageCount, int currentPage) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_PAGE_COUNT, pageCount);
        args.putInt(EXTRA_CURRENT_PAGE, currentPage);
        args.putInt(EXTRA_SELECT_INDEX, 1);
        CActionEditDialogFragment fragment = new CActionEditDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CActionEditDialogFragment newInstanceWithEmail(int pageCount, String email) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_PAGE_COUNT, pageCount);
        args.putString(EXTRA_EMAIL, email);
        args.putInt(EXTRA_SELECT_INDEX, 2);
        CActionEditDialogFragment fragment = new CActionEditDialogFragment();
        fragment.setArguments(args);
        return fragment;
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
        return R.layout.tools_properties_action_edit_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        tabLayout = rootView.findViewById(R.id.tab_layout);
        etText = rootView.findViewById(R.id.et_text);
        btnSave = rootView.findViewById(R.id.btn_save);
        CToolBar toolBar = rootView.findViewById(R.id.cl_tool_bar);
        toolBar.setBackBtnClickListener(v -> dismiss());
        btnSave.setOnClickListener(this);
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEnableSaveBtn();
                if (tabLayout.getSelectedTabPosition() == 0) {
                    url = TextUtils.isEmpty(s) ? "" : s.toString();
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    if (TextUtils.isEmpty(s)){
                        currentPage = 0;
                    }else {
                        try {
                            currentPage = Integer.parseInt(s.toString().trim());
                        }catch (Exception e){
                            currentPage = 0;
                        }
                    }
                } else if (tabLayout.getSelectedTabPosition() == 2) {
                   email = TextUtils.isEmpty(s) ? "" : s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onViewCreate() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            pageCount = getArguments().getInt(EXTRA_PAGE_COUNT, 1);
            url = getArguments().getString(EXTRA_URL, "");
            email = getArguments().getString(EXTRA_EMAIL, "");
            currentPage = getArguments().getInt(EXTRA_CURRENT_PAGE, -1);
            selectIndex = getArguments().getInt(EXTRA_SELECT_INDEX, 0);
            if (savedInstanceState != null && savedInstanceState.containsKey("show_email")){
                showEmail = savedInstanceState.getBoolean("show_email");
            }
        }
        initTabLayout();
        tabLayout.selectTab(tabLayout.getTabAt(selectIndex), true);
        switchLinkType(true);
    }

    private void initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tools_url), true);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tools_page));
        if (showEmail){
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tools_email));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchLinkType(false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setShowEmail(boolean showEmail) {
        this.showEmail = showEmail;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            createLink();
        } else if (v.getId() == R.id.iv_close) {
            isCreate = false;
            dismiss();
        }
    }

    private void switchLinkType(boolean showKeyboard) {

        if (tabLayout.getSelectedTabPosition() == 0) {
            etText.setHint(R.string.tools_compdf_website);
            etText.setText(url);
            etText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            etText.setHint("1~" + pageCount);
            String text = currentPage <=0 ? "" : String.valueOf(currentPage);
            etText.setText(text);
            etText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (tabLayout.getSelectedTabPosition() == 2) {
            etText.setHint(R.string.tools_compdf_email);
            etText.setText(email);
            etText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        checkEnableSaveBtn();
        if (showKeyboard) {
            CViewUtils.showKeyboard(etText);
            if (!TextUtils.isEmpty(etText.getText())){
                etText.setSelection(etText.getText().length());
                etText.requestFocus();
            }
        }
    }

    private void checkEnableSaveBtn() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            btnSave.setEnabled(etText.getText() != null && etText.getText().toString().trim().length() > 0);
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            try {
                if (!TextUtils.isEmpty(etText.getText())) {
                    int page = Integer.parseInt(etText.getText().toString());
                    btnSave.setEnabled(page > 0 && page <= pageCount);
                }else {
                    btnSave.setEnabled(false);
                }
            }catch (Exception e){
                btnSave.setEnabled(false);
            }
        } else if (tabLayout.getSelectedTabPosition() == 2) {
            btnSave.setEnabled(etText.getText() != null && etText.getText().toString().trim().length() > 0);
        }
    }

    private void createLink() {
        if (etText.getText() == null) {
            return;
        }
        if (tabLayout.getSelectedTabPosition() == 0) {
            String url = etText.getText().toString().trim();
            if (cOnActionInfoChangeListener != null) {
                isCreate = true;
                cOnActionInfoChangeListener.createWebsiteLink(url);
            }
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            try {
                String page = etText.getText().toString().trim();
                if (cOnActionInfoChangeListener != null) {
                    isCreate = true;
                    cOnActionInfoChangeListener.createPageLink(Integer.parseInt(page));
                }
            } catch (NumberFormatException e) {

            }
        } else if (tabLayout.getSelectedTabPosition() == 2) {
            String email = etText.getText().toString().trim();
            if (cOnActionInfoChangeListener != null) {
                isCreate = true;
                if (!email.contains("mailto:")) {
                    cOnActionInfoChangeListener.createEmailLink("mailto:" + email);
                } else {
                    cOnActionInfoChangeListener.createEmailLink(email);
                }
            }
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        CViewUtils.hideKeyboard(etText);
        super.dismiss();
        if (cOnActionInfoChangeListener != null && !isCreate) {
            cOnActionInfoChangeListener.cancel();
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (cOnActionInfoChangeListener != null) {
            cOnActionInfoChangeListener.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("show_email", showEmail);
        super.onSaveInstanceState(outState);
    }

    public void setOnLinkInfoChangeListener(COnActionInfoChangeListener cOnActionInfoChangeListener) {
        this.cOnActionInfoChangeListener = cOnActionInfoChangeListener;
    }

    public interface COnActionInfoChangeListener {

        void cancel();

        void createWebsiteLink(String url);

        void createEmailLink(String email);

        void createPageLink(int page);
    }
}
