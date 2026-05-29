/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfpic;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts;
import com.compdfkit.tools.common.utils.activitycontracts.CPermissionResultLauncher;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;

import java.io.File;


public class CImageStyleFragment extends CBasicPropertiesFragment {

    private ActivityResultLauncher<CImageResultContracts.Request> imageLauncher = registerForActivityResult(new CImageResultContracts(), result -> {
        if (result != null && viewModel != null) {
            CUriUtil.logImageUriInfo(getContext(), "pic result uri", result);
            File file = new File(getContext().getFilesDir(), CFileUtils.CACHE_FOLDER);
            String ext = CUriUtil.getImageExtension(getContext(), result);
            String path = CFileUtils.copyFileToInternalDirectory(getContext(), result, file.getAbsolutePath(), "pic_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ext);
            CLog.e("ImageAnnot", "pic copied, uri=" + result + ", ext=" + ext + ", path=" + path);
            CUriUtil.logImageFileInfo("pic copied file", path);
            if (!TextUtils.isEmpty(path)) {
                CLog.e("ImageAnnot", "pic setImagePath before, path=" + path);
                viewModel.getStyle().setImagePath(path);
                CLog.e("ImageAnnot", "pic setImagePath after, path=" + path);
                dismissStyleDialog();
            } else {
                CLog.e("ImageAnnot", "pic copy failed, uri=" + result);
            }
        } else {
            CLog.e("ImageAnnot", "pic result ignored, result=" + result + ", viewModel=" + viewModel);
        }
    });

    protected CPermissionResultLauncher permissionResultLauncher = new CPermissionResultLauncher(this);

    public static CImageStyleFragment newInstance() {
        return new CImageStyleFragment();
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_import_image_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        ConstraintLayout clFromAlbum = rootView.findViewById(R.id.cl_from_album);
        ConstraintLayout clFromCamera = rootView.findViewById(R.id.cl_from_camera);
        clFromAlbum.setOnClickListener(v -> {
            imageLauncher.launch(new CImageResultContracts.Request(CImageResultContracts.RequestType.PHOTO_ALBUM));
        });
        clFromCamera.setOnClickListener(v -> {
            if (!CPermissionUtil.checkManifestPermission(getContext(), Manifest.permission.CAMERA)){
                launchCamera();
            }else {
                permissionResultLauncher.launch(Manifest.permission.CAMERA, granted -> {
                    if (granted){
                        launchCamera();
                    } else {
                        if (getActivity() != null) {
                            if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                                CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getActivity());
                            }
                        }
                    }
                });
            }
        });
    }

    private void launchCamera() {
        Uri outputUri = CImageResultContracts.createCameraOutputUri(getContext());
        if (outputUri == null) {
            CLog.e("ImageAnnot", "pic camera output uri is null");
            return;
        }
        imageLauncher.launch(new CImageResultContracts.Request(CImageResultContracts.RequestType.CAMERA, outputUri));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
