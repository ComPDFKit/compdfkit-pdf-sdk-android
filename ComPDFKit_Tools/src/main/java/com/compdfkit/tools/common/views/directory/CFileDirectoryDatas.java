/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.directory;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CFileDirectoryDatas {


    public static List<File> getDirectories(String rootDir){
        List<File> list = new ArrayList<>();
        File file = new File(rootDir);
        File[] files = file.listFiles(pathname -> !pathname.isHidden() && pathname.isDirectory());
        if (files == null || files.length == 0){
            return list;
        }else {
            Collections.addAll(list, files);
            Collections.sort(list, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            return list;
        }
    }


}
