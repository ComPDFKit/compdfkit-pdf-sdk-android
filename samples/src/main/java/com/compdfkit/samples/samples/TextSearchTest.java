/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;


import android.graphics.Color;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFHighlightAnnotation;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextRange;
import com.compdfkit.core.page.CPDFTextSearcher;
import com.compdfkit.core.page.CPDFTextSelection;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;
import com.compdfkit.ui.textsearch.ITextSearcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TextSearchTest extends PDFSamples {

    public TextSearchTest() {
        setTitle(R.string.text_search_test_title);
        setDescription(R.string.text_search_test_desc);
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
            printHead();
            outputListener.println();
            CPDFDocument document = new CPDFDocument(context);
            document.open(FileUtils.getAssetsTempFile(context, "text.pdf"));
            String keywords = "PDF";
            // Search for all relevant text in a document based on a keyword
            List<CPDFTextRange> list = startSearch(document, keywords);
            if (list != null && list.size() > 0) {
                CPDFTextRange textRange = list.get(0);
                CPDFPage pdfPage = document.pageAtIndex(0);
                CPDFTextPage pdfTextPage = pdfPage.getTextPage();
                CPDFTextSelection[] textSelectionArr = pdfTextPage.getSelectionsByTextForRange(textRange);
                //Then, add a highlight annotation for the specific area.
                RectF annotRect = new RectF();
                CPDFHighlightAnnotation highlightAnnotation = (CPDFHighlightAnnotation) pdfPage.addAnnot(CPDFAnnotation.Type.HIGHLIGHT);
                highlightAnnotation.setColor(Color.YELLOW);
                highlightAnnotation.setAlpha((255 / 2));
                RectF[] quadRects = new RectF[textSelectionArr.length];
                StringBuilder markedTextSb = new StringBuilder();
                int len = textSelectionArr.length;
                for (int i = 0; i < len; i++) {
                    CPDFTextSelection textSelection = textSelectionArr[i];
                    if (textSelection == null) {
                        continue;
                    }
                    RectF rect = new RectF(textSelection.getRectF());
                    if (annotRect.isEmpty()) {
                        annotRect.set(rect);
                    } else {
                        annotRect.union(rect);
                    }
                    quadRects[i] = new RectF(textSelection.getRectF());
                    String text = pdfTextPage.getText(textSelection.getTextRange());
                    if (!TextUtils.isEmpty(text)) {
                        markedTextSb.append(text);
                    }
                }
                highlightAnnotation.setQuadRects(quadRects);
                highlightAnnotation.setMarkedText(markedTextSb.toString());
                highlightAnnotation.setRect(annotRect);
                highlightAnnotation.updateAp();
            }
            outputListener.println("the key `" + keywords + "` have " + list.size() + " results");
            File file = new File(outputDir(), "TextSearchTest/TextSearchResults.pdf");
            saveSamplePDF(document, file, false);
            outputListener.println("Done. Results saved in TextSearchResults.pdf");
            printFooter();
    }

    public static List<CPDFTextRange> startSearch(CPDFDocument document, String keywords) {
        ITextSearcher textSearcher = new com.compdfkit.ui.textsearch.CPDFTextSearcher(document.getContext(), document);
        if (null == textSearcher) {
            return null;
        }
        if (null == document) {
            return null;
        }
        textSearcher.setSearchConfig(keywords, CPDFTextSearcher.PDFSearchOptions.PDFSearchCaseInsensitive);
        List<CPDFTextRange> searchTextInfoList = new ArrayList<>();
        for (int i = 0; i < document.getPageCount(); i++) {
            CPDFPage page = document.pageAtIndex(i);
            CPDFTextPage textPage = page.getTextPage();
            if ((null == textPage) || !textPage.isValid()) {
                continue;
            }
            final List<CPDFTextRange> searchPageContent = textSearcher.searchKeyword(i);
            if (searchPageContent.size() > 0) {
                searchTextInfoList.addAll(searchPageContent);
            }
            page.close();
        }
        return searchTextInfoList;
    }
}
