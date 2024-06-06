package com.compdfkit.tools.common.pdf.config;

import androidx.annotation.NonNull;

import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @classname:
 * @author: LiuXiaoLong
 * @date: 2023/12/6
 * description:
 */
public class ModeConfig implements Serializable {

    public ModeConfig() {

    }

    public static ModeConfig normal(){
        ModeConfig modeConfig = new ModeConfig();
        modeConfig.initialViewMode = CPreviewMode.Viewer;
        modeConfig.availableViewModes = Arrays.asList(
                CPreviewMode.Viewer,
                CPreviewMode.Annotation,
                CPreviewMode.Edit,
                CPreviewMode.Form,
                CPreviewMode.Signature
        );
        return modeConfig;
    }

    public ModeConfig(CPreviewMode initialViewMode) {
        this.initialViewMode = initialViewMode;
        availableViewModes = Arrays.asList(
                CPreviewMode.Viewer,
                CPreviewMode.Annotation,
                CPreviewMode.Edit,
                CPreviewMode.Form,
                CPreviewMode.Signature
        );
    }

    public CPreviewMode initialViewMode = CPreviewMode.Viewer;

    public boolean readerOnly = false;

    public List<CPreviewMode> availableViewModes = new ArrayList<>();

    @NonNull
    @Override
    public String toString() {
        String stringBuilder = "[modeConfig: " + "initialViewMode:" + initialViewMode.name() +
                ", availableViewModes: " + availableViewModes.toString() + "]";
        return stringBuilder;
    }
}
