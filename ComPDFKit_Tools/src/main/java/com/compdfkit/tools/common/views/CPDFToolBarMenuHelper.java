/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views;


import android.content.Context;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFAnnotation.Type;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.pdf.config.CPDFThumbnailConfig;
import com.compdfkit.tools.common.pdf.config.CustomToolbarItem;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.dialog.CLoadingDialog;
import com.compdfkit.tools.common.utils.image.CBitmapUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CPopupMenuWindow;

import com.compdfkit.ui.reader.CPDFReaderView;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPDFToolBarMenuHelper {

  public enum ToolBarAction {
    Back,
    Thumbnail,
    Search,
    Bota,
    Menu,
    ViewSettings,
    DocumentEditor,
    Security,
    Watermark,
    DocumentInfo,
    Save,
    Share,
    OpenDocument,
    Flattened,
    Snip,
    Custom;

    public static ToolBarAction fromString(String str) {
      try {
        String firstLetter = str.substring(0, 1).toUpperCase();
        String result = firstLetter + str.substring(1);
        return ToolBarAction.valueOf(result);
      } catch (Exception e) {
        return null;
      }
    }
  }

  private static final EnumMap<ToolBarAction, Integer> ICON_RES_MAP = new EnumMap<>(ToolBarAction.class);
  private static final EnumMap<ToolBarAction, Integer> TITLE_RES_MAP = new EnumMap<>(ToolBarAction.class);

  static {
    ICON_RES_MAP.put(ToolBarAction.Back, R.drawable.tools_ic_back);
    ICON_RES_MAP.put(ToolBarAction.Thumbnail, R.drawable.tools_ic_thumbnail);
    ICON_RES_MAP.put(ToolBarAction.Search, R.drawable.tools_ic_search);
    ICON_RES_MAP.put(ToolBarAction.Bota, R.drawable.tools_ic_bookmark);
    ICON_RES_MAP.put(ToolBarAction.Menu, R.drawable.tools_ic_more);
    ICON_RES_MAP.put(ToolBarAction.ViewSettings, R.drawable.tools_ic_preview_settings);
    ICON_RES_MAP.put(ToolBarAction.DocumentEditor, R.drawable.tools_page_edit);
    ICON_RES_MAP.put(ToolBarAction.Security, R.drawable.tools_ic_add_security);
    ICON_RES_MAP.put(ToolBarAction.Watermark, R.drawable.tools_ic_add_watermark);
    ICON_RES_MAP.put(ToolBarAction.DocumentInfo, R.drawable.tools_ic_document_info);
    ICON_RES_MAP.put(ToolBarAction.Save, R.drawable.tools_ic_menu_save);
    ICON_RES_MAP.put(ToolBarAction.Share, R.drawable.tools_ic_share);
    ICON_RES_MAP.put(ToolBarAction.OpenDocument, R.drawable.tools_ic_new_file);
    ICON_RES_MAP.put(ToolBarAction.Flattened, R.drawable.tools_ic_flattened);
    ICON_RES_MAP.put(ToolBarAction.Snip, R.drawable.tools_ic_snap);
    // Custom
    TITLE_RES_MAP.put(ToolBarAction.Back, R.string.tools_back);
    TITLE_RES_MAP.put(ToolBarAction.Thumbnail, R.string.tools_thumbnail);
    TITLE_RES_MAP.put(ToolBarAction.Search, R.string.tools_search);
    TITLE_RES_MAP.put(ToolBarAction.Bota, R.string.tools_bota);
    TITLE_RES_MAP.put(ToolBarAction.Menu, R.string.tools_menu);
    TITLE_RES_MAP.put(ToolBarAction.ViewSettings, R.string.tools_view_setting);
    TITLE_RES_MAP.put(ToolBarAction.DocumentEditor, R.string.tools_page_edit_toolbar_title);
    TITLE_RES_MAP.put(ToolBarAction.Security, R.string.tools_security);
    TITLE_RES_MAP.put(ToolBarAction.Watermark, R.string.tools_watermark);
    TITLE_RES_MAP.put(ToolBarAction.DocumentInfo, R.string.tools_document_info);
    TITLE_RES_MAP.put(ToolBarAction.Save, R.string.tools_save);
    TITLE_RES_MAP.put(ToolBarAction.Share, R.string.tools_share);
    TITLE_RES_MAP.put(ToolBarAction.OpenDocument, R.string.tools_open_document);
    TITLE_RES_MAP.put(ToolBarAction.Flattened, R.string.tools_flattened);
    TITLE_RES_MAP.put(ToolBarAction.Snip, R.string.tools_snap);
  }

  public static int getDefaultIconRes(@NonNull ToolBarAction action) {
    Integer res = ICON_RES_MAP.get(action);
    return res == null ? 0 : res;
  }

  public static int getDefaultTitleRes(@NonNull ToolBarAction action) {
    Integer res = TITLE_RES_MAP.get(action);
    return res == null ? 0 : res;
  }


  public static class ActionConfig {
    public final ToolBarAction action;
    public final int iconResId;
    public final int titleResId;

    /**
     * only for custom action
     */
    public final String identifier;

    public final String title;

    public final View.OnClickListener clickListener;

    private ActionConfig(@NonNull ToolBarAction action,
                         int iconResId,
                         int titleResId,
                         @Nullable String title,
                         @Nullable String identifier,
                         @Nullable View.OnClickListener clickListener) {
      this.action = action;
      this.iconResId = iconResId;
      this.titleResId = titleResId;
      this.title = title;
      this.identifier = identifier;
      this.clickListener = clickListener;
    }

    public static ActionConfig ofDefault(@NonNull ToolBarAction action,
                                         @NonNull View.OnClickListener listener) {
      if (action == ToolBarAction.Custom) {
        throw new IllegalArgumentException("Custom action please use ofCustom。");
      }
      return new ActionConfig(action,
              getDefaultIconRes(action),
              getDefaultTitleRes(action),
              null,
              null,
              listener);
    }

    public static ActionConfig ofCustom(int iconResId,
                                        @Nullable String title,
                                        @Nullable String identifier,
                                        @Nullable View.OnClickListener listener) {
      return new ActionConfig(ToolBarAction.Custom,
              iconResId,
              0,
              title,
              identifier,
              listener);
    }
  }

  /**
   * Get toolbar main action view
   */
  public static View getToolbarMainAction(Context context, ActionConfig config) {
      View itemView = CViewUtils.getThemeLayoutInflater(context).inflate(R.layout.tools_cpdf_tool_bar_pdf_view_menu_action, null);
      AppCompatImageView actionView = itemView.findViewById(R.id.iv_action);
      if (config.action == ToolBarAction.Custom){
        actionView.setImageTintList(null);
      }
      if (config.iconResId != 0) {
        actionView.setImageResource(config.iconResId);
      }
    if (config.titleResId != 0) {
      actionView.setContentDescription(context.getString(config.titleResId));
    } else if (!TextUtils.isEmpty(config.title)) {
      actionView.setContentDescription(config.title);
    }
      actionView.setOnClickListener(config.clickListener);
      return itemView;
  }

  public static View getMoreAction(Context context, CPopupMenuWindow popupMenuWindow, ActionConfig config) {
    View itemView = CViewUtils.getThemeLayoutInflater(context).inflate(R.layout.tools_menu_window_item, null);
    AppCompatImageView ivIcon = itemView.findViewById(R.id.iv_menu_icon);
    AppCompatTextView tvTitle = itemView.findViewById(R.id.tv_menu_title);
    if (config.action == ToolBarAction.Custom){
      ivIcon.setImageTintList(null);
    }
    ivIcon.setImageResource(config.iconResId);
    if (config.titleResId != 0){
      tvTitle.setText(config.titleResId);
    }else {
      tvTitle.setText(config.title);
    }
    if (config.clickListener != null){
      itemView.setOnClickListener(v -> {
        config.clickListener.onClick(v);
        popupMenuWindow.dismiss();
      });
    }
    return itemView;
  }


  public static View.OnClickListener getDefaultClickListener(CPDFDocumentFragment fragment, ToolBarAction toolBarAction){
    return v -> {
      switch (toolBarAction){
        case Back:
          fragment.onBackPressedCallback.handleOnBackPressed();
          break;
        case Thumbnail:
          CPDFThumbnailConfig thumbnailConfig = fragment.pdfView.getCPDFConfiguration().globalConfig.thumbnail;
          fragment.showPageEdit(false, thumbnailConfig.editMode);
          break;
        case Search:
          fragment.showTextSearchView();
          break;
        case Bota:
          fragment.showBOTA();
          break;
        case Menu:
          fragment.showToolbarMenuDialog(v);
          break;
        case ViewSettings:
          fragment.showDisplaySettings(fragment.pdfView);
          break;
        case DocumentEditor:
          fragment.showPageEdit(true,true);
          break;
        case Security:
          fragment.showSecurityDialog();
          break;
        case Watermark:
          fragment.showAddWatermarkDialog();
          break;
        case DocumentInfo:
          fragment.showDocumentInfo(fragment.pdfView);
          break;
        case Save:
          CLoadingDialog loadingDialog = CLoadingDialog.newInstance(fragment.getString(R.string.tools_saveing));
          loadingDialog.show(fragment.getChildFragmentManager(), "saveLoadingDialog");
          fragment.pdfView.savePDF((filePath, pdfUri) -> {
            loadingDialog.dismiss();
            CToastUtil.showLongToast(fragment.getContext(), R.string.tools_save_success);
          }, e -> loadingDialog.dismiss());
          break;
        case Share:
          fragment.editToolBar.resetStatus();
          fragment.sharePDF(fragment.pdfView);
          break;
        case OpenDocument:
          fragment.selectDocument();
          break;
        case Flattened:
          fragment.showFlattenedDialog();
          break;
        case Snip:
          fragment.enterSnipMode();
          break;
        default:
          break;
      }
    };
  }


  public static ActionConfig getDefaultMenuActionConfig(CPDFDocumentFragment fragment, ToolBarAction action){
    View.OnClickListener listener = CPDFToolBarMenuHelper.getDefaultClickListener(fragment, action);
      return  ActionConfig.ofDefault(action, listener);
  }

  public static ActionConfig getCustomMenuActionConfig(Context context, CustomToolbarItem customToolbarItem, View.OnClickListener clickListener){
    int bitmapResId;
    if (customToolbarItem.action == ToolBarAction.Custom){
      bitmapResId = CBitmapUtil.getBitmapResId(context, customToolbarItem.icon);
    }else {
      bitmapResId = getDefaultIconRes(customToolbarItem.action);
    }
    String title = customToolbarItem.title;
    if (TextUtils.isEmpty(title) && customToolbarItem.action != ToolBarAction.Custom){
      int titleResId = getDefaultTitleRes(customToolbarItem.action);
      title = context.getString(titleResId);
    }
    return ActionConfig.ofCustom(bitmapResId, title, customToolbarItem.identifier, clickListener);
  }

}
