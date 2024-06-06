/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.dialog;

import android.Manifest;
import android.net.Uri;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts;
import com.compdfkit.tools.common.utils.activitycontracts.CPermissionResultLauncher;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class CImportImageDialogFragment extends CBasicBottomSheetDialogFragment {

    private COnImportImageListener importImageListener;

    private ActivityResultLauncher<CImageResultContracts.RequestType> imageLauncher = registerForActivityResult(new CImageResultContracts(), result -> {
        if (importImageListener != null) {
            importImageListener.image(result);
        }
    });

    protected CPermissionResultLauncher permissionResultLauncher = new CPermissionResultLauncher(this);

    public static CImportImageDialogFragment newInstance() {
        return new CImportImageDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
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
            permissionResultLauncher.launch(Manifest.permission.CAMERA, granted -> {
                if (granted){
                    imageLauncher.launch(CImageResultContracts.RequestType.CAMERA);
                }else {
                    if (getActivity() != null) {
                        if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)){
                            CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getActivity());
                        }
                    }
                }
            });
        });
    }

    @Override
    protected void onViewCreate() {

    }

    public void setImportImageListener(COnImportImageListener importImageListener) {
        this.importImageListener = importImageListener;
    }

    public interface COnImportImageListener{
        void image(@Nullable Uri imageUri);
    }
}
