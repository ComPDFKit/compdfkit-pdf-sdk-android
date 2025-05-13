package com.compdfkit.tools.annotation.pdfannotationbar.data;


import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationbar.bean.CAnnotToolBean;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;

import java.util.ArrayList;
import java.util.List;

public class CAnnotationToolDatas {

    public static List<CAnnotToolBean> getAnnotationList(CPDFViewCtrl pdfView) {
        List<CAnnotToolBean> list = new ArrayList<>();
        CStyleManager styleManager = new CStyleManager(pdfView);

        CAnnotStyle noteStyle = styleManager.getStyle(CStyleType.ANNOT_TEXT);
        list.add(new CAnnotToolBean(CAnnotationType.TEXT, R.drawable.tools_ic_annotation_note, noteStyle.getColor(), noteStyle.getOpacity()));

        CAnnotStyle highStyle = styleManager.getStyle(CStyleType.ANNOT_HIGHLIGHT);
        list.add(new CAnnotToolBean(CAnnotationType.HIGHLIGHT, R.drawable.tools_ic_annotation_highlight,
                R.drawable.tools_ic_annotation_highlight_dark, highStyle.getColor(), highStyle.getOpacity()));

        CAnnotStyle underLineStyle = styleManager.getStyle(CStyleType.ANNOT_UNDERLINE);
        list.add(new CAnnotToolBean(CAnnotationType.UNDERLINE, R.drawable.tools_ic_annotation_underline,
                R.drawable.tools_ic_annotation_underline_dark,  underLineStyle.getColor(), underLineStyle.getOpacity()));

        CAnnotStyle strikeoutStyle = styleManager.getStyle(CStyleType.ANNOT_STRIKEOUT);
        list.add(new CAnnotToolBean(CAnnotationType.STRIKEOUT, R.drawable.tools_ic_annotation_strikeout,
                R.drawable.tools_ic_annotation_strikeout_dark, strikeoutStyle.getColor(), strikeoutStyle.getOpacity()));

        CAnnotStyle squigglyStyle = styleManager.getStyle(CStyleType.ANNOT_SQUIGGLY);
        list.add(new CAnnotToolBean(CAnnotationType.SQUIGGLY, R.drawable.tools_ic_annotation_squiggly,
                R.drawable.tools_ic_annotation_squiggly_dark, squigglyStyle.getColor(), squigglyStyle.getOpacity()));

        CAnnotStyle inkStyle = styleManager.getStyle(CStyleType.ANNOT_INK);
        list.add(new CAnnotToolBean(CAnnotationType.INK, R.drawable.tools_ic_annotation_ink,
                R.drawable.tools_ic_annotation_ink_dark, inkStyle.getColor(), inkStyle.getOpacity()));

        list.add(new CAnnotToolBean(CAnnotationType.INK_ERASER, R.drawable.tools_ic_eraser));
        list.add(new CAnnotToolBean(CAnnotationType.CIRCLE, R.drawable.tools_ic_annotation_shape_circular));
        list.add(new CAnnotToolBean(CAnnotationType.SQUARE, R.drawable.tools_ic_annotation_shape_rectangle));
        list.add(new CAnnotToolBean(CAnnotationType.ARROW, R.drawable.tools_ic_annotation_shape_arrow));
        list.add(new CAnnotToolBean(CAnnotationType.LINE, R.drawable.tools_ic_annotation_shape_line));
        list.add(new CAnnotToolBean(CAnnotationType.FREETEXT, R.drawable.tools_ic_annotation_freetext));
        list.add(new CAnnotToolBean(CAnnotationType.SIGNATURE, R.drawable.tools_ic_annotation_sign));
        list.add(new CAnnotToolBean(CAnnotationType.STAMP, R.drawable.tools_ic_annotation_stamp));
        list.add(new CAnnotToolBean(CAnnotationType.PIC, R.drawable.tools_ic_annotation_pic));
        list.add(new CAnnotToolBean(CAnnotationType.LINK, R.drawable.tools_ic_annotation_link));
        list.add(new CAnnotToolBean(CAnnotationType.SOUND, R.drawable.tools_ic_annotation_sound));
        return list;
    }

}
