package com.compdfkit.samples.samples;

import android.graphics.Color;
import android.graphics.RectF;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFFreetextAnnotation;
import com.compdfkit.core.annotation.CPDFReplyAnnotation;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;

public class AnnotationReplyTest extends PDFSamples {

    public AnnotationReplyTest(){
        setTitle(R.string.annotation_reply_title);
        setDescription(R.string.annotation_reply_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        printDividingLine();
        CPDFDocument document = CPDFDocument.createDocument(context);
        document.insertBlankPage(0, 595, 842);

        // Insert the free text annotation into the first page of the PDF document.
        CPDFPage page = document.pageAtIndex(0);
        CPDFFreetextAnnotation freetextAnnotation = (CPDFFreetextAnnotation) page.addAnnot(CPDFAnnotation.Type.FREETEXT);
        RectF size = page.getSize();
        RectF freeText1Rect = page.convertRectToPage(false, size.width(), size.height(), new RectF(10, 20, 160, 100));
        freetextAnnotation.setRect(freeText1Rect);
        freetextAnnotation.setContent("FreeText Annotation Reply Test");
        CPDFTextAttribute textAttribute = new CPDFTextAttribute(CPDFTextAttribute.FontNameHelper.obtainFontName(
                CPDFTextAttribute.FontNameHelper.FontType.Courier, false, false
        ), 12, Color.RED);
        freetextAnnotation.setFreetextDa(textAttribute);
        // set text color opacity
        freetextAnnotation.setAlpha(255);
        freetextAnnotation.setTitle("ComPDFKit");
        freetextAnnotation.updateAp();

        // ----------------------------------------
        // Samples1 : create annotation reply info
        if (freetextAnnotation.getAllReplyAnnotations() == null || freetextAnnotation.getAllReplyAnnotations().length == 0) {
            outputListener.println("Annotation Type:" + freetextAnnotation.getType().name());
            // -----------------------
            CPDFReplyAnnotation replyAnnotation = freetextAnnotation.createReplyAnnotation();
            replyAnnotation.setMarkedAnnotState(CPDFAnnotation.MarkState.MARKED);
            replyAnnotation.setReviewAnnotState(CPDFAnnotation.ReviewState.REVIEW_COMPLETED);
            replyAnnotation.setTitle("Youna");
            replyAnnotation.setContent("Hello ComPDFKit");
            outputListener.println("Create Reply Annotation:");
            outputListener.println("Title:" + replyAnnotation.getTitle());
            outputListener.println("Content:" + replyAnnotation.getContent());
            outputListener.println("ReviewState:" + replyAnnotation.getReviewAnnotState().name());
            outputListener.println("MarkedState:" + replyAnnotation.getMarkedAnnotState().name());
            outputListener.println("Create Reply Annotation End !!!");
            printDividingLine();

            // -----------------------
            CPDFReplyAnnotation replyAnnotation1 = freetextAnnotation.createReplyAnnotation();
            replyAnnotation1.setMarkedAnnotState(CPDFAnnotation.MarkState.UNMARKED);
            replyAnnotation1.setReviewAnnotState(CPDFAnnotation.ReviewState.REVIEW_ACCEPTED);
            replyAnnotation1.setTitle("C-Long");
            replyAnnotation1.setContent("Hello World");
            outputListener.println("Create Reply Annotation:");
            outputListener.println("Title:" + replyAnnotation1.getTitle());
            outputListener.println("Content:" + replyAnnotation1.getContent());
            outputListener.println("ReviewState:" + replyAnnotation1.getReviewAnnotState().name());
            outputListener.println("MarkedState:" + replyAnnotation1.getMarkedAnnotState().name());
            outputListener.println("Create Reply Annotation End !!!");
            printDividingLine();
        }

        // Samples 2: get all reply annotations
        CPDFReplyAnnotation[] replyAnnotations = freetextAnnotation.getAllReplyAnnotations();
        outputListener.println("Get All Reply Annotations: ");
        outputListener.println("Size: " + replyAnnotations.length);
        outputListener.println("Annotation Type:" + freetextAnnotation.getType().name());
        if (replyAnnotations != null) {
            for (CPDFReplyAnnotation replyAnnotation : replyAnnotations) {
                printDividingLine();
                outputListener.println("Title:" + replyAnnotation.getTitle());
                outputListener.println("Content:" + replyAnnotation.getContent());
                outputListener.println("ReviewState:" + replyAnnotation.getReviewAnnotState().name());
                outputListener.println("MarkedState:" + replyAnnotation.getMarkedAnnotState().name());
            }
        }
        saveSamplePDF(document, new File(outputDir(), "AnnotationTest/AnnotationReplyTest.pdf"), true);
        printFooter();
    }
}
