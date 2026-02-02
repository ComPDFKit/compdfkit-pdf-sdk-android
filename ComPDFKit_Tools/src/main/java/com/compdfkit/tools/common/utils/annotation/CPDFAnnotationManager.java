/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.annotation;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFAnnotation.Type;
import com.compdfkit.core.annotation.CPDFFreetextAnnotation;
import com.compdfkit.core.annotation.CPDFReplyAnnotation;
import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.annotation.form.CPDFPushbuttonWidget;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.annotation.form.CPDFWidgetItems;
import com.compdfkit.core.document.CPDFDestination;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.action.CPDFAction;
import com.compdfkit.core.document.action.CPDFGoToAction;
import com.compdfkit.core.document.action.CPDFUriAction;
import com.compdfkit.core.edit.CPDFEditArea;
import com.compdfkit.core.edit.CPDFEditTextArea;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextSelection;
import com.compdfkit.core.utils.TMathUtils;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationlist.dialog.CPDFEditReplyDialogFragment;
import com.compdfkit.tools.annotation.pdfannotationlist.dialog.CPDFReplyDetailsDialogFragment;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.CNoteEditDialog;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.CTypeUtil;
import com.compdfkit.tools.common.views.pdfproperties.action.CActionEditDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider.CEditSelectionsProvider;
import com.compdfkit.tools.forms.pdfproperties.option.edit.CFormOptionEditFragment;
import com.compdfkit.ui.attribute.CPDFAnnotAttribute;
import com.compdfkit.ui.attribute.CPDFFreetextAttr;
import com.compdfkit.ui.attribute.CPDFReaderAttribute;
import com.compdfkit.ui.attribute.CPDFTextAttr;
import com.compdfkit.ui.edit.CPDFEditTextSelections;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.proxy.CPDFFreetextAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.utils.CPDFTextUtils;
import com.compdfkit.ui.widget.selection.ISelectionHelper;

import java.util.List;

public class CPDFAnnotationManager {

    public void addFreeText(String content, CPDFReaderView readerView, CPDFPageView pageView, PointF pointF) {
        CPDFPage page = readerView.getPDFDocument().pageAtIndex(readerView.getPageNum());
        CPDFFreetextAnnotation freetextAnnotation = (CPDFFreetextAnnotation) page.addAnnot(CPDFAnnotation.Type.FREETEXT);
        CPDFFreetextAttr freetextAttr = readerView.getReaderAttribute().getAnnotAttribute().getFreetextAttr();
        if (freetextAnnotation.isValid()) {
            CPDFTextAttribute attribute = freetextAttr.getTextAttribute();
            freetextAnnotation.setFreetextDa(attribute);
            freetextAnnotation.setFreetextAlignment(freetextAttr.getAlignment());
            freetextAnnotation.setAlpha(freetextAttr.getAlpha());
            freetextAnnotation.setContent(content);
            freetextAnnotation.setBorderWidth(0);
            PointF freeTextPointF = new PointF();
            TMathUtils.scalePointF(pointF, freeTextPointF, pageView.getScaleValue());
            RectF area = CPDFTextUtils.measureTextArea(pageView,
                    freetextAnnotation.getFreetextDa().getFontName(),
                    freetextAnnotation.getFreetextDa().getFontSize() * pageView.getScaleValue(),
                    freeTextPointF, content);
            TMathUtils.scaleRectF(area, area, 1 / pageView.getScaleValue());
            RectF size = readerView.getPageNoZoomSize(pageView.getPageNum());
            area = page.convertRectToPage(readerView.isCropMode(), size.width(), size.height(), area);
            freetextAnnotation.setRect(area);
            freetextAnnotation.updateAp();
            pageView.addAnnotation(freetextAnnotation, false);
        }
    }

    public void addFreeTextInputBox(CPDFReaderView readerView, CPDFPageView pageView, PointF pointF) {
        CPDFPage page = readerView.getPDFDocument().pageAtIndex(readerView.getPageNum());
        CPDFFreetextAnnotation freetextAnnotation = (CPDFFreetextAnnotation) page.addAnnot(CPDFAnnotation.Type.FREETEXT);
        CPDFFreetextAttr freetextAttr = readerView.getReaderAttribute().getAnnotAttribute().getFreetextAttr();
        if (freetextAnnotation.isValid()) {
            CPDFTextAttribute attribute = freetextAttr.getTextAttribute();
            freetextAnnotation.setFreetextDa(attribute);
            freetextAnnotation.setFreetextAlignment(freetextAttr.getAlignment());
            freetextAnnotation.setAlpha(freetextAnnotation.getAlpha());
            freetextAnnotation.setContent("");
            freetextAnnotation.setBorderWidth(0);
            RectF area = new RectF(pointF.x, pointF.y, pointF.x, pointF.y);
            area = page.convertRectToPage(readerView.isCropMode(), page.getSize().width(), page.getSize().height(), area);
            freetextAnnotation.setRect(area);
            freetextAnnotation.updateAp();
            CPDFFreetextAnnotImpl freetextAnnot = (CPDFFreetextAnnotImpl) pageView.addAnnotation(freetextAnnotation, false);
            pageView.createInputBox(freetextAnnot);
        }
    }

    public void addNote(CPDFReaderView readerView, CPDFPageView pageView, PointF pointF) {
        CPDFPage page = readerView.getPDFDocument().pageAtIndex(readerView.getPageNum());
        CPDFTextAnnotation textAnnotation = (CPDFTextAnnotation) page.addAnnot(CPDFAnnotation.Type.TEXT);
        if (textAnnotation.isValid()) {
            CNoteEditDialog editDialog = CNoteEditDialog.newInstance(textAnnotation.getContent());
            editDialog.setSaveListener(v -> {
                CPDFTextAttr textAttr = readerView.getReaderAttribute().getAnnotAttribute().getTextAttr();
                RectF pageSize = readerView.getPageNoZoomSize(readerView.getPageNum());
                RectF insertRect = new RectF(pointF.x, pointF.y, pointF.x + 50, pointF.y + 50);
                insertRect = page.convertRectToPage(readerView.isCropMode(), pageSize
                        .width(), pageSize.height(), insertRect);
                textAnnotation.setRect(insertRect);
                String content = editDialog.getContent();
                textAnnotation.setContent(content);
                textAnnotation.setColor(textAttr.getColor());
                textAnnotation.setAlpha(textAttr.getAlpha());
                textAnnotation.updateAp();
                pageView.addAnnotation(textAnnotation, false);
                editDialog.dismiss();
            });
            editDialog.setDeleteListener(v -> {
                page.deleteAnnotation(textAnnotation);
                editDialog.dismiss();
            });
            editDialog.setDialogCancelListener(() -> {
                page.deleteAnnotation(textAnnotation);
            });
            if (readerView != null && readerView.getContext() != null) {
                FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
                if (fragmentActivity != null) {
                    editDialog.show(fragmentActivity.getSupportFragmentManager(), "noteEditDialog");
                }
            }
        }
    }

    public void addStandardStamp(CPDFStampAnnotation.StandardStamp standardStamp, CPDFReaderView readerView, CPDFPageView pageView, PointF pointF) {
        CPDFPage page = readerView.getPDFDocument().pageAtIndex(readerView.getPageNum());
        CPDFStampAnnotation stampAnnotation = (CPDFStampAnnotation) page.addAnnot(CPDFAnnotation.Type.STAMP);
        stampAnnotation.setStandardStamp(standardStamp);
        addStamp(stampAnnotation, readerView, pageView, pointF);
    }

    public void addTextStamp(CPDFStampAnnotation.TextStamp textStamp, CPDFReaderView readerView, CPDFPageView pageView, PointF pointF) {
        CPDFPage page = readerView.getPDFDocument().pageAtIndex(readerView.getPageNum());
        CPDFStampAnnotation stampAnnotation = (CPDFStampAnnotation) page.addAnnot(CPDFAnnotation.Type.STAMP);
        stampAnnotation.setTextStamp(textStamp);
        addStamp(stampAnnotation, readerView, pageView, pointF);
    }

    public void addImageStamp(String imagePath, CPDFReaderView readerView, CPDFPageView pageView, PointF pointF) {
        CPDFPage page = readerView.getPDFDocument().pageAtIndex(readerView.getPageNum());
        CPDFStampAnnotation stampAnnotation = (CPDFStampAnnotation) page.addAnnot(CPDFAnnotation.Type.STAMP);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int rotate = CUriUtil.getBitmapDegree(imagePath);

        RectF area;
        float defaultWidth = 200F;
        PointF vertex = new PointF(pointF.x - (defaultWidth / 2), pointF.y);
        if (rotate == 0 || rotate == 180) {
            area = new RectF(vertex.x, vertex.y, vertex.x + defaultWidth, vertex.y + defaultWidth * options.outHeight / options.outWidth);
        } else {
            area = new RectF(vertex.x, vertex.y, vertex.x + defaultWidth, vertex.y + defaultWidth * options.outWidth / options.outHeight);
        }
        RectF size = readerView.getPageNoZoomSize(pageView.getPageNum());
        area.set(page.convertRectToPage(readerView.isCropMode(), size.width(), size.height(), area));
        stampAnnotation.setRect(area);

        if (imagePath.endsWith(".png")) {
            BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
            tmpOptions.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, tmpOptions);

            if (rotate == 0) {
                stampAnnotation.updateApWithBitmap(bitmap);
            } else {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                Bitmap newBM = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
                stampAnnotation.updateApWithBitmap(newBM);
                newBM.recycle();
            }
        } else {
            stampAnnotation.setImageStamp(imagePath);
            stampAnnotation.updateAp();
        }
        pageView.addAnnotation(stampAnnotation, false);
    }

    private void addStamp(CPDFStampAnnotation stampAnnotation, CPDFReaderView readerView, CPDFPageView pageView, PointF pointF) {
        CPDFPage page = readerView.getPDFDocument().pageAtIndex(readerView.getPageNum());
        RectF pageSize = readerView.getPageNoZoomSize(readerView.getPageNum());
        RectF insertRect = stampAnnotation.getRect();
        insertRect.set(page.convertRectFromPage(readerView.isCropMode(), pageSize.width(), pageSize.height(), insertRect));
        float defaultWidth = 200F;
        PointF vertex = new PointF(pointF.x - (defaultWidth / 2), pointF.y);
        insertRect.set(vertex.x, vertex.y, vertex.x + defaultWidth, vertex.y + defaultWidth
                * Math.abs(insertRect.height() / insertRect.width()));
        stampAnnotation.setRect(page.convertRectToPage(readerView.isCropMode(), pageSize.width(),
                pageSize.height(), insertRect));
        stampAnnotation.updateAp();
        pageView.addAnnotation(stampAnnotation, false);
    }

    public void editNote(CPDFReaderView readerView, CPDFPageView pageView, CPDFAnnotation pdfAnnotation) {
        CNoteEditDialog editDialog = CNoteEditDialog.newInstance(pdfAnnotation.getContent());
        editDialog.setSaveListener(v -> {
            String content = editDialog.getContent();
            pdfAnnotation.setContent(content);
            pdfAnnotation.updateAp();
            editDialog.dismiss();
        });
        editDialog.setDeleteListener(v -> {
            pdfAnnotation.setContent("");
            editDialog.dismiss();
        });
        if (readerView != null && readerView.getContext() != null) {
            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
            if (fragmentActivity != null) {
                editDialog.show(fragmentActivity.getSupportFragmentManager(), "noteEditDialog");
            }
        }
    }


    public void showFormListEditFragment(FragmentManager fragmentManager, CPDFBaseAnnotImpl<CPDFAnnotation> cpdfBaseAnnot, CPDFPageView pageView, boolean needDefaultSelect){
        CPDFWidgetItems widgetItems = (CPDFWidgetItems) cpdfBaseAnnot.onGetAnnotation();
        CFormOptionEditFragment editFragment = CFormOptionEditFragment.newInstance(
                R.string.tools_edit_list_box,
                R.string.tools_add_items,
                R.string.tools_edit);
        editFragment.setPdfWidgetItems(widgetItems);
        editFragment.setNeedDefaultSelect(needDefaultSelect);
        editFragment.setEditListListener((widgetItems1, selectedIndexs1) -> {
            if (widgetItems1.length == 0){
                widgetItems.setOptionItems(null);
            }else {
                widgetItems.setOptionItems(widgetItems1, selectedIndexs1);
            }
            widgetItems.updateAp();
            cpdfBaseAnnot.onAnnotAttrChange();
            pageView.invalidate();

        });
        editFragment.show(fragmentManager, "editFragment");
    }

    public void showFormComboBoxEditFragment(FragmentManager fragmentManager, CPDFBaseAnnotImpl<CPDFAnnotation> cpdfBaseAnnot, CPDFPageView pageView, boolean needDefaultSelect){
        CPDFWidgetItems widgetItems = (CPDFWidgetItems) cpdfBaseAnnot.onGetAnnotation();
        CFormOptionEditFragment editFragment = CFormOptionEditFragment.newInstance(
                R.string.tools_edit_combo_box,
                R.string.tools_add_items,
                R.string.tools_edit);
        editFragment.setPdfWidgetItems(widgetItems);
        editFragment.setNeedDefaultSelect(needDefaultSelect);
        editFragment.setEditListListener((widgetItems1, selectedIndexs1) -> {
            if (widgetItems1.length == 0){
                widgetItems.setOptionItems(null);
            }else {
                widgetItems.setOptionItems(widgetItems1, selectedIndexs1);
            }
            widgetItems.updateAp();
            cpdfBaseAnnot.onAnnotAttrChange();
            pageView.invalidate();

        });
        editFragment.show(fragmentManager, "editFragment");
    }

    public void showPushButtonActionDialog(FragmentManager fragmentManager, CPDFReaderView readerView, CPDFBaseAnnotImpl<CPDFAnnotation> cpdfBaseAnnot, CPDFPageView pageView){
        CPDFPushbuttonWidget pushbuttonWidget = (CPDFPushbuttonWidget) cpdfBaseAnnot.onGetAnnotation();
        CPDFAction action = pushbuttonWidget.getButtonAction();
        int pageCount = readerView.getPDFDocument().getPageCount();
        CActionEditDialogFragment actionEditDialogFragment;
        if (action != null && action.getActionType() == CPDFAction.ActionType.PDFActionType_URI){
            CPDFUriAction uriAction = (CPDFUriAction) action;
            actionEditDialogFragment = CActionEditDialogFragment.newInstanceWithUrl(pageCount, uriAction.getUri());
        } else if (action != null && action.getActionType() == CPDFAction.ActionType.PDFActionType_GoTo) {
            CPDFGoToAction goToAction = (CPDFGoToAction) action;
            int currentPage = goToAction.getDestination(readerView.getPDFDocument()).getPageIndex() + 1;
            actionEditDialogFragment = CActionEditDialogFragment.newInstanceWithPage(pageCount, currentPage);
        } else {
            actionEditDialogFragment = CActionEditDialogFragment.newInstance(pageCount);
        }
        actionEditDialogFragment.setShowEmail(false);
        actionEditDialogFragment.setOnLinkInfoChangeListener(new CActionEditDialogFragment.COnActionInfoChangeListener() {
            @Override
            public void cancel() {

            }

            @Override
            public void createWebsiteLink(String url) {
                CPDFUriAction uriAction = new CPDFUriAction();
                uriAction.setUri(url);
                pushbuttonWidget.setButtonAction(uriAction);
                pushbuttonWidget.updateAp();
                cpdfBaseAnnot.onAnnotAttrChange();
                pageView.invalidate();
            }

            @Override
            public void createEmailLink(String email) {

            }

            @Override
            public void createPageLink(int page) {
                if (readerView.getPDFDocument() != null) {
                    CPDFDocument document = readerView.getPDFDocument();
                    CPDFGoToAction goToAction = new CPDFGoToAction();
                    float height = document.pageAtIndex(page - 1).getSize().height();
                    CPDFDestination destination = new CPDFDestination(page - 1, 0F, height, 1F);
                    goToAction.setDestination(document, destination);
                    pushbuttonWidget.setButtonAction(goToAction);
                    pushbuttonWidget.updateAp();
                    cpdfBaseAnnot.onAnnotAttrChange();
                    pageView.invalidate();
                }
            }
        });
        actionEditDialogFragment.show(fragmentManager, "actionEdit");
    }

    public void showAddReplyDialog(CPDFPageView pageView, CPDFBaseAnnotImpl annotImpl, CPDFContextMenuHelper helper, boolean jumpToDetail){
        CPDFEditReplyDialogFragment editReplyDialogFragment = CPDFEditReplyDialogFragment.addReply();
        editReplyDialogFragment.setReplyContentListener(content -> {
            CPDFReplyAnnotation replyAnnotation = annotImpl.onGetAnnotation()
                    .createReplyAnnotation();
            replyAnnotation.setTitle(helper.getAnnotationAuthor());
            replyAnnotation.setContent(content);
            if (jumpToDetail){
                showReplyDetailsDialog(pageView, annotImpl, helper);
            }
        });
        editReplyDialogFragment.show(helper.getFragmentManager(), "addReplyDialogFragment");
    }

    public void showReplyDetailsDialog(CPDFPageView pageView, CPDFBaseAnnotImpl annotImpl, CPDFContextMenuHelper helper){
        CPDFReplyDetailsDialogFragment dialogFragment = CPDFReplyDetailsDialogFragment.newInstance();
        dialogFragment.setCPDFAnnotation(annotImpl.onGetAnnotation());
        dialogFragment.setAnnotAuthor(helper.getAnnotationAuthor());
        dialogFragment.setUpdateAnnotationListListener(()->{
            pageView.deleteAnnotation(annotImpl);
            helper.dismissContextMenu();
        });
        dialogFragment.show(helper.getFragmentManager(), "ReplyDetailsDialogFragment");
    }

    public static void showPropertiesDialog(FragmentManager fragmentManager, CPDFAnnotation annotation, @Nullable CPDFPageView pageView){
        CStyleManager styleManager = new CStyleManager(annotation, pageView);
        CStyleType styleType;
        if (annotation.getType() == Type.WIDGET){
            CPDFWidget widget = (CPDFWidget) annotation;
            styleType = CTypeUtil.getFormStyleType(widget.getWidgetType());
        }else {
            styleType = CTypeUtil.getStyleType(annotation.getType());
        }
        CAnnotStyle annotStyle = styleManager.getStyle(styleType);
        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(annotStyle);
        styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
        if (pageView != null && pageView.getParentView() instanceof CPDFReaderView){
            styleManager.setDialogHeightCallback(styleDialogFragment,
                (CPDFReaderView) pageView.getParentView());
        }
        styleDialogFragment.show(fragmentManager, "propertiesDialog");
    }

    public static void showPropertiesDialog(FragmentManager fragmentManager, CPDFBaseAnnotImpl annotImpl, @Nullable CPDFPageView pageView){
        CStyleManager styleManager = new CStyleManager(annotImpl, pageView);
        CStyleType styleType;
        if (annotImpl.getAnnotType() == Type.WIDGET){
            CPDFWidget widget = (CPDFWidget) annotImpl.onGetAnnotation();
            styleType = CTypeUtil.getFormStyleType(widget.getWidgetType());
        }else {
            styleType = CTypeUtil.getStyleType(annotImpl.getAnnotType());
        }
        CAnnotStyle annotStyle = styleManager.getStyle(styleType);
        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(annotStyle);
        styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
        if (pageView != null && pageView.getParentView() instanceof CPDFReaderView){
            styleManager.setDialogHeightCallback(styleDialogFragment,
                (CPDFReaderView) pageView.getParentView());
        }
        styleDialogFragment.show(fragmentManager, "propertiesDialog");
    }

    public static void showPropertiesDialog(FragmentManager fragmentManager, CPDFEditArea editArea, @Nullable CPDFPageView pageView){
        CAnnotStyle annotStyle = null;
        CStyleManager styleManager;
        if (editArea instanceof CPDFEditTextArea){
            CPDFEditTextArea editTextArea = (CPDFEditTextArea) editArea;
            CPDFEditTextSelections textSelections = new CPDFEditTextSelections(pageView, editTextArea, ((CPDFEditTextArea) editArea).getCurrentAreaSelections());
            styleManager = new CStyleManager(textSelections, pageView);
            annotStyle = styleManager.getStyle(CStyleType.EDIT_TEXT);
        }else {
            styleManager = new CStyleManager(new CEditSelectionsProvider(null, pageView));
            annotStyle = styleManager.getStyle(CStyleType.EDIT_IMAGE);
        }
        CStyleDialogFragment dialogFragment = CStyleDialogFragment.newInstance(annotStyle);
        styleManager.setAnnotStyleFragmentListener(dialogFragment);
        if (pageView != null && pageView.getParentView() instanceof CPDFReaderView){
            styleManager.setDialogHeightCallback(dialogFragment,
                (CPDFReaderView) pageView.getParentView());
        }
        dialogFragment.show(fragmentManager, "propertiesDialog");
    }

    public static RectF getSelectionRect(CPDFReaderView readerView, CPDFPageView pageView){
        RectF annotRect = new RectF();
        CPDFDocument document = readerView.getPDFDocument();
        if (document == null) return annotRect;

        CPDFPage page = document.pageAtIndex(pageView.getPageNum());
        if (page == null) return annotRect;

        CPDFTextPage textPage = page.getTextPage();
        if (textPage == null || !textPage.isValid()) return annotRect;

        ISelectionHelper selectionHelper = pageView.getSelectionHelper();
        if (selectionHelper == null) return annotRect;

        List<CPDFTextSelection> selections = selectionHelper.getSelections();
        if (selections == null || selections.size() <= 0) return annotRect;

        RectF size = readerView.getPageNoZoomSize(pageView.getPageNum());
        if (size.isEmpty()) return annotRect;

        int len = selections.size();
        for (int i = 0; i < len; i++) {
            CPDFTextSelection textSelection = selections.get(i);
            if (textSelection == null) {
                continue;
            }
            RectF rect = new RectF(textSelection.getRectF());
            rect = page.convertRectFromPage(readerView.isCropMode(), size.width(), size.height(), rect);
            if (annotRect.isEmpty()) {
                annotRect.set(rect);
            } else {
                annotRect.union(rect);
            }
        }
        annotRect = page.convertRectToPage(readerView.isCropMode(), size.width(), size.height(), annotRect);
        return annotRect;
    }

    public static String getSelectionText(CPDFReaderView readerView, CPDFPageView pageView){

        CPDFDocument document = readerView.getPDFDocument();
        if (document == null) return "";

        CPDFPage page = document.pageAtIndex(pageView.getPageNum());
        if (page == null) return "";

        CPDFTextPage textPage = page.getTextPage();
        if (textPage == null || !textPage.isValid()) return "";

        ISelectionHelper selectionHelper = pageView.getSelectionHelper();
        if (selectionHelper == null) return "";

        List<CPDFTextSelection> selections = selectionHelper.getSelections();
        if (selections == null || selections.size() <= 0) return "";

        RectF size = readerView.getPageNoZoomSize(pageView.getPageNum());
        if (size.isEmpty()) return "";

        int len = selections.size();
        RectF annotRect = new RectF();
        RectF[] quadRects = new RectF[selections.size()];
        StringBuilder markedTextSb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            CPDFTextSelection textSelection = selections.get(i);
            if (textSelection == null) {
                continue;
            }
            RectF rect = new RectF(textSelection.getRectF());
            rect = page.convertRectFromPage(readerView.isCropMode(), size.width(), size.height(), rect);
            if (annotRect.isEmpty()) {
                annotRect.set(rect);
            } else {
                annotRect.union(rect);
            }
            quadRects[i] = new RectF(textSelection.getRectF());
            String text = textPage.getText(textSelection.getTextRange());
            if (!TextUtils.isEmpty(text)) {
                markedTextSb.append(text);
            }
        }
        return markedTextSb.toString();
    }


}
