/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.graphics.PointF;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

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
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
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
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.utils.CPDFTextUtils;

import java.util.List;
import java.util.Map;

public class CEditTextContextMenuView implements ContextMenuEditTextProvider {
    @Override
    public View createEditTextAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView,
                                              CPDFEditSelections cpdfEditSelections) {
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance()
                .getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> editTextAreaContent = editorModeConfig.get(
                "editTextAreaContent");

        if (editTextAreaContent == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : editTextAreaContent) {
            switch (contextMenuActionItem.key) {
                case "properties":
                    menuView.addItem(R.string.tools_context_menu_properties, v -> {
                        CStyleManager styleManager = new CStyleManager(cpdfEditSelections, pageView);
                        CAnnotStyle annotStyle = styleManager.getStyle(CStyleType.EDIT_TEXT);
                        CStyleDialogFragment dialogFragment = CStyleDialogFragment.newInstance(annotStyle);
                        styleManager.setAnnotStyleFragmentListener(dialogFragment);
                        styleManager.setDialogHeightCallback(dialogFragment, helper.getReaderView());
                        if (helper != null && helper.getReaderView() != null
                                && helper.getReaderView().getContext() != null) {
                            if (helper.getFragmentManager() != null) {
                                dialogFragment.show(helper.getFragmentManager(), "noteEditDialog");
                            }
                            helper.dismissContextMenu();
                        }
                    });
                    break;
                case "edit":
                    menuView.addItem(R.string.tools_context_menu_edit, v -> {
                        pageView.operateEditTextArea(CPDFPageView.EditTextAreaFuncType.EDIT);
                    });
                    break;
                case "cut":
                    menuView.addItem(R.string.tools_context_menu_cut, v -> {
                        pageView.operateEditTextArea(CPDFPageView.EditTextAreaFuncType.CUT);
                        updateEditToolbar(helper);
                        helper.dismissContextMenu();
                    });
                    break;
                case "copy":
                    menuView.addItem(R.string.tools_copy, v -> {
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
                    break;
                case "delete":
                    menuView.addItem(R.string.tools_context_menu_delete, v -> {
                        pageView.operateEditTextArea(CPDFPageView.EditTextAreaFuncType.DELETE);
                        updateEditToolbar(helper);
                        helper.dismissContextMenu();
                    });
                    break;
            }
        }

        return menuView;
    }

    public View createEditLongPressContentView(CPDFContextMenuHelper helper,
                                               final CPDFPageView pageView, final PointF point) {
        if (pageView == null || helper == null || helper.getReaderView() == null
                || helper.getReaderView().getContext() == null) {
            return null;
        }
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        int type = helper.getReaderView().getLoadType();
        switch (type) {
            case CPDFEditPage.LoadText:
                return createLongPressWithEditTextModeView(helper, pageView, point);
            case CPDFEditPage.LoadImage:
                return createLongPressWithEditImageModeView(helper, pageView, point);
            case (CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath):
                return createLongPressWithAllModeView(helper, pageView, point);
            case CPDFEditPage.LoadPath:
                return null;
            default:
                break;
        }
        return menuView;
    }

    private View createLongPressWithEditTextModeView(CPDFContextMenuHelper helper,
                                                     final CPDFPageView pageView, final PointF point) {
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        boolean isEditAreaValid = pageView.isEditTextAreaInClipboardValid();
        String text = CPDFTextUtils.getClipData(pageView.getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance()
                .getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> longPressWithEditTextMode = editorModeConfig.get(
                "longPressWithEditTextMode");

        if (longPressWithEditTextMode == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : longPressWithEditTextMode) {
            switch (contextMenuActionItem.key) {
                case "addText":
                    menuView.addItem(R.string.tools_context_menu_add_text, v -> {
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
                    break;
                case "paste":
                    if (TextUtils.isEmpty(text)) {
                        continue;
                    }
                    menuView.addItem(R.string.tools_context_menu_paste, v -> {
                        pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(),
                                pageView.getCopyTextAreaHeight());
                        helper.dismissContextMenu();
                    });
                    break;
                case "keepSourceFormatingPaste":
                    if (TextUtils.isEmpty(text)) {
                        continue;
                    }
                    if (isEditAreaValid) {
                        menuView.addItem(R.string.tools_context_menu_select_paste_with_style, v -> {
                            pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(),
                                    pageView.getCopyTextAreaHeight(), true);
                            helper.dismissContextMenu();
                        });
                    }
                    break;
            }
        }
        return menuView;
    }

    private View createLongPressWithEditImageModeView(CPDFContextMenuHelper helper,
                                                      final CPDFPageView pageView, final PointF point) {
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        String text = CPDFTextUtils.getClipData(pageView.getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance()
                .getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> longPressWithEditImageMode = editorModeConfig.get(
                "longPressWithEditImageMode");

        if (longPressWithEditImageMode == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : longPressWithEditImageMode) {
            switch (contextMenuActionItem.key) {
                case "addImages":
                    menuView.addItem(R.string.tools_context_menu_add_image, v -> {
                        helper.getReaderView().setAddImagePoint(point);
                        helper.getReaderView().setAddImagePage(pageView.getPageNum());
                        CImportImageDialogFragment fragment = CImportImageDialogFragment.quickStart(
                                CImageResultContracts.RequestType.PHOTO_ALBUM);
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
                    break;
                case "paste":
                    if (TextUtils.isEmpty(text) && pageView.getAreaInfoType() == CPDFEditPage.LoadImage) {
                        menuView.addItem(R.string.tools_context_menu_paste, v -> {
                            pageView.pasteEditImageArea(point, pageView.getCopyTextAreaWidth(),
                                    pageView.getCopyTextAreaHeight());
                            helper.dismissContextMenu();
                        });
                    }
                    break;
            }
        }
        return menuView;
    }

    private View createLongPressWithAllModeView(CPDFContextMenuHelper helper,
                                                final CPDFPageView pageView, final PointF point) {
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        String text = CPDFTextUtils.getClipData(pageView.getContext());
        boolean isEditAreaValid = pageView.isEditTextAreaInClipboardValid();

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance()
                .getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> longPressWithAllMode = editorModeConfig.get(
                "longPressWithAllMode");

        if (longPressWithAllMode == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : longPressWithAllMode) {
            switch (contextMenuActionItem.key) {
                case "paste":
                    if (TextUtils.isEmpty(text)) {
                        if (pageView.getAreaInfoType() == CPDFEditPage.LoadImage) {
                            menuView.addItem(R.string.tools_context_menu_paste, v -> {
                                pageView.pasteEditImageArea(point, pageView.getCopyTextAreaWidth(),
                                        pageView.getCopyTextAreaHeight());
                                helper.dismissContextMenu();
                            });
                        }
                    } else {
                        menuView.addItem(R.string.tools_context_menu_paste, v -> {
                            pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(),
                                    pageView.getCopyTextAreaHeight(), false);
                            helper.dismissContextMenu();
                        });
                    }
                    break;
                case "keepSourceFormatingPaste":
                    if (isEditAreaValid && pageView.getAreaInfoType() != CPDFEditPage.LoadImage) {
                        menuView.addItem(R.string.tools_context_menu_select_paste_with_style, v -> {
                            pageView.pasteEditTextArea(point, pageView.getCopyTextAreaWidth(),
                                    pageView.getCopyTextAreaHeight(), true);
                            helper.dismissContextMenu();
                        });
                    }
                    break;
            }
        }
        return menuView;
    }


    @Override
    public View createEditSelectTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView,
                                                CPDFEditSelections selections) {
        if (pageView == null || helper == null || helper.getReaderView() == null
                || helper.getReaderView().getContext() == null) {
            return null;
        }
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        final CPDFEditTextSelections textSelections = (CPDFEditTextSelections) selections;

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance()
                .getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> editSelectTextContent = editorModeConfig.get(
                "editSelectTextContent");

        if (editSelectTextContent == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : editSelectTextContent) {
            switch (contextMenuActionItem.key) {
                case "properties":
                    menuView.addItem(R.string.tools_context_menu_properties, v -> {
                        CViewUtils.hideKeyboard(pageView);
                        CStyleManager styleManager = new CStyleManager(textSelections, pageView);
                        CAnnotStyle annotStyle = styleManager.getStyle(CStyleType.EDIT_TEXT);
                        CStyleDialogFragment dialogFragment = CStyleDialogFragment.newInstance(annotStyle);
                        styleManager.setAnnotStyleFragmentListener(dialogFragment);
                        if (helper.getFragmentManager() != null) {
                            dialogFragment.show(helper.getFragmentManager(), "noteEditDialog");
                        }
                        helper.dismissContextMenu();
                    });
                    break;
                case "opacity":
                    if (contextMenuActionItem.subItems == null || contextMenuActionItem.subItems.isEmpty()) {
                        continue;
                    }

                    menuView.addItem(R.string.tools_context_menu_transparancy, v -> {
                        menuView.showSecondView(true);
                    });
                    menuView.addSecondView();
                    View view = LayoutInflater.from(pageView.getContext())
                            .inflate(R.layout.tools_context_menu_image_item_layout, null);
                    view.setOnClickListener(v -> {
                        menuView.showSecondView(false);
                    });
                    menuView.addItemToSecondView(view);

                    List<String> opacityList = contextMenuActionItem.subItems;
                    for (String opacityItem : opacityList) {
                        int titleRes = 0;
                        float opacityValue = 255F;
                        switch (opacityItem) {
                            case "25%":
                                titleRes = R.string.tools_context_menu_transparacy_25;
                                opacityValue = 63.75F;
                                break;
                            case "50%":
                                titleRes = R.string.tools_context_menu_transparacy_50;
                                opacityValue = 127.5F;
                                break;
                            case "75%":
                                titleRes = R.string.tools_context_menu_transparacy_75;
                                opacityValue = 191.25F;
                                break;
                            case "100%":
                                titleRes = R.string.tools_context_menu_transparacy_100;
                                opacityValue = 255F;
                                break;
                        }
                        if (titleRes > 0) {
                            float finalOpacityValue = opacityValue;
                            menuView.addItemToSecondView(titleRes, v -> {
                                textSelections.setTransparancy(finalOpacityValue);
                                pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.ATTR);
                            });
                        }
                    }
                    break;
                case "cut":
                    menuView.addItem(R.string.tools_context_menu_cut, v -> {
                        pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.CUT);
                    });
                    break;
                case "copy":
                    menuView.addItem(R.string.tools_copy, v -> {
                        if (helper.isAllowsCopying()) {
                            pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.COPY);
                        } else {
                            helper.showInputOwnerPasswordDialog(ownerPassword -> {
                                pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.COPY);
                            });
                        }
                    });
                    break;
                case "delete":
                    menuView.addItem(R.string.tools_delete, v -> {
                        pageView.operateEditTextSelect(CPDFPageView.EditTextSelectFuncType.DELETE);
                        updateEditToolbar(helper);

                    });
                    break;
            }
        }
        return menuView;
    }

    @Override
    public View createEditTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView) {
        if (pageView == null) {
            return null;
        }
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance()
                .getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> editTextContent = editorModeConfig.get(
                "editTextContent");

        if (editTextContent == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : editTextContent) {
            switch (contextMenuActionItem.key) {
                case "select":
                    menuView.addItem(R.string.tools_context_menu_select, v -> {
                        pageView.operateEditText(CPDFPageView.EditTextFuncType.SELECT);
                    });
                    break;
                case "selectAll":
                    menuView.addItem(R.string.tools_context_menu_select_all, v -> {
                        pageView.operateEditText(CPDFPageView.EditTextFuncType.SELECT_ALL);
                    });
                    break;
                case "paste":
                    String clipData = CPDFTextUtils.getClipData(pageView.getContext());
                    if (!TextUtils.isEmpty(clipData)) {
                        menuView.addItem(R.string.tools_context_menu_paste, v -> {
                            pageView.operateEditText(CPDFPageView.EditTextFuncType.PASTE);
                        });
                    }
                    break;
            }
        }
        return menuView;
    }

    private void updateEditToolbar(CPDFContextMenuHelper helper) {
        CPDFReaderView readerView = helper.getReaderView();
        if (readerView.getSelectEditAreaChangeListener() != null) {
            readerView.getSelectEditAreaChangeListener()
                    .onSelectEditAreaChange(0);
        }
    }
}
