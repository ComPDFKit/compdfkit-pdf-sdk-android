/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home;


import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.compdfkit.pdfviewer.R;
import com.compdfkit.tools.common.utils.CFileUtils;

public class HomeFunBean {

    public enum FunType {

        FunSample,

        SamplePDF,

        Viewer,

        Annotations,

        Forms,

        DocumentEditor,

        ContentEditor,

        Signature,

        Security,

        Redaction,

        Watermark,

        CompareDocuments,

        Conversion,

        Compress,

        Measurement;
    }

    private boolean isHead;

    private FunType type;

    private int iconResId;

    private String title;

    private String description;

    private String filePath;

    private boolean isNew;

    public HomeFunBean(Context context, FunType funType, @DrawableRes int icon, @StringRes int title, @StringRes int description) {
        this.type = funType;
        this.iconResId = icon;
        this.title = context.getString(title);
        this.description = context.getString(description);
    }

    public HomeFunBean(Context context, FunType funType, @DrawableRes int icon, @StringRes int title, @StringRes int description, boolean isNew) {
        this.type = funType;
        this.iconResId = icon;
        this.title = context.getString(title);
        this.description = context.getString(description);
        this.isNew = isNew;
    }

    public HomeFunBean() {

    }

    public static HomeFunBean head(Context context, @StringRes int title) {
        HomeFunBean funBean = new HomeFunBean();
        funBean.setHead(true);
        funBean.setTitle(context.getString(title));
        return funBean;
    }

    public static HomeFunBean assetsFile(Context context, String assetsName, String title) {
        HomeFunBean funBean = new HomeFunBean();
        funBean.setType(FunType.SamplePDF);
        funBean.setTitle(title);
        funBean.setIconResId(R.drawable.ic_fun_samples_pdf);
        funBean.setFilePath(CFileUtils.getAssetsTempFile(context, assetsName, title));
        return funBean;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(@DrawableRes int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FunType getType() {
        return type;
    }

    public void setType(FunType type) {
        this.type = type;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
