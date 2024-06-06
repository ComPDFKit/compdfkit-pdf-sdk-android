/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import android.graphics.Color;
import android.graphics.RectF;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.form.CPDFCheckboxWidget;
import com.compdfkit.core.annotation.form.CPDFComboboxWidget;
import com.compdfkit.core.annotation.form.CPDFListboxWidget;
import com.compdfkit.core.annotation.form.CPDFPushbuttonWidget;
import com.compdfkit.core.annotation.form.CPDFRadiobuttonWidget;
import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.annotation.form.CPDFTextWidget;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.annotation.form.CPDFWidgetItem;
import com.compdfkit.core.document.CPDFDestination;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.action.CPDFAction;
import com.compdfkit.core.document.action.CPDFGoToAction;
import com.compdfkit.core.document.action.CPDFUriAction;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.SampleApplication;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class InteractiveFormsTest extends PDFSamples {

    public InteractiveFormsTest() {
        setTitle(R.string.interactive_forms_test_title);
        setDescription(R.string.interactive_forms_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);

        printHead();
        CPDFDocument document = CPDFDocument.createDocument(context);
        // Create a blank new page and add some form fields.
        document.insertBlankPage(0, 595, 842);
        document.insertBlankPage(1, 595, 842);

        //---------------------------------
        //Samples 1 : Programmatically create new Form Fields and Widget Annotations.
        createTestForms(document);

        //---------------------------------
        //Samples 2 : Traverse all form fields in the document (and print out their names).
        printFormsMessage(document);

        File file = new File(outputDir(), "forms/Create_From_Test.pdf");
        saveSamplePDF(document, file, true);
        deleteForm();
        printFooter();
    }

    private void createTestForms(CPDFDocument document) {
        // create new Form Fields and Widget Annotations.
        int pageNumber = 0;
        RectF pageSize = document.getPageSize(pageNumber);
        CPDFPage cpdfPage = document.pageAtIndex(pageNumber);

        //Insert a single-line TextField.
        RectF singleLineTextRect = new RectF(28F, 32F, 237F, 75F);
        singleLineTextRect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), singleLineTextRect);
        CPDFTextWidget singleLineTextWidget = (CPDFTextWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_TextField);
        singleLineTextWidget.setRect(singleLineTextRect);
        singleLineTextWidget.setFieldName("TextField1");
        singleLineTextWidget.setText("Basic Text Field");
        singleLineTextWidget.setFontColor(Color.BLACK);
        singleLineTextWidget.setFontSize(15);
        singleLineTextWidget.updateAp();

        //Insert a multiline TextField.
        RectF multilineTextRect = new RectF(28F, 97F, 237F, 189F);
        multilineTextRect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), multilineTextRect);
        CPDFTextWidget multiLineTextWidget = (CPDFTextWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_TextField);
        multiLineTextWidget.setRect(multilineTextRect);
        multiLineTextWidget.setFieldName("TextField2");
        multiLineTextWidget.setText("Basic Text Field\nBasic Text Field\nBasic Text Field");
        multiLineTextWidget.setMultiLine(true);
        multiLineTextWidget.setFontColor(Color.BLACK);
        multiLineTextWidget.setFontSize(15);
        multiLineTextWidget.updateAp();

        //Insert a ListBox widget.
        RectF listBoxRect = new RectF(267F, 32F, 567F, 138F);
        listBoxRect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), listBoxRect);
        CPDFListboxWidget listBoxWidget = (CPDFListboxWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_ListBox);
        listBoxWidget.setRect(listBoxRect);
        listBoxWidget.setFieldName("ListBox1");
        listBoxWidget.setFillColor(Color.WHITE);
        listBoxWidget.setBorderColor(Color.BLACK);
        listBoxWidget.setBorderWidth(2);
        CPDFWidgetItem[] listBoxItems = new CPDFWidgetItem[]{
                new CPDFWidgetItem("List Box No.1", "List Box No.1"),
                new CPDFWidgetItem("List Box No.2", "List Box No.2"),
                new CPDFWidgetItem("List Box No.3", "List Box No.3"),
        };
        listBoxWidget.setOptionItems(listBoxItems);
        listBoxWidget.setSelectedIndexes(new int[]{1});
        listBoxWidget.updateAp();

        //Insert a ComboBox Widget.
        RectF comboBoxRect = new RectF(267F, 143F, 567F, 189F);
        comboBoxRect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), comboBoxRect);
        CPDFComboboxWidget comboBoxWidget = (CPDFComboboxWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_ComboBox);
        comboBoxWidget.setRect(comboBoxRect);
        comboBoxWidget.setFieldName("ComboBox1");
        CPDFWidgetItem[] comboBoxItems = new CPDFWidgetItem[]{
                new CPDFWidgetItem("Combo Box No.1", "Combo Box No.1"),
                new CPDFWidgetItem("Combo Box No.2", "Combo Box No.2"),
                new CPDFWidgetItem("Combo Box No.3", "Combo Box No.3"),
        };
        comboBoxWidget.setOptionItems(comboBoxItems, new int[]{1});
        comboBoxWidget.updateAp();

        //Insert a Form Signature Widget (unsigned)
        RectF signatureRect = new RectF(28F, 206F, 237F, 301F);
        signatureRect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), signatureRect);
        CPDFSignatureWidget signatureWidget = (CPDFSignatureWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_SignatureFields);
        signatureWidget.setFieldName("Signature1");
        signatureWidget.setFillColor(Color.WHITE);
        signatureWidget.setBorderColor(Color.BLACK);
        signatureWidget.setBorderWidth(2F);
        signatureWidget.setRect(signatureRect);
        signatureWidget.updateAp();

        //Insert a PushButton to jump to a page.
        RectF pushButton1Rect = new RectF(267F, 203F, 401F, 235F);
        pushButton1Rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), pushButton1Rect);
        CPDFPushbuttonWidget pushButtonWidget1 = (CPDFPushbuttonWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_PushButton);
        pushButtonWidget1.setRect(pushButton1Rect);
        pushButtonWidget1.setFieldName("PushButton1");
        pushButtonWidget1.setFontColor(Color.BLACK);
        pushButtonWidget1.setFontSize(15);
        pushButtonWidget1.setButtonTitle("PushButton");
        //set PushButton jump to a page action
        CPDFGoToAction goToAction = new CPDFGoToAction();
        CPDFDestination destination = new CPDFDestination(1, 0, 842, 0F);
        goToAction.setDestination(document, destination);
        pushButtonWidget1.setButtonAction(goToAction);
        pushButtonWidget1.updateAp();

        //Insert a PushButton to jump to a website.
        RectF pushButton2Rect = new RectF(433F, 203F, 567F, 235F);
        pushButton2Rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), pushButton2Rect);
        CPDFPushbuttonWidget pushButtonWidget2 = (CPDFPushbuttonWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_PushButton);
        pushButtonWidget2.setRect(pushButton2Rect);
        pushButtonWidget2.setFieldName("PushButton2");
        pushButtonWidget2.setFontColor(Color.BLACK);
        pushButtonWidget2.setFontSize(15);
        pushButtonWidget2.setButtonTitle("PushButton");
        //set PushButton jump to a website
        CPDFUriAction uriAction = new CPDFUriAction();
        uriAction.setUri("https://www.compdf.com/");
        pushButtonWidget2.setButtonAction(uriAction);
        pushButtonWidget2.updateAp();

        //Insert CheckBox Widget
        RectF checkBox1 = new RectF(267F, 251F, 299F, 283F);
        checkBox1 = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), checkBox1);
        CPDFCheckboxWidget checkboxWidget = (CPDFCheckboxWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_CheckBox);
        checkboxWidget.setRect(checkBox1);
        checkboxWidget.setFieldName("CheckBox1");
        checkboxWidget.setFillColor(Color.parseColor("#CCE5E5FF"));
        checkboxWidget.setBorderColor(Color.BLACK);
        checkboxWidget.setBorderWidth(2F);
        checkboxWidget.setChecked(false);
        checkboxWidget.updateAp();

        RectF checkBox2 = new RectF(326F, 251F, 358F, 283F);
        checkBox2 = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), checkBox2);
        CPDFCheckboxWidget checkboxWidget2 = (CPDFCheckboxWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_CheckBox);
        checkboxWidget2.setRect(checkBox2);
        checkboxWidget2.setFieldName("CheckBox1");
        checkboxWidget2.setFillColor(Color.parseColor("#CCE5E5FF"));
        checkboxWidget2.setBorderColor(Color.BLACK);
        checkboxWidget2.setBorderWidth(2F);
        checkboxWidget2.setChecked(true);
        checkboxWidget2.updateAp();

        //Insert RadioButton Widget
        RectF radioButton1 = new RectF(385F, 251F, 424F, 290F);
        radioButton1 = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), radioButton1);
        CPDFRadiobuttonWidget radiobuttonWidget1 = (CPDFRadiobuttonWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_RadioButton);
        radiobuttonWidget1.setRect(radioButton1);
        radiobuttonWidget1.setFieldName("RadioButton");
        radiobuttonWidget1.setCheckStyle(CPDFWidget.CheckStyle.CK_Circle);
        radiobuttonWidget1.setFillColor(Color.parseColor("#CCE5E5FF"));
        radiobuttonWidget1.setBorderColor(Color.BLACK);
        radiobuttonWidget1.setBorderWidth(2F);
        radiobuttonWidget1.setChecked(false);
        radiobuttonWidget1.setFieldName("RadioButton1");
        radiobuttonWidget1.updateAp();

        RectF radioButton2 = new RectF(450, 251, 489, 290);
        radioButton2 = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), radioButton2);
        CPDFRadiobuttonWidget radiobuttonWidget2 = (CPDFRadiobuttonWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_RadioButton);
        radiobuttonWidget2.setRect(radioButton2);
        radiobuttonWidget2.setFieldName("RadioButton");
        radiobuttonWidget2.setCheckStyle(CPDFWidget.CheckStyle.CK_Circle);
        radiobuttonWidget2.setFillColor(Color.parseColor("#CCE5E5FF"));
        radiobuttonWidget2.setBorderColor(Color.BLACK);
        radiobuttonWidget2.setBorderWidth(2F);
        // Check the widget (by default it is unchecked).
        radiobuttonWidget2.setChecked(true);
        radiobuttonWidget2.setFieldName("RadioButton1");
        radiobuttonWidget2.updateAp();

        RectF radioButton3 = new RectF(515, 251, 554, 290);
        radioButton3 = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), radioButton3);
        CPDFRadiobuttonWidget radiobuttonWidget3 = (CPDFRadiobuttonWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_RadioButton);
        radiobuttonWidget3.setRect(radioButton3);
        radiobuttonWidget3.setFieldName("RadioButton");
        radiobuttonWidget3.setCheckStyle(CPDFWidget.CheckStyle.CK_Circle);
        radiobuttonWidget3.setFillColor(Color.parseColor("#CCE5E5FF"));
        radiobuttonWidget3.setBorderColor(Color.BLACK);
        radiobuttonWidget3.setBorderWidth(2F);
        radiobuttonWidget3.setChecked(false);
        radiobuttonWidget3.setFieldName("RadioButton1");
        radiobuttonWidget3.updateAp();
        outputListener.println("Done.");
        outputListener.println("Done. Result saved in Create_Form_Test.pdf");
        printDividingLine();
    }


    private void deleteForm(){
        File file = new File(outputDir(), "forms/Create_From_Test.pdf");
        CPDFDocument document = new CPDFDocument(SampleApplication.getInstance());
        document.open(file.getAbsolutePath());
        CPDFPage page = document.pageAtIndex(0);
        for (CPDFAnnotation annotation : page.getAnnotations()) {
            if (annotation.getType() == CPDFAnnotation.Type.WIDGET){
                page.deleteAnnotation(annotation);
                break;
            }
        }
        File resultsForms = new File(outputDir(), "forms/Delete_Form_Test.pdf");
        saveSamplePDF(document, resultsForms,true);
        outputListener.println("Done.");
        outputListener.println("Done. Result saved in Delete_Form_Test.pdf");
    }

    private void printFormsMessage(CPDFDocument document) {
        for (int i = 0; i < document.getPageCount(); i++) {
            CPDFPage page = document.pageAtIndex(i);
            for (CPDFAnnotation annotation : page.getAnnotations()) {
                switch (annotation.getType()) {
                    case WIDGET:
                        CPDFWidget cpdfWidget = (CPDFWidget) annotation;
                        outputListener.println("Field name : " + cpdfWidget.getFieldName());
                        switch (cpdfWidget.getWidgetType()) {
                            case Widget_TextField:
                                outputListener.println("Field partial name : " + ((CPDFTextWidget) cpdfWidget).getText());
                                break;
                            case Widget_ListBox:
                                CPDFListboxWidget listBoxWidget = (CPDFListboxWidget) cpdfWidget;
                                CPDFWidgetItem[] options = listBoxWidget.getOptions();
                                int[] selectedIndexs = listBoxWidget.getSelectedIndexes();
                                if (options != null && selectedIndexs != null) {
                                    CPDFWidgetItem selectItem = options[selectedIndexs[0]];
                                    outputListener.println("Field Select Item : " + selectItem.text);
                                }
                                break;
                            case Widget_ComboBox:
                                CPDFComboboxWidget comboBoxWidget = (CPDFComboboxWidget) cpdfWidget;
                                CPDFWidgetItem[] comboBoxOptions = comboBoxWidget.getOptions();
                                int[] selectedIndexs1 = comboBoxWidget.getSelectedIndexes();
                                if (comboBoxOptions != null && selectedIndexs1 != null) {
                                    CPDFWidgetItem selectItem = comboBoxOptions[selectedIndexs1[0]];
                                    outputListener.println("Field Select Item : " + selectItem.text);
                                }
                                break;
                            case Widget_SignatureFields:
                                CPDFSignatureWidget signatureWidget = (CPDFSignatureWidget) cpdfWidget;
                                outputListener.println("Field isSigned : " + signatureWidget.isSigned());
                                break;
                            case Widget_CheckBox:
                                outputListener.println("Field isChecked : " + ((CPDFCheckboxWidget) cpdfWidget).isChecked());
                                break;
                            case Widget_RadioButton:
                                outputListener.println("Field isChecked : " + ((CPDFRadiobuttonWidget) cpdfWidget).isChecked());
                                break;
                            case Widget_PushButton:
                                CPDFPushbuttonWidget pushButtonWidget = (CPDFPushbuttonWidget) cpdfWidget;
                                outputListener.println("Field PushButton Title : " + pushButtonWidget.getButtonTitle());
                                CPDFAction cpdfAction = pushButtonWidget.getButtonAction();
                                if (cpdfAction != null) {
                                    if (cpdfAction instanceof CPDFUriAction) {
                                        outputListener.println("Field PushButton Action : " + ((CPDFUriAction) cpdfAction).getUri());
                                    } else if (cpdfAction instanceof CPDFGoToAction) {
                                        outputListener.println("Field PushButton Action : Jump to page " + (((CPDFGoToAction) cpdfAction).getDestination(document).getPageIndex() + 1) + " of the document");
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                        RectF widgetRect = cpdfWidget.getRect();
                        RectF position = page.convertRectFromPage(false, document.getPageSize(i).width(),
                                document.getPageSize(i).height(),widgetRect);
                        outputListener.println(String.format("Field Position : %d, %d, %d, %d", (int)position.left, (int)position.top, (int)position.right, (int)position.bottom));
                        outputListener.println("Widget type : " + cpdfWidget.getWidgetType().name());
                        printDividingLine();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
