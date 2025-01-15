/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdflink;


import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFLinkAnnotation;
import com.compdfkit.core.document.CPDFDestination;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.action.CPDFGoToAction;
import com.compdfkit.core.document.action.CPDFUriAction;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.action.CActionEditDialogFragment;
import com.compdfkit.ui.proxy.attach.CPDFLinkAnnotAttachHelper;


public class CLinkAnnotAttachHelper extends CPDFLinkAnnotAttachHelper {

    @Override
    public void setLinkAction(CPDFLinkAnnotation cpdfLinkAnnotation) {
        super.setLinkAction(cpdfLinkAnnotation);
        if (cpdfLinkAnnotation == null || !cpdfLinkAnnotation.isValid()) {
            return;
        }
        CActionEditDialogFragment linkStyleDialogFragment = CActionEditDialogFragment.newInstance(
                readerView.getPDFDocument().getPageCount());
        linkStyleDialogFragment.setOnLinkInfoChangeListener(new CActionEditDialogFragment.COnActionInfoChangeListener() {
            @Override
            public void cancel() {
                tpdfPage.deleteAnnotation(cpdfLinkAnnotation);
                readerView.invalidate();
            }

            @Override
            public void createWebsiteLink(String url) {
                CPDFUriAction uriAction = new CPDFUriAction();
                uriAction.setUri(url);
                cpdfLinkAnnotation.setLinkAction(uriAction);
                if (pageView != null) {
                    pageView.addAnnotation(cpdfLinkAnnotation, false);
                    pageView.invalidate();
                }
            }

            @Override
            public void createEmailLink(String email) {
                CPDFUriAction uriAction = new CPDFUriAction();
                uriAction.setUri(email);
                cpdfLinkAnnotation.setLinkAction(uriAction);
                if (pageView != null) {
                    pageView.addAnnotation(cpdfLinkAnnotation, false);
                }
            }

            @Override
            public void createPageLink(int page) {
                if (readerView.getPDFDocument() != null) {
                    CPDFDocument document = readerView.getPDFDocument();
                    CPDFGoToAction goToAction = new CPDFGoToAction();
                    float height = document.pageAtIndex(page - 1).getSize().height();
                    CPDFDestination destination = new CPDFDestination(page - 1, 0F, height, 1F);
                    goToAction.setDestination(document, destination);
                    cpdfLinkAnnotation.setLinkAction(goToAction);
                    if (pageView != null) {
                        pageView.addAnnotation(cpdfLinkAnnotation, false);
                    }
                    readerView.saveReaderAttribute();
                }
            }
        });
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity != null) {
            linkStyleDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "linkDialog");
        }
    }
}
