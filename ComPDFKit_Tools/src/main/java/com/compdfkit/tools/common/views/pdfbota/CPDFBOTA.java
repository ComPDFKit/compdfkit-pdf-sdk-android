/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfbota;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({CPDFBOTA.BOOKMARKS, CPDFBOTA.OUTLINE, CPDFBOTA.THUMBNAIL, CPDFBOTA.ANNOTATION})
@Retention(RetentionPolicy.SOURCE)
public @interface CPDFBOTA {
    int BOOKMARKS = 1;

    int OUTLINE = 2;

    int THUMBNAIL = 3;

    int ANNOTATION = 4;
}
