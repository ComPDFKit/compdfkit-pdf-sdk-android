package com.compdfkit.tools.common.utils.activitycontracts;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import java.util.Map;

public class CMultiplePermissionResultLauncher extends BaseActivityResultLauncher<String[], Map<String, Boolean>> {

    public CMultiplePermissionResultLauncher(@NonNull ActivityResultCaller caller) {
        super(caller, new ActivityResultContracts.RequestMultiplePermissions());
    }
}
