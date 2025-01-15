/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfpic;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts;
import com.compdfkit.tools.common.utils.activitycontracts.CPermissionResultLauncher;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;

import java.io.File;


public class CImageStyleFragment extends CBasicPropertiesFragment {

    private ActivityResultLauncher<CImageResultContracts.RequestType> imageLauncher = registerForActivityResult(new CImageResultContracts(), result -> {
        if (result != null && viewModel != null) {
            File file = new File(getContext().getFilesDir(), CFileUtils.CACHE_FOLDER);
            String path = CFileUtils.copyFileToInternalDirectory(getContext(), result, file.getAbsolutePath(), "pic_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".png");
            viewModel.getStyle().setImagePath(path);
            dismissStyleDialog();
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
            imageLauncher.launch(CImageResultContracts.RequestType.PHOTO_ALBUM);
        });
        clFromCamera.setOnClickListener(v -> {
            if (!CPermissionUtil.checkManifestPermission(getContext(), Manifest.permission.CAMERA)){
                imageLauncher.launch(CImageResultContracts.RequestType.CAMERA);
            }else {
                permissionResultLauncher.launch(Manifest.permission.CAMERA, granted -> {
                    if (granted){
                        imageLauncher.launch(CImageResultContracts.RequestType.CAMERA);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
