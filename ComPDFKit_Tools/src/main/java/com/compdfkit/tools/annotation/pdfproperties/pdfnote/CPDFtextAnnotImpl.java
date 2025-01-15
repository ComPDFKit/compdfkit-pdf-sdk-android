/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfnote;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.ui.proxy.CPDFTextAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;


public class CPDFtextAnnotImpl extends CPDFTextAnnotImpl {

    @Override
    protected void onTouchTextAnnot(CPDFTextAnnotation cpdfTextAnnotation) {
        setFocused(true);
        CNoteEditDialog editDialog = CNoteEditDialog.newInstance(cpdfTextAnnotation.getContent());
        editDialog.setSaveListener(v -> {
            String content = editDialog.getContent();
            cpdfTextAnnotation.setContent(content);
            editDialog.dismiss();
        });
        editDialog.setDeleteListener(v -> {
            CPDFPageView cpdfPageView = (CPDFPageView) pageView;
            cpdfPageView.deleteAnnotation(this);
            cpdfTextAnnotation.removeFromPage();
            editDialog.dismiss();
        });
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity != null) {
            editDialog.show(fragmentActivity.getSupportFragmentManager(), "noteEditDialog");
        }
    }
}

