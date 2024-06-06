package com.compdfkit.tools.viewer.pdfsearch;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;
import com.compdfkit.tools.viewer.pdfsearch.data.CPDFSearchKeywordsDatas;
import com.compdfkit.ui.textsearch.ITextSearcher;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CSearchToolbar extends RelativeLayout implements View.OnClickListener {

    private EditText etSearchKeywords;

    private CPDFViewCtrl pdfView;

    OnExitSearchListener onExitSearchListener = null;

    OnSearchResultListener onSearchResultListener = null;

    List<CSearchTextInfo> searchTextInfos = null;

    Lock lock = new ReentrantLock();

    private AsyncTask<Void, Void, List<CSearchTextInfo>> searchTask;
    private ImageView ivSearchResultList;

    private boolean enableSearchResultList = true;

    public CSearchToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CSearchToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    private void init(Context context) {
        inflate(context, R.layout.tools_search_keywords_toolbar, this);
        CViewUtils.applyViewBackground(this);
        AppCompatImageView tvComplete = findViewById(R.id.iv_search_back);
        etSearchKeywords = findViewById(R.id.et_search_keywords);
        ImageView ivNext = findViewById(R.id.iv_search_next);
        ImageView ivPrevious = findViewById(R.id.iv_search_previous);
        ivSearchResultList = findViewById(R.id.iv_search_list);
        tvComplete.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivSearchResultList.setOnClickListener(this);

        etSearchKeywords.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                startSearch(etSearchKeywords.getText().toString(), 1, list -> {
                    searchTextInfos = list;
                    boolean hasResult = list.size() > 0;
                    ivPrevious.setVisibility(hasResult ? VISIBLE : INVISIBLE);
                    ivNext.setVisibility(hasResult ? VISIBLE : INVISIBLE);
                    if (enableSearchResultList){
                        ivSearchResultList.setVisibility(hasResult ? VISIBLE : INVISIBLE);
                    }
                    if (hasResult) {
                        CSearchTextInfo searchTextInfo = searchTextInfos.get(0);
                        pdfView.getCPdfReaderView().getTextSearcher().searchBegin(searchTextInfo.page, searchTextInfo.textRangeIndex);
                    }else {
                        resetSearch();
                        CToastUtil.showLongToast(context,R.string.tools_sorry_no_contents);
                    }
                });
                etSearchKeywords.clearFocus();
                CViewUtils.hideKeyboard(etSearchKeywords);
                return false;
            }
            return false;
        });
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

    private void startSearch(String keywords, int searchCount, OnSearchResultListener searchResultListener) {
        if (TextUtils.isEmpty(keywords)) {
            resetSearch();
            cancelTask();
            return;
        }
        cancelTask();
        searchTask = new AsyncTask<Void, Void, List<CSearchTextInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<CSearchTextInfo> doInBackground(Void... voids) {
                return CPDFSearchKeywordsDatas.startSearch(pdfView.getCPdfReaderView(), keywords, searchCount);
            }

            @Override
            protected void onPostExecute(List<CSearchTextInfo> list) {
                super.onPostExecute(list);
                if (searchResultListener != null) {
                    searchResultListener.result(list);
                }
            }
        };
        searchTask.execute();
    }

    private void previous() {
        pdfView.getCPdfReaderView().getTextSearcher().searchBackward();
    }

    private void next() {
        pdfView.getCPdfReaderView().getTextSearcher().searchForward();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_search_back) {
            cancelTask();
            ITextSearcher searcher = pdfView.getCPdfReaderView().getTextSearcher();
            if (searcher != null) {
                searcher.cancelSearch();
                pdfView.getCPdfReaderView().invalidateAllChildren();
            }
            hideKeyboard();
            if (null != onExitSearchListener) {
                onExitSearchListener.exitSearch();
            }
            etSearchKeywords.clearFocus();
            etSearchKeywords.setText("");
        } else if (id == R.id.iv_search_next) {
            next();
        } else if (id == R.id.iv_search_previous) {
            previous();
        } else if (id == R.id.iv_search_list) {
            hideKeyboard();
            startSearch(etSearchKeywords.getText().toString(), Integer.MAX_VALUE, list -> {
                if (null == list || list.size() <= 0) {
                    Toast.makeText(getContext(), getContext().getString(R.string.tools_sorry_no_contents), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (onSearchResultListener != null) {
                    onSearchResultListener.result(list);
                }
            });
        }
    }

    private void resetSearch() {
        if (pdfView != null) {
            ITextSearcher searcher = pdfView.getCPdfReaderView().getTextSearcher();
            if (searcher != null) {
                searcher.cancelSearch();
                pdfView.getCPdfReaderView().invalidateAllChildren();
            }
        }
    }

    public void enableSearchResultList(boolean enable){
        enableSearchResultList = enable;
    }

    public void showKeyboard(){
        if (etSearchKeywords != null) {
            CViewUtils.showKeyboard(etSearchKeywords);
        }
    }

    public void hideKeyboard(){
        if (etSearchKeywords != null){
            CViewUtils.hideKeyboard(etSearchKeywords);
        }
    }

    public void setExitSearchListener(OnExitSearchListener onExitSearchListener) {
        this.onExitSearchListener = onExitSearchListener;
    }

    public void onSearchQueryResults(OnSearchResultListener searchResultListener) {
        this.onSearchResultListener = searchResultListener;
    }

    public interface OnSearchResultListener {
        void result(List<CSearchTextInfo> list);
    }

    public interface OnExitSearchListener {
        void exitSearch();
    }
}
