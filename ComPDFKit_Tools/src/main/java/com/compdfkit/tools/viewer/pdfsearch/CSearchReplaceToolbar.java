/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * <p>
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
 * <p>
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
 *
 * @see CPDFSearch
 * @see CViewerSearchImpl
 * @see ContentEditorSearchImpl
 * @see ContentEditorSearchReplaceDecorator
 */
public class CSearchReplaceToolbar extends LinearLayout implements View.OnClickListener {
    private static final String IDENTIFIER_SEARCH_BACK_BUTTON = "search_back_button";
    private static final String SOURCE_SEARCH_TOOLBAR = "searchToolbar";

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

    private AppCompatTextView tvSearchCount;

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

    /**
     * Flattened match list (header entries excluded) backing the index/total indicator
     * and the previous/next navigation. Kept in sync with {@link #currentMatchIndex}.
     */
    private final List<CSearchTextInfo> matches = new ArrayList<>();

    /** Zero-based position within {@link #matches}; -1 when there is no active match. */
    private int currentMatchIndex = -1;

    /** Keywords the cached {@link #matches} were produced for; {@code null} when empty. */
    private String matchesKeywords = null;

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
        tvSearchCount = rootView.findViewById(R.id.tv_search_count);
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
                if (viewType == ViewType.SearchReplace) {
                    if (tab.getPosition() == 1) {
                        ivSearchList.setVisibility(View.GONE);
                    } else {
                        ivSearchList.setVisibility(groupSearchAfter.getVisibility());
                    }
                }
                updateSearchCount();
                CPDFReaderView readerView = pdfView.getCPdfReaderView();
                if (viewType == ViewType.SearchReplace && tabLayout.getSelectedTabPosition() == 1) {
                    if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator) {
                        ((ContentEditorSearchReplaceDecorator) cpdfSearch).setShowSearchReplaceContextMenu(true);
                    }
                    readerView.showSearchReplaceContextMenu();
                } else {
                    if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator) {
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
                // Editing the keyword invalidates the results on screen: drop them so the
                // index/total indicator and previous/next can never refer to a stale result
                // set. resetSearch() clears matchesKeywords, so this fires once per edit
                // rather than on every keystroke.
                if (TextUtils.isEmpty(s)
                        || (matchesKeywords != null && !TextUtils.equals(matchesKeywords, s))) {
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
            navigate(-1);
        } else if (v.getId() == R.id.iv_next) {
            navigate(1);
        } else if (v.getId() == R.id.iv_search_list) {
            hideKeyboard();
            if (searchTextInfos != null && !searchTextInfos.isEmpty()
                    && TextUtils.equals(matchesKeywords, etSearch.getText().toString())) {
                // searchFirst() already fetched the full result set. Reuse it instead of running
                // a second full-document pass, which would also reset the searcher's cursor and
                // drop the current highlight. The copy is deliberate: cancelTask() clears
                // searchTextInfos, and the dialog outlives this call.
                showSearchList(new ArrayList<>(searchTextInfos));
                return;
            }
            if (viewType == ViewType.SearchReplace) {
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
            if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator) {
                // Do not dismiss the loading dialog here: refreshAfterReplaceAll() re-runs the
                // search and keeps its own dialog up until the new results are in.
                ((ContentEditorSearchReplaceDecorator) cpdfSearch).replaceAll(etReplace.getText().toString(),
                        this::refreshAfterReplaceAll);
            }
        } else if (v.getId() == toolBar.getIvToolBarBackBtn().getId()) {
            closeSearch(true);
        }
    }

    private void closeSearch(boolean notifyCustomEvent) {
        cancelTask();
        resetSearch();
        hideKeyboard();
        tabLayout.selectTab(tabLayout.getTabAt(0), true);
        etSearch.clearFocus();
        etSearch.setText("");
        etReplace.clearFocus();
        etReplace.setText("");
        if (notifyCustomEvent) {
            notifySearchBackButtonTapped();
        }
        if (null != onExitSearchListener) {
            onExitSearchListener.exitSearch();
        }
    }

    private void notifySearchBackButtonTapped() {
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, CPDFCustomEventType.SEARCH_BACK_BUTTON_TAPPED);
        CPDFCustomEventCallbackHelper.getInstance().notifyClick(IDENTIFIER_SEARCH_BACK_BUTTON, extraMap);
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

    @Override
    protected void onDetachedFromWindow() {
        // The full-document traversal can outlive this view; without this a finished task
        // would touch a document the reader has already closed.
        cancelTask();
        super.onDetachedFromWindow();
    }

    /**
     * Run the search for the current keywords and select the first match.
     *
     * <p>This is a single full-document pass: the same result list drives the selection, the
     * index/total indicator and the previous/next navigation. The searcher is stateful —
     * every {@code getSearchResults()} call reconfigures it and resets its internal cursor —
     * so a second pass purely to count the matches would clobber the selection made by the
     * first one.
     */
    private void searchFirst() {
        searchFirst(true);
    }

    private void searchFirst(boolean showNoResultsToast) {
        // The cached matches belong to the previous run; drop them up front so previous/next
        // can never act on stale coordinates while the new search is running.
        matches.clear();
        currentMatchIndex = -1;
        matchesKeywords = null;
        updateSearchCount();
        String keywords = etSearch.getText().toString();
        startSearch(keywords, Integer.MAX_VALUE, list -> {
            searchTextInfos = list;
            applyMatches(keywords, list);
            boolean hasResult = !matches.isEmpty();
            groupSearchBefore.setVisibility(hasResult ? GONE : VISIBLE);
            groupSearchAfter.setVisibility(hasResult ? VISIBLE : GONE);
            if (tabLayout.getSelectedTabPosition() == 1) {
                ivSearchList.setVisibility(View.GONE);
            }
            if (hasResult) {
                CSearchTextInfo first = matches.get(0);
                cpdfSearch.select(first.page, first.textRangeIndex);
                updateSearchCount();
            } else {
                resetSearch();
                if (showNoResultsToast) {
                    CToastUtil.showLongToast(getContext(), R.string.tools_sorry_no_contents);
                }
            }
        });
        etSearch.clearFocus();
        CViewUtils.hideKeyboard(etSearch);
    }

    /**
     * Cache {@code list} as the match set for {@code keywords} and point the indicator at its
     * first entry. Only the flattened matches are kept — the per-page header entries produced
     * by the search implementations are dropped, so the total matches what the user can
     * actually step through.
     */
    private void applyMatches(String keywords, List<CSearchTextInfo> list) {
        matches.clear();
        if (list != null) {
            for (CSearchTextInfo info : list) {
                if (!info.isHeader) {
                    matches.add(info);
                }
            }
        }
        matchesKeywords = matches.isEmpty() ? null : keywords;
        currentMatchIndex = matches.isEmpty() ? -1 : 0;
    }

    /**
     * Re-run the search after "replace all" so the index/total indicator reflects the
     * rewritten document.
     *
     * <p>Replacing clears the searcher's state, so the cached matches are stale by
     * definition. Normally every occurrence is gone and the toolbar falls back to its
     * pre-search state; when the replacement text still contains the keyword (for example
     * replacing "a" with "ab") matches remain, and the indicator is repopulated from them.
     */
    private void refreshAfterReplaceAll() {
        CToastUtil.showLongToast(getContext(), R.string.tools_replace_all_completed);
        if (TextUtils.isEmpty(etSearch.getText())) {
            dismissLoadingDialog();
            resetSearch();
            return;
        }
        // searchFirst() clears the cache before dispatching and keeps a loading dialog up for
        // the duration, so there is no window in which previous/next can select pre-replace
        // coordinates.
        searchFirst(false);
    }

    /**
     * Move the selection {@code step} matches forward (positive) or backward (negative),
     * wrapping around at both ends, and select the resulting match.
     *
     * <p>Navigation is driven by the cached {@link #matches} rather than
     * {@code searchForward()}/{@code searchBackward()} so the indicator can never drift out
     * of sync with the highlighted match. The cache is populated by the same pass that makes
     * the first selection, so it is always ready by the time these buttons are visible; a
     * cache miss means the results are gone or stale and stepping is a no-op.
     */
    private void navigate(int step) {
        if (cpdfSearch == null) {
            return;
        }
        if (matches.isEmpty() || !TextUtils.equals(matchesKeywords, etSearch.getText().toString())) {
            return;
        }
        int size = matches.size();
        currentMatchIndex = ((currentMatchIndex + step) % size + size) % size;
        CSearchTextInfo target = matches.get(currentMatchIndex);
        cpdfSearch.select(target.page, target.textRangeIndex);
        updateSearchCount();
    }

    /** Point the indicator at the match identified by {@code page}/{@code textRangeIndex}. */
    private void syncIndicatorTo(int page, int textRangeIndex) {
        int index = indexOf(page, textRangeIndex);
        if (index >= 0) {
            currentMatchIndex = index;
            updateSearchCount();
        }
    }

    private int indexOf(int page, int textRangeIndex) {
        for (int i = 0; i < matches.size(); i++) {
            CSearchTextInfo info = matches.get(i);
            if (info.page == page && info.textRangeIndex == textRangeIndex) {
                return i;
            }
        }
        return -1;
    }

    /** Render the indicator as {@code index/total}, hiding it when there is nothing to show. */
    private void updateSearchCount() {
        if (tvSearchCount == null) {
            return;
        }
        if (currentMatchIndex < 0 || matches.isEmpty()) {
            tvSearchCount.setText("");
            tvSearchCount.setVisibility(GONE);
            return;
        }
        tvSearchCount.setText(getContext().getString(R.string.tools_search_result_index,
                currentMatchIndex + 1, matches.size()));
        // Shown on both the search and replace rows: previous/next stay active under the
        // replace tab, so the indicator has to track them there too.
        tvSearchCount.setVisibility(groupSearchAfter.getVisibility() == VISIBLE ? VISIBLE : GONE);
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
                if (cpdfSearch == null) {
                    return new ArrayList<>();
                }
                if (searchCount == 1) {
                    List<CSearchTextInfo> list = new ArrayList<>();
                    CSearchTextInfo textInfo = cpdfSearch.searchFirst(keywords, ignoreCase, wholeWordsOnly);
                    if (textInfo != null) {
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
                if (isCancelled()) {
                    // A newer search superseded this one; its results would be stale.
                    return;
                }
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
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
        if (fragmentActivity == null) {
            return;
        }
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
        settingsDialog.setDialogDismissListener(() -> {
            if (ignoreCase != tempIgnoreCase || wholeWordsOnly != tempWholeWordsOnly) {
                ignoreCase = tempIgnoreCase;
                wholeWordsOnly = tempWholeWordsOnly;
                cancelTask();
                resetSearch();
            }
        });
        settingsDialog.show(fragmentActivity.getSupportFragmentManager(), "searchSettingDialog");
    }

    private void showSearchList(List<CSearchTextInfo> list) {
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
        if (fragmentActivity == null) {
            return;
        }
        CSearchResultDialogFragment searchResultDialog = new CSearchResultDialogFragment();
        searchResultDialog.show(fragmentActivity.getSupportFragmentManager(), "searchResultDialogFragment");
        searchResultDialog.setSearchTextInfos(list);
        searchResultDialog.setOnClickSearchItemListener(clickItem -> {
            searchResultDialog.dismiss();
            pdfView.getCPdfReaderView().setDisplayPageIndex(clickItem.page);
            cpdfSearch.select(clickItem.page, clickItem.textRangeIndex);
            // Keep the index/total indicator in sync with the item picked from the list.
            syncIndicatorTo(clickItem.page, clickItem.textRangeIndex);
        });
    }

    private void resetSearch() {
        try {
            if (cpdfSearch != null) {
                cpdfSearch.resetSearch();
            }
            if (groupSearchBefore != null) {
                groupSearchBefore.setVisibility(VISIBLE);
            }
            if (groupSearchAfter != null) {
                groupSearchAfter.setVisibility(GONE);
            }
            matches.clear();
            currentMatchIndex = -1;
            matchesKeywords = null;
            if (tvSearchCount != null) {
                tvSearchCount.setText("");
                tvSearchCount.setVisibility(GONE);
            }
        }catch (Exception e){

        }
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

    private void showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dismiss();
        }
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
        if (fragmentActivity != null) {
            if (pdfView == null || pdfView.getCPdfReaderView() == null) {
                return;
            }
            if (pdfView.getCPdfReaderView().getPDFDocument().getPageCount() < 40) {
                return;
            }
            loadingDialog = CLoadingDialog.newInstance();
            loadingDialog.show(fragmentActivity.getSupportFragmentManager(), "loadingDialog");
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void exitSearch() {
        closeSearch(false);
    }

    public void setExitSearchListener(OnExitSearchListener onExitSearchListener) {
        this.onExitSearchListener = onExitSearchListener;
    }


    public void showSearchReplaceContextMenu() {
        if (viewType == ViewType.SearchReplace && tabLayout.getSelectedTabPosition() == 1) {
            if (cpdfSearch instanceof ContentEditorSearchReplaceDecorator) {
                ((ContentEditorSearchReplaceDecorator) cpdfSearch).showSearchReplaceContextMenu();
            }
        }
    }

    public interface OnExitSearchListener {
        void exitSearch();
    }
}
