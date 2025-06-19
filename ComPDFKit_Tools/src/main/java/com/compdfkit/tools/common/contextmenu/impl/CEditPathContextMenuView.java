package com.compdfkit.tools.common.contextmenu.impl;

import android.graphics.RectF;
import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditPathProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.List;
import java.util.Map;

public class CEditPathContextMenuView implements ContextMenuEditPathProvider {
    @Override
    public View createEditPathAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, RectF area) {
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance().getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> editPathContent = editorModeConfig.get("editPathContent");

        if (editPathContent == null) {
            return menuView;
        }
        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : editPathContent) {
            switch (contextMenuActionItem.key) {
                case "delete":
                    menuView.addItem(R.string.tools_delete, v -> {
                        pageView.operateEditPathArea(CPDFPageView.EditPathFuncType.DELETE);
                        helper.dismissContextMenu();
                    });
                    break;
            }
        }
        return menuView;
    }
}
