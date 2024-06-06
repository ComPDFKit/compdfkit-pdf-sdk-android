/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.forms;

import java.io.Serializable;


public class FormsAttributes implements Serializable {

    public FormsTextFieldAttr textField = new FormsTextFieldAttr();

    public FormsCheckBoxAttr checkBox = new FormsCheckBoxAttr();

    public FormsRadioButtonAttr radioButton = new FormsRadioButtonAttr();

    public FormsListBoxAttr listBox = new FormsListBoxAttr();

    public FormsComboBoxAttr comboBox = new FormsComboBoxAttr();

    public FormsPushButtonAttr pushButton = new FormsPushButtonAttr();

    public FormsAttr signatureFields = new FormsAttr();

}
