/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf;

import static android.view.View.GONE;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintSet;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.CPDFUIVisibilityMode;
import com.compdfkit.tools.common.utils.animation.CFillScreenManager;
import com.compdfkit.tools.common.utils.animation.ConstraintSetUtils;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

public class CSampleScreenManager {
    private CPDFDocumentFragment documentFragment;

    public CFillScreenManager fillScreenManager = new CFillScreenManager();

    public boolean isFillScreen;

    private ConstraintSet constraintSet = new ConstraintSet();

    private ConstraintSetUtils constraintSetUtils = new ConstraintSetUtils();

    public void bind(CPDFDocumentFragment documentFragment){
        this.documentFragment = documentFragment;
        constraintSet.clone(documentFragment.clRoot);
        fillScreenManager.bindRightToolViewList(documentFragment.pdfView.slideBar);
        fillScreenManager.bindBottomToolViewList(documentFragment.pdfView.indicatorView);
        fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
    }


    public void changeWindowStatus(CPreviewMode mode){
        if (mode == CPreviewMode.Viewer){
            fillScreenManager.removeToolView(documentFragment.flBottomToolBar);
            documentFragment.annotationToolbar.setVisibility(GONE);
            documentFragment.formToolBar.setVisibility(GONE);
            documentFragment.editToolBar.setVisibility(GONE);
            documentFragment.signatureToolBar.setVisibility(GONE);
            constraintSetUtils.hide(constraintSet, documentFragment.flBottomToolBar);
            constraintSetUtils.hideFromBottom(constraintSet, documentFragment.flBottomToolBar);
        }else if (mode == CPreviewMode.Annotation){
            fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
            CPDFConfiguration configuration = documentFragment.pdfView.getCPDFConfiguration();
            boolean visible = configuration != null && configuration.toolbarConfig.annotationToolbarVisible;
            documentFragment.annotationToolbar.setVisibility(visible ? View.VISIBLE : GONE);
            documentFragment.editToolBar.setVisibility(GONE);
            documentFragment.formToolBar.setVisibility(GONE);
            documentFragment.signatureToolBar.setVisibility(GONE);
            constraintSetUtils.show(constraintSet, documentFragment.flBottomToolBar);
            constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
        } else if (mode == CPreviewMode.Edit) {
            fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
            documentFragment.annotationToolbar.setVisibility(GONE);
            CPDFConfiguration configuration = documentFragment.pdfView.getCPDFConfiguration();
            boolean visible = configuration != null && configuration.toolbarConfig.contentEditorToolbarVisible;
            documentFragment.editToolBar.setVisibility(visible ? View.VISIBLE : GONE);
            documentFragment.formToolBar.setVisibility(GONE);
            documentFragment.signatureToolBar.setVisibility(GONE);
            constraintSetUtils.show(constraintSet, documentFragment.flBottomToolBar);
            constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
        } else if (mode == CPreviewMode.Form){
            fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
            documentFragment.annotationToolbar.setVisibility(GONE);
            documentFragment.editToolBar.setVisibility(GONE);
            CPDFConfiguration configuration = documentFragment.pdfView.getCPDFConfiguration();
            boolean visible = configuration != null && configuration.toolbarConfig.formToolbarVisible;
            documentFragment.formToolBar.setVisibility(visible ? View.VISIBLE : GONE);
            documentFragment.signatureToolBar.setVisibility(GONE);
            constraintSetUtils.show(constraintSet, documentFragment.flBottomToolBar);
            constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
        } else if (mode == CPreviewMode.Signature){
            fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
            documentFragment.annotationToolbar.setVisibility(GONE);
            documentFragment.editToolBar.setVisibility(GONE);
            documentFragment.formToolBar.setVisibility(GONE);
            CPDFConfiguration configuration = documentFragment.pdfView.getCPDFConfiguration();
            boolean visible = configuration != null && configuration.toolbarConfig.signatureToolbarVisible;
            documentFragment.signatureToolBar.setVisibility(visible ? View.VISIBLE : GONE);
            constraintSetUtils.show(constraintSet, documentFragment.flBottomToolBar);
            constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
        }
        constraintSetUtils.apply(constraintSet, documentFragment.clRoot);
    }

    public void changeWindowStatus(CAnnotationType type){
        CPDFUIVisibilityMode uiVisibilityMode = documentFragment.pdfView.getCPDFConfiguration().modeConfig.uiVisibilityMode;
        if (type == CAnnotationType.INK){
            if (uiVisibilityMode != CPDFUIVisibilityMode.ALWAYS){
                fillScreenManager.hideFromTop(documentFragment.flTool, 100);
            }
        }else {
            boolean isGone = documentFragment.flTool.getVisibility() == GONE;
            boolean isEnable = documentFragment.pdfView.getCPDFConfiguration().toolbarConfig.mainToolbarVisible;
            boolean isShowToolbar = uiVisibilityMode != CPDFUIVisibilityMode.NEVER && isEnable;
            if (isShowToolbar && isGone){
                fillScreenManager.showFromTop(documentFragment.flTool, 100);
            }
        }
    }

    public void changeWindowStatus(CPDFAnnotation.Type pdfType){
        if (pdfType == CPDFAnnotation.Type.INK){
            fillScreenManager.hideFromTop(documentFragment.flTool, 100);
            fillScreenManager.hideFromBottom(documentFragment.flBottomToolBar, 100);
        } else {
            if (documentFragment.flTool.getVisibility() == GONE){
                fillScreenManager.showFromTop(documentFragment.flTool, 100);
            }
        }
    }

    public void fillScreenChange() {
        fillScreenManager.fillScreenChange(!isFillScreen);
        if (!isFillScreen) {
            //enter full screen
            constraintSetUtils.hideFromTop(constraintSet, documentFragment.flTool);
            constraintSetUtils.hideFromBottom(constraintSet, documentFragment.flBottomToolBar);
            isFillScreen = true;
        } else {
            //enter normal state
            constraintSetUtils.showFromTop(constraintSet, documentFragment.flTool);
            //show bottom annotationBar
            if (fillScreenManager.bottomToolViewList.contains(documentFragment.flBottomToolBar)) {
                constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
            }else {
                constraintSetUtils.hideFromBottom(constraintSet, documentFragment.flBottomToolBar);
            }
            if (fillScreenManager.topToolViewList.contains(documentFragment.signStatusView)) {
                constraintSetUtils.show(constraintSet, documentFragment.signStatusView);
            }else {
                constraintSetUtils.hide(constraintSet, documentFragment.signStatusView);
            }
            isFillScreen = false;
        }
        constraintSetUtils.apply(constraintSet, documentFragment.clRoot);
    }

    public void constraintShow(View view){
        constraintSetUtils.show(constraintSet, view);
        constraintSetUtils.apply(constraintSet, documentFragment.clRoot);
    }

    public void constraintHide(View view){
        constraintSetUtils.hide(constraintSet, view);
        constraintSetUtils.apply(constraintSet, documentFragment.clRoot);
    }
}
