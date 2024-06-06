/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfoutline.bean;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;

import java.util.ArrayList;

/**
 * This class represents a single item of a PDF outline, which can be displayed in a UI.
 */
public class COutlineData implements Parcelable {

    /**
     * The level of indentation of this item in the PDF outline hierarchy.
     */
    private int level;

    /**
     * The index of the PDF page that this item links to.
     */
    private int pageIndex;

    /**
     * The title text of this item.
     */
    private String title;

    /**
     * The child items of this item in the PDF outline hierarchy.
     */
    private ArrayList<COutlineData> childOutline;

    /**
     * Whether this item is expanded or collapsed in the UI.
     */
    private boolean isExpand;

    public COutlineData() {
    }

    public COutlineData(int level, String title, int pageIndex, ArrayList<COutlineData> childOutlineList) {
        this.level = level;
        this.title = title;
        this.pageIndex = pageIndex;
        this.childOutline = childOutlineList;
    }

    protected COutlineData(Parcel in) {
        level = in.readInt();
        pageIndex = in.readInt();
        title = in.readString();
        childOutline = in.createTypedArrayList(COutlineData.CREATOR);
        isExpand = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(level);
        dest.writeInt(pageIndex);
        dest.writeString(title);
        dest.writeTypedList(childOutline);
        dest.writeByte((byte) (isExpand ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<COutlineData> CREATOR = new Creator<COutlineData>() {
        @Override
        public COutlineData createFromParcel(Parcel in) {
            return new COutlineData(in);
        }

        @Override
        public COutlineData[] newArray(int size) {
            return new COutlineData[size];
        }
    };

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<COutlineData> getChildOutline() {
        return childOutline;
    }

    public void setChildOutline(ArrayList<COutlineData> childOutline) {
        this.childOutline = childOutline;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public int getLevelMargin(Context context) {
        return CDimensUtils.dp2px(context, (level - 1) * 15);
    }

    public boolean childOutlineIsEmpty() {
        return childOutline == null || childOutline.size() == 0;
    }
}
