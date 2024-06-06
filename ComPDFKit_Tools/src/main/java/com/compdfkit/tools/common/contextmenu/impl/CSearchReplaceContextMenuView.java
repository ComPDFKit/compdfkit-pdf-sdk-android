/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.contextmenu.impl;

import android.view.LayoutInflater;
import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSearchReplaceProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.viewer.pdfsearch.data.CPDFSearchReplaceDatas;
import com.compdfkit.ui.edit.CPDFEditTextSearchReplace;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;


public class CSearchReplaceContextMenuView implements ContextMenuSearchReplaceProvider {


    @Override
    public View createSearchReplaceContentView(CPDFPageView cpdfPageView, LayoutInflater layoutInflater, CPDFContextMenuHelper helper) {
        ContextMenuView menuView = new ContextMenuView(cpdfPageView.getContext());
        menuView.addItem(R.string.tools_replace, v -> {
            CPDFReaderView readerView = helper.getReaderView();
            CPDFEditTextSearchReplace editTextSearchReplace = readerView.getEditTextSearchReplace();
            editTextSearchReplace.replace(CPDFSearchReplaceDatas.getInstance().getKeywords());
            helper.dismissContextMenu();
            readerView.invalidateChildrenAp();
        });
        return menuView;
    }
}
