/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.interfaces;


import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;

import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.ui.reader.CPDFPageView;

public interface ContextMenuScreenShotProvider {

    View getScreenShotContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, LayoutInflater inflater, RectF rectF);

}
