/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
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
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.viewer.pdfsearch.data.CPDFSearchReplaceDatas;
import com.compdfkit.ui.edit.CPDFEditTextSearchReplace;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.List;
import java.util.Map;


public class CSearchReplaceContextMenuView implements ContextMenuSearchReplaceProvider {


    @Override
    public View createSearchReplaceContentView(CPDFPageView cpdfPageView, LayoutInflater layoutInflater, CPDFContextMenuHelper helper) {
        ContextMenuView menuView = new ContextMenuView(cpdfPageView.getContext());
        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> contentEditorModeConfig = CPDFApplyConfigUtil.getInstance().getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> searchReplace = contentEditorModeConfig.get("searchReplace");

        if (searchReplace == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : searchReplace) {
            switch (contextMenuActionItem.key){
                case "replace":
                    menuView.addItem(R.string.tools_replace, v -> {
                        CPDFReaderView readerView = helper.getReaderView();
                        CPDFEditTextSearchReplace editTextSearchReplace = readerView.getEditTextSearchReplace();
                        editTextSearchReplace.replace(CPDFSearchReplaceDatas.getInstance().getKeywords());
                        helper.dismissContextMenu();
                        readerView.invalidateChildrenAp();
                    });
                    break;
            }
        }
        return menuView;
    }
}
