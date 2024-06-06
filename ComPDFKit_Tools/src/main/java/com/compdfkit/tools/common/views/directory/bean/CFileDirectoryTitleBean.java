/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.directory.bean;

import java.io.File;


public class CFileDirectoryTitleBean {

    public CFileDirectoryTitleBean() {
    }

    public CFileDirectoryTitleBean(File file){
        this.file = file;
    }

    public static CFileDirectoryTitleBean separator(){
        CFileDirectoryTitleBean bean = new CFileDirectoryTitleBean();
        bean.setSeparator(true);
        return bean;
    }


    private File file;

    private boolean isSeparator;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSeparator() {
        return isSeparator;
    }

    public void setSeparator(boolean separator) {
        isSeparator = separator;
    }
}
