/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.basic.fragment;

import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.activitycontracts.CPermissionResultLauncher;


public class CPermissionFragment extends Fragment {

    protected final static String[] STORAGE_PERMISSIONS = CPermissionUtil.STORAGE_PERMISSIONS;

    protected CPermissionResultLauncher permissionResultLauncher = new CPermissionResultLauncher(this);

    protected CMultiplePermissionResultLauncher multiplePermissionResultLauncher = new CMultiplePermissionResultLauncher(this);

    public boolean hasPermission(String permission){
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    protected void showPermissionsRequiredDialog(){
        CPermissionUtil.showPermissionsRequiredDialog(getParentFragmentManager(), getContext());
    }

    protected void toSelfSetting(){
        CPermissionUtil.toSelfSetting(getContext());
    }

}
