/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.activitycontracts;

import android.app.Activity;
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
import com.compdfkit.tools.common.utils.date.CDateUtil;

import java.io.File;

public class CImageResultContracts extends ActivityResultContract<CImageResultContracts.RequestType, Uri> {

    public enum RequestType {

        PHOTO_ALBUM,

        CAMERA

    }

    private Uri cameraUri;

    private RequestType requestType;

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, RequestType requestType) {
        this.requestType = requestType;
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intentCamera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            File file = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                file = new File(Environment.getExternalStorageDirectory(), CFileUtils.CACHE_FOLDER + File.separator +"camera_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".png");
            }else {
                file = new File(context.getFilesDir(), CFileUtils.CACHE_FOLDER + File.separator +"camera_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".png");
            }
            file.getParentFile().mkdirs();
            cameraUri = CFileUtils.getUriBySystem(context, file);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            return intentCamera;
        }
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (requestType == RequestType.CAMERA){
            if (resultCode == Activity.RESULT_OK){
                return cameraUri;
            }else {
                return null;
            }
        }else {
            return intent != null ? intent.getData() : null;
        }
    }

}
