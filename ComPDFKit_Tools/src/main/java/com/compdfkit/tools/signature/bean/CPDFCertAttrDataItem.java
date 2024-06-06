/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.bean;


public class CPDFCertAttrDataItem {

    public CPDFCertAttrDataItem() {
    }

    public CPDFCertAttrDataItem(String headTitle){
        this.headTitle = headTitle;
        this.isHead = true;
    }

    public CPDFCertAttrDataItem(String title, String value){
        this.title = title;
        this.value = value;
    }

    private boolean isHead;

    private boolean isCertTrustedType;

    private boolean certIsTrusted;

    private String headTitle;

    private String title;

    private String value;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public String getHeadTitle() {
        return headTitle;
    }

    public void setHeadTitle(String headTitle) {
        this.headTitle = headTitle;
    }

    public boolean isCertTrustedType() {
        return isCertTrustedType;
    }

    public void setCertTrustedType(boolean certTrustedType) {
        isCertTrustedType = certTrustedType;
    }

    public boolean isCertIsTrusted() {
        return certIsTrusted;
    }

    public void setCertIsTrusted(boolean certIsTrusted) {
        this.certIsTrusted = certIsTrusted;
    }
}
