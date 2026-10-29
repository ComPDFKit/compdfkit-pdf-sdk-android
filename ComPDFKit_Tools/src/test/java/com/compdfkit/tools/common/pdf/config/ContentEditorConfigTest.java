/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config;

import static org.junit.Assert.assertEquals;

import com.compdfkit.tools.common.pdf.CPDFConfigurationUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ContentEditorConfigTest {

    @Test
    public void textFontIsStoredAsUnresolvedFontConfig() {
        String json = "{\"contentEditorConfig\":{\"initAttribute\":{\"text\":{" +
                "\"familyName\":\"Noto Sans\"," +
                "\"styleName\":\"Bold Italic\"}}}}";
        CPDFConfiguration configuration = CPDFConfigurationUtils.fromJson(json);

        FontConfig fontConfig = configuration.contentEditorConfig.initAttribute.text.getFontConfig();

        assertEquals("Noto Sans", fontConfig.getFamilyName());
        assertEquals("Bold Italic", fontConfig.getStyleName());
    }
}
