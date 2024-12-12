package com.compdfkit.tools.common.contextmenu.interfaces;

import android.graphics.RectF;
import android.view.View;

import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.ui.reader.CPDFPageView;

public interface ContextMenuEditPathProvider {
    View createEditPathAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, RectF area);
}
