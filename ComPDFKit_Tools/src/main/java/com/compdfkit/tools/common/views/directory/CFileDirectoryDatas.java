/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.directory;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CFileDirectoryDatas {


    public static List<File> getDirectories(Context context, String rootDir){
        List<File> list = new ArrayList<>();
        File file = new File(rootDir);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && file.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            for (String path : publicRootDirectoryList()) {
                File publicDirectory = new File(path);
                if (!containsDirectory(list, publicDirectory)) {
                    list.add(publicDirectory);
                }
            }
            Collections.sort(list, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            return list;
        }
        boolean hasStoragePermission = CPermissionUtil.hasStoragePermissions(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasStoragePermission = false;
        }
        List<String> publicDirectoryList = publicDirectoryList();
        boolean finalHasStoragePermission = hasStoragePermission;
        File[] files = file.listFiles(pathname -> {
            if (finalHasStoragePermission){
                return !pathname.isHidden() && pathname.isDirectory();
            }else {
                boolean isPublicDirectory =false;
                for (String s : publicDirectoryList) {
                    if (isSameOrChildDirectory(pathname.getAbsolutePath(), s)){
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
        list.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        list.add(CPDFStorageManager.getLegacyPublicDirectoryPath(CPDFStorageManager.getDownloadRelativePath()));
        list.add(CPDFStorageManager.getLegacyPublicDirectoryPath(CPDFStorageManager.getImageRelativePath()));
        list.add(CPDFStorageManager.getLegacyPublicDirectoryPath(CPDFStorageManager.getCertificateRelativePath()));
        return list;
    }

    private static List<String> publicRootDirectoryList() {
        List<String> list = new ArrayList<>();
        list.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        list.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        return list;
    }

    private static boolean containsDirectory(List<File> list, File directory) {
        for (File file : list) {
            if (file.getAbsolutePath().equals(directory.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSameOrChildDirectory(String path, String parentPath) {
        if (path == null || parentPath == null) {
            return false;
        }
        return path.equals(parentPath) || path.startsWith(parentPath + File.separator);
    }

}
