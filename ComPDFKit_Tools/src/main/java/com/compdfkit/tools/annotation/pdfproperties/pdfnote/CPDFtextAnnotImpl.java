/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfnote;

import android.util.Log;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.ui.proxy.CPDFTextAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;
import java.util.Map;


public class CPDFtextAnnotImpl extends CPDFTextAnnotImpl {

    @Override
    protected void onTouchTextAnnot(CPDFTextAnnotation cpdfTextAnnotation) {
        CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
        if (configuration != null && configuration.annotationsConfig != null && configuration.annotationsConfig.interceptNoteAction) {
            Map<String, Object> extraMap = new java.util.HashMap<>();
            extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, CPDFCustomEventType.INTERCEPT_ANNOTATION_DO_ACTION);
            extraMap.put(CPDFCustomEventField.ANNOTATION, cpdfTextAnnotation);
            CPDFCustomEventCallbackHelper.getInstance().notifyClick("", extraMap);
            return;
        }
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
