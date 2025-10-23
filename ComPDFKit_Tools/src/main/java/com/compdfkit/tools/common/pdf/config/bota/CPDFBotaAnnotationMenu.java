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
import java.util.ArrayList;
import java.util.List;

public class CPDFBotaAnnotationMenu implements Serializable {

    /**
     * ----------------------------->
     * annotation global menu id
     */
    public static final String GLOBAL_MENU_ID_IMPORT_ANNOTATION = "importAnnotation";
    public static final String GLOBAL_MENU_ID_EXPORT_ANNOTATION = "exportAnnotation";
    public static final String GLOBAL_MENU_ID_REMOVE_ALL_ANNOTATION = "removeAllAnnotation";
    public static final String GLOBAL_MENU_ID_REMOVE_ALL_REPLY = "removeAllReply";
    /**
     * ----------------------------->
     * annotation item menu id
     */
    public static final String MENU_ID_REVIEW_STATUS = "reviewStatus";
    public static final String MENU_ID_MARKED_STATUS = "markedStatus";
    public static final String MENU_ID_MORE = "more";

    /**
     * ----------------------------->
     * review status
     */
    public static final String REVIEW_STATUS_ACCEPTED = "accepted";
    public static final String REVIEW_STATUS_REJECTED = "rejected";
    public static final String REVIEW_STATUS_CANCELLED = "cancelled";
    public static final String REVIEW_STATUS_COMPLETED = "completed";
    public static final String REVIEW_STATUS_NONE = "none";

    /**
     * ----------------------------->
     * annotation item more menu id
     */
    public static final String MORE_MENU_ID_ADD_REPLY = "addReply";
    public static final String MORE_MENU_ID_VIEW_REPLY = "viewReply";
    public static final String MORE_MENU_ID_DELETE = "delete";

    private List<CPDFBotaGlobalMenuItem> global;
    private List<CPDFBotaItemMenu> item;

    public List<CPDFBotaGlobalMenuItem> getGlobal() {
        return global;
    }

    public void setGlobal(List<CPDFBotaGlobalMenuItem> global) {
        this.global = global;
    }

    public List<CPDFBotaItemMenu> getItem() {
        return item;
    }

    public void setItem(List<CPDFBotaItemMenu> item) {
        this.item = item;
    }

    public CPDFBotaItemMenu getReviewStatusMenu() {
        if (item != null) {
            for (CPDFBotaItemMenu menu : item) {
                if (MENU_ID_REVIEW_STATUS.equals(menu.getId())) {
                    return menu;
                }
            }
        }
        return null;
    }

    public CPDFBotaItemMenu getMarkedStatusMenu() {
        if (item != null) {
            for (CPDFBotaItemMenu menu : item) {
                if (MENU_ID_MARKED_STATUS.equals(menu.getId())) {
                    return menu;
                }
            }
        }
        return null;
    }

    public CPDFBotaItemMenu getMoreMenu() {
        if (item != null) {
            for (CPDFBotaItemMenu menu : item) {
                if (MENU_ID_MORE.equals(menu.getId())) {
                    return menu;
                }
            }
        }
        return null;
    }

    public CPDFBotaAnnotationMenu() {
        global = new ArrayList<>();
        global.add(new CPDFBotaGlobalMenuItem(GLOBAL_MENU_ID_IMPORT_ANNOTATION));
        global.add(new CPDFBotaGlobalMenuItem(GLOBAL_MENU_ID_EXPORT_ANNOTATION));
        global.add(new CPDFBotaGlobalMenuItem(GLOBAL_MENU_ID_REMOVE_ALL_ANNOTATION));
        global.add(new CPDFBotaGlobalMenuItem(GLOBAL_MENU_ID_REMOVE_ALL_REPLY));
        item = new ArrayList<>();
        item.add(new CPDFBotaItemMenu(
                MENU_ID_REVIEW_STATUS,
                REVIEW_STATUS_ACCEPTED, REVIEW_STATUS_REJECTED, REVIEW_STATUS_CANCELLED,
                REVIEW_STATUS_COMPLETED, REVIEW_STATUS_NONE
        ));
        item.add(new CPDFBotaItemMenu(
                MENU_ID_MARKED_STATUS
        ));
        item.add(new CPDFBotaItemMenu(
                MENU_ID_MORE,
                MORE_MENU_ID_ADD_REPLY, MORE_MENU_ID_VIEW_REPLY, MORE_MENU_ID_DELETE
        ));
    }
}
