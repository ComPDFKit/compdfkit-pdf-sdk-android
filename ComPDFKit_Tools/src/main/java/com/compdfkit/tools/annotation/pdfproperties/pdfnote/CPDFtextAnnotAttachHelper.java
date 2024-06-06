package com.compdfkit.tools.annotation.pdfproperties.pdfnote;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFAnnotation;
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
        if (readerView.getContext() instanceof FragmentActivity) {
            editDialog.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "noteEditDialog");
        }
    }
}
