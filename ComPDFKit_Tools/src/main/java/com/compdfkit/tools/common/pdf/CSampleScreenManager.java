/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintSet;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.common.utils.animation.CFillScreenManager;
import com.compdfkit.tools.common.utils.animation.ConstraintSetUtils;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

public class CSampleScreenManager {
    private CPDFDocumentFragment documentFragment;

    public CFillScreenManager fillScreenManager = new CFillScreenManager();

    private boolean isFillScreen;

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
            constraintSetUtils.hideFromBottom(constraintSet, documentFragment.flBottomToolBar);
        }else if (mode == CPreviewMode.Annotation){
            fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
            documentFragment.annotationToolbar.setVisibility(View.VISIBLE);
            documentFragment.editToolBar.setVisibility(View.GONE);
            documentFragment.formToolBar.setVisibility(View.GONE);
            documentFragment.signatureToolBar.setVisibility(View.GONE);
            constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
        } else if (mode == CPreviewMode.Edit) {
            fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
            documentFragment.annotationToolbar.setVisibility(View.GONE);
            documentFragment.editToolBar.setVisibility(View.VISIBLE);
            documentFragment.formToolBar.setVisibility(View.GONE);
            documentFragment.signatureToolBar.setVisibility(View.GONE);
            constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
        } else if (mode == CPreviewMode.Form){
            fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
            documentFragment.annotationToolbar.setVisibility(View.GONE);
            documentFragment.editToolBar.setVisibility(View.GONE);
            documentFragment.formToolBar.setVisibility(View.VISIBLE);
            documentFragment.signatureToolBar.setVisibility(View.GONE);
            constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
        } else if (mode == CPreviewMode.Signature){
            fillScreenManager.bindBottomToolViewList(documentFragment.flBottomToolBar);
            documentFragment.annotationToolbar.setVisibility(View.GONE);
            documentFragment.editToolBar.setVisibility(View.GONE);
            documentFragment.formToolBar.setVisibility(View.GONE);
            documentFragment.signatureToolBar.setVisibility(View.VISIBLE);
            constraintSetUtils.showFromBottom(constraintSet, documentFragment.flBottomToolBar);
        }
        constraintSetUtils.apply(constraintSet, documentFragment.clRoot);
    }

    public void changeWindowStatus(CAnnotationType type){
        if (type == CAnnotationType.INK){
            fillScreenChange();
            fillScreenManager.bindTopToolView(documentFragment.inkCtrlView);
            documentFragment.inkCtrlView.setVisibility(View.VISIBLE);
        }else {
            if (isFillScreen){
                fillScreenChange();
            }
            documentFragment.inkCtrlView.setVisibility(View.GONE);
            fillScreenManager.removeToolView(documentFragment.inkCtrlView);
        }
    }

    public void changeWindowStatus(CPDFAnnotation.Type pdfType){
        if (pdfType == CPDFAnnotation.Type.INK){
            fillScreenChange();
            fillScreenManager.hideFromTop(documentFragment.pdfToolBar, 200);
            fillScreenManager.hideFromBottom(documentFragment.flBottomToolBar, 200);
        }else {
            if (isFillScreen) {
                fillScreenChange();
            }
            documentFragment.inkCtrlView.setVisibility(View.GONE);
            fillScreenManager.removeToolView(documentFragment.inkCtrlView);
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
