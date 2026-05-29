/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.utils.activitycontracts;

import android.net.Uri;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;


public class CImageResultLauncher extends BaseActivityResultLauncher<CImageResultContracts.Request, Uri> {

    public CImageResultLauncher(@NonNull ActivityResultCaller caller) {
        super(caller, new CImageResultContracts());
    }

    public void launch(CImageResultContracts.RequestType requestType, @NonNull ActivityResultCallback<Uri> callback) {
        launch(new CImageResultContracts.Request(requestType), callback);
    }

    public void launch(CImageResultContracts.RequestType requestType) {
        launch(new CImageResultContracts.Request(requestType));
    }
}
