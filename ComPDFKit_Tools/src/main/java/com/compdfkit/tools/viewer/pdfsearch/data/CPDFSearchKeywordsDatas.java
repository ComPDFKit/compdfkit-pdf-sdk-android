/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfsearch.data;


import android.content.Context;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextRange;
import com.compdfkit.core.page.CPDFTextSearcher;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.textsearch.ITextSearcher;

import java.util.ArrayList;
import java.util.List;


public class CPDFSearchKeywordsDatas {

    public static List<CSearchTextInfo> startSearch(CPDFReaderView readerView, String keywords, int searchCount) {
        return startSearch(readerView, keywords, searchCount, CPDFTextSearcher.PDFSearchOptions.PDFSearchCaseInsensitive);
    }

    public static List<CSearchTextInfo> startSearch(CPDFReaderView readerView, String keywords, int searchCount, int searchOptions) {
        ITextSearcher textSearcher = readerView.getTextSearcher();
        if (null == textSearcher) {
            return new ArrayList<>();
        }
        CPDFDocument document = readerView.getPDFDocument();
        if (null == document) {
            return new ArrayList<>();
        }
        textSearcher.setSearchConfig(keywords, searchOptions);
        List<CSearchTextInfo> searchTextInfoList = new ArrayList<>();
        for (int i = 0; i < document.getPageCount(); i++) {
            CPDFPage page = document.pageAtIndex(i);
            CPDFTextPage textPage = page.getTextPage();
            if ((null == textPage) || !textPage.isValid()) {
                continue;
            }
            final List<CSearchTextInfo> searchPageContent = findSearchText(readerView.getContext(), textSearcher, textPage, keywords, i);
            if (searchPageContent.size() > 0) {
                final CSearchTextInfo pageTextInfo = new CSearchTextInfo(readerView.getContext(), i, keywords, 0, true);
                pageTextInfo.initHighLightTextData(readerView.getContext(), null, null);
                searchTextInfoList.add(pageTextInfo);
                searchTextInfoList.addAll(searchPageContent);
            }
            if (searchTextInfoList.size() >= searchCount) {
                return searchTextInfoList;
            }
        }
        return searchTextInfoList;
    }

    public static List<CSearchTextInfo> findSearchText(Context context, ITextSearcher textSearcher, CPDFTextPage textPage, String keyword, int page) {
        ArrayList<CPDFTextRange> textRanges = textSearcher.searchKeyword(page);
        ArrayList<CSearchTextInfo> searchTextInfos = new ArrayList<>(textRanges.size());
        for (int i = 0; i < textRanges.size(); i++) {
            CSearchTextInfo textInfo = new CSearchTextInfo(context, page, keyword, i, false);
            textInfo.initHighLightTextData(context, textPage, textRanges.get(i));
            searchTextInfos.add(textInfo);
        }
        return searchTextInfos;
    }
}
