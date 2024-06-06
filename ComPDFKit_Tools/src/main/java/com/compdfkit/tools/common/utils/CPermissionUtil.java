/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CPermissionUtil {

    public static final int VERSION_TIRAMISU = 33;
    public static final int VERSION_S_V2 = 32;
    public static final int VERSION_R = 30;

    public final static String[] STORAGE_PERMISSIONS = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static boolean hasStoragePermissions(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= VERSION_TIRAMISU) {
            if (CPermissionUtil.checkManifestPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
                return Environment.isExternalStorageManager();
            }else {
                return true;
            }
        }
        if (Build.VERSION.SDK_INT >= VERSION_R) {
            boolean hasPermission = true;
            for (String perm : STORAGE_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(context, perm)
                        != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            }
            if (CPermissionUtil.checkManifestPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
                return Environment.isExternalStorageManager() && hasPermission;
            }else {
                return hasPermission;
            }
        }
        for (String perm : STORAGE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void openManageAllFileAppSettings(Context context){
        try{
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:"+context.getPackageName()));
            context.startActivity(intent);
        }catch (Exception e){
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            context.startActivity(intent);
        }
    }

    public static void toSelfSetting(Context context) {
        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            mIntent.setAction(Intent.ACTION_VIEW);
            mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            mIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(mIntent);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static void showPermissionsRequiredDialog(FragmentManager fragmentManager, Context context){
        CAlertDialog alertDialog = CAlertDialog.newInstance(context.getString(R.string.tools_permission_tips_title), context.getString(R.string.tools_permission_tips_msg));
        alertDialog.setCancelable(false);
        alertDialog.setConfirmClickListener(v -> {
            CPermissionUtil.toSelfSetting(context);
            alertDialog.dismiss();
        });
        alertDialog.setCancelClickListener(v -> alertDialog.dismiss());
        alertDialog.show(fragmentManager, "permissionRequiredDialog");
    }

    public static List<String> getManifestPermissions(Context context){
        ArrayList<String> requestedPermissionsArrayList = new ArrayList<String>();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions;
            }

            if (requestedPermissions != null && requestedPermissions.length > 0) {
                List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
                requestedPermissionsArrayList.addAll(requestedPermissionsList);
            }
            return requestedPermissionsArrayList;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return requestedPermissionsArrayList;
    }

    public static boolean checkManifestPermission(Context context, String permission) {
        List<String> manifestPermissions = getManifestPermissions(context);
        return manifestPermissions.contains(permission);
    }


}
