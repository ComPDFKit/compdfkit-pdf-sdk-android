/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfoutline.data;


import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFOutline;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfoutline.bean.COutlineData;

import java.util.ArrayList;

public class COutlineDatas {

    /**
     * Get the list of outlines for a PDF document
     * @param cpdfViewCtrl
     * @see COutlineData
     * @return ArrayList COutlineData
     */
    public static ArrayList<COutlineData> getOutlineList(CPDFViewCtrl cpdfViewCtrl, int maxLevel) {
        // Retrieve the outline root from the PDF document and convert it to a list of COutlineData objects
        CPDFDocument document = cpdfViewCtrl.getCPdfReaderView().getPDFDocument();
        if (document == null){
            return new ArrayList<>();
        }
        CPDFOutline outlineRoot = cpdfViewCtrl.getCPdfReaderView().getPDFDocument().getOutlineRoot();
        if (outlineRoot != null){
            return convertToCOutlineDataList(outlineRoot.getChildList(), maxLevel);
        }else {
            return new ArrayList<>();
        }
    }

    /**
     * Recursively convert an array of CPDFOutline objects to an ArrayList of COutlineData objects
     * @param outlines outline list data
     */
    private static ArrayList<COutlineData> convertToCOutlineDataList(CPDFOutline[] outlines, int maxLevel) {
        ArrayList<COutlineData> outlineDataList = new ArrayList<>();
        for (CPDFOutline outline : outlines) {
            // Convert the CPDFOutline object to a COutlineData object
            COutlineData outlineData = convertToCOutlineData(outline, maxLevel);
            // Only add the COutlineData object to the list if its level is 1
            if (outlineData.getLevel() == 1) {
                outlineDataList.add(outlineData);
            }
            // If the CPDFOutline object has child outlines, convert them to COutlineData objects and add them to the list
            if (outline.getChildList() != null && outline.getChildList().length > 0) {
                outlineDataList.addAll(convertToCOutlineDataList(outline.getChildList(), maxLevel));
            }
        }
        return outlineDataList;
    }

    /**
     * Convert a CPDFOutline object to a COutlineData object
     */
    private static COutlineData convertToCOutlineData(CPDFOutline outline, int maxLevel) {
        COutlineData outlineData = new COutlineData();
        outlineData.setLevel(outline.getLevel());
        outlineData.setPageIndex(outline.getDestination().getPageIndex());
        outlineData.setTitle(outline.getTitle());
        outlineData.setChildOutline(new ArrayList<>());

        // If the CPDFOutline object has child outlines, convert them to COutlineData objects and add them to the list
        if (outline.getChildList() != null && outline.getChildList().length > 0) {
            for (CPDFOutline child : outline.getChildList()) {
                if (child.getLevel() <=maxLevel){
                    outlineData.getChildOutline().add(convertToCOutlineData(child, maxLevel));
                }
            }
        }
        return outlineData;
    }

}
