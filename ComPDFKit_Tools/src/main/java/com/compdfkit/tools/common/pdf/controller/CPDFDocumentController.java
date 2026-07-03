/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import com.compdfkit.tools.common.basic.fragment.CBasicPDFFragment;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.pdf.CSampleScreenManager;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;

/**
 * Shared context object passed to every controller.
 * Gives controllers access to the fragment, pdfView, configuration, and screen manager
 * without each controller needing a direct reference to the 1000-line fragment.
 */
public class CPDFDocumentController {

    private final CPDFDocumentFragment fragment;
    private final CPDFViewCtrl pdfView;
    private final CPDFConfiguration configuration;
    private final CSampleScreenManager screenManager;

    public CPDFDocumentController(CPDFDocumentFragment fragment, CPDFViewCtrl pdfView,
                                  CPDFConfiguration configuration, CSampleScreenManager screenManager) {
        this.fragment = fragment;
        this.pdfView = pdfView;
        this.configuration = configuration;
        this.screenManager = screenManager;
    }

    public CPDFDocumentFragment getFragment() {
        return fragment;
    }

    public CBasicPDFFragment getBasicFragment() {
        return fragment;
    }

    public CPDFViewCtrl getPdfView() {
        return pdfView;
    }

    public CPDFConfiguration getConfiguration() {
        return configuration;
    }

    public CSampleScreenManager getScreenManager() {
        return screenManager;
    }
}
