/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.contextmenu;


import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSelectContentProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.List;
import java.util.Map;

public class CopyContextMenuView implements ContextMenuSelectContentProvider {

    @Override
    public View createSelectTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView) {
        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());
        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> viewModeConfig = CPDFApplyConfigUtil.getInstance().getViewModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> textSelectMenu = viewModeConfig.get("textSelect");
        if (textSelectMenu == null){
            return menuView;
        }
        for (ContextMenuConfig.ContextMenuActionItem item : textSelectMenu) {
            switch (item.key){
                case "copy":
                    menuView.addItem(R.string.tools_copy, v -> {
                        if (helper.isAllowsCopying()){
                            pageView.operateSelections(CPDFPageView.SelectFuncType.COPY);
                            helper.dismissContextMenu();
                        }else {
                            helper.showInputOwnerPasswordDialog(ownerPassword -> {
                                pageView.operateSelections(CPDFPageView.SelectFuncType.COPY);
                                helper.dismissContextMenu();
                            });
                        }
                    });
                    break;
            }
        }

        return menuView;
    }
}
