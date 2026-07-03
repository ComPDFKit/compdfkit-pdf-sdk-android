package com.compdfkit.tools.annotation.pdfproperties.pdfnote;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.ui.proxy.CPDFTextAnnotImpl;
import com.compdfkit.ui.proxy.attach.CPDFTextAnnotAttachHelper;
import com.compdfkit.ui.reader.CPDFReaderView;


/**
 * Create a helper class for text annotations that displays a pop-up window for adding text content
 * when a text (note) annotation is added and the screen is tapped
 * <p/>
 * pdfView.getCPdfReaderView().getAnnotImplRegistry()
 * .registAttachHelper(CPDFTextAnnotation.class, CPDFtextAnnotAttachHelper.class);
 * <p/>
 * @see CPDFReaderView#getAnnotImplRegistry()
 *
 */
public class CPDFtextAnnotAttachHelper extends CPDFTextAnnotAttachHelper {
    @Override
    protected void onAddTextAnnot(CPDFTextAnnotImpl cpdfTextAnnot) {
        cpdfTextAnnot.setFocused(true);
        if (!shouldAutoShowNoteEditDialog()) {
            CPDFDocumentFragment documentFragment = getDocumentFragment();
            if (documentFragment != null) {
                readerView.setCurrentFocusedType(CPDFAnnotation.Type.UNKNOWN);
                readerView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
                pageView.setFocusAnnot(cpdfTextAnnot);
                documentFragment.annotationToolbar.annotationCreatePreparedListenersChanged(
                        CAnnotationType.TEXT, cpdfTextAnnot.onGetAnnotation());
                return;
            }
        }
        CNoteEditDialog editDialog = CNoteEditDialog.newInstance("");
        editDialog.setDismissListener(() -> {
            String content = editDialog.getContent();
            cpdfTextAnnot.onGetAnnotation().setContent(content);
        });
        editDialog.setSaveListener(v -> {
            String content = editDialog.getContent();
            cpdfTextAnnot.onGetAnnotation().setContent(content);
            editDialog.dismiss();

            readerView.setCurrentFocusedType(CPDFAnnotation.Type.UNKNOWN);
            readerView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
            pageView.setFocusAnnot(cpdfTextAnnot);
        });
        editDialog.setDeleteListener(v -> {
            pageView.deleteAnnotation(cpdfTextAnnot);
            editDialog.dismiss();
        });
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity != null) {
            editDialog.show(fragmentActivity.getSupportFragmentManager(), "noteEditDialog");
        }
    }

    private boolean shouldAutoShowNoteEditDialog() {
        CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
        return configuration == null || configuration.annotationsConfig == null ||
                configuration.annotationsConfig.autoShowNoteEditDialog;
    }

    private CPDFDocumentFragment getDocumentFragment(){
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity == null) {
            return null;
        }
        Fragment fragment = fragmentActivity.getSupportFragmentManager().findFragmentByTag("documentFragment");
        if (fragment instanceof CPDFDocumentFragment){
            return (CPDFDocumentFragment) fragment;
        }
        for (Fragment item : fragmentActivity.getSupportFragmentManager().getFragments()) {
            if (item instanceof CPDFDocumentFragment) {
                return (CPDFDocumentFragment) item;
            }
        }
        return null;
    }
}
