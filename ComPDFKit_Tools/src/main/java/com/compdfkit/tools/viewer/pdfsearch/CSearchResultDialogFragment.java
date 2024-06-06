package com.compdfkit.tools.viewer.pdfsearch;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.viewer.pdfsearch.adapter.CSearchPDFTextRecyclerviewAdapter;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;

import java.util.List;

public class CSearchResultDialogFragment extends CBasicBottomSheetDialogFragment {
    private View mContentView;

    private CToolBar toolBar;

    private RecyclerView recyclerView;

    private ConstraintLayout clSearchResultEmpty;

    private List<CSearchTextInfo> searchTextInfos = null;

    private CSearchPDFTextRecyclerviewAdapter searchTextAdapter;

    private CSearchPDFTextRecyclerviewAdapter.OnClickSearchItemListener onClickSearchItemListener;

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
            int count = 0;
            for (CSearchTextInfo searchTextInfo : searchTextInfos) {
                if (!searchTextInfo.isHeader) {
                    count++;
                }
            }
            tvResultInfo.setText(getContext().getString(R.string.tools_search_result_found, count));
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

    public void setSearchTextInfos(List<CSearchTextInfo> searchTextInfos) {
        this.searchTextInfos = searchTextInfos;
        if (null != searchTextAdapter) {
            searchTextAdapter.clearList();
            searchTextAdapter.addList(searchTextInfos);
            searchTextAdapter.notifyDataSetChanged();
        }
    }

    public void setOnClickSearchItemListener(CSearchPDFTextRecyclerviewAdapter.OnClickSearchItemListener listener) {
        onClickSearchItemListener = listener;
        if (null != searchTextAdapter) {
            searchTextAdapter.setOnClickSearchItemListener(listener);
        }
    }
}
