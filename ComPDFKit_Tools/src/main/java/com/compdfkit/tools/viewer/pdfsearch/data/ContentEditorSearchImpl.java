/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.viewer.pdfsearch.data;

import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.compdfkit.core.edit.CPDFEditFindSelection;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextSearcher;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.viewer.pdfsearch.bean.CEditSearchReplaceInfo;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;
import com.compdfkit.ui.edit.CPDFEditTextSearchReplace;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.ArrayList;
import java.util.List;

/**
 * Content editing and search, including search and replace function,
 * Please make sure that CPDFReaderView.getViewMode() is ViewMode.PDFEDIT mode when searching
 */
public class ContentEditorSearchImpl implements CPDFSearch {

    public CPDFReaderView readerView;

    public ContentEditorSearchImpl(CPDFReaderView readerView) {
        this.readerView = readerView;
    }

    @Override
    public CPDFReaderView getReaderView() {
        return readerView;
    }

    @Nullable
    @Override
    public CSearchTextInfo searchFirst(String keywords, boolean ignoreCase, boolean wholeWordsOnly) {
        List<CSearchTextInfo> list = getSearchResults(keywords, ignoreCase, wholeWordsOnly);
        if (list != null && list.size() >= 2){
            return list.get(1);
        }
        return null;
    }

    @Override
    public List<CSearchTextInfo> getSearchResults(String keywords, boolean ignoreCase, boolean wholeWordsOnly) {
        ArrayList<CSearchTextInfo> infoList = new ArrayList<>();
        if (readerView == null) {
            return infoList;
        }
        if (readerView.getViewMode() != CPDFReaderView.ViewMode.PDFEDIT) {
            CLog.e("ContentEditorSearchImpl", "please set cpdfReaderView.setViewMode(ViewMode.PDFEDIT)");
            return infoList;
        }
        CPDFEditTextSearchReplace editTextSearchReplace = readerView.getEditTextSearchReplace();
        editTextSearchReplace.clear();
        editTextSearchReplace.setSearchConfig(keywords, getSearchOptions(ignoreCase, wholeWordsOnly));
        SparseArray<ArrayList<CPDFEditFindSelection>> results = editTextSearchReplace.findSelections();
        if (results == null || results.size() == 0) {
            return infoList;
        }
        for (int i = 0; i < readerView.getPageCount(); i++) {
            ArrayList<CPDFEditFindSelection> selectionList = results.get(i);
            CPDFPage page = readerView.getPDFDocument().pageAtIndex(i);
            if (page == null) {
                continue;
            }
            CPDFTextPage textPage = page.getTextPage();
            if (selectionList != null && selectionList.size() > 0) {
                CEditSearchReplaceInfo headInfo = new CEditSearchReplaceInfo(readerView.getContext(), i, keywords,true, 0,null);
                headInfo.initHighLightTextData(readerView.getContext(), null);
                infoList.add(headInfo);
                for (int j = 0; j < selectionList.size(); j++) {
                    CPDFEditFindSelection selection = selectionList.get(j);
                    int textRangeIndex = j;
                    CEditSearchReplaceInfo info = new CEditSearchReplaceInfo(readerView.getContext(), i, keywords,false, textRangeIndex, selection);
                    info.initHighLightTextData(readerView.getContext(), textPage);
                    infoList.add(info);
                }
            }
        }
        return infoList;
    }

    @Override
    public void searchBackward() {
        if (readerView != null) {
            readerView.getEditTextSearchReplace().searchBackward();
            readerView.invalidateAllChildren();
        }
    }

    @Override
    public void searchForward() {
        if (readerView != null) {
            readerView.getEditTextSearchReplace().searchForward();
            readerView.invalidateAllChildren();
        }
    }

    @Override
    public void resetSearch() {
        if (readerView != null) {
            CPDFEditTextSearchReplace editTextSearchReplace = readerView.getEditTextSearchReplace();
            if (editTextSearchReplace != null){
                editTextSearchReplace.clear();;
            }
            readerView.invalidateAllChildren();
            if (readerView.getContextMenuShowListener() != null) {
                readerView.getContextMenuShowListener().dismissContextMenu();
            }
        }
    }

    @Override
    public void select(int page, int textRangeIndex) {
        if (readerView != null){
            readerView.getEditTextSearchReplace()
                    .setCurrentSelection(page, textRangeIndex);
            readerView.invalidateAllChildren();
        }
    }

    private int tempPage;

    private int tempTextRangeIndex;

    public void recordSelectIndex(){
        if (readerView != null){
            CPDFEditTextSearchReplace editTextSearchReplace = readerView.getEditTextSearchReplace();
            tempPage = editTextSearchReplace.getCurrentPageIndex();
            tempTextRangeIndex = editTextSearchReplace.getCurrentSelectionIndex();
        }
    }

    public void restoreSelectIndex(){
        if (readerView != null){
            CPDFEditTextSearchReplace editTextSearchReplace = readerView.getEditTextSearchReplace();
            select(tempPage, tempTextRangeIndex);
        }
    }

    private int getSearchOptions(boolean ignoreCase, boolean wholeWordsOnly) {
        if (ignoreCase) {
            if (wholeWordsOnly) {
                return CPDFEditTextSearchReplace.SearchModeMatchWholeWord;
            } else {
                return CPDFTextSearcher.PDFSearchOptions.PDFSearchCaseInsensitive;
            }
        } else {
            if (wholeWordsOnly) {
                return CPDFEditTextSearchReplace.SearchModeCaseSensitive |
                        CPDFTextSearcher.PDFSearchOptions.PDFSearchMatchWholeWord;
            } else {
                return CPDFEditTextSearchReplace.SearchModeCaseSensitive;
            }
        }
    }
}
