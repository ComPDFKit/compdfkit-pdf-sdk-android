/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config;


import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.common.pdf.config.forms.FormsAttributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FormsConfig implements Serializable {

    public FormsConfig() {

    }

    public static FormsConfig normal() {
        FormsConfig formsConfig = new FormsConfig();
        formsConfig.availableTypes = Arrays.asList(
                CPDFWidget.WidgetType.Widget_TextField,
                CPDFWidget.WidgetType.Widget_CheckBox,
                CPDFWidget.WidgetType.Widget_RadioButton,
                CPDFWidget.WidgetType.Widget_ListBox,
                CPDFWidget.WidgetType.Widget_ComboBox,
                CPDFWidget.WidgetType.Widget_PushButton,
                CPDFWidget.WidgetType.Widget_SignatureFields
        );
        formsConfig.availableTools = Arrays.asList(
                FormsTools.Undo,
                FormsTools.Redo
        );
        return formsConfig;
    }

    public List<CPDFWidget.WidgetType> availableTypes = new ArrayList<>();

    public List<FormsTools> availableTools = new ArrayList<>();

    public FormsAttributes initAttribute = new FormsAttributes();

    public enum FormsTools implements Serializable {

        Undo,

        Redo;

        public static FormsTools fromString(String str) {
            try {
                String firstLetter = str.substring(0, 1).toUpperCase();
                String result = firstLetter + str.substring(1);
                return FormsTools.valueOf(result);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
