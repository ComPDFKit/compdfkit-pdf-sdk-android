/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.directory;


import static com.compdfkit.tools.common.utils.CPermissionUtil.VERSION_R;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.compdfkit.tools.common.utils.CPermissionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CFileDirectoryDatas {


    public static List<File> getDirectories(Context context, String rootDir){
        List<File> list = new ArrayList<>();
        File file = new File(rootDir);
        boolean hasStoragePermission = CPermissionUtil.hasStoragePermissions(context);
        if (Build.VERSION.SDK_INT >= VERSION_R){
            if (!CPermissionUtil.checkManifestPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE) ||
                    !Environment.isExternalStorageManager()){
                hasStoragePermission = false;
            }
        }
        List<String> publicDirectoryList = publicDirectoryList();
        boolean finalHasStoragePermission = hasStoragePermission;
        File[] files = file.listFiles(pathname -> {
            if (finalHasStoragePermission){
                return !pathname.isHidden() && pathname.isDirectory();
            }else {
                boolean isPublicDirectory =false;
                for (String s : publicDirectoryList) {
                    if (pathname.getAbsolutePath().startsWith(s)){
                        isPublicDirectory = true;
                        break;
                    }
                }
                return !pathname.isHidden() && pathname.isDirectory() && isPublicDirectory;
            }
        });
        if (files == null || files.length == 0){
            return list;
        }else {
            Collections.addAll(list, files);
            Collections.sort(list, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            return list;
        }
    }

    public static List<String> publicDirectoryList(){
        List<String> list = new ArrayList<>();
        list.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        list.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        list.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        return list;
    }

}
