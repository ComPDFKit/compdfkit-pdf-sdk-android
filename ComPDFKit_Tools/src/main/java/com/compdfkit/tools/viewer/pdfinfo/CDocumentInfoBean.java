/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfinfo;


import java.io.Serializable;

public class CDocumentInfoBean implements Serializable {

    private String fileName;

    private String fileSize;

    private String title;

    private String author;

    private String subject;

    private String keywords;

    private String version;

    private int pageCount;

    private String creator;

    private String creationDate;

    private String modificationDate;

    private boolean allowsPrinting;

    private boolean allowsCopying;

    private boolean allowsDocumentChanges;

    private boolean allowsDocumentAssembly;

    private boolean allowsCommenting;

    private boolean allowsFormFieldEntry;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public boolean isAllowsPrinting() {
        return allowsPrinting;
    }

    public void setAllowsPrinting(boolean allowsPrinting) {
        this.allowsPrinting = allowsPrinting;
    }

    public boolean isAllowsCopying() {
        return allowsCopying;
    }

    public void setAllowsCopying(boolean allowsCopying) {
        this.allowsCopying = allowsCopying;
    }

    public boolean isAllowsDocumentChanges() {
        return allowsDocumentChanges;
    }

    public void setAllowsDocumentChanges(boolean allowsDocumentChanges) {
        this.allowsDocumentChanges = allowsDocumentChanges;
    }

    public boolean isAllowsDocumentAssembly() {
        return allowsDocumentAssembly;
    }

    public void setAllowsDocumentAssembly(boolean allowsDocumentAssembly) {
        this.allowsDocumentAssembly = allowsDocumentAssembly;
    }

    public boolean isAllowsCommenting() {
        return allowsCommenting;
    }

    public void setAllowsCommenting(boolean allowsCommenting) {
        this.allowsCommenting = allowsCommenting;
    }

    public boolean isAllowsFormFieldEntry() {
        return allowsFormFieldEntry;
    }

    public void setAllowsFormFieldEntry(boolean allowsFormFieldEntry) {
        this.allowsFormFieldEntry = allowsFormFieldEntry;
    }
}
