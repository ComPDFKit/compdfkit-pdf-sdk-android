package com.compdfkit.tools.common.contextmenu.impl;


import android.content.Context;
import android.graphics.RectF;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuEditImageProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuMultipleLineView;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts;
import com.compdfkit.tools.common.utils.dialog.CImportImageDialogFragment;
import com.compdfkit.tools.common.utils.image.CImageUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider.CEditSelectionsProvider;
import com.compdfkit.ui.reader.CPDFPageView;

import com.compdfkit.ui.reader.CPDFReaderView;
import java.io.File;
import java.util.List;
import java.util.Map;

public class CEditImageContextMenuView implements ContextMenuEditImageProvider {
    @Override
    public View createEditImageAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, RectF area) {
        if (pageView == null || helper == null || helper.getReaderView() == null || helper.getReaderView().getContext() == null) {
            return null;
        }
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance().getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> imageAreaContent = editorModeConfig.get("imageAreaContent");

        if (imageAreaContent == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : imageAreaContent) {
            switch (contextMenuActionItem.key) {
                case "properties":
                    menuView.addItem(R.string.tools_context_menu_properties, v -> {
                        CStyleManager styleManager = new CStyleManager(new CEditSelectionsProvider(null, pageView));
                        CAnnotStyle annotStyle = styleManager.getStyle(CStyleType.EDIT_IMAGE);
                        CStyleDialogFragment dialogFragment = CStyleDialogFragment.newInstance(annotStyle);
                        styleManager.setAnnotStyleFragmentListener(dialogFragment);
                        styleManager.setDialogHeightCallback(dialogFragment, helper.getReaderView());
                        if (helper != null && helper.getReaderView() != null && helper.getReaderView().getContext() != null) {
                            Context context = helper.getReaderView().getContext();
                            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(context);
                            if (fragmentActivity != null) {
                                dialogFragment.show(fragmentActivity.getSupportFragmentManager(), "noteEditDialog");
                            }
                        }
                        helper.dismissContextMenu();
                    });
                    break;
                case "rotateLeft":
                    menuView.addItem(R.string.tools_edit_image_property_rotate_left, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.ROTATE, 90.0f);
                    });
                    break;
                case "rotateRight":
                    menuView.addItem(R.string.tools_edit_image_property_rotate_right, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.ROTATE, -90.0f);
                    });
                    break;
                case "replace":
                    menuView.addItem(R.string.tools_context_menu_image_replace, v -> {
                        CImportImageDialogFragment fragment = CImportImageDialogFragment.quickStart(CImageResultContracts.RequestType.PHOTO_ALBUM);
                        fragment.setImportImageListener(imageUri -> {
                            fragment.dismiss();
                            if (imageUri != null) {
                                pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.REPLACE, imageUri);
                            }
                        });
                        FragmentManager fragmentManager = helper.getFragmentManager();
                        if (fragmentManager != null) {
                            fragment.show(fragmentManager, "replaceEditImageDialog");
                            helper.dismissContextMenu();
                        }
                    });
                    break;
                case "export":
                    menuView.addItem(R.string.tools_context_menu_image_extract, v -> {
                        try {
                            Context context = pageView.getContext();
                            String sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                            File file = new File(sdPath, "ComPDFKit" + File.separator + System.currentTimeMillis() + ".png");
                            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.EXTRACT_IMAGE, file.getAbsolutePath());
                            CImageUtil.scanFile(context, file.getAbsolutePath(),"image/png");
                            helper.dismissContextMenu();
                            Toast.makeText(pageView.getContext(), R.string.tools_export_success, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            CToastUtil.showLongToast(pageView.getContext(), R.string.tools_page_edit_extract_fail);
                        }
                    });
                    break;
                case "opacity":
                    if (contextMenuActionItem.subItems == null || contextMenuActionItem.subItems.isEmpty()) {
                        continue;
                    }
                    menuView.addSecondView();
                    View view = LayoutInflater.from(pageView.getContext()).inflate(R.layout.tools_context_menu_image_item_layout, null);
                    view.setOnClickListener(v -> {
                        menuView.showSecondView(false);
                    });
                    menuView.addItemToSecondView(view);
                    menuView.addItem(R.string.tools_context_menu_image_transparancy, v -> {
                        menuView.showSecondView(true);
                    });

                    List<String> opacityItems = contextMenuActionItem.subItems;
                    for (String opacityItem : opacityItems) {
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
                            default:
                                break;
                        }
                        if (titleRes > 0) {
                            float finalOpacityValue = opacityValue;
                            menuView.addItemToSecondView(titleRes, v -> {
                                pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.TRANCPARENCY, finalOpacityValue);
                            });
                        }
                    }
                    break;
                case "flipHorizontal":
                    menuView.addItem(R.string.tools_context_menu_image_horizental_mirror, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.HORIZENTAL_MIRROR, null);
                    });
                    break;
                case "flipVertical":
                    menuView.addItem(R.string.tools_context_menu_image_vertical_mirror, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.VERTICLE_MIRROR, null);
                    });
                    break;
                case "crop":
                    menuView.addItem(R.string.tools_crop, v -> {
                        pageView.setCropRectChangeCallback(rect -> {
                        });
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.ENTER_CROP, null);
                        helper.dismissContextMenu();
                    });
                    break;
                case "delete":
                    menuView.addItem(R.string.tools_delete, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.DELETE, null);
                        updateEditToolbar(helper);
                        helper.dismissContextMenu();
                    });
                    break;
                case "copy":
                    menuView.addItem(R.string.tools_context_menu_image_copy, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.COPY, null);
                        helper.dismissContextMenu();
                    });
                    break;
                case "cut":
                    menuView.addItem(R.string.tools_context_menu_image_cut, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.CUT, null);
                        updateEditToolbar(helper);
                        helper.dismissContextMenu();
                    });
                    break;
            }
        }
        return menuView;
    }

    @Override
    public View createGetCropImageAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView) {
        if (pageView == null || helper == null) {
            return null;
        }
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> editorModeConfig = CPDFApplyConfigUtil.getInstance().getContentEditorModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> imageCropMode = editorModeConfig.get("imageCropMode");

        if (imageCropMode == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : imageCropMode) {
            switch (contextMenuActionItem.key) {
                case "done":
                    menuView.addItem(R.string.tools_context_menu_image_crop_done, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.CROP, null);
                        helper.dismissContextMenu();
                    });
                    break;
                case "cancel":
                    menuView.addItem(R.string.tools_context_menu_image_crop_cancel, v -> {
                        pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.EXIT_CROP, null);
                        helper.dismissContextMenu();
                    });
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
