/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider;


import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;

import java.util.LinkedHashSet;

public interface CStyleProvider {

    public void updateStyle(CAnnotStyle annotStyle);

    public void updateStyle(LinkedHashSet<CAnnotStyle> annotStyle);

    public CAnnotStyle getStyle(CStyleType type);




    default public String getAnnotStyleFontName(CAnnotStyle annotStyle){
        CLog.e("字体", "类型：" + annotStyle.getType().name() +"更新字体：" + annotStyle.getExternFontName());
        return annotStyle.getExternFontName();
    }

    default public void updateAnnotStyleFont(CAnnotStyle annotStyle, String attrFontName){
        CLog.e("字体", "类型：" + annotStyle.getType().name() +"获取到字体：" + attrFontName);
        annotStyle.setExternFontName(attrFontName);
    }
}
