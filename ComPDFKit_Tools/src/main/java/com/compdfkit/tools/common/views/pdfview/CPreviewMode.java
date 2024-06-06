package com.compdfkit.tools.common.views.pdfview;

import com.compdfkit.tools.R;

public enum CPreviewMode {
    Viewer("viewer"),

    Annotation("annotations"),

    Edit("contentEditor"),

    Form("forms"),

    PageEdit("pageEdit"),

    Signature("digitalSignatures");

    public String alias;

    CPreviewMode(String alias){
        this.alias = alias;
    }

    public int getTitleByMode() {
        switch (this) {
            case Annotation:
                return R.string.tools_pdf_annotation_mode;
            case Edit:
                return R.string.tools_pdf_edit_mode;
            case PageEdit:
                return R.string.tools_page_edit_mode;
            case Form:
                return R.string.tools_form_mode;
            case Signature:
                return R.string.tools_signatures;
            default:
                return R.string.tools_pdf_viewer_mode;
        }
    }

    public static CPreviewMode fromAlias(String alias){
        try{
            for (CPreviewMode value : CPreviewMode.values()) {
                if (value.alias.equalsIgnoreCase(alias)){
                    return value;
                }
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }
}