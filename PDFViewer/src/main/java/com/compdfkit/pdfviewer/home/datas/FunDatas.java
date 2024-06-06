/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home.datas;


import android.content.Context;

import com.compdfkit.pdfviewer.CPDFConfiguration;
import com.compdfkit.pdfviewer.R;
import com.compdfkit.pdfviewer.home.HomeFunBean;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FunDatas {


    public static List<HomeFunBean> getFunList(Context context) {
        return Arrays.asList(
                HomeFunBean.head(context, R.string.tools_features),
                new HomeFunBean(context, HomeFunBean.FunType.Viewer, R.drawable.ic_fun_viewer, R.string.tools_fun_viewer, R.string.tools_fun_viewer_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Annotations, R.drawable.ic_fun_annotations, R.string.tools_fun_annotations, R.string.tools_fun_annotations_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Forms, R.drawable.ic_fun_form, R.string.tools_fun_forms, R.string.tools_fun_forms_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Signature, R.drawable.ic_fun_digital_signature, R.string.tools_fun_signatures, R.string.tools_fun_signatures_desc),
                new HomeFunBean(context, HomeFunBean.FunType.DocumentEditor, R.drawable.ic_fun_document_editor, R.string.tools_fun_document_editor, R.string.tools_fun_document_editor_desc),
                new HomeFunBean(context, HomeFunBean.FunType.ContentEditor, R.drawable.ic_fun_content_editor, R.string.tools_fun_content_editor, R.string.tools_fun_content_editor_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Security, R.drawable.ic_fun_security, R.string.tools_fun_security, R.string.tools_fun_security_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Redaction, R.drawable.ic_fun_redaction, R.string.tools_fun_redaction, R.string.tools_fun_redaction_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Watermark, R.drawable.ic_fun_watermark, R.string.tools_fun_watermark, R.string.tools_fun_watermark_desc),
                new HomeFunBean(context, HomeFunBean.FunType.CompareDocuments, R.drawable.ic_fun_compare_documents, R.string.tools_fun_compare_documents, R.string.tools_fun_compare_documents_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Conversion, R.drawable.ic_fun_conversion, R.string.tools_fun_conversion, R.string.tools_fun_conversion_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Compress, R.drawable.ic_fun_compress, R.string.tools_fun_compress, R.string.tools_fun_compress_desc),
                new HomeFunBean(context, HomeFunBean.FunType.Measurement, R.drawable.ic_fun_mesurement, R.string.tools_fun_measurement, R.string.tools_fun_measurement_desc),
                HomeFunBean.head(context, R.string.tools_click_to_open_process),
                HomeFunBean.assetsFile(context, "ComPDFKit_Sample_File_Android.pdf", context.getString(R.string.tools_compdfkit_sample_file_android))
        );
    }


    public static List<HomeFunBean> getDocumentListByFunType(Context context, HomeFunBean.FunType funType){
        List<HomeFunBean> list = new ArrayList<>();
        list.add( HomeFunBean.head(context, R.string.tools_click_to_open_process));
        switch (funType) {
            case Viewer:
                list.add(HomeFunBean.assetsFile(context, "ComPDFKit_Sample_File_Android.pdf", context.getString(R.string.tools_compdfkit_sample_file_viewer)));
                break;
            case Annotations:
                list.add(HomeFunBean.assetsFile(context, "ComPDFKit_Annotations_Sample_File.pdf", context.getString(R.string.tools_compdfkit_sample_file_annotations)));
                break;
            case Forms:
                list.add(HomeFunBean.assetsFile(context, "ComPDFKit_Forms_Sample_File.pdf", context.getString(R.string.tools_compdfkit_sample_file_forms)));
                break;
            case Signature:
                list.add(HomeFunBean.assetsFile(context, "ComPDFKit_Signatures_Sample_File.pdf", context.getString(R.string.tools_compdfkit_sample_file_signatures)));
                break;
            case DocumentEditor:
                list.add(HomeFunBean.assetsFile(context, "ComPDFKit_Sample_File_Android.pdf", context.getString(R.string.tools_compdfkit_sample_file_document_editor)));
                break;
            case ContentEditor:
                list.add(HomeFunBean.assetsFile(context, "ComPDFKit_Sample_File_Android.pdf", context.getString(R.string.tools_compdfkit_sample_file_content_editor)));
                break;
            case Security:
                list.add(HomeFunBean.assetsFile(context, "Password_compdfkit_Security_Sample_File.pdf", context.getString(R.string.tools_compdfkit_sample_file_security)));
                break;
            case Watermark:
                list.add(HomeFunBean.assetsFile(context, "ComPDFKit_Watermark_Sample_File.pdf", context.getString(R.string.tools_compdfkit_sample_file_watermark)));
                break;
            default:
                list.add(HomeFunBean.assetsFile(context, "ComPDFKit_Sample_File_Android.pdf", context.getString(R.string.tools_compdfkit_sample_file_android)));
                break;
        }
        return list;
    }

    /**
     * Enter the additional configuration information of pdf Activity
     * @return
     */
    public static CPDFConfiguration getConfiguration(Context context, CPreviewMode cPreviewMode) {
        CPDFConfiguration.Builder builder = new CPDFConfiguration.Builder()
                .setModeConfig(new CPDFConfiguration.ModeConfig(cPreviewMode));

        CPDFConfiguration.ReaderViewConfig readerViewConfig = new CPDFConfiguration.ReaderViewConfig();
        readerViewConfig.linkHighlight = SettingDatas.isHighlightLink(context);
        readerViewConfig.formFieldHighlight = SettingDatas.isHighlightForm(context);
        builder.setReaderViewConfig(readerViewConfig);

        CPDFConfiguration.ToolbarConfig toolbarConfig = new CPDFConfiguration.ToolbarConfig();
        toolbarConfig.androidAvailableActions = Arrays.asList(
                CPDFConfiguration.ToolbarConfig.ToolbarAction.Thumbnail,
                CPDFConfiguration.ToolbarConfig.ToolbarAction.Search,
                CPDFConfiguration.ToolbarConfig.ToolbarAction.Bota,
                CPDFConfiguration.ToolbarConfig.ToolbarAction.Menu
        );
        toolbarConfig.availableMenus = Arrays.asList(
                CPDFConfiguration.ToolbarConfig.MenuAction.ViewSettings,
                CPDFConfiguration.ToolbarConfig.MenuAction.DocumentEditor,
                CPDFConfiguration.ToolbarConfig.MenuAction.DocumentInfo,
                CPDFConfiguration.ToolbarConfig.MenuAction.Save,
                CPDFConfiguration.ToolbarConfig.MenuAction.Share,
                CPDFConfiguration.ToolbarConfig.MenuAction.OpenDocument
        );
        builder.setToolbarConfig(toolbarConfig);
        return builder.create();

    }
}
