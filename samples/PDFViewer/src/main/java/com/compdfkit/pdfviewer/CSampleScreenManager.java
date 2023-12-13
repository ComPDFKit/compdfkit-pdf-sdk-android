package com.compdfkit.pdfviewer;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintSet;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.pdfviewer.databinding.PdfSampleActivityBinding;
import com.compdfkit.tools.common.utils.animation.CFillScreenManager;
import com.compdfkit.tools.common.utils.animation.ConstraintSetUtils;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

public class CSampleScreenManager {
    private PdfSampleActivityBinding binding;

    public CFillScreenManager fillScreenManager = new CFillScreenManager();

    private boolean isFillScreen;

    private ConstraintSet constraintSet = new ConstraintSet();

    private ConstraintSetUtils constraintSetUtils = new ConstraintSetUtils();

    public void bind(PdfSampleActivityBinding mainBinding){
        this.binding = mainBinding;
        constraintSet.clone(binding.getRoot());
        fillScreenManager.bindRightToolViewList(binding.pdfView.slideBar);
        fillScreenManager.bindBottomToolViewList(binding.pdfView.indicatorView);
        fillScreenManager.bindBottomToolViewList(binding.flBottomToolBar);
    }


    public void changeWindowStatus(CPreviewMode mode){
        if (mode == CPreviewMode.Viewer){
            fillScreenManager.removeToolView(binding.flBottomToolBar);
            constraintSetUtils.hideFromBottom(constraintSet, binding.flBottomToolBar);
        }else if (mode == CPreviewMode.Annotation){
            fillScreenManager.bindBottomToolViewList(binding.flBottomToolBar);
            binding.annotationToolBar.setVisibility(View.VISIBLE);
            binding.editToolBar.setVisibility(View.GONE);
            binding.formToolBar.setVisibility(View.GONE);
            binding.signatureToolBar.setVisibility(View.GONE);
            constraintSetUtils.showFromBottom(constraintSet, binding.flBottomToolBar);
        } else if (mode == CPreviewMode.Edit) {
            fillScreenManager.bindBottomToolViewList(binding.flBottomToolBar);
            binding.annotationToolBar.setVisibility(View.GONE);
            binding.editToolBar.setVisibility(View.VISIBLE);
            binding.formToolBar.setVisibility(View.GONE);
            binding.signatureToolBar.setVisibility(View.GONE);
            constraintSetUtils.showFromBottom(constraintSet, binding.flBottomToolBar);
        } else if (mode == CPreviewMode.Form){
            fillScreenManager.bindBottomToolViewList(binding.flBottomToolBar);
            binding.annotationToolBar.setVisibility(View.GONE);
            binding.editToolBar.setVisibility(View.GONE);
            binding.formToolBar.setVisibility(View.VISIBLE);
            binding.signatureToolBar.setVisibility(View.GONE);
            constraintSetUtils.showFromBottom(constraintSet, binding.flBottomToolBar);
        } else if (mode == CPreviewMode.Signature){
            fillScreenManager.bindBottomToolViewList(binding.flBottomToolBar);
            binding.annotationToolBar.setVisibility(View.GONE);
            binding.editToolBar.setVisibility(View.GONE);
            binding.formToolBar.setVisibility(View.GONE);
            binding.signatureToolBar.setVisibility(View.VISIBLE);
            constraintSetUtils.showFromBottom(constraintSet, binding.flBottomToolBar);
        }
        constraintSetUtils.apply(constraintSet, binding.getRoot());
    }

    public void changeWindowStatus(CAnnotationType type){
        if (type == CAnnotationType.INK){
            fillScreenChange();
            fillScreenManager.bindTopToolView(binding.inkCtrlView);
            binding.inkCtrlView.setVisibility(View.VISIBLE);
        }else {
            if (isFillScreen){
                fillScreenChange();
            }
            binding.inkCtrlView.setVisibility(View.GONE);
            fillScreenManager.removeToolView(binding.inkCtrlView);
        }
    }

    public void changeWindowStatus(CPDFAnnotation.Type pdfType){
        if (pdfType == CPDFAnnotation.Type.INK){
            fillScreenChange();
            fillScreenManager.hideFromTop(binding.pdfToolBar, 200);
            fillScreenManager.hideFromBottom(binding.flBottomToolBar, 200);
        }else {
            if (isFillScreen) {
                fillScreenChange();
            }
            binding.inkCtrlView.setVisibility(View.GONE);
            fillScreenManager.removeToolView(binding.inkCtrlView);
        }
    }

    public void fillScreenChange() {
        fillScreenManager.fillScreenChange(!isFillScreen);
        if (!isFillScreen) {
            //enter full screen
            constraintSetUtils.hideFromTop(constraintSet, binding.flTool);
            constraintSetUtils.hideFromBottom(constraintSet, binding.flBottomToolBar);
            isFillScreen = true;
        } else {
            //enter normal state
            constraintSetUtils.showFromTop(constraintSet, binding.flTool);
            //show bottom annotationBar
            if (fillScreenManager.bottomToolViewList.contains(binding.flBottomToolBar)) {
                constraintSetUtils.showFromBottom(constraintSet, binding.flBottomToolBar);
            }else {
                constraintSetUtils.hideFromBottom(constraintSet, binding.flBottomToolBar);
            }
            if (fillScreenManager.topToolViewList.contains(binding.signStatusView)) {
                constraintSetUtils.show(constraintSet, binding.signStatusView);
            }else {
                constraintSetUtils.hide(constraintSet, binding.signStatusView);
            }
            isFillScreen = false;
        }
        constraintSetUtils.apply(constraintSet, binding.getRoot());
    }

    public void constraintShow(View view){
        constraintSetUtils.show(constraintSet, view);
        constraintSetUtils.apply(constraintSet, binding.getRoot());
    }

    public void constraintHide(View view){
        constraintSetUtils.hide(constraintSet, view);
        constraintSetUtils.apply(constraintSet, binding.getRoot());
    }
}
