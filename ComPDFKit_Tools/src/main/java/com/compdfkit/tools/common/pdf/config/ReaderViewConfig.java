/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;


public class ReaderViewConfig implements Serializable {

    public boolean linkHighlight = true;

    public boolean formFieldHighlight = true;

    public DisplayMode displayMode = DisplayMode.SinglePage;

    public boolean verticalMode = true;

    public boolean continueMode = true;

    public boolean cropMode = false;

    public Themes themes = Themes.Light;

    public boolean enableSliderBar = true;

    public boolean enablePageIndicator = true;

    public int pageSpacing = 10;

    public float pageScale = 1.0F;

    public boolean pageSameWidth = true;

    public boolean enableMinScale = true;

    public ArrayList<Integer> margins = new ArrayList<>();

    @NonNull
    @Override
    public String toString() {
        String stringBuilder = "[readerViewConfig: " + "linkHighlight:" + linkHighlight + ", " +
                "formFieldHighlight:" + formFieldHighlight + ", " +
                "displayMode:" + displayMode.name() + ", " +
                "verticalMode:" + verticalMode + ", " +
                "continueMode:" + continueMode + ", " +
                "cropMode:" + cropMode + ", " +
                "cropMode:" + cropMode + ", " +
                "themes:" + themes.name() + ", " +
                "enableSliderBar:" + enableSliderBar + ", " +
                "enablePageIndicator:" + enablePageIndicator + ", " +
                "pageSpacing:" + pageSpacing + ", " +
                "pageScale:" + pageScale + ", " +
                "pageSameWidth:" + pageSameWidth + ", " +
                "enableMinScale:" + enableMinScale + ", " +
                "margins:" + margins.toString();
        return stringBuilder;
    }

    public enum DisplayMode{
        DoublePage,

        SinglePage,

        CoverPage
    }

    public enum Themes{

        Light,

        Dark,

        Sepia,

        Reseda

    }
}
