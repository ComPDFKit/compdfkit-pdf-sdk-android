/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.watermark;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.watermark.CPDFWatermark;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicThemeFragment;
import com.compdfkit.tools.common.utils.glide.GlideApp;
import com.compdfkit.tools.common.utils.threadpools.SimpleBackgroundTask;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CBasicOnStyleChangeListener;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.security.watermark.pdfproperties.CPageRange;
import com.compdfkit.tools.security.watermark.view.CWatermarkPageView;
import com.compdfkit.tools.security.watermark.view.CWatermarkView;

import java.util.HashMap;
import java.util.Map;

/**
 * The watermark editing page fragment provides the function of editing
 * text watermarks and image watermarks.
 * WatermarkView.EditType is passed in when creating an instance.
 * <p>
 * CWatermarkPageFragment.java
 * -----> CWatermarkPageView.java
 * -----> CWatermarkView.java
 * <br/><br/>
 * create txt watermark:
 * <blockquote><pre>
 *     CWatermarkPageFragment pageFragment = CWatermarkPageFragment.newInstance(EditType.TXT);
 *     pageFragment.setDocument(cpdfDocument);
 *     pageFragment.setPageIndex(pageIndex);
 *     pageFragment.applyWatermark();
 * </pre></blockquote><p>
 * create image watermark:
 * <blockquote><pre>
 *     CWatermarkPageFragment pageFragment = CWatermarkPageFragment.newInstance(EditType.Image);
 *     pageFragment.setDocument(cpdfDocument);
 *     pageFragment.setPageIndex(pageIndex);
 *     pageFragment.applyWatermark();
 * </pre></blockquote><p>
 *
 * Edit an existing watermark:
 * <blockquote><pre>
 *     CWatermarkPageFragment pageFragment = CWatermarkPageFragment.newInstance(EditType.Image); or EditType.Txt
 *     pageFragment.setDocument(cpdfDocument);
 *     pageFragment.setPageIndex(pageIndex);
 *     pageFragment.setEditWatermark(cpdfDocument.getWatermark(index));
 *     pageFragment.applyWatermark();
 * </pre></blockquote><p>
 *
 * @see CWatermarkPageView
 * @see CWatermarkView
 *
 */
public class CWatermarkPageFragment extends CBasicThemeFragment {

    private CWatermarkPageView watermarkPageView;

    private CWatermarkView.EditType editType;

    private CPDFDocument document;

    private int pageIndex = 0;

    private CPDFWatermark watermark;

    private String defaultText;

    private String defaultImagePath;

    public static CWatermarkPageFragment newInstance(CWatermarkView.EditType type) {
        Bundle args = new Bundle();
        args.putString("edit_type", type.name());
        CWatermarkPageFragment fragment = new CWatermarkPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setDocument(CPDFDocument document) {
        this.document = document;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public void setDefaultImagePath(String defaultImagePath) {
        this.defaultImagePath = defaultImagePath;
    }

    public void setEditWatermark(CPDFWatermark watermark) {
        this.watermark = watermark;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_cpdf_security_watermark_page_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        watermarkPageView = rootView.findViewById(R.id.watermark_page_view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        watermarkPageView.setDocument(document, pageIndex);
        if (getArguments() != null) {
            editType = CWatermarkView.EditType.valueOf(getArguments().getString("edit_type", CWatermarkView.EditType.TXT.name()));
            watermarkPageView.afterMeasured(() -> {
                if (watermark != null) {
                    watermarkPageView.editWatermark(watermark);
                    return;
                }
                if (editType == CWatermarkView.EditType.TXT) {
                    if (!TextUtils.isEmpty(defaultText)){
                        // If it is a text watermark type, a watermark named 'Watermark' will be created by default.
                        watermarkPageView.createTextWatermark(defaultText);
                    }else {
                        // If it is a text watermark type, a watermark named 'Watermark' will be created by default.
                        watermarkPageView.createTextWatermark(getString(R.string.tools_default_watermark_text));
                    }
                } else {
                    watermarkPageView.watermarkView.setWatermarkType(editType);
                    if (!TextUtils.isEmpty(defaultImagePath)) {
                        updateImageWatermark(defaultImagePath);
                    }
                }
            });
        }
    }

    /**
     * According to the watermark type,
     * display the text watermark and image watermark attribute adjustment panel
     * <p>
     * txt watermark
     *
     * @see com.compdfkit.tools.security.watermark.pdfproperties.CWatermarkTextStyleFragment
     * <p>
     * image watermark
     */
    public void showWatermarkStyleDialog() {
        CStyleType styleType = editType == CWatermarkView.EditType.TXT ? CStyleType.WATERMARK_TEXT : CStyleType.WATERMARK_IMAGE;
        CAnnotStyle annotStyle = new CAnnotStyle(styleType);
        annotStyle.setFontColor(watermarkPageView.watermarkView.getTextColor());
        annotStyle.setFontSize(watermarkPageView.watermarkView.getTextSize());
        annotStyle.setTextColorOpacity(watermarkPageView.watermarkView.getWatermarkAlpha());
        annotStyle.setExternFontName(watermarkPageView.watermarkView.getFontPsName());
        annotStyle.setChecked(watermarkPageView.isTile());
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("pageRange", watermarkPageView.getPageRange().name());
        extraMap.put("front", watermarkPageView.isFront());
        annotStyle.setCustomExtraMap(extraMap);
        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(annotStyle);
        styleDialogFragment.addAnnotStyleChangeListener(new CBasicOnStyleChangeListener() {
            @Override
            public void onChangeTextColor(int textColor) {
                super.onChangeTextColor(textColor);
                watermarkPageView.watermarkView.setTextColor(textColor);
                watermarkPageView.updateTileWatermark();
            }

            @Override
            public void onChangeTextColorOpacity(int textColorOpacity) {
                super.onChangeTextColorOpacity(textColorOpacity);
                watermarkPageView.watermarkView.setWatermarkAlpha(textColorOpacity);
                watermarkPageView.updateTileWatermark();
            }

            @Override
            public void onChangeAnnotExternFontType(String fontPsName) {
                super.onChangeAnnotExternFontType(fontPsName);
                watermarkPageView.watermarkView.setTypeface(fontPsName);
                watermarkPageView.updateTileWatermark();
            }

            @Override
            public void onChangeFontSize(int fontSize) {
                super.onChangeFontSize(fontSize);
                watermarkPageView.watermarkView.setTextSize(fontSize);
                watermarkPageView.updateTileWatermark();
            }

            @Override
            public void onChangeIsChecked(boolean isChecked) {
                super.onChangeIsChecked(isChecked);
                watermarkPageView.setTile(isChecked);
                watermarkPageView.updateTileWatermark();
            }

            @Override
            public void onChangeExtraMap(Map<String, Object> extraMap) {
                super.onChangeExtraMap(extraMap);
                if (extraMap.containsKey("pageRange")) {
                    watermarkPageView.setPageRange(CPageRange.valueOf((String) extraMap.get("pageRange")));
                }
                if (extraMap.containsKey("front")) {
                    watermarkPageView.setFront((Boolean) extraMap.get("front"));
                }
            }

            @Override
            public void onChangeImagePath(String imagePath, Uri imageUri) {
                super.onChangeImagePath(imagePath, imageUri);
                if (!TextUtils.isEmpty(imagePath)){
                    updateImageWatermark(imagePath);
                }else {
                    updateImageWatermark(imageUri.toString());
                }
            }
        });
        styleDialogFragment.show(getChildFragmentManager(), "styleFragment");
    }

    private void updateImageWatermark(String image){
        new SimpleBackgroundTask<Bitmap>(getContext()) {

            @Override
            protected Bitmap onRun() {
                try {
                    Bitmap bitmap = GlideApp.with(getContext()).asBitmap().load(image).submit(360, 480).get();
                    return bitmap;
                } catch (Exception e) {
                }
                return null;
            }

            @Override
            protected void onSuccess(Bitmap result) {
                if (result != null) {
                    if (watermarkPageView.watermarkView.getImageWatermarkBitmap() != null) {
                        watermarkPageView.watermarkView.setImageBitmap(result);
                        return;
                    }
                    watermarkPageView.createImageWatermark(result);
                    watermarkPageView.updateTileWatermark();
                }
            }
        }.execute();
    }

    public boolean hasWatermark() {
        if (editType == CWatermarkView.EditType.Image) {
            return watermarkPageView.watermarkView.getImageWatermarkBitmap() != null;
        }
        return true;
    }

    /**
     * Apply the currently edited watermark to the document
     */
    public boolean applyWatermark() {
        if (watermark != null) {
            return watermarkPageView.modifyWatermark(watermark);
        } else {
            return watermarkPageView.createWatermark();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            watermarkPageView.afterMeasured(()->{
                watermarkPageView.updateCenterPoint();
            });
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            watermarkPageView.afterMeasured(()->{
                watermarkPageView.updateCenterPoint();
            });
        }
    }
}
