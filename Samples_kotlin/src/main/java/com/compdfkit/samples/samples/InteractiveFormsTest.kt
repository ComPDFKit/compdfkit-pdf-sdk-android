/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.samples

import android.graphics.Color
import android.graphics.RectF
import com.compdfkit.core.annotation.CPDFAnnotation
import com.compdfkit.core.annotation.form.CPDFCheckboxWidget
import com.compdfkit.core.annotation.form.CPDFComboboxWidget
import com.compdfkit.core.annotation.form.CPDFListboxWidget
import com.compdfkit.core.annotation.form.CPDFPushbuttonWidget
import com.compdfkit.core.annotation.form.CPDFRadiobuttonWidget
import com.compdfkit.core.annotation.form.CPDFSignatureWidget
import com.compdfkit.core.annotation.form.CPDFTextWidget
import com.compdfkit.core.annotation.form.CPDFWidget
import com.compdfkit.core.annotation.form.CPDFWidget.WidgetType
import com.compdfkit.core.annotation.form.CPDFWidgetItem
import com.compdfkit.core.document.CPDFDestination
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.document.action.CPDFGoToAction
import com.compdfkit.core.document.action.CPDFUriAction
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.SampleApplication
import com.compdfkit.samples.util.OutputListener
import java.io.File

class InteractiveFormsTest : PDFSamples() {
    init {
        setTitle(R.string.interactive_forms_test_title)
        setDescription(R.string.interactive_forms_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        val document = CPDFDocument.createDocument(context())
        // Create a blank new page and add some form fields.
        document.insertBlankPage(0, 595F, 842F)
        document.insertBlankPage(1, 595F, 842F)

        //---------------------------------
        //Samples 1 : Programmatically create new Form Fields and Widget Annotations.
        createTestForms(document)

        //---------------------------------
        //Samples 2 : Traverse all form fields in the document (and print out their names).
        printFormsMessage(document)
        val file = File(outputDir(), "forms/Create_From_Test.pdf")
        saveSamplePDF(document, file, true)
        deleteForm()
        printFooter()
    }

    private fun createTestForms(document: CPDFDocument) {
        // create new Form Fields and Widget Annotations.
        val pageNumber = 0
        val pageSize = document.getPageSize(pageNumber)
        val cpdfPage = document.pageAtIndex(pageNumber)

        //Insert a single-line TextField.
        (cpdfPage.addFormWidget(WidgetType.Widget_TextField) as CPDFTextWidget).apply {
            rect = kotlin.run {
                val singleLineTextRect = RectF(28F, 32F, 237F, 75F)
                cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), singleLineTextRect)
            }
            fieldName = "TextField1"
            text = "Basic Text Field"
            fontColor = Color.BLACK
            fontSize = 15F
            updateAp()
        }

        //Insert a multiline TextField.
        val multiLineTextWidget = cpdfPage.addFormWidget(WidgetType.Widget_TextField) as CPDFTextWidget
        multiLineTextWidget.apply {
            rect = kotlin.run {
                val multilineTextRect: RectF? = RectF(28F, 97F, 237F, 189F)
                cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), multilineTextRect)
            }
            fieldName = "TextField2"
            text = "Basic Text Field\nBasic Text Field\nBasic Text Field"
            isMultiLine = true
            fontColor = Color.BLACK
            fontSize = 15f
            updateAp()
        }


        //Insert a ListBox widget.
        val listBoxWidget = cpdfPage.addFormWidget(WidgetType.Widget_ListBox) as CPDFListboxWidget
        listBoxWidget.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(267F, 32F, 567F, 138F))
            fieldName = "ListBox1"
            fillColor = Color.WHITE
            borderColor = Color.BLACK
            borderWidth = 2F
            val listBoxItems = arrayOf(
                    CPDFWidgetItem("List Box No.1", "List Box No.1"),
                    CPDFWidgetItem("List Box No.2", "List Box No.2"),
                    CPDFWidgetItem("List Box No.3", "List Box No.3"))
            setOptionItems(listBoxItems)
            selectedIndexes = intArrayOf(1)
            updateAp()
        }

        //Insert a ComboBox Widget.
        val comboBoxWidget = cpdfPage.addFormWidget(WidgetType.Widget_ComboBox) as CPDFComboboxWidget
        comboBoxWidget.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(267F, 143F, 567F, 189F))
            fieldName = "ComboBox1"
            val comboBoxItems = arrayOf(
                    CPDFWidgetItem("Combo Box No.1", "Combo Box No.1"),
                    CPDFWidgetItem("Combo Box No.2", "Combo Box No.2"),
                    CPDFWidgetItem("Combo Box No.3", "Combo Box No.3"))
            setOptionItems(comboBoxItems, intArrayOf(1))
            updateAp()
        }

        //Insert a Form Signature Widget (unsigned)
        val signatureWidget = cpdfPage.addFormWidget(WidgetType.Widget_SignatureFields) as CPDFSignatureWidget
        signatureWidget.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(28F, 206F, 237F, 301F))
            fieldName = "Signature1"
            fillColor = Color.WHITE
            borderColor = Color.BLACK
            borderWidth = 2F
            updateAp()
        }

        //Insert a PushButton to jump to a page.
        val pushButtonWidget1 = cpdfPage.addFormWidget(WidgetType.Widget_PushButton) as CPDFPushbuttonWidget
        pushButtonWidget1.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(267F, 203F, 401F, 235F))
            fieldName = "PushButton1"
            fontColor = Color.BLACK
            fontSize = 15F
            buttonTitle = "PushButton"
            //set PushButton jump to a page action
            buttonAction = CPDFGoToAction().apply {
                setDestination(document, CPDFDestination(1, 0F, 842F, 0F))
            }
            updateAp()
        }

        //Insert a PushButton to jump to a website.
        val pushButtonWidget2 = cpdfPage.addFormWidget(WidgetType.Widget_PushButton) as CPDFPushbuttonWidget
        pushButtonWidget2.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(433F, 203F, 567F, 235F))
            fieldName = "PushButton2"
            fontColor = Color.BLACK
            fontSize = 15F
            buttonTitle = "PushButton"
            //set PushButton jump to a website
            buttonAction = CPDFUriAction().apply {
                uri = "https://www.compdf.com/"
            }
            updateAp()
        }

        //Insert CheckBox Widget
        val checkboxWidget = cpdfPage.addFormWidget(WidgetType.Widget_CheckBox) as CPDFCheckboxWidget
        checkboxWidget.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(267F, 251F, 299F, 283F))
            fieldName = "CheckBox1"
            fillColor = Color.parseColor("#CCE5E5FF")
            borderColor = Color.BLACK
            borderWidth = 2F
            isChecked = false
            updateAp()
        }

        val checkboxWidget2 = cpdfPage.addFormWidget(WidgetType.Widget_CheckBox) as CPDFCheckboxWidget
        checkboxWidget2.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(326F, 251F, 358F, 283F))
            fieldName = "CheckBox1"
            fillColor = Color.parseColor("#CCE5E5FF")
            borderColor = Color.BLACK
            borderWidth = 2F
            isChecked = true
            updateAp()
        }

        //Insert RadioButton Widget
        val radiobuttonWidget1 = cpdfPage.addFormWidget(WidgetType.Widget_RadioButton) as CPDFRadiobuttonWidget
        radiobuttonWidget1.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(385F, 251F, 424f, 290f))
            fieldName = "RadioButton"
            checkStyle = CPDFWidget.CheckStyle.CK_Circle
            fillColor = Color.parseColor("#CCE5E5FF")
            borderColor = Color.BLACK
            borderWidth = 2F
            isChecked = false
            fieldName = "RadioButton1"
            updateAp()
        }


        val radiobuttonWidget2 = cpdfPage.addFormWidget(WidgetType.Widget_RadioButton) as CPDFRadiobuttonWidget
        radiobuttonWidget2.apply {
            rect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(450F, 251F, 489F, 290F))
            fieldName = "RadioButton"
            checkStyle = CPDFWidget.CheckStyle.CK_Circle
            fillColor = Color.parseColor("#CCE5E5FF")
            borderColor = Color.BLACK
            borderWidth = 2F
            // Check the widget (by default it is unchecked).
            isChecked = true
            fieldName = "RadioButton1"
            updateAp()
        }

        val radiobuttonWidget3 = cpdfPage.addFormWidget(WidgetType.Widget_RadioButton) as CPDFRadiobuttonWidget
        radiobuttonWidget3.apply {
            rect =  cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(515F, 251F, 554F, 290F))
            fieldName = "RadioButton"
            checkStyle = CPDFWidget.CheckStyle.CK_Circle
            fillColor = Color.parseColor("#CCE5E5FF")
            borderColor = Color.BLACK
            borderWidth = 2F
            isChecked = false
            fieldName = "RadioButton1"
            updateAp()
        }
        outputListener?.println("Done.")
        outputListener?.println("Done. Result saved in Create_Form_Test.pdf")
        printDividingLine()
    }

    private fun deleteForm() {
        val file = File(outputDir(), "forms/Create_From_Test.pdf")
        val document = CPDFDocument(SampleApplication.instance)
        document.open(file.absolutePath)
        val page = document.pageAtIndex(0)
        for (annotation in page.annotations) {
            if (annotation.type == CPDFAnnotation.Type.WIDGET) {
                page.deleteAnnotation(annotation)
                break
            }
        }
        val resultsForms = File(outputDir(), "forms/Delete_Form_Test.pdf")
        saveSamplePDF(document, resultsForms, true)
        outputListener?.println("Done.")
        outputListener?.println("Done. Result saved in Delete_Form_Test.pdf")
    }

    private fun printFormsMessage(document: CPDFDocument) {
        for (i in 0 until document.pageCount) {
            val page = document.pageAtIndex(i)
            for (annotation in page.annotations) {
                when (annotation.type) {
                    CPDFAnnotation.Type.WIDGET -> {
                        val cpdfWidget = annotation as CPDFWidget
                        outputListener?.println("Field name : ${cpdfWidget.fieldName}")
                        when (cpdfWidget.widgetType) {
                            WidgetType.Widget_TextField -> outputListener?.println("Field partial name : ${(cpdfWidget as CPDFTextWidget).text}")
                            WidgetType.Widget_ListBox -> {
                                val listBoxWidget = cpdfWidget as CPDFListboxWidget
                                val options = listBoxWidget.options
                                val selectedIndexs = listBoxWidget.selectedIndexes
                                if (options != null && selectedIndexs != null) {
                                    val selectItem = options[selectedIndexs[0]]
                                    outputListener?.println("Field Select Item : ${selectItem.text}")
                                }
                            }

                            WidgetType.Widget_ComboBox -> {
                                val comboBoxWidget = cpdfWidget as CPDFComboboxWidget
                                val comboBoxOptions = comboBoxWidget.options
                                val selectedIndexs1 = comboBoxWidget.selectedIndexes
                                if (comboBoxOptions != null && selectedIndexs1 != null) {
                                    val selectItem = comboBoxOptions[selectedIndexs1[0]]
                                    outputListener?.println("Field Select Item : ${selectItem.text}")
                                }
                            }

                            WidgetType.Widget_SignatureFields -> {
                                val signatureWidget = cpdfWidget as CPDFSignatureWidget
                                outputListener?.println("Field isSigned : ${signatureWidget.isSigned}")
                            }

                            WidgetType.Widget_CheckBox -> outputListener?.println("Field isChecked : ${(cpdfWidget as CPDFCheckboxWidget).isChecked}")
                            WidgetType.Widget_RadioButton -> outputListener?.println("Field isChecked : ${(cpdfWidget as CPDFRadiobuttonWidget).isChecked}")
                            WidgetType.Widget_PushButton -> {
                                val pushButtonWidget = cpdfWidget as CPDFPushbuttonWidget
                                outputListener?.println("Field PushButton Title : ${pushButtonWidget.buttonTitle}")
                                val cpdfAction = pushButtonWidget.buttonAction
                                if (cpdfAction != null) {
                                    if (cpdfAction is CPDFUriAction) {
                                        outputListener?.println("Field PushButton Action : ${cpdfAction.uri}")
                                    } else if (cpdfAction is CPDFGoToAction) {
                                        outputListener?.println("Field PushButton Action : Jump to page ${(cpdfAction.getDestination(document).pageIndex + 1)} of the document")
                                    }
                                }
                            }

                            else -> {}
                        }
                        val widgetRect = cpdfWidget.rect
                        val position = page.convertRectFromPage(false, document.getPageSize(i).width(),
                                document.getPageSize(i).height(), widgetRect)
                        outputListener?.println(String.format("Field Position : %d, %d, %d, %d", position.left.toInt(), position.top.toInt(), position.right.toInt(), position.bottom.toInt()))
                        outputListener?.println("Widget type : ${cpdfWidget.widgetType.name}")
                        printDividingLine()
                    }

                    else -> {}
                }
            }
        }
    }
}