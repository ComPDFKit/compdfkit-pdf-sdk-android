/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfsearch.data;


public class CPDFSearchReplaceDatas {

    private static CPDFSearchReplaceDatas instance;

    public static CPDFSearchReplaceDatas getInstance() {
        if (instance == null){
            instance = new CPDFSearchReplaceDatas();
        }
        return instance;
    }

    private String keywords;


    public String getKeywords() {
        return keywords;
    }

    public void updateKeywords(String keywords){
        this.keywords = keywords;
    }
}
