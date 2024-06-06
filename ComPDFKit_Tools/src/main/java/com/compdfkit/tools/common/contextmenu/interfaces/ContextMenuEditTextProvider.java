/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.interfaces;


import android.graphics.PointF;
import android.view.View;

import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.ui.edit.CPDFEditSelections;
import com.compdfkit.ui.reader.CPDFPageView;

public interface ContextMenuEditTextProvider {

    View createEditTextAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView,  CPDFEditSelections cpdfEditSelections);
    View createEditLongPressContentView(CPDFContextMenuHelper helper,  CPDFPageView pageView,  PointF point);
    View createEditSelectTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFEditSelections selections);
    View createEditTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView);
}
