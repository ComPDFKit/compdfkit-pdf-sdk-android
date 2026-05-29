/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.activitycontracts;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.date.CDateUtil;

import java.io.File;

public class CImageResultContracts extends ActivityResultContract<CImageResultContracts.Request, Uri> {

    public enum RequestType {

        PHOTO_ALBUM,

        CAMERA

    }

    private Uri cameraUri;

    private RequestType requestType;

    public static class Request {

        private final RequestType requestType;

        private final Uri outputUri;

        public Request(RequestType requestType) {
            this(requestType, null);
        }

        public Request(RequestType requestType, Uri outputUri) {
            this.requestType = requestType;
            this.outputUri = outputUri;
        }

        public RequestType getRequestType() {
            return requestType;
        }

        public Uri getOutputUri() {
            return outputUri;
        }
    }

    public static Uri createCameraOutputUri(@NonNull Context context) {
        File file;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            file = new File(Environment.getExternalStorageDirectory(), CFileUtils.CACHE_FOLDER + File.separator +"camera_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".jpg");
        }else {
            file = new File(context.getFilesDir(), CFileUtils.CACHE_FOLDER + File.separator +"camera_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".jpg");
        }
        file.getParentFile().mkdirs();
        Uri uri = CFileUtils.getUriBySystem(context, file);
        CLog.e("ImageAnnot", "camera output uri created, uri=" + uri);
        return uri;
    }

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Request request) {
        this.requestType = request.getRequestType();
        if (requestType == RequestType.PHOTO_ALBUM) {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.setAction(MediaStore.ACTION_PICK_IMAGES);
            } else {
                intent.setAction(Intent.ACTION_PICK);
            }
            intent.setType("image/*");
            return intent;
        } else {
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraUri = request.getOutputUri();
            if (cameraUri == null) {
                cameraUri = createCameraOutputUri(context);
            }
            if (cameraUri == null) {
                CLog.e("ImageAnnot", "camera createIntent, cameraUri is null");
                return intentCamera;
            }
            intentCamera.setClipData(ClipData.newUri(context.getContentResolver(), "camera_output", cameraUri));
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            CLog.e("ImageAnnot", "camera createIntent, outputUri=" + cameraUri);
            return intentCamera;
        }
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        CLog.e("ImageAnnot", "parseResult, requestType=" + requestType
                + ", resultCode=" + resultCode
                + ", intentData=" + (intent != null ? intent.getData() : null)
                + ", cameraUri=" + cameraUri);
        if (resultCode != Activity.RESULT_OK) {
            return null;
        }
        if (requestType == RequestType.CAMERA){
            return cameraUri;
        }else {
            return intent != null ? intent.getData() : null;
        }
    }

}
