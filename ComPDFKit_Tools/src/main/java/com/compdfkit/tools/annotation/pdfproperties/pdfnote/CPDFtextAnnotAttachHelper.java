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
        editDialog.setDismissListener(new CNoteEditDialog.DialogDismiss() {
            @Override
            public void onDialogDismiss() {
                String content = editDialog.getContent();
                cpdfTextAnnot.onGetAnnotation().setContent(content);
            }
        });
        editDialog.setSaveListener(v -> {
            readerView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
            readerView.setCurrentFocusedType(CPDFAnnotation.Type.UNKNOWN);
            pageView.setFocusAnnot(cpdfTextAnnot);

            String content = editDialog.getContent();
            cpdfTextAnnot.onGetAnnotation().setContent(content);
            cpdfTextAnnot.onGetAnnotation().updateAp();
            pageView.invalidate();
            editDialog.dismiss();
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
