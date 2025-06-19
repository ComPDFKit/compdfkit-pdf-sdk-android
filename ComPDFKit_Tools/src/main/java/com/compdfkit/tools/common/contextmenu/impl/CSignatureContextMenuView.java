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

import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuFormSignProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.pdf.config.GlobalConfig;
import com.compdfkit.tools.signature.CSignaturesUtils;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.proxy.form.CPDFSignatureWidgetImpl;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.List;
import java.util.Map;


public class CSignatureContextMenuView implements ContextMenuFormSignProvider {


    @Override
    public View createFormSignContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFBaseAnnotImpl annotImpl) {
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        CPDFSignatureWidget signatureWidget = (CPDFSignatureWidget) annotImpl.onGetAnnotation();

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> formModeConfig = CPDFApplyConfigUtil.getInstance().getFormsModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> signatureFieldContent = formModeConfig.get("signatureField");
        if (signatureFieldContent == null) {
            return menuView;
        }
        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : signatureFieldContent) {
            switch (contextMenuActionItem.key) {
                case "startToSign":
                    if (!signatureWidget.isSigned()) {
                        menuView.addItem(R.string.tools_sign, view -> {
                            CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
                            if (configuration == null) {
                                CSignaturesUtils.manualSelectSignature((CPDFSignatureWidgetImpl) annotImpl, helper.getReaderView(), pageView);
                            } else {
                                GlobalConfig globalConfig = configuration.globalConfig;
                                switch (globalConfig.signatureType) {
                                    case Manual:
                                        CSignaturesUtils.manualSelectSignature((CPDFSignatureWidgetImpl) annotImpl, helper.getReaderView(), pageView);
                                        break;
                                    case Digital:
                                        CSignaturesUtils.digitalSign((CPDFSignatureWidgetImpl) annotImpl, helper.getReaderView(), pageView);
                                        break;
                                    case Electronic:
                                        CSignaturesUtils.electronicSignature((CPDFSignatureWidgetImpl) annotImpl, pageView);
                                        break;
                                }
                            }
                            helper.dismissContextMenu();
                        });
                    }
                    break;
                case "delete":
                    menuView.addItem(R.string.tools_delete, v -> {
                        pageView.deleteAnnotation(annotImpl);
                        helper.dismissContextMenu();
                    });
                    break;
            }
        }
        return menuView;
    }
}
