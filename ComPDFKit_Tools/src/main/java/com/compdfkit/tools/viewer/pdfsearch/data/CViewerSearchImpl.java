/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.viewer.pdfsearch.data;


import com.compdfkit.core.page.CPDFTextSearcher;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.textsearch.ITextSearcher;

import java.util.List;

/**
 * Regular search, excluding replacement functionality. If you need to use search and replace features, please refer to
 * {@link ContentEditorSearchImpl}
 */
public class CViewerSearchImpl implements CPDFSearch {

    private CPDFReaderView readerView;

    public CViewerSearchImpl(CPDFReaderView readerView) {
        this.readerView = readerView;
    }

    @Override
    public CPDFReaderView getReaderView() {
        return readerView;
    }

    @Override
    public CSearchTextInfo searchFirst(String keywords, boolean ignoreCase, boolean wholeWordsOnly) {
        List<CSearchTextInfo> list = CPDFSearchKeywordsDatas.startSearch(
                readerView, keywords, 1, getSearchOptions(ignoreCase, wholeWordsOnly)
        );
        if (list != null && list.size() > 0){
            return list.get(0);
        }else {
            return null;
        }
    }

    @Override
    public List<CSearchTextInfo> getSearchResults(String keywords, boolean ignoreCase, boolean wholeWordsOnly) {
        return CPDFSearchKeywordsDatas.startSearch(
                readerView, keywords, Integer.MAX_VALUE, getSearchOptions(ignoreCase, wholeWordsOnly)
        );
    }

    @Override
    public void searchBackward() {
        if (readerView != null) {
            readerView.getTextSearcher()
                    .searchBackward();
        }
    }

    @Override
    public void searchForward() {
        if (readerView != null) {
            readerView.getTextSearcher()
                    .searchForward();
        }
    }

    @Override
    public void resetSearch() {
        if (readerView != null) {
            ITextSearcher searcher = readerView.getTextSearcher();
            if (searcher != null) {
                searcher.cancelSearch();
                readerView.invalidateAllChildren();
            }
        }
    }

    @Override
    public void select(int page, int textRangeIndex) {
        readerView.getTextSearcher().searchBegin(page, textRangeIndex);
        readerView.invalidateAllChildren();
    }

    private int getSearchOptions(boolean ignoreCase, boolean wholeWordsOnly){
        if (ignoreCase) {
            if (wholeWordsOnly) {
                return CPDFTextSearcher.PDFSearchOptions.PDFSearchCaseInsensitive |
                        CPDFTextSearcher.PDFSearchOptions.PDFSearchMatchWholeWord;
            } else {
                return CPDFTextSearcher.PDFSearchOptions.PDFSearchCaseInsensitive;
            }
        } else {
            if (wholeWordsOnly) {
                return CPDFTextSearcher.PDFSearchOptions.PDFSearchCaseSensitive |
                        CPDFTextSearcher.PDFSearchOptions.PDFSearchMatchWholeWord;
            } else {
                return CPDFTextSearcher.PDFSearchOptions.PDFSearchCaseSensitive;
            }
        }
    }
}
