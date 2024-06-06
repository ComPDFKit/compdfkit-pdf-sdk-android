/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfbota;


import java.io.Serializable;

public class CPDFBotaFragmentTabs implements Serializable {

    @CPDFBOTA
    private int botaType;

    private String title;

    private boolean defaultSelect;

    public CPDFBotaFragmentTabs(@CPDFBOTA int botaType, String title){
        this.botaType = botaType;
        this.title = title;
    }

    public int getBotaType() {
        return botaType;
    }

    public void setBotaType(int botaType) {
        this.botaType = botaType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDefaultSelect() {
        return defaultSelect;
    }

    public void setDefaultSelect(boolean defaultSelect) {
        this.defaultSelect = defaultSelect;
    }
}
