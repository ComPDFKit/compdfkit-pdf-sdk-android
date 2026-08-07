/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationlist.data;


import android.graphics.Color;

import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFCircleAnnotation;
import com.compdfkit.core.annotation.CPDFFreetextAnnotation;
import com.compdfkit.core.annotation.CPDFHighlightAnnotation;
import com.compdfkit.core.annotation.CPDFInkAnnotation;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFReplyAnnotation;
import com.compdfkit.core.annotation.CPDFSoundAnnotation;
import com.compdfkit.core.annotation.CPDFSquareAnnotation;
import com.compdfkit.core.annotation.CPDFSquigglyAnnotation;
import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.core.annotation.CPDFStrikeoutAnnotation;
import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.core.annotation.CPDFUnderlineAnnotation;
import com.compdfkit.core.common.CPDFDate;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.annotation.pdfannotationlist.bean.CPDFAnnotListItem;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.storage.CPDFPublicFileSaver;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CPDFAnnotDatas {

    public static List<CPDFAnnotListItem> getAnnotationList(@Nullable CPDFViewCtrl pdfView) {
        List<CPDFAnnotListItem> list = new ArrayList<>();
        try{
            if (pdfView == null){
                return new ArrayList<>();
            }
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            int count = document.getPageCount();

            for (int i = 0; i <= count - 1; i++) {
                List<CPDFAnnotation> pageAnnotations = document.pageAtIndex(i).getAnnotations();
                List<CPDFAnnotListItem> annotListItems = convertPageAnnotations(pageAnnotations);
                if (!annotListItems.isEmpty()){
                    sortByModifyDate(annotListItems);
                    CPDFAnnotListItem headerItem = new CPDFAnnotListItem();
                    headerItem.setHeader(true);
                    headerItem.setAnnotationCount(annotListItems.size());
                    headerItem.setPage(i);
                    list.add(headerItem);
                    list.addAll(annotListItems);
                }
            }
        }catch (Exception ignored){
        }
        return list;
    }

    private static void sortByModifyDate(List<CPDFAnnotListItem> annotListItems) {
        Collections.sort(annotListItems, (first, second) -> {
            CPDFDate firstDate = first.getAttr().getRecentlyModifyDate();
            CPDFDate secondDate = second.getAttr().getRecentlyModifyDate();
            boolean firstDateValid = firstDate != null && firstDate.isValid();
            boolean secondDateValid = secondDate != null && secondDate.isValid();

            if (!firstDateValid || !secondDateValid) {
                if (firstDateValid) {
                    return -1;
                }
                if (secondDateValid) {
                    return 1;
                }
                return 0;
            }
            return Long.compare(
                    CDateUtil.transformToTimestamp(firstDate),
                    CDateUtil.transformToTimestamp(secondDate));
        });
    }

    private static List<CPDFAnnotListItem> convertPageAnnotations(List<CPDFAnnotation> list){
        List<CPDFAnnotListItem> annotListItems = new ArrayList<>();
        if (list == null || list.isEmpty()){
            return annotListItems;
        }
        for (CPDFAnnotation cpdfAnnotation : list) {
            if (cpdfAnnotation == null || !cpdfAnnotation.isValid()){
                continue;
            }
            CPDFAnnotListItem item = convertToListItem(cpdfAnnotation);
            if (item != null){
                annotListItems.add(item);
            }
        }
        return annotListItems;
    }

    public static CPDFAnnotListItem convertToListItem(CPDFAnnotation cpdfAnnotation){
        CPDFAnnotation.Type type = cpdfAnnotation.getType();
        boolean isArrowLine = true;
        int attrColor = Color.TRANSPARENT;
        String attrTime = "";
        CPDFDate modifyDate = cpdfAnnotation.getRecentlyModifyDate();
        if (null != modifyDate){
            attrTime = CPDFDate.toStandardDate(modifyDate);
        }
        int attrAlpha = 0;
        String attrContent;
        switch (cpdfAnnotation.getType()){
            case TEXT:
                CPDFTextAnnotation textAnnotation = (CPDFTextAnnotation) cpdfAnnotation;
                attrColor = textAnnotation.getColor();
                attrAlpha = textAnnotation.getAlpha();
                attrContent = textAnnotation.getContent();
                break;
            case HIGHLIGHT:
                CPDFHighlightAnnotation highlightAnnotation = (CPDFHighlightAnnotation) cpdfAnnotation;
                attrColor = highlightAnnotation.getColor();
                attrAlpha = highlightAnnotation.getAlpha();
                attrContent = highlightAnnotation.getMarkedText();
                break;
            case UNDERLINE:
                CPDFUnderlineAnnotation underlineAnnotation = (CPDFUnderlineAnnotation) cpdfAnnotation;
                attrColor = underlineAnnotation.getColor();
                attrAlpha = underlineAnnotation.getAlpha();
                attrContent = underlineAnnotation.getMarkedText();
                break;
            case STRIKEOUT:
                CPDFStrikeoutAnnotation strikeoutAnnotation = (CPDFStrikeoutAnnotation) cpdfAnnotation;
                attrColor = strikeoutAnnotation.getColor();
                attrAlpha = strikeoutAnnotation.getAlpha();
                attrContent = strikeoutAnnotation.getMarkedText();
                break;
            case SQUIGGLY:
                CPDFSquigglyAnnotation squigglyAnnotation = (CPDFSquigglyAnnotation) cpdfAnnotation;
                attrColor = squigglyAnnotation.getColor();
                attrAlpha = squigglyAnnotation.getAlpha();
                attrContent = squigglyAnnotation.getMarkedText();
                break;
            case INK:
                CPDFInkAnnotation cpdfInkAnnotation = (CPDFInkAnnotation) cpdfAnnotation;
                attrColor = cpdfInkAnnotation.getColor();
                attrAlpha = cpdfInkAnnotation.getAlpha();
                attrContent = cpdfInkAnnotation.getContent();
                break;
            case LINE:
                CPDFLineAnnotation lineAnnotation = (CPDFLineAnnotation) cpdfAnnotation;
                isArrowLine = lineAnnotation.getLineHeadType() == CPDFLineAnnotation.LineType.LINETYPE_ARROW;
                attrColor = lineAnnotation.getBorderColor();
                attrAlpha = lineAnnotation.getBorderAlpha();
                attrContent = lineAnnotation.getContent();
                break;
            case FREETEXT:
                CPDFFreetextAnnotation annotation = (CPDFFreetextAnnotation) cpdfAnnotation;
                attrContent = annotation.getContent();
                break;
            case STAMP:
                CPDFStampAnnotation stampAnnotation = (CPDFStampAnnotation) cpdfAnnotation;
                attrContent = stampAnnotation.getContent();
                if (stampAnnotation.isStampSignature()){
                    return null;
                }
                break;
            case SQUARE:
                CPDFSquareAnnotation squareAnnotation = (CPDFSquareAnnotation) cpdfAnnotation;
                attrColor = squareAnnotation.getBorderColor();
                attrAlpha = squareAnnotation.getBorderAlpha();
                attrContent = squareAnnotation.getContent();
                break;
            case CIRCLE:
                CPDFCircleAnnotation circleAnnotation = (CPDFCircleAnnotation) cpdfAnnotation;
                attrColor = circleAnnotation.getBorderColor();
                attrAlpha = circleAnnotation.getBorderAlpha();
                attrContent = circleAnnotation.getContent();
                break;
            case SOUND:
                CPDFSoundAnnotation soundAnnotation = (CPDFSoundAnnotation) cpdfAnnotation;
                attrContent = soundAnnotation.getContent();
                break;
            default:
                return null;
        }
        CPDFAnnotListItem item = new CPDFAnnotListItem();
        item.setAnnotType(type);
        item.setArrowLine(isArrowLine);
        item.setModifyDate(CDateUtil.transformPDFDate(attrTime));
        item.setColor(attrColor);
        item.setColorAlpha(attrAlpha);
        item.setContent(attrContent);
        item.setPage(cpdfAnnotation.pdfPage.getPageNum());
        item.setAnnotationCount(0);
        item.setAttr(cpdfAnnotation);
        item.setHeader(false);
        return item;
    }


    /**
     * export annotations to xfdf file
     * @param document the document
     * @param saveDir Saved directory
     * @param saveName The name of the saved file, excluding the xfdf file suffix
     * @return export success or failure status
     */
    public static boolean exportAnnotations(CPDFDocument document, String saveDir, String saveName) {
        if (document == null || saveDir == null || saveName == null) {
            CLog.e("ComPDFKit", "Invalid parameters: document, saveDir, or saveName is null");
            return false;
        }

        File cacheFile = new File(document.getContext().getCacheDir(), "annotationExportCache");
        if (cacheFile.exists()) {
            if (!cacheFile.isDirectory()) {
                CLog.e("ComPDFKit", "Cache path exists but is not a directory: " + cacheFile.getAbsolutePath());
                return false;
            }
        } else if (!cacheFile.mkdirs()) {
            CLog.e("ComPDFKit", "Failed to create cache directory: " + cacheFile.getAbsolutePath());
            return false;
        }

        CPDFPublicFileSaver.SaveResult saveResult = CPDFPublicFileSaver.saveXfdfToSelectedDirectory(
                document.getContext(),
                saveDir,
                saveName + ".xfdf",
                tempPath -> document.exportAnnotations(tempPath, cacheFile.getAbsolutePath()));
        return saveResult.isSuccess();
    }

    public static boolean exportAnnotations(CPDFDocument document, String saveName) {
        if (document == null || saveName == null) {
            CLog.e("ComPDFKit", "Invalid parameters: document or saveName is null");
            return false;
        }
        File saveFile = CPDFStorageManager.createTempFile(document.getContext(), saveName + ".xfdf");
        File cacheFile = new File(document.getContext().getCacheDir(), "annotationExportCache");
        if (cacheFile.exists()) {
            if (!cacheFile.isDirectory()) {
                CLog.e("ComPDFKit", "Cache path exists but is not a directory: " + cacheFile.getAbsolutePath());
                return false;
            }
        } else if (!cacheFile.mkdirs()) {
            CLog.e("ComPDFKit", "Failed to create cache directory: " + cacheFile.getAbsolutePath());
            return false;
        }
        boolean result = document.exportAnnotations(saveFile.getAbsolutePath(), cacheFile.getAbsolutePath());
        if (!result) {
            saveFile.delete();
            return false;
        }
        CUriUtil.MediaStoreSaveResult saveResult = CUriUtil.publishFileToMediaStore(document.getContext(),
                saveFile,
                CPDFStorageManager.getDownloadRelativePath(),
                saveFile.getName(),
                "application/vnd.adobe.xfdf",
                true);
        return saveResult.isSuccess();
    }

    public static boolean exportWidgets(CPDFDocument document, String saveDir, String saveName){
        File cacheFile = new File(document.getContext().getCacheDir(), "widgetExportCache");
        if (!cacheFile.exists() && !cacheFile.mkdirs()){
            CLog.e("ComPDFKit", "Failed to create directory: " + cacheFile.getAbsolutePath());
        }
        CPDFPublicFileSaver.SaveResult saveResult = CPDFPublicFileSaver.saveXfdfToSelectedDirectory(
                document.getContext(),
                saveDir,
                saveName + "_widgets.xfdf",
                tempPath -> document.exportWidgets(tempPath, cacheFile.getAbsolutePath()));
        return saveResult.isSuccess();
    }

    public static boolean importWidgets(CPDFDocument document, String importFilePath){
        File cacheFile = new File(document.getContext().getCacheDir(), "widgetsExportCache");
        if (!cacheFile.exists()){
            boolean result = cacheFile.mkdirs();
        }
        return document.importWidgets(importFilePath, cacheFile.getAbsolutePath());
    }
    /**
     * Import annotations in xfdf file
     * @param document the document
     * @param importFilePath imported xfdf file path
     * @return import result
     */
    public static boolean importAnnotations(CPDFDocument document, String importFilePath){
        File cacheFile = new File(document.getContext().getCacheDir(), "annotationExportCache");
        boolean result = cacheFile.mkdirs();
        return document.importAnnotations(importFilePath, cacheFile.getAbsolutePath());
    }

    public static void removeAllAnnotationReply(CPDFDocument document){
        for (int i = 0; i < document.getPageCount(); i++) {
            CPDFPage page = document.pageAtIndex(i);
            List<CPDFAnnotation> annotations = page.getAnnotations();
            if (annotations != null) {
                for (CPDFAnnotation annotation : annotations) {
                    CPDFReplyAnnotation[] replyAnnotations = annotation.getAllReplyAnnotations();
                    if (replyAnnotations != null){
                        for (CPDFReplyAnnotation replyAnnotation : replyAnnotations) {
                            replyAnnotation.pdfPage.deleteAnnotation(replyAnnotation);
                        }
                    }
                }
            }
        }
    }

    public static boolean removeAllAnnotations(List<CPDFAnnotListItem> items){
        for (CPDFAnnotListItem item : items) {
            CPDFAnnotation annotation = item.getAttr();
            if (annotation == null || !annotation.isValid()){
                continue;
            }
            annotation.pdfPage.deleteAnnotation(annotation);
        }
        return true;
    }
}
