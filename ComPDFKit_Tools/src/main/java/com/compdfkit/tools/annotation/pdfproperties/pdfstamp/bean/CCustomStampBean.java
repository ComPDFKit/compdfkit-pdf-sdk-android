/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean;


public class CCustomStampBean {

    public static final int ITEM_TEXT_STAMP_TITLE = 0;
    public static final int ITEM_IMAGE_STAMP_TITLE = 1;

    public static final int ITEM_TEXT_STAMP = 2;

    public static final int ITEM_IMAGE_STAMP = 3;

    private int itemType = ITEM_TEXT_STAMP_TITLE;

    private String title;

    private CTextStampBean textStampBean;

    private String imageStampPath;

    public static CCustomStampBean headItem(String title, int itemType){
        CCustomStampBean bean = new CCustomStampBean();
        bean.setItemType(itemType);
        bean.setTitle(title);
        return bean;
    }
    public CCustomStampBean(){

    }
    public CCustomStampBean(CTextStampBean textStampBean){
        this.itemType = ITEM_TEXT_STAMP;
        this.textStampBean = textStampBean;
    }

    public CCustomStampBean(String imageStampPath){
        this.itemType = ITEM_IMAGE_STAMP;
        this.imageStampPath = imageStampPath;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CTextStampBean getTextStampBean() {
        return textStampBean;
    }

    public void setTextStampBean(CTextStampBean textStampBean) {
        this.textStampBean = textStampBean;
    }

    public String getImageStampPath() {
        return imageStampPath;
    }

    public void setImageStampPath(String imageStampPath) {
        this.imageStampPath = imageStampPath;
    }
}
