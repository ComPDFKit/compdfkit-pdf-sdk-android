/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.bota;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;


public class CPDFBotaItemMenu implements Serializable {

    private String id;
    private List<String> subMenus;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public @Nullable List<String> getSubMenus() { return subMenus; }
    public void setSubMenus(List<String> subMenus) { this.subMenus = subMenus; }

    public CPDFBotaItemMenu(){
    }

    public CPDFBotaItemMenu(String id, String... subMenus){
        this.id = id;
        if(subMenus != null && subMenus.length > 0){
            this.subMenus = java.util.Arrays.asList(subMenus);
        }
    }
}
