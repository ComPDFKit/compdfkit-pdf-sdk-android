/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.interfaces;


import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import java.util.Map;

public interface COnAnnotationCreatePreparedListener {

    void prepared(CAnnotationType type, CPDFAnnotation annotation);
}
