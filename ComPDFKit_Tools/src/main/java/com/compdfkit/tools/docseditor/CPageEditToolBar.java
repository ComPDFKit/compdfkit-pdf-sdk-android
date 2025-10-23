package com.compdfkit.tools.docseditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.CPDFEditorConfig;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;

import java.util.List;

public class CPageEditToolBar extends FrameLayout {

    private CPDFViewCtrl pdfView;

    public LinearLayout llMenu;
    private View.OnClickListener insertClickListener;
    private View.OnClickListener replaceClickListener;
    private View.OnClickListener extractClickListener;
    private View.OnClickListener copyClickListener;
    private View.OnClickListener rotateClickListener;
    private View.OnClickListener deleteClickListener;

    public CPageEditToolBar(@NonNull Context context) {
        this(context, null);
    }

    public CPageEditToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPageEditToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CPageEditToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
        CPDFConfiguration configuration = pdfView.getCPDFConfiguration();
        if (configuration != null && configuration.globalConfig != null && configuration.globalConfig.pageEditor != null) {
            setMenus(configuration.globalConfig.pageEditor.menus);
        }else {
            setMenus(new CPDFEditorConfig().menus);
        }
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_pageedit_tool_bar, this);
        CViewUtils.applyViewBackground(this);
        llMenu = findViewById(R.id.ll_menu);
    }

    public void setMenus(List<CPDFEditorConfig.CPDFEditorMenus> menus){
        for (CPDFEditorConfig.CPDFEditorMenus menu : menus) {
            switch (menu){
                case INSERT_PAGE:
                    addItem(R.drawable.tools_ic_pageedit_insert, R.string.tools_page_edit_toolbar_insert, v -> {
                        if (insertClickListener != null) {
                            insertClickListener.onClick(v);
                        }
                    });
                    break;
                case REPLACE_PAGE:
                    addItem(R.drawable.tools_ic_pageedit_replace, R.string.tools_page_edit_toolbar_replace, v -> {
                        if (replaceClickListener != null) {
                            replaceClickListener.onClick(v);
                        }
                    });
                    break;
                case EXTRACT_PAGE:
                    addItem(R.drawable.tools_ic_pageedit_extract, R.string.tools_page_edit_toolbar_extract, v -> {
                        if (extractClickListener != null) {
                            extractClickListener.onClick(v);
                        }
                    });
                    break;
                case COPY_PAGE:
                    addItem(R.drawable.tools_ic_pageedit_copy, R.string.tools_page_edit_toolbar_copy, v -> {
                        if (copyClickListener != null) {
                            copyClickListener.onClick(v);
                        }
                    });
                    break;
                case ROTATE_PAGE:
                    addItem(R.drawable.tools_ic_pageedit_rotate, R.string.tools_page_edit_toolbar_rotate, v -> {
                        if (rotateClickListener != null) {
                            rotateClickListener.onClick(v);
                        }
                    });
                    break;
                case DELETE_PAGE:
                    addItem(R.drawable.tools_ic_pageedit_delete, R.string.tools_page_edit_toolbar_delete, v -> {
                        if (deleteClickListener != null) {
                            deleteClickListener.onClick(v);
                        }
                    });
                    break;
            }
        }
    }

    public void addItem(@DrawableRes int iconRes, @StringRes int titleRes, View.OnClickListener listener) {
        View itemView = inflate(getContext(), R.layout.tools_page_edit_tool_bar_item, null);
        AppCompatImageView icon = itemView.findViewById(R.id.iv_icon);
        icon.setImageResource(iconRes);
        AppCompatTextView title = itemView.findViewById(R.id.tv_title);
        title.setText(titleRes);
        itemView.setOnClickListener(listener);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        llMenu.addView(itemView, layoutParams);
    }

    public void setInsertPageListener(View.OnClickListener listener) {
        insertClickListener = listener;
    }

    public void setReplacePageListener(View.OnClickListener listener) {
        replaceClickListener = listener;
    }

    public void setExtractPageListener(View.OnClickListener listener) {
        extractClickListener = listener;
    }

    public void setCopyPageListener(View.OnClickListener listener) {
        copyClickListener = listener;
    }

    public void setRotatePageListener(View.OnClickListener listener) {
        rotateClickListener = listener;
    }

    public void setDeletePageListener(View.OnClickListener listener) {
        deleteClickListener = listener;
    }
}
