/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;

import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuComboBoxProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.proxy.form.CPDFComboboxWidgetImpl;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.List;
import java.util.Map;


public class ComboBoxContextMenuView implements ContextMenuComboBoxProvider {

    @Override
    public View createComboBoxContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFComboboxWidgetImpl comboBoxWidgetImpl) {
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> formModeConfig = CPDFApplyConfigUtil.getInstance().getFormsModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> comboBoxContent = formModeConfig.get("comboBox");
        if (comboBoxContent == null) {
            return menuView;
        }
        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : comboBoxContent) {
            switch (contextMenuActionItem.key) {
                case "options":
                    menuView.addItem(R.string.tools_options, v -> {
                        CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                        if (helper.getFragmentManager() != null) {
                            annotationManager.showFormComboBoxEditFragment(
                                    helper.getFragmentManager(),
                                    comboBoxWidgetImpl,
                                    pageView,
                                    true
                            );
                        }
                        helper.dismissContextMenu();
                    });
                    break;
                case "properties":
                    menuView.addItem(R.string.tools_context_menu_properties, v -> {
                        CStyleManager styleManager = new CStyleManager(comboBoxWidgetImpl, pageView);
                        CAnnotStyle style = styleManager.getStyle(CStyleType.FORM_COMBO_BOX);
                        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(style);
                        styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
                        styleManager.setDialogHeightCallback(styleDialogFragment, helper.getReaderView());
                        styleDialogFragment.show(helper.getFragmentManager());
                        helper.dismissContextMenu();
                    });
                    break;
                case "delete":
                    menuView.addItem(R.string.tools_delete, v -> {
                        pageView.deleteAnnotation(comboBoxWidgetImpl);
                        helper.dismissContextMenu();
                    });
                    break;
            }
        }
        return menuView;
    }
}
