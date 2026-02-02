/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.CustomToolbarItem;
import com.compdfkit.tools.common.pdf.config.ToolbarConfig;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.window.CModeSwitchDialogFragment;
import com.compdfkit.tools.common.views.CPDFToolBarMenuHelper.ActionConfig;
import com.compdfkit.tools.common.views.CPDFToolBarMenuHelper.ToolBarAction;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;


/**
 * pdf ui tool bar <br/>
 * ︳------------------------------------------------︳<br/>
 * ︳　　　　　　　　title　　　　　icon1  icon2 icon3   ︳<br/>
 * ︳------------------------------------------------︳<br/>
 * icon1: searchIcon <br/>
 * icon2: boTaIcon <br/>
 * icon3: moreIcon <br/>
 * <p/>
 * use samples:<br/>
 * com.compdfkit.tools.common.utils.view.CPDFToolBar <br/>
 * android:id="@+id/pdf_tool_bar" <br/>
 * android:layout_width="match_parent" <br/>
 * android:layout_height="?android:attr/actionBarSize" <br/>
 * android:title="@string/viewer_toolbar_title" <br/>
 * app:tools_toolbar_bota_icon="@drawable/xxx" <br/>
 * app:tools_toolbar_more_icon="@drawable/xxx" <br/>
 * app:tools_toolbar_search_icon="@drawable/xxx"/> <br/>
 * <p/>
 * custom attrs: <br/>
 * android:title="@string/xxx" <br/>
 * app:tools_toolbar_search_icon="@drawable/xxx" <br/>
 * app:tools_toolbar_bota_icon="@drawable/xxx" <br/>
 * app:tools_toolbar_more_icon="@drawable/xxx" <br/>
 * <p/>
 */
public class CPDFToolBar extends FrameLayout {

    private static final String TAG_MODE_SWITCH_DIALOG = "modeSwitchDialogFragment";

    private AppCompatTextView tvToolBarTitle;
    private LinearLayout rightMenuContainer;
    private LinearLayout leftMenuContainer;

    private final LinkedHashSet<CPreviewMode> previewModes = new LinkedHashSet<>();
    private CPreviewMode currentPreviewMode = CPreviewMode.Viewer;

    private CModeSwitchDialogFragment.OnPreviewModeChangeListener previewModeChangeListener;

    public CPDFToolBar(@NonNull Context context) {
        this(context, null);
    }

    public CPDFToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPDFToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.tools_cpdf_tool_bar, this, true);
        ConstraintLayout clRoot = findViewById(R.id.cl_pdf_root_tool_bar);
        clRoot.setOnClickListener(v -> { /* consume to avoid click-through */ });

        rightMenuContainer = findViewById(R.id.ll_menu);
        leftMenuContainer = findViewById(R.id.ll_left_menu);
        tvToolBarTitle = findViewById(R.id.tv_tool_bar_title);
        LinearLayout titleContainer = findViewById(R.id.ll_title);

        titleContainer.setOnClickListener(v -> {
            if (previewModes.size() > 1) {
                CViewUtils.hideKeyboard(this);
                CModeSwitchDialogFragment fragment =
                        CModeSwitchDialogFragment.newInstance(previewModes, currentPreviewMode);
                fragment.setSwitchModeListener(previewModeChangeListener);
                FragmentActivity activity = CViewUtils.getFragmentActivity(getContext());
                if (activity != null && !activity.isFinishing()) {
                    fragment.show(activity.getSupportFragmentManager(), TAG_MODE_SWITCH_DIALOG);
                }
            }
        });
        applyAttributes(context, attrs, defStyleAttr);
    }

    private void applyAttributes(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            CViewUtils.applyViewBackground(this);
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CPDFToolBar, defStyleAttr, 0);
        try {
            String title = a.getString(R.styleable.CPDFToolBar_android_title);
            if (!TextUtils.isEmpty(title)) {
                tvToolBarTitle.setText(title);
            }
        } finally {
            a.recycle();
        }
        CViewUtils.applyViewBackground(this);
    }

    public void selectMode(CPreviewMode mode){
        currentPreviewMode = mode;
        tvToolBarTitle.setText(mode.getTitleByMode());
    }

    public CPreviewMode getMode() {
        return currentPreviewMode;
    }

    public void addMode(CPreviewMode previewMode) {
        previewModes.add(previewMode);
        if (previewModes.size() > 1) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.tools_ic_syas_arrow);
            if (drawable != null) {
                drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvToolBarTitle.setCompoundDrawables(null, null, drawable, null);
            }
        }else {
            tvToolBarTitle.setCompoundDrawables(null,null,null,null);
        }
    }

    public void addModes(List<CPreviewMode> modes){
        for (CPreviewMode mode : modes) {
            addMode(mode);
        }
    }

    @MainThread
    public CPDFToolBar clearMenus() {
        if (leftMenuContainer != null) leftMenuContainer.removeAllViews();
        if (rightMenuContainer != null) rightMenuContainer.removeAllViews();
        return this;
    }

    public void addAction(@DrawableRes int iconRes, View.OnClickListener listener){
        if (rightMenuContainer == null) return;
        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.tools_cpdf_tool_bar_pdf_view_menu_action, rightMenuContainer, false);
        AppCompatImageView iv = item.findViewById(R.id.iv_action);
        iv.setImageResource(iconRes);
        iv.setOnClickListener(listener);
        rightMenuContainer.addView(item);
    }

    public void setMenuItems(CPDFDocumentFragment fragment, ToolbarConfig toolbarConfig){
        CPDFConfiguration cfg = fragment.pdfView != null ? fragment.pdfView.getCPDFConfiguration() : null;
        if (cfg == null) return;
        if (toolbarConfig == null) return;
        // Left
        if (toolbarConfig.customToolbarLeftItems != null && !toolbarConfig.customToolbarLeftItems.isEmpty()) {
            setCustomMenuItemsInternal(fragment, leftMenuContainer, toolbarConfig.customToolbarLeftItems);
        } else {
            setDefaultMenuItemsInternal(fragment, leftMenuContainer, toolbarConfig.toolbarLeftItems);
        }
        // Right
        if (toolbarConfig.customToolbarRightItems != null && !toolbarConfig.customToolbarRightItems.isEmpty()) {
            setCustomMenuItemsInternal(fragment, rightMenuContainer, toolbarConfig.customToolbarRightItems);
        } else {
            setDefaultMenuItemsInternal(fragment, rightMenuContainer, toolbarConfig.toolbarRightItems);
        }
    }

    public void setLeftMenuItems(CPDFDocumentFragment fragment, List<ToolBarAction> toolBarActions){
        setDefaultMenuItemsInternal(fragment, leftMenuContainer, toolBarActions);
    }

    public void setLeftCustomMenuItems(CPDFDocumentFragment fragment, List<CustomToolbarItem> customToolbarItems){
        setCustomMenuItemsInternal(fragment, leftMenuContainer, customToolbarItems);
    }

    public void setRightMenuItems(CPDFDocumentFragment fragment, List<ToolBarAction> toolBarActions){
        setDefaultMenuItemsInternal(fragment, rightMenuContainer, toolBarActions);
    }

    public void setRightCustomMenuItems(CPDFDocumentFragment fragment, List<CustomToolbarItem> customToolbarItems){
        setCustomMenuItemsInternal(fragment, rightMenuContainer, customToolbarItems);
    }

    public void setMoreMenuActions(@NonNull CPDFDocumentFragment fragment, @Nullable ToolbarConfig toolbarConfig) {
        if (toolbarConfig == null || fragment.menuWindow == null) return;

        if (toolbarConfig.customMoreMenuItems != null && !toolbarConfig.customMoreMenuItems.isEmpty()) {
            for (CustomToolbarItem item : toolbarConfig.customMoreMenuItems) {
                View.OnClickListener clickListener;
                if (item.action == ToolBarAction.Custom){
                    clickListener = v -> notifyCustomItemClick(item.identifier);
                }else {
                    clickListener = CPDFToolBarMenuHelper.getDefaultClickListener(fragment, item.action);
                }
                ActionConfig config = CPDFToolBarMenuHelper.getCustomMenuActionConfig(
                        fragment.getContext(), item,
                        clickListener
                );
                View view = CPDFToolBarMenuHelper.getMoreAction(fragment.getContext(), fragment.menuWindow, config);
                fragment.menuWindow.addItem(view);
            }
        } else if (toolbarConfig.availableMenus != null) {
            for (ToolBarAction action : toolbarConfig.availableMenus) {
                ActionConfig config = CPDFToolBarMenuHelper.getDefaultMenuActionConfig(fragment, action);
                View view = CPDFToolBarMenuHelper.getMoreAction(fragment.getContext(), fragment.menuWindow, config);
                fragment.menuWindow.addItem(view);
            }
        }
    }

    public void setMoreCustomMenuItems(@NonNull CPDFDocumentFragment fragment,
                                           @Nullable List<CustomToolbarItem> customToolbarItems) {
        if (customToolbarItems == null || fragment.menuWindow == null) return;
        for (CustomToolbarItem item : customToolbarItems) {
            ActionConfig config = CPDFToolBarMenuHelper.getCustomMenuActionConfig(
                    fragment.getContext(), item,
                    v -> notifyCustomItemClick(item.identifier)
            );
            View view = CPDFToolBarMenuHelper.getMoreAction(fragment.getContext(), fragment.menuWindow, config);
            fragment.menuWindow.addItem(view);
        }
    }

    private void setDefaultMenuItemsInternal(CPDFDocumentFragment fragment,
                                             @Nullable LinearLayout container,
                                             @Nullable List<ToolBarAction> actions) {
        if (container == null) return;
        container.removeAllViews();
        if (actions == null || actions.isEmpty()) return;
        for (ToolBarAction action : actions) {
            ActionConfig config = CPDFToolBarMenuHelper.getDefaultMenuActionConfig(fragment, action);
            View v = CPDFToolBarMenuHelper.getToolbarMainAction(fragment.getContext(), config);
            container.addView(v);
        }
        container.setVisibility(VISIBLE);
    }

    private void setCustomMenuItemsInternal(CPDFDocumentFragment fragment,
                                            @Nullable LinearLayout container,
                                            @Nullable List<CustomToolbarItem> items) {
        if (container == null) return;
        container.removeAllViews();
        if (items == null || items.isEmpty()) return;
        for (CustomToolbarItem item : items) {
            View.OnClickListener clickListener;
            if (item.action == ToolBarAction.Custom){
                clickListener = v -> notifyCustomItemClick(item.identifier);
            }else {
                clickListener = CPDFToolBarMenuHelper.getDefaultClickListener(fragment, item.action);
            }
            ActionConfig config = CPDFToolBarMenuHelper.getCustomMenuActionConfig(
                    fragment.getContext(), item,
                    clickListener
            );
            View v = CPDFToolBarMenuHelper.getToolbarMainAction(fragment.getContext(), config);
            container.addView(v);
        }
        container.setVisibility(VISIBLE);
    }

    private void notifyCustomItemClick(@Nullable String identifier) {
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, CPDFCustomEventType.TOOLBAR_ITEM_TAPPED);
        CPDFCustomEventCallbackHelper.getInstance().notifyClick(identifier, extraMap);
    }

    public void setPreviewModeChangeListener(CModeSwitchDialogFragment.OnPreviewModeChangeListener changeListener) {
        this.previewModeChangeListener = changeListener;
    }

}
