/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean;

import org.json.JSONObject;

public class CTextStampBean {

    private String filePath;

    private int bgColor;

    private int textColor;

    private int lineColor;

    private String textContent;

    private boolean showDate;

    private boolean showTime;

    private int textStampShapeId;

    private int textStampColorId;

    private String dateStr;

    public CTextStampBean(int bgColor,
                          int textColor,
                          int lineColor,
                          String textContent,
                          String dateStr,
                          boolean showDate,
                          boolean showTime,
                          int textStampShapeId,
                          int textStampColorId) {
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.lineColor = lineColor;
        this.textContent = textContent;
        this.dateStr = dateStr;
        this.showDate = showDate;
        this.showTime = showTime;
        this.textStampShapeId = textStampShapeId;
        this.textStampColorId = textStampColorId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getDateStr() {
        return dateStr;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public int getTextStampShapeId() {
        return textStampShapeId;
    }

    public void setTextStampShapeId(int textStampShapeId) {
        this.textStampShapeId = textStampShapeId;
    }

    public void setTextStampColorId(int textStampColorId) {
        this.textStampColorId = textStampColorId;
    }

    public int getTextStampColorId() {
        return textStampColorId;
    }

    public String toJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bgColor", bgColor);
            jsonObject.put("textColor", textColor);
            jsonObject.put("lineColor", lineColor);
            jsonObject.put("textContent", textContent);
            jsonObject.put("dateStr", dateStr);
            jsonObject.put("showDate", showDate);
            jsonObject.put("showTime", showTime);
            jsonObject.put("textStampShapeId", textStampShapeId);
            jsonObject.put("textStampColorId", textStampColorId);
            return jsonObject.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
