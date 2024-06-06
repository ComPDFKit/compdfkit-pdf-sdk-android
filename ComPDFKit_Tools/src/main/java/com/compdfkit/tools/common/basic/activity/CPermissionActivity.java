package com.compdfkit.tools.common.basic.activity;


import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.activitycontracts.CPermissionResultLauncher;


public class CPermissionActivity extends AppCompatActivity {

    protected final static String[] STORAGE_PERMISSIONS = CPermissionUtil.STORAGE_PERMISSIONS;

    protected CPermissionResultLauncher permissionResultLauncher = new CPermissionResultLauncher(this);

    protected CMultiplePermissionResultLauncher multiplePermissionResultLauncher = new CMultiplePermissionResultLauncher(this);

    public boolean hasPermission(String permission){
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    protected void showPermissionsRequiredDialog(){
        CPermissionUtil.showPermissionsRequiredDialog(getSupportFragmentManager(), this);
    }

    protected void toSelfSetting(){
        CPermissionUtil.toSelfSetting(this);
    }

}


