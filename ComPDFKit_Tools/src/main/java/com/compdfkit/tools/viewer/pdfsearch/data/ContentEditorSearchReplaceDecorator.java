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

import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.viewer.pdfsearch.bean.CSearchTextInfo;
import com.compdfkit.ui.edit.CPDFEditTextSearchReplace;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.List;


/**
 * Search Feature, Content Editing Search and Replace Decorator Class,
 * Enhanced with Highlighted Keyword Context Menu Display When Using Content Editing Search and Replace Functionality.<br><br>
 * Samples :<br>
 * <pre>
 * {@code
 *      CPDFSearch cpdfSearch = new ContentEditorSearchReplaceDecorator(new ContentEditorSearchImpl(cpdfReaderView));
 *      cpdfSearch.searchFirst(String keywords, boolean ignoreCase, boolean wholeWordsOnly);
 *      cpdfSearch.getSearchResults(String keywords, boolean ignoreCase, boolean wholeWordsOnly);
 *      cpdfSearch.searchForward();
 *      cpdfSearch.searchBackward();
 * }
 */
public class ContentEditorSearchReplaceDecorator extends CPDFSearchDecorator {

    private boolean showSearchReplaceContextMenu = false;

    public ContentEditorSearchReplaceDecorator(ContentEditorSearchImpl decoratedSearch) {
        super(decoratedSearch);
    }

    @Override
    public CPDFReaderView getReaderView() {
        return search.getReaderView();
    }

    @Nullable
    @Override
    public CSearchTextInfo searchFirst(String keywords, boolean ignoreCase, boolean wholeWordsOnly) {
        return search.searchFirst(keywords, ignoreCase, wholeWordsOnly);
    }

    @Override
    public List<CSearchTextInfo> getSearchResults(String keywords, boolean ignoreCase, boolean wholeWordsOnly) {
        return search.getSearchResults(keywords, ignoreCase, wholeWordsOnly);
    }

    @Override
    public void searchBackward() {
        search.searchBackward();
        showSearchReplaceContextMenu();

    }

    @Override
    public void searchForward() {
        search.searchForward();
        showSearchReplaceContextMenu();
    }

    @Override
    public void resetSearch() {
        search.resetSearch();
    }

    @Override
    public void select(int page, int textRangeIndex) {
        search.select(page, textRangeIndex);
        showSearchReplaceContextMenu();
    }

    public void setShowSearchReplaceContextMenu(boolean showSearchReplaceContextMenu) {
        this.showSearchReplaceContextMenu = showSearchReplaceContextMenu;
    }

    private boolean showing = false;

    public void showSearchReplaceContextMenu(){
        if (showSearchReplaceContextMenu) {
            if (showing){
                return;
            }
            showing = true;
            getReaderView().postDelayed(()->{
                getReaderView().showSearchReplaceContextMenu();
                showing = false;
            }, 100);
        }
    }

    public void replaceAll(String replaceValue, CSearchReplaceCallback callback) {
        if (getReaderView() != null) {
            CPDFEditTextSearchReplace editTextSearchReplace = getReaderView().getEditTextSearchReplace();
            CThreadPoolUtils.getInstance().executeIO(() -> {
                editTextSearchReplace.replaceAll(replaceValue);
                editTextSearchReplace.clear();
                CThreadPoolUtils.getInstance().executeMain(() -> {
                    try {
                        getReaderView().invalidateChildrenAp();
                        if (getReaderView().getContextMenuShowListener() != null) {
                            getReaderView().getContextMenuShowListener().dismissContextMenu();
                        }
                        if (callback != null) {
                            callback.complete();
                        }
                    } catch (Exception e) {
                        if (callback != null) {
                            callback.complete();
                        }
                    }
                });
            });
        }
    }

    public interface CSearchReplaceCallback{
        void complete();
    }


    public void recordSelectIndex(){
        if (search instanceof ContentEditorSearchImpl) {
            ((ContentEditorSearchImpl) search).recordSelectIndex();
        }
    }

    public void restoreSelectIndex(){
        if (search instanceof ContentEditorSearchImpl) {
            ((ContentEditorSearchImpl) search).restoreSelectIndex();
        }
    }
}
