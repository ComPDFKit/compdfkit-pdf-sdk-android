/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 */
package com.compdfkit.tools.document;

import com.compdfkit.core.document.CPDFBates;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFHeaderFooter;

/** Provides add, update, delete, and read operations for document Bates numbering. */
public class CPDFBatesManager extends CPDFHeaderFooterManager {

    @Override
    protected CPDFHeaderFooter requireHeaderFooter(CPDFDocument document) {
        if (document == null || !document.isValid()) {
            throw new IllegalArgumentException("A valid PDF document is required.");
        }
        CPDFBates bates = document.getBates();
        if (bates == null || !bates.isValid()) {
            throw new IllegalStateException("Unable to access the document Bates numbering.");
        }
        return bates;
    }
}
