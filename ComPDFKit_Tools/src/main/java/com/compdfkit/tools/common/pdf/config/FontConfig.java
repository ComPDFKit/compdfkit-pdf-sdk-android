/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO THE ComPDFKit LICENSE AGREEMENT.
 */

package com.compdfkit.tools.common.pdf.config;

import java.io.Serializable;

/**
 * A configured font before it is resolved to an SDK PostScript name.
 */
public class FontConfig implements Serializable {

    public static final String DEFAULT_FAMILY_NAME = "Helvetica";
    public static final String DEFAULT_STYLE_NAME = "Regular";

    private String familyName = DEFAULT_FAMILY_NAME;
    private String styleName = DEFAULT_STYLE_NAME;

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }
}
