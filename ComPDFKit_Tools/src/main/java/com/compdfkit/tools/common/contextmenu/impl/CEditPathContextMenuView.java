package com.compdfkit.tools.common.contextmenu.impl;

import android.graphics.RectF;
import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditPathProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.ui.reader.CPDFPageView;

public class CEditPathContextMenuView implements ContextMenuEditPathProvider {
    @Override
    public View createEditPathAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, RectF area) {
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        menuView.addItem(R.string.tools_delete, v -> {
            pageView.operateEditPathArea(CPDFPageView.EditPathFuncType.DELETE);
            helper.dismissContextMenu();
        });
        return menuView;
    }
}
