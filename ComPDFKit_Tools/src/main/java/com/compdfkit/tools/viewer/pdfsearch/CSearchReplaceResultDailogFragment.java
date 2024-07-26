package com.compdfkit.tools.viewer.pdfsearch;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.viewer.pdfsearch.adapter.CSearchPDFTextRecyclerviewAdapter;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchReplaceInfo;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CSearchReplaceResultDailogFragment extends CBasicBottomSheetDialogFragment {
    private View mContentView;

    private CToolBar toolBar;

    private RecyclerView recyclerView;

    private ConstraintLayout clSearchResultEmpty;

    private List<CSearchTextInfo> searchTextInfos = null;
    private EditText etSearchKeywords;
    private CSearchPDFTextRecyclerviewAdapter searchTextAdapter;

    private boolean enableSearchResultList = true;
    Lock lock = new ReentrantLock();
    protected CSearchReplaceToolbar.OnExitSearchListener onExitSearchListener = null;

    private AsyncTask<Void, Void, List<CSearchTextInfo>> searchTask;

    private CSearchPDFTextRecyclerviewAdapter.OnClickSearchItemListener onClickSearchItemListener;

    private CPDFReaderView readerView;
    private CPDFDocument tpdfDocument;

    private boolean ignoreCase = true;

    private boolean wholeWordsOnly = false;

    protected String keyword;
    protected Context context;
    SparseArray<ArrayList<CSearchReplaceInfo>> results = null;
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
        return R.layout.tools_search_keywords_list_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        mContentView = rootView;
    }

    @Override
    protected void onViewCreate() {
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        toolBar = mContentView.findViewById(R.id.id_search_head);
        recyclerView = mContentView.findViewById(R.id.id_reader_search_recyclerView);
        clSearchResultEmpty = mContentView.findViewById(R.id.cl_search_result_empty_view);
        AppCompatTextView tvResultInfo = mContentView.findViewById(R.id.tv_search_result);
        if (searchTextInfos != null) {
            tvResultInfo.setText(getContext().getString(R.string.tools_search_result_found, searchTextInfos.size()));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        searchTextAdapter = new CSearchPDFTextRecyclerviewAdapter();
        recyclerView.setAdapter(searchTextAdapter);
        if (null != searchTextInfos && searchTextInfos.size() > 0) {
            searchTextAdapter.clearList();
            searchTextAdapter.addList(searchTextInfos);
            searchTextAdapter.notifyDataSetChanged();
        }else {
            clSearchResultEmpty.setVisibility(View.VISIBLE);
        }
        toolBar.setBackBtnClickListener(view -> {
            dismiss();
        });
        searchTextAdapter.setOnClickSearchItemListener(onClickSearchItemListener);
    }

    ArrayList<CSearchReplaceInfo> getEditSearchReplaceInfo() {
        if (results == null) {
            return null;
        }
        ArrayList<CSearchReplaceInfo> infoList = new ArrayList<>();
        for (int i = 0; i < readerView.getPageCount();i++) {
            ArrayList<CSearchReplaceInfo> selectionList = results.get(i);

            CPDFPage page = tpdfDocument.pageAtIndex(i);
            if (page == null) {
                continue;
            }
            CPDFTextPage textPage = page.getTextPage();

            if (selectionList != null && selectionList.size() > 0) {
                CSearchReplaceInfo headInfo = new CSearchReplaceInfo(context,i,  keyword,0, false);
                headInfo.initHighLightTextData(context, textPage,null);
                infoList.add(headInfo);
            }
        }
        return infoList;
    }

    public void setKeyword(String word) {
        keyword = word;
    }

    public void setSearchTextInfos(List<CSearchTextInfo> searchTextInfos) {
        this.searchTextInfos = searchTextInfos;
        if (null != searchTextAdapter) {
            searchTextAdapter.clearList();
            searchTextAdapter.addList(searchTextInfos);
            searchTextAdapter.notifyDataSetChanged();
        }
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

    public void setOnClickSearchItemListener(CSearchPDFTextRecyclerviewAdapter.OnClickSearchItemListener listener) {
        onClickSearchItemListener = listener;
        if (null != searchTextAdapter) {
            searchTextAdapter.setOnClickSearchItemListener(listener);
        }
    }

    private void showSearchSetting() {
        FragmentManager fragmentManager = null;
        if (getContext() instanceof FragmentActivity) {
            fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        }
        if (fragmentManager != null) {
            CSearchSettingsDialog settingsDialog = CSearchSettingsDialog.newInstance(ignoreCase, wholeWordsOnly);
            settingsDialog.setIgnoreCaseCheckedChangeListener((buttonView, isChecked) -> {
                ignoreCase = isChecked;
            });
            settingsDialog.setWholeWordsOnlyCheckedChangeListener((buttonView, isChecked) -> {
                wholeWordsOnly = isChecked;
            });
            settingsDialog.show(fragmentManager, "searchSettingDialog");
        }
    }
}
