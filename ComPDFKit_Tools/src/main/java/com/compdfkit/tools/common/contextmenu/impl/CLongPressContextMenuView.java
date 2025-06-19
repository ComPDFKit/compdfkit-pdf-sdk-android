/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.graphics.PointF;
import android.text.TextUtils;
import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuLongPressProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.utils.CPDFTextUtils;

import java.util.List;
import java.util.Map;

public class CLongPressContextMenuView implements ContextMenuLongPressProvider {

    @Override
    public View createLongPressContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, PointF pointF) {
        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> formModeConfig = CPDFApplyConfigUtil.getInstance().getAnnotationModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> longPressContent = formModeConfig.get("longPressContent");

        if (longPressContent == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : longPressContent) {
            switch (contextMenuActionItem.key) {
                case "paste":
                    if (!TextUtils.isEmpty(CPDFTextUtils.getClipData(pageView.getContext()))) {
                        menuView.addItem(R.string.tools_paste, v -> {
                            CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                            annotationManager.addFreeText(CPDFTextUtils.getClipData(pageView.getContext()), helper.getReaderView(), pageView, pointF);
                            helper.dismissContextMenu();
                        });
                    }
                    break;
                case "note":
                    menuView.addItem(R.string.tools_annot_note, v -> {
                        CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                        annotationManager.addNote(helper.getReaderView(), pageView, pointF);
                        helper.dismissContextMenu();
                    });
                    break;
                case "textBox":
                    menuView.addItem(R.string.tools_context_menu_text, v -> {
                        CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                        annotationManager.addFreeTextInputBox(helper.getReaderView(), pageView, pointF);
                        helper.dismissContextMenu();
                    });
                    break;
                case "stamp":
                    menuView.addItem(R.string.tools_annot_stamp, v -> {
                        CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                        CStyleDialogFragment dialogFragment = showAnnotStyleFragment(helper, CStyleType.ANNOT_STAMP);
                        dialogFragment.setStyleDialogDismissListener(() -> {
                            CAnnotStyle annotStyle = dialogFragment.getAnnotStyle();
                            if (annotStyle.stampIsAvailable()) {
                                if (annotStyle.getStandardStamp() != null) {
                                    annotationManager.addStandardStamp(annotStyle.getStandardStamp(), helper.getReaderView(), pageView, pointF);
                                } else if (annotStyle.getTextStamp() != null) {
                                    annotationManager.addTextStamp(annotStyle.getTextStamp(), helper.getReaderView(), pageView, pointF);
                                } else if (!TextUtils.isEmpty(annotStyle.getImagePath())) {
                                    annotationManager.addImageStamp(annotStyle.getImagePath(), helper.getReaderView(), pageView, pointF);
                                } else {

                                }
                            }
                            helper.dismissContextMenu();
                        });
                    });
                    break;
                case "image":
                    menuView.addItem(R.string.tools_image, v -> {
                        CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                        CStyleDialogFragment dialogFragment = showAnnotStyleFragment(helper, CStyleType.ANNOT_PIC);
                        dialogFragment.setStyleDialogDismissListener(() -> {
                            CAnnotStyle annotStyle = dialogFragment.getAnnotStyle();
                            if (!TextUtils.isEmpty(annotStyle.getImagePath())) {
                                annotationManager.addImageStamp(annotStyle.getImagePath(), helper.getReaderView(), pageView, pointF);
                            }
                            helper.dismissContextMenu();
                        });
                    });
                    break;
            }
        }
        return menuView;
    }

    private CStyleDialogFragment showAnnotStyleFragment(CPDFContextMenuHelper helper, CStyleType type) {
        CStyleManager styleManager = new CStyleManager(helper.getReaderView());
        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(styleManager.getStyle(type));
        if (helper.getFragmentManager() != null) {
            styleDialogFragment.show(helper.getFragmentManager(), "styleDialog");
        }
        return styleDialogFragment;
    }


}
