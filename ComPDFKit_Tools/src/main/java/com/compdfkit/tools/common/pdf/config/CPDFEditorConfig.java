package com.compdfkit.tools.common.pdf.config;


import java.io.Serializable;
import java.util.List;

public class CPDFEditorConfig implements Serializable {

    public enum CPDFEditorMenus {

        INSERT_PAGE,
        REPLACE_PAGE,
        EXTRACT_PAGE,

        COPY_PAGE,

        ROTATE_PAGE,

        DELETE_PAGE,

    }

    public List<CPDFEditorMenus> menus;

    public CPDFEditorConfig(){
        menus = List.of(
                CPDFEditorMenus.INSERT_PAGE,
                CPDFEditorMenus.REPLACE_PAGE,
                CPDFEditorMenus.EXTRACT_PAGE,
                CPDFEditorMenus.COPY_PAGE,
                CPDFEditorMenus.ROTATE_PAGE,
                CPDFEditorMenus.DELETE_PAGE
        );
    }
}
