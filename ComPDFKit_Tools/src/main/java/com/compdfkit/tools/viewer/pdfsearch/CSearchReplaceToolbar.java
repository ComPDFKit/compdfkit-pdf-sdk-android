/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfsearch;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.dialog.CLoadingDialog;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;
import com.compdfkit.tools.viewer.pdfsearch.data.CPDFSearch;
import com.compdfkit.tools.viewer.pdfsearch.data.CPDFSearchReplaceDatas;
import com.compdfkit.tools.viewer.pdfsearch.data.CViewerSearchImpl;
import com.compdfkit.tools.viewer.pdfsearch.data.ContentEditorSearchImpl;
import com.compdfkit.tools.viewer.pdfsearch.data.ContentEditorSearchReplaceDecorator;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Search and replace function toolbar,
 * including default search function,
 * as well as search and replace text functions in content editing.<br>
 * step1: Add this View in the layout
 * <pre>
 * {@code
 *     <com.compdfkit.tools.viewer.pdfsearch.CSearchReplaceToolbar
 *         android:id="@+id/search_toolbar_view"
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content"
 *         />
 * }
 * </pre>
 * step2: Set search type<br>
 * Default search: {@link ViewType#Search} <br>
 * Content editing search and replacement: {@link ViewType#SearchReplace}<br>
 * <pre>
 * {@code
 *      searchToolBarView.setViewType(viewType);
 * }
 * </pre>
 *
 * Please see the default search function:
 * <br>
 * <pre>
 * {@code
 *      CPDFSearch cpdfSearch = new CViewerSearchImpl(cpdfReaderView);
 *      cpdfSearch.searchFirst(String keywords, boolean ignoreCase, boolean wholeWordsOnly);
 *      cpdfSearch.getSearchResults(String keywords, boolean ignoreCase, boolean wholeWordsOnly);
 *      cpdfSearch.searchForward();
 *      cpdfSearch.searchBackward();
 * }
 * <br>
 * </pre>
 *
 * Please check for content editing, search and replacement
 * <pre>
 * {@code
 *      CPDFSearch cpdfSearch = new ContentEditorSearchReplaceDecorator(new ContentEditorSearchImpl(cpdfReaderView));
 *      cpdfSearch.searchFirst(String keywords, boolean ignoreCase, boolean wholeWordsOnly);
 *      cpdfSearch.getSearchResults(String keywords, boolean ignoreCase, boolean wholeWordsOnly);
 *      cpdfSearch.searchForward();
 *      cpdfSearch.searchBackward();
 * }
 *
 * </pre>
 * @see CPDFSearch
 * @see CViewerSearchImpl
 * @see ContentEditorSearchImpl
 * @see ContentEditorSearchReplaceDecorator
 */
public class CSearchReplaceToolbar extends LinearLayout implements View.OnClickListener {

    private CLoadingDialog loadingDialog;

    public enum ViewType {

        /**
         * Default search functionality
         */
        Search,

        /**
         * Content editing with search and replace
         */
        SearchReplace

    }

    private ViewType viewType = ViewType.Search;

    private CToolBar toolBar;

    private TabLayout tabLayout;

    private AppCompatEditText etSearch;

    private AppCompatImageView ivSearchList;

    private Group groupSearchBefore;

    private Group groupSearchAfter;

    private AppCompatEditText etReplace;

    private ConstraintLayout clReplaceLayout;

    private boolean ignoreCase = true;

    private boolean wholeWordsOnly = false;

    private CPDFViewCtrl pdfView;

    protected OnExitSearchListener onExitSearchListener = null;

    private AsyncTask<Void, Void, List<CSearchTextInfo>> searchTask;

    private List<CSearchTextInfo> searchTextInfos = null;

    Lock lock = new ReentrantLock();

    public CPDFSearch cpdfSearch;

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
        createCPDFSearcher();
    }

    public CSearchReplaceToolbar(Context context) {
        this(context, null);
    }

    public CSearchReplaceToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CSearchReplaceToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = inflate(context, R.layout.tools_search_toolbar_view, this);
        toolBar = rootView.findViewById(R.id.search_toolbar);
        tabLayout = rootView.findViewById(R.id.tab_search_type_tab_layout);
        AppCompatImageView ivSetting = rootView.findViewById(R.id.iv_search_setting);
        etSearch = rootView.findViewById(R.id.et_search);
        AppCompatImageView ivClean = rootView.findViewById(R.id.iv_clean);
        AppCompatImageView ivSearch = rootView.findViewById(R.id.iv_search);
        AppCompatImageView ivPrevious = rootView.findViewById(R.id.iv_previous);
        AppCompatImageView ivNext = rootView.findViewById(R.id.iv_next);
        ivSearchList = rootView.findViewById(R.id.iv_search_list);
        groupSearchBefore = rootView.findViewById(R.id.group_search_before);
        groupSearchAfter = rootView.findViewById(R.id.group_search_after);
        etReplace = rootView.findViewById(R.id.et_replace_with);
        AppCompatButton btnReplaceAll = rootView.findViewById(R.id.btn_replace_all);
        clReplaceLayout = rootView.findViewById(R.id.cl_replace_layout);

        ivSetting.setOnClickListener(this);
        ivClean.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivSearchList.setOnClickListener(this);
        btnReplaceAll.setOnClickListener(this);
        toolBar.setBackBtnClickListener(this);

        setViewType(viewType);
        initData();
    }

    private void createCPDFSearcher() {
        if (pdfView == null) {
            return;
        }
        if (pdfView.getCPdfReaderView().getViewMode() == CPDFReaderView.ViewMode.PDFEDIT) {
            pdfView.getCPdfReaderView().getEditTextSearchReplace().selectNextAfterReplace(false);
            cpdfSearch = new ContentEditorSearchReplaceDecorator(new ContentEditorSearchImpl(pdfView.getCPdfReaderView()));
        } else {
            cpdfSearch = new CViewerSearchImpl(pdfView.getCPdfReaderView());
        }
    }

    private void initData() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tools_search_hint));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tools_replace));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                clReplaceLayout.setVisibility(tab.getPosition() == 1 ? VISIBLE : GONE);
                if (viewType == ViewType.SearchReplace){
                    if (tab.getPosition() == 1){
                        ivSearchList.setVisibility(View.GONE);
                    }else {
                        ivSearchList.setVisibility(groupSearchAfter.getVisibility());
                    }
                }
                CPDFReaderView readerView = pdfView.getCPdfReaderView();
                if (viewType == ViewType.SearchReplace && tabLayout.getSelectedTabPosition() == 1) {
                    if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator){
                        ((ContentEditorSearchReplaceDecorator) cpdfSearch).setShowSearchReplaceContextMenu(true);
                    }
                    readerView.showSearchReplaceContextMenu();
                } else {
                    if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator){
                        ((ContentEditorSearchReplaceDecorator) cpdfSearch).setShowSearchReplaceContextMenu(false);
                    }
                    if (readerView.getContextMenuShowListener() != null) {
                        readerView.getContextMenuShowListener().dismissContextMenu();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFirst();
                return false;
            }
            return false;
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    resetSearch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etReplace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CPDFSearchReplaceDatas.getInstance().updateKeywords(TextUtils.isEmpty(s) ? "" : s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_search_setting) {
            hideKeyboard();
            showSearchSetting();
        } else if (v.getId() == R.id.iv_clean) {
            etSearch.setText("");
        } else if (v.getId() == R.id.iv_search) {
            searchFirst();
        } else if (v.getId() == R.id.iv_previous) {
            cpdfSearch.searchBackward();
        } else if (v.getId() == R.id.iv_next) {
            cpdfSearch.searchForward();
        } else if (v.getId() == R.id.iv_search_list) {
            hideKeyboard();
            if (viewType == ViewType.SearchReplace){
                if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator) {
                    ((ContentEditorSearchReplaceDecorator) cpdfSearch).recordSelectIndex();
                }
            }
            startSearch(etSearch.getText().toString(), Integer.MAX_VALUE, list -> {
                if (null == list || list.size() == 0) {
                    Toast.makeText(getContext(), getContext().getString(R.string.tools_sorry_no_contents), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator) {
                    ((ContentEditorSearchReplaceDecorator) cpdfSearch).restoreSelectIndex();
                }
                showSearchList(list);
            });
        } else if (v.getId() == R.id.btn_replace_all) {
            hideKeyboard();
            showLoadingDialog();
            if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator){
                ((ContentEditorSearchReplaceDecorator) cpdfSearch).replaceAll(etReplace.getText().toString(), ()->{
                    dismissLoadingDialog();
                });
            }
        } else if (v.getId() == toolBar.getIvToolBarBackBtn().getId()) {
            cancelTask();
            resetSearch();
            hideKeyboard();
            tabLayout.selectTab(tabLayout.getTabAt(0), true);
            etSearch.clearFocus();
            etSearch.setText("");
            etReplace.clearFocus();
            etReplace.setText("");
            if (null != onExitSearchListener) {
                onExitSearchListener.exitSearch();
            }
        }
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
        if (viewType == ViewType.Search) {
            tabLayout.setVisibility(INVISIBLE);
            clReplaceLayout.setVisibility(GONE);
        } else {
            tabLayout.setVisibility(VISIBLE);
        }
        createCPDFSearcher();
    }

    private void cancelTask() {
        lock.lock();
        try {
            if (searchTextInfos != null) {
                searchTextInfos.clear();
            }
            if (searchTask != null && !searchTask.isCancelled()) {
                searchTask.cancel(true);
            }
        } finally {
            lock.unlock();
        }
    }

    private void searchFirst() {
        startSearch(etSearch.getText().toString(), 1, list -> {
            searchTextInfos = list;
            boolean hasResult = list.size() > 0;
            groupSearchBefore.setVisibility(hasResult ? GONE : VISIBLE);
            groupSearchAfter.setVisibility(hasResult ? VISIBLE : GONE);
            if (tabLayout.getSelectedTabPosition() == 1){
                ivSearchList.setVisibility(View.GONE);
            }
            if (hasResult) {
                CSearchTextInfo searchTextInfo = searchTextInfos.get(0);
                cpdfSearch.select(searchTextInfo.page, searchTextInfo.textRangeIndex);
            } else {
                resetSearch();
                CToastUtil.showLongToast(getContext(), R.string.tools_sorry_no_contents);
            }
        });
        etSearch.clearFocus();
        CViewUtils.hideKeyboard(etSearch);
    }

    private void startSearch(String keywords, int searchCount, CSearchToolbar.OnSearchResultListener searchResultListener) {
        if (TextUtils.isEmpty(keywords)) {
            resetSearch();
            cancelTask();
            return;
        }
        cancelTask();
        showLoadingDialog();
        searchTask = new AsyncTask<Void, Void, List<CSearchTextInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<CSearchTextInfo> doInBackground(Void... voids) {
                if (searchCount == 1) {
                    List<CSearchTextInfo> list = new ArrayList<>();
                    CSearchTextInfo textInfo = cpdfSearch.searchFirst(keywords, ignoreCase, wholeWordsOnly);
                    if (textInfo != null){
                        list.add(textInfo);
                    }
                    return list;
                } else {
                    return cpdfSearch.getSearchResults(keywords, ignoreCase, wholeWordsOnly);
                }
            }

            @Override
            protected void onPostExecute(List<CSearchTextInfo> list) {
                super.onPostExecute(list);
                if (searchResultListener != null) {
                    searchResultListener.result(list);
                }
                dismissLoadingDialog();
            }
        };
        searchTask.execute();
    }
    boolean tempIgnoreCase;
    boolean tempWholeWordsOnly;
    private void showSearchSetting() {
        FragmentManager fragmentManager = null;
        if (getContext() instanceof FragmentActivity) {
            fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        }
        if (fragmentManager != null) {
            tempIgnoreCase = ignoreCase;
            tempWholeWordsOnly = wholeWordsOnly;
            CSearchSettingsDialog settingsDialog = CSearchSettingsDialog.newInstance(ignoreCase, wholeWordsOnly);
            settingsDialog.setIgnoreCaseCheckedChangeListener((buttonView, isChecked) -> {
                tempIgnoreCase = isChecked;
                CToastUtil.showLongToast(getContext(), R.string.tools_effective_immediately_after_setting);
            });
            settingsDialog.setWholeWordsOnlyCheckedChangeListener((buttonView, isChecked) -> {
                tempWholeWordsOnly = isChecked;
                CToastUtil.showLongToast(getContext(), R.string.tools_effective_immediately_after_setting);
            });
            settingsDialog.setDialogDismissListener(()-> {
                if (ignoreCase != tempIgnoreCase || wholeWordsOnly != tempWholeWordsOnly){
                    ignoreCase = tempIgnoreCase;
                    wholeWordsOnly = tempWholeWordsOnly;
                    cancelTask();
                    resetSearch();
                }
            });
            settingsDialog.show(fragmentManager, "searchSettingDialog");
        }
    }

    private void showSearchList(List<CSearchTextInfo> list) {
        FragmentManager fragmentManager = null;
        if (getContext() instanceof FragmentActivity) {
            fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        }
        if (fragmentManager != null) {
            CSearchResultDialogFragment searchResultDialog = new CSearchResultDialogFragment();
            searchResultDialog.show(fragmentManager, "searchResultDialogFragment");
            searchResultDialog.setSearchTextInfos(list);
            searchResultDialog.setOnClickSearchItemListener(clickItem -> {
                searchResultDialog.dismiss();
                pdfView.getCPdfReaderView().setDisplayPageIndex(clickItem.page);
                cpdfSearch.select(clickItem.page, clickItem.textRangeIndex);
            });
        }
    }

    private void resetSearch() {
        if (cpdfSearch != null) {
            cpdfSearch.resetSearch();
        }
        groupSearchBefore.setVisibility(VISIBLE);
        groupSearchAfter.setVisibility(GONE);
    }

    public void showKeyboard() {
        if (etSearch != null) {
            CViewUtils.showKeyboard(etSearch);
        }
    }

    public void hideKeyboard() {
        if (etSearch != null) {
            CViewUtils.hideKeyboard(etSearch);
        }
    }

    private void showLoadingDialog(){
        if (loadingDialog != null && loadingDialog.isVisible()){
            loadingDialog.dismiss();
        }
        if (getContext() instanceof FragmentActivity){
            if (pdfView == null || pdfView.getCPdfReaderView() == null){
                return;
            }
            if (pdfView.getCPdfReaderView().getPDFDocument().getPageCount() < 40) {
                return;
            }
            loadingDialog = CLoadingDialog.newInstance();
            loadingDialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "loadingDialog");
        }
    }

    private void dismissLoadingDialog(){
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void exitSearch() {
        toolBar.getIvToolBarBackBtn().performClick();
    }

    public void setExitSearchListener(OnExitSearchListener onExitSearchListener) {
        this.onExitSearchListener = onExitSearchListener;
    }


    public void showSearchReplaceContextMenu(){
        if (viewType == ViewType.SearchReplace && tabLayout.getSelectedTabPosition() == 1){
            if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator) {
                ((ContentEditorSearchReplaceDecorator) cpdfSearch).showSearchReplaceContextMenu();
            }
        }
    }

    public interface OnExitSearchListener {
        void exitSearch();
    }
}
