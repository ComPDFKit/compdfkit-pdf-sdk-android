package com.compdfkit.tools.common.contextmenu.interfaces;

import android.graphics.RectF;
import android.view.View;

import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.ui.reader.CPDFPageView;

public interface ContextMenuEditImageProvider {
    View createEditImageAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, RectF area);

    View createGetCropImageAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView);
}
