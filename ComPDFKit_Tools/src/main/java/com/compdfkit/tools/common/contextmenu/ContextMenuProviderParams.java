/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu;



import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuCheckBoxProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuComboBoxProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditImageProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditPathProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditTextProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuFormSignProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuFreeTextProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuInkProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuLinkProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuListBoxProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuLongPressProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuMarkupProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuPushButtonProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuRadioButtonProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuScreenShotProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSearchReplaceProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSelectContentProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuShapeProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSoundContentProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuStampProvider;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuTextFieldProvider;

class ContextMenuProviderParams {

    public ContextMenuMarkupProvider markupProvider;

    public ContextMenuSelectContentProvider selectContentProvider;

    public ContextMenuSoundContentProvider soundContentProvider;

    public ContextMenuLongPressProvider longPressProvider;

    public ContextMenuInkProvider inkProvider;

    public ContextMenuShapeProvider shapeProvider;

    public ContextMenuFreeTextProvider freeTextProvider;

    public ContextMenuEditTextProvider editTextProvider;

    public ContextMenuStampProvider stampProvider;

    public ContextMenuLinkProvider linkProvider;

    public ContextMenuEditImageProvider editImageProvider;
    public ContextMenuEditPathProvider editPathProvider;

    public ContextMenuTextFieldProvider textFieldProvider;

    public ContextMenuCheckBoxProvider checkBoxProvider;

    public ContextMenuRadioButtonProvider radioButtonProvider;

    public ContextMenuListBoxProvider listBoxProvider;

    public ContextMenuComboBoxProvider comboBoxProvider;

    public ContextMenuFormSignProvider formSignatureProvider;

    public ContextMenuPushButtonProvider pushButtonProvider;

    public ContextMenuSearchReplaceProvider searchReplaceProvider;

    public ContextMenuScreenShotProvider screenShotProvider;
}
