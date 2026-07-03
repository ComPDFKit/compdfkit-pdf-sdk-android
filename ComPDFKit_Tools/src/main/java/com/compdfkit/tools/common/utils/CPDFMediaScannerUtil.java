/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.text.TextUtils;

import java.io.File;

public final class CPDFMediaScannerUtil {

    private static final String MIME_TYPE_PDF = "application/pdf";

    private CPDFMediaScannerUtil() {
    }

    public static void scanPdfFile(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try {
            MediaScannerConnection.scanFile(
                    context.getApplicationContext(),
                    new String[]{file.getAbsolutePath()},
                    new String[]{MIME_TYPE_PDF},
                    null
            );
        } catch (Exception ignored) {
        }
    }
}
