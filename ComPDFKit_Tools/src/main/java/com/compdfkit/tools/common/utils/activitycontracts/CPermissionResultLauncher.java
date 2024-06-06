package com.compdfkit.tools.common.utils.activitycontracts;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

/**
 * @classname:
 * @author: LiuXiaoLong
 * @date: 2023/8/30
 * description:
 */
public class CPermissionResultLauncher extends BaseActivityResultLauncher<String, Boolean>{
    public CPermissionResultLauncher(@NonNull ActivityResultCaller caller) {
        super(caller, new ActivityResultContracts.RequestPermission());
    }
}

