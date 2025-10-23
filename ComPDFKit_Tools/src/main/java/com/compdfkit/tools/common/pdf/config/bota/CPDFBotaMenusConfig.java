/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.bota;


import java.io.Serializable;

public class CPDFBotaMenusConfig implements Serializable {

    private CPDFBotaAnnotationMenu annotations;

    public CPDFBotaAnnotationMenu getAnnotations() { return annotations; }
    public void setAnnotations(CPDFBotaAnnotationMenu annotations) { this.annotations = annotations; }

    public CPDFBotaMenusConfig(){
        annotations = new CPDFBotaAnnotationMenu();
    }

}
