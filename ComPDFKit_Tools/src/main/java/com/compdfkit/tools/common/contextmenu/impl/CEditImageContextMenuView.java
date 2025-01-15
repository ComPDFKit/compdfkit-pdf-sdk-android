package com.compdfkit.tools.common.contextmenu.impl;


import android.content.Context;
import android.graphics.RectF;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts;
import com.compdfkit.tools.common.utils.dialog.CImportImageDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider.CEditSelectionsProvider;
import com.compdfkit.ui.reader.CPDFPageView;

import java.io.File;

public class CEditImageContextMenuView implements ContextMenuEditImageProvider {
    @Override
    public View createEditImageAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, RectF area) {
        if (pageView == null || helper == null || helper.getReaderView() == null || helper.getReaderView().getContext() == null) {
            return null;
        }
        ContextMenuMultipleLineView menuView = new ContextMenuMultipleLineView(pageView.getContext());
        menuView.setLineNumber(3);
        menuView.addItem(R.string.tools_context_menu_properties, 0, v -> {
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
        menuView.addItem(R.string.tools_edit_image_property_rotate_left, 0, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.ROTATE, -90.0f);
        });
        menuView.addItem(R.string.tools_edit_image_property_rotate_right, 0, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.ROTATE, 90.0f);
        });
        menuView.addItem(R.string.tools_context_menu_image_replace, 1, v -> {
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
        menuView.addItem(R.string.tools_context_menu_image_extract, 1, v -> {
            try {
                Context context = pageView.getContext();
                String sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                File file = new File(sdPath, "ComPDFKit" + File.separator + System.currentTimeMillis() + ".png");
                pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.EXTRACT_IMAGE, file.getAbsolutePath());
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), file.getName(), "description");
                helper.dismissContextMenu();
                Toast.makeText(pageView.getContext(), R.string.tools_export_success, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                CToastUtil.showLongToast(pageView.getContext(), R.string.tools_page_edit_extract_fail);
            }
        });
        menuView.addSecondView();
        View view = LayoutInflater.from(pageView.getContext()).inflate(R.layout.tools_context_menu_image_item_layout, null);
        view.setOnClickListener(v -> {
            menuView.showSecondView(false);
        });
        menuView.addItemToSecondView(view);
        menuView.addItemToSecondView(R.string.tools_context_menu_transparacy_25, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.TRANCPARENCY, 63.75F);
        });
        menuView.addItemToSecondView(R.string.tools_context_menu_transparacy_50, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.TRANCPARENCY, 127.5F);
        });
        menuView.addItemToSecondView(R.string.tools_context_menu_transparacy_75, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.TRANCPARENCY, 191.25F);
        });
        menuView.addItemToSecondView(R.string.tools_context_menu_transparacy_100, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.TRANCPARENCY, 255F);
        });
        menuView.addItem(R.string.tools_context_menu_image_transparancy, 1, v -> {
            menuView.showSecondView(true);
        });
        menuView.addItem(R.string.tools_context_menu_image_horizental_mirror, 1, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.HORIZENTAL_MIRROR, null);
        });
        menuView.addItem(R.string.tools_context_menu_image_vertical_mirror, 2, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.VERTICLE_MIRROR, null);
        });
        menuView.addItem(R.string.tools_crop, 2, v -> {
            pageView.setCropRectChangeCallback(rect -> {
            });
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.ENTER_CROP, null);
            helper.dismissContextMenu();
        });
        menuView.addItem(R.string.tools_delete, 2, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.DELETE, null);
            helper.dismissContextMenu();
        });
        menuView.addItem(R.string.tools_context_menu_image_copy, 2, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.COPY, null);
            helper.dismissContextMenu();
        });
        menuView.addItem(R.string.tools_context_menu_image_cut, 2, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.CUT, null);
            helper.dismissContextMenu();
        });
        return menuView;
    }

    @Override
    public View createGetCropImageAreaContentView(CPDFContextMenuHelper helper, CPDFPageView pageView) {
        if (pageView == null || helper == null) {
            return null;
        }
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        menuView.addItem(R.string.tools_context_menu_image_crop_done, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.CROP, null);
            helper.dismissContextMenu();
        });
        menuView.addItem(R.string.tools_context_menu_image_crop_cancel, v -> {
            pageView.operateEditImageArea(CPDFPageView.EditImageFuncType.EXIT_CROP, null);
            helper.dismissContextMenu();
        });
        return menuView;
    }
}
