/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.content.Context;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditTextProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuMultipleLineView;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.ContentEditorConfig;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts;
import com.compdfkit.tools.common.utils.dialog.CImportImageDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.edit.CPDFEditSelections;
import com.compdfkit.ui.edit.CPDFEditTextSelections;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.utils.CPDFTextUtils;

public class CEditTextContextMenuView implements ContextMenuEditTextProvider {
    @Override
    public View createEditTextAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFEditSelections cpdfEditSelections) {
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        menuView.setLineNumber(1);

        menuView.addItem(R.string.tools_context_menu_properties, 0, v -> {
            CStyleManager styleManager = new CStyleManager(cpdfEditSelections, pageView);
            CAnnotStyle annotStyle = styleManager.getStyle(CStyleType.EDIT_TEXT);
            CStyleDialogFragment dialogFragment = CStyleDialogFragment.newInstance(annotStyle);
            styleManager.setAnnotStyleFragmentListener(dialogFragment);
            styleManager.setDialogHeightCallback(dialogFragment, helper.getReaderView());
            if (helper != null && helper.getReaderView() != null && helper.getReaderView().getContext() != null) {
                Context context = helper.getReaderView().getContext();
                if (context instanceof FragmentActivity) {
                    dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "noteEditDialog");
                }
                helper.dismissContextMenu();
            }
        });
        menuView.addItem(R.string.tools_context_menu_edit, 0, v -> {
            pageView.operateEditTextArea(CPDFPageView.EditTextAreaFuncType.EDIT);
        });
        menuView.addItem(R.string.tools_context_menu_cut, 0, v -> {
            pageView.operateEditTextArea(CPDFPageView.EditTextAreaFuncType.CUT);
            helper.dismissContextMenu();
        });

        menuView.addItem(R.string.tools_copy, 0, v -> {
            if (helper.isAllowsCopying()) {
                pageView.operateEditTextArea(CPDFPageView.EditTextAreaFuncType.COPY);
                helper.dismissContextMenu();
            } else {
                helper.showInputOwnerPasswordDialog(ownerPassword -> {
                    pageView.operateEditTextArea(CPDFPageView.EditTextAreaFuncType.COPY);
                    helper.dismissContextMenu();
                });
            }
        });
        menuView.addItem(R.string.tools_context_menu_delete, 0, v -> {
            pageView.operateEditTextArea(CPDFPageView.EditTextAreaFuncType.DELETE);
            helper.dismissContextMenu();
        });
        return menuView;
    }

    public View createEditLongPressContentView(CPDFContextMenuHelper helper, final CPDFPageView pageView, final PointF point) {
        if (pageView == null || helper == null || helper.getReaderView() == null || helper.getReaderView().getContext() == null) {
            return null;
        }
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        boolean isEditAreaValid = pageView.isEditTextAreaInClipboardValid();
        String text = CPDFTextUtils.getClipData(pageView.getContext());
        int type = helper.getReaderView().getLoadType();

        int lines = (!TextUtils.isEmpty(text) && isEditAreaValid == true) == true ? 2 : 1;
        menuView.setLineNumber(lines);

        if (type == CPDFEditPage.LoadText) {
            menuView.addItem(R.string.tools_context_menu_add_text, 0, v -> {
                CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
                if (configuration != null) {
                    ContentEditorConfig.TextAttr textAttr = configuration.contentEditorConfig.initAttribute.text;
                    String comboBoxFontName = CPDFTextAttribute.FontNameHelper.obtainFontName(
                            textAttr.getTypeface(), textAttr.isBold(), textAttr.isItalic());
                    pageView.addEditTextArea(point,
                            comboBoxFontName,
                            textAttr.getFontSize(),
                            textAttr.getFontColor(),
                            textAttr.getFontColorAlpha(),
                            textAttr.isBold(),
                            textAttr.isItalic(),
                            textAttr.getAlignment()
                    );
                } else {
                    pageView.addEditTextArea(point);
                }
            });
        }
        if (type == CPDFEditPage.LoadImage) {
            menuView.addItem(R.string.tools_context_menu_add_image, 0, v -> {
                helper.getReaderView().setAddImagePoint(point);
                helper.getReaderView().setAddImagePage(pageView.getPageNum());
                CImportImageDialogFragment fragment = CImportImageDialogFragment.quickStart(CImageResultContracts.RequestType.PHOTO_ALBUM);
                fragment.setImportImageListener(imageUri -> {
                    fragment.dismiss();
                    if (imageUri != null) {
                        pageView.addEditImageArea(point, imageUri);
                    }
                });
                FragmentManager fragmentManager = helper.getFragmentManager();
                if (fragmentManager != null) {
                    fragment.show(fragmentManager, "importImage");
                    helper.dismissContextMenu();
                }
            });
        }
        if (type == CPDFEditPage.LoadText) {
            if (!TextUtils.isEmpty(text) && isEditAreaValid == false) {
                menuView.addItem(R.string.tools_context_menu_paste, 0, v -> {
                    pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(), pageView.getCopyTextAreaHeight());
                    helper.dismissContextMenu();
                });
            } else if (!TextUtils.isEmpty(text) && isEditAreaValid == true) {
                menuView.addItem(R.string.tools_context_menu_select_paste, 0, v -> {
                    pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(), pageView.getCopyTextAreaHeight(), false);
                    helper.dismissContextMenu();
                });
                menuView.addItem(R.string.tools_context_menu_select_paste_with_style, 1, v -> {
                    pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(), pageView.getCopyTextAreaHeight(), true);
                    helper.dismissContextMenu();
                });
            } else {

            }
        } else if (type == CPDFEditPage.LoadImage) {
            if (TextUtils.isEmpty(text) && pageView.getAreaInfoType() == CPDFEditPage.LoadImage) {
                menuView.addItem(R.string.tools_context_menu_paste, 0, v -> {
                    pageView.pasteEditImageArea(point, pageView.getCopyTextAreaWidth(), pageView.getCopyTextAreaHeight());
                    helper.dismissContextMenu();
                });
            }
        } else if (type == 4) {
            if (!TextUtils.isEmpty(text)) {
                menuView.addItem(R.string.tools_context_menu_paste, 0, v -> {
                    pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(), pageView.getCopyTextAreaHeight(), false);
                    helper.dismissContextMenu();
                });
            }
            if (!TextUtils.isEmpty(text) && isEditAreaValid == true) {
                menuView.addItem(R.string.tools_context_menu_select_paste_with_style, 1, v -> {
                    pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(), pageView.getCopyTextAreaHeight(), true);
                    helper.dismissContextMenu();
                });
            }
            if (TextUtils.isEmpty(text)) {
                menuView.addItem(R.string.tools_context_menu_paste, 0, v -> {
                    pageView.pasteEditImageArea(point, pageView.getCopyTextAreaWidth(), pageView.getCopyTextAreaHeight());
                    helper.dismissContextMenu();
                });
            }
        }
        return menuView;
    }

    @Override
    public View createEditSelectTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFEditSelections selections) {
        if (pageView == null || helper == null || helper.getReaderView() == null || helper.getReaderView().getContext() == null) {
            return null;
        }
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        menuView.setLineNumber(1);
        final CPDFEditTextSelections textSelections = (CPDFEditTextSelections) selections;

        menuView.addItem(R.string.tools_context_menu_properties, 0, v -> {
            CViewUtils.hideKeyboard(pageView);
            CStyleManager styleManager = new CStyleManager(textSelections, pageView);
            CAnnotStyle annotStyle = styleManager.getStyle(CStyleType.EDIT_TEXT);
            CStyleDialogFragment dialogFragment = CStyleDialogFragment.newInstance(annotStyle);
            styleManager.setAnnotStyleFragmentListener(dialogFragment);
            if (helper.getReaderView().getContext() instanceof FragmentActivity) {
                dialogFragment.show(((FragmentActivity) helper.getReaderView().getContext()).getSupportFragmentManager(), "noteEditDialog");
            }
            helper.dismissContextMenu();
        });
        menuView.addItem(R.string.tools_context_menu_transparancy, 0, v -> {
            menuView.showSecondView(true);
        });
        menuView.addSecondView();
        View view = LayoutInflater.from(pageView.getContext()).inflate(R.layout.tools_context_menu_image_item_layout, null);
        view.setOnClickListener(v -> {
            menuView.showSecondView(false);
        });
        menuView.addItemToSecondView(view);
        menuView.addItemToSecondView(R.string.tools_context_menu_transparacy_25, v -> {
            // 25%
            textSelections.setTransparancy(63.75F);
            pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.ATTR);
        });
        menuView.addItemToSecondView(R.string.tools_context_menu_transparacy_50, v -> {
            textSelections.setTransparancy(127.5F);
            pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.ATTR);
        });
        menuView.addItemToSecondView(R.string.tools_context_menu_transparacy_75, v -> {
            textSelections.setTransparancy(191.25F);
            pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.ATTR);
        });
        menuView.addItemToSecondView(R.string.tools_context_menu_transparacy_100, v -> {
            textSelections.setTransparancy(255F);
            pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.ATTR);
        });
        menuView.addItem(R.string.tools_context_menu_cut, 0, v -> {
            pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.CUT);
        });
        menuView.addItem(R.string.tools_copy, 0, v -> {
            if (helper.isAllowsCopying()) {
                pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.COPY);
            } else {
                helper.showInputOwnerPasswordDialog(ownerPassword -> {
                    pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.COPY);
                });
            }
        });
        menuView.addItem(R.string.tools_delete, 0, v -> {
            pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.DELETE);
        });
        return menuView;
    }

    @Override
    public View createEditTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView) {
        if (pageView == null) {
            return null;
        }
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        menuView.addItem(R.string.tools_context_menu_select, v -> {
            pageView.operateEditText(CPDFPageView.EditTextFuncType.SELECT);
        });
        menuView.addItem(R.string.tools_context_menu_select_all, v -> {
            pageView.operateEditText(CPDFPageView.EditTextFuncType.SELECT_ALL);
        });
        String clipData = CPDFTextUtils.getClipData(pageView.getContext());
        if (!TextUtils.isEmpty(clipData)) {
            menuView.addItem(R.string.tools_context_menu_paste, v -> {
                pageView.operateEditText(CPDFPageView.EditTextFuncType.PASTE);
            });
        }
        return menuView;
    }
}
