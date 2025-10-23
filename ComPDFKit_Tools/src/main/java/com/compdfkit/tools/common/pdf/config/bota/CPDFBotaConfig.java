/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.bota;


import com.compdfkit.tools.common.views.pdfbota.CPDFBOTA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CPDFBotaConfig implements Serializable {

    public List<Integer> tabs;

    public CPDFBotaMenusConfig menus;


    public CPDFBotaConfig(){
        tabs = new ArrayList<>(Arrays.asList(CPDFBOTA.OUTLINE, CPDFBOTA.BOOKMARKS, CPDFBOTA.ANNOTATION));
        menus = new CPDFBotaMenusConfig();
    }

    public List<Integer> getTabs() {
        return tabs;
    }

    public void setTabs(List<Integer> tabs) {
        this.tabs = tabs;
    }

    public CPDFBotaMenusConfig getMenus() {
        return menus;
    }

    public void setMenus(CPDFBotaMenusConfig menus) {
        this.menus = menus;
    }
}



