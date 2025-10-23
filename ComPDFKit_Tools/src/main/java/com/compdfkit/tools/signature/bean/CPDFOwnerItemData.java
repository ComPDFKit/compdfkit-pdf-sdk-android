/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.bean;


import com.compdfkit.core.signature.CPDFOwnerInfo;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.tools.signature.CertificateDigitalDatas;

public class CPDFOwnerItemData {

    public CPDFOwnerItemData() {

    }

    public CPDFOwnerItemData(CPDFX509 cpdfx509){
        this.cpdfx509 = cpdfx509;
        CPDFOwnerInfo ownerInfo = cpdfx509.getCertInfo().getSubject();
        this.content = CertificateDigitalDatas.getOwnerContent(ownerInfo, ",");
    }

    private CPDFX509 cpdfx509;

    private String content;

    private boolean isExpanded = true;

    private boolean hasParent;

    public CPDFX509 getCpdfx509() {
        return cpdfx509;
    }

    public void setCpdfx509(CPDFX509 cpdfx509) {
        this.cpdfx509 = cpdfx509;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setHasParent(boolean hasParent) {
        this.hasParent = hasParent;
    }

    public boolean isHasParent() {
        return hasParent;
    }
}
