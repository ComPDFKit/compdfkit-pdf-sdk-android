/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.util;


import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.widget.AppCompatTextView;

public class LoggingOutputListener implements OutputListener{

    private AppCompatTextView logTextview;

    private ScrollView scrollView;

    public LoggingOutputListener(AppCompatTextView logTextview, ScrollView scrollView){
        this.logTextview = logTextview;
        this.scrollView = scrollView;
    }

    @Override
    public void print(String output) {
        scrollView.post(()->{
            logTextview.append(output);
            scrollView.fullScroll(View.FOCUS_DOWN);
        });
    }

    @Override
    public void print() {
        logTextview.append("");
    }

    @Override
    public void println(String output) {
        scrollView.post(()->{
            logTextview.append(output+"\n");
            scrollView.fullScroll(View.FOCUS_DOWN);
        });
    }

    @Override
    public void println() {
        logTextview.post(()->{
            logTextview.append("\n");
        });
    }

    @Override
    public void printError(String errorMessage) {
        println(errorMessage);
    }
}
