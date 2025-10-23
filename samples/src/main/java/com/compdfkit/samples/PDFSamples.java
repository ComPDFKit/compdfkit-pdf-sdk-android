/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples;


import android.content.Context;
import android.os.Environment;

import androidx.annotation.StringRes;

import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class PDFSamples {

    protected static final String INPUT_PATH = "TestFiles/";

    protected Context context;

    protected OutputListener outputListener;

    private ArrayList<String> outputFileList;

    private String title;

    private String description;

    public PDFSamples() {
        context = SampleApplication.getInstance();
        title = "{title}";
        description = "{description}";
        outputFileList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(@StringRes int titleResId) {
        this.title = SampleApplication.getInstance().getString(titleResId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescription(@StringRes int descriptionResId) {
        this.description = SampleApplication.getInstance().getString(descriptionResId);
    }

    public List<String> getOutputFileList() {
        return outputFileList;
    }

    public String[] getOutputFileNames() {
        String[] names = new String[outputFileList.size()];
        outputFileList.toArray(names);
        for (int i = 0; i < names.length; i++) {
            File file = new File(names[i]);
            names[i] = file.getName();
        }
        return names;
    }

    public void addFileList(String file) {
        this.outputFileList.add(file);
    }

    protected void printHead() {
        String head = SampleApplication.getInstance().getString(R.string.sample_header, title);
        if (outputListener != null) {
            outputListener.println(head);
        }
    }

    protected void printFooter() {
        String footer = SampleApplication.getInstance().getString(R.string.sample_footer);
        if (outputListener != null) {
            outputListener.println("\n" + footer);
            printDividingLine();
        }
    }

    protected void printDividingLine(){
        if (outputListener != null) {
            outputListener.println("--------------------------------------------");
        }
    }

    protected void run(OutputListener outputListener) {
        this.outputListener = outputListener;
        this.outputFileList.clear();
    }

    protected void saveSamplePDF(CPDFDocument document, File file, boolean close){
        try {
            file.getParentFile().mkdirs();
            document.saveAs(file.getAbsolutePath(), false, true);
            if (file.exists()) {
                getOutputFileList().add(file.getAbsolutePath());
            }
            if (close){
                document.close();
            }
        } catch (CPDFDocumentException e) {
            e.printStackTrace();
        }
    }

    protected File outputDir(){
        return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    }
}
