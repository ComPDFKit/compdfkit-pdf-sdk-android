/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFLinkAnnotation;
import com.compdfkit.core.document.CPDFDestination;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.action.CPDFAction;
import com.compdfkit.core.document.action.CPDFGoToAction;
import com.compdfkit.core.document.action.CPDFUriAction;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuLinkProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.views.pdfproperties.action.CActionEditDialogFragment;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.proxy.CPDFLinkAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;

public class CLinkContextMenuView implements ContextMenuLinkProvider {

    @Override
    public View createLinkContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFLinkAnnotImpl linkAnnotImpl) {
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        menuView.addItem(R.string.tools_edit, v -> {
            showEditDialog(helper.getReaderView(), pageView, linkAnnotImpl);
            helper.dismissContextMenu();
        });
        menuView.addItem(R.string.tools_delete, v -> {
            pageView.deleteAnnotation((CPDFBaseAnnotImpl)linkAnnotImpl);
            helper.dismissContextMenu();
        });
        return menuView;
    }

    private void showEditDialog(CPDFReaderView readerView, CPDFPageView pageView, CPDFLinkAnnotImpl linkAnnotImpl) {
        CPDFLinkAnnotation linkAnnotation = linkAnnotImpl.onGetAnnotation();
        int pageCount = readerView.getPDFDocument().getPageCount();
        CActionEditDialogFragment linkStyleDialogFragment = null;
        CPDFAction action = linkAnnotation.getLinkAction();
        CPDFAction.ActionType actionType = action != null ? action.getActionType() : null;
        if (actionType != null && actionType == CPDFAction.ActionType.PDFActionType_GoTo) {
            CPDFGoToAction goToAction = (CPDFGoToAction) linkAnnotation.getLinkAction();
            int currentPage = goToAction.getDestination(readerView.getPDFDocument()).getPageIndex() + 1;
            linkStyleDialogFragment = CActionEditDialogFragment.newInstanceWithPage(pageCount, currentPage);
        } else if (actionType != null && actionType == CPDFAction.ActionType.PDFActionType_URI) {
            CPDFUriAction uriAction = (CPDFUriAction) linkAnnotation.getLinkAction();
            String uri = uriAction.getUri();
            if (uri.startsWith("mailto:")) {
                uri = uri.replace("mailto:", "");
                linkStyleDialogFragment = CActionEditDialogFragment.newInstanceWithEmail(pageCount, uri);
            } else {
                linkStyleDialogFragment = CActionEditDialogFragment.newInstanceWithUrl(pageCount, uri);
            }
        } else {
            linkStyleDialogFragment = CActionEditDialogFragment.newInstance(pageCount);
        }
        if (linkStyleDialogFragment == null) {
            return;
        }
        linkStyleDialogFragment.setOnLinkInfoChangeListener(new CActionEditDialogFragment.COnActionInfoChangeListener() {
            @Override
            public void cancel() {
            }

            @Override
            public void createWebsiteLink(String url) {
                CPDFUriAction uriAction = new CPDFUriAction();
                uriAction.setUri(url);
                linkAnnotation.setLinkAction(uriAction);
                linkAnnotation.updateAp();
                linkAnnotImpl.onAnnotAttrChange();
                pageView.invalidate();
            }

            @Override
            public void createEmailLink(String email) {
                CPDFUriAction uriAction = new CPDFUriAction();
                uriAction.setUri(email);
                linkAnnotation.setLinkAction(uriAction);
                linkAnnotation.updateAp();
                linkAnnotImpl.onAnnotAttrChange();
                pageView.invalidate();
            }

            @Override
            public void createPageLink(int page) {
                if (readerView.getPDFDocument() != null) {
                    CPDFDocument document = readerView.getPDFDocument();
                    CPDFGoToAction goToAction = new CPDFGoToAction();
                    float height = document.pageAtIndex(page - 1).getSize().height();
                    CPDFDestination destination = new CPDFDestination(page - 1, 0F, height, 1F);
                    goToAction.setDestination(document, destination);
                    linkAnnotation.setLinkAction(goToAction);
                    linkAnnotation.updateAp();
                    linkAnnotImpl.onAnnotAttrChange();
                    pageView.invalidate();
                }
            }
        });
        if (readerView.getContext() instanceof FragmentActivity) {
            linkStyleDialogFragment.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "linkDialog");
        }
    }
}
