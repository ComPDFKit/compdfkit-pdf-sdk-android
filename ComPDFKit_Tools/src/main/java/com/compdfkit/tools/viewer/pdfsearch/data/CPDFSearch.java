/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfsearch.data;


import androidx.annotation.Nullable;

import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.List;

/**
 * Tools module search function interface, including default search function and PDF content editing, search and replacement function
 * For default search please see:
 * @see CViewerSearchImpl
 *
 * Content editing search and replace function, please check:
 * @see ContentEditorSearchImpl
 */
public interface CPDFSearch {

    CPDFReaderView getReaderView();
    /**
     * Search keywords return the first result
     * @param keywords
     * @param ignoreCase Whether to ignore case
     * @param wholeWordsOnly If {true}, then the complete word must be entered to match the keyword
     * @return
     */
    @Nullable
    CSearchTextInfo searchFirst(String keywords, boolean ignoreCase, boolean wholeWordsOnly);

    /**
     * Return results for all search keywords
     * @param keywords
     * @param ignoreCase Whether to ignore case
     * @param wholeWordsOnly If {true}, then the complete word must be entered to match the keyword
     * @return
     */
    List<CSearchTextInfo> getSearchResults(String keywords, boolean ignoreCase, boolean wholeWordsOnly);

    /**
     * Search previous result
     */
    void searchBackward();

    /**
     * Search next result
     */
    void searchForward();

    void resetSearch();

    void select(int page, int textRangeIndex);

}
