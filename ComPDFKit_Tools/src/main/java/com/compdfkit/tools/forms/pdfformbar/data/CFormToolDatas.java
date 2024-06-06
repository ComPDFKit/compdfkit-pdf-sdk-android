package com.compdfkit.tools.forms.pdfformbar.data;


import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.forms.pdfformbar.bean.CFormToolBean;

import java.util.ArrayList;
import java.util.List;

public class CFormToolDatas {

    public static List<CFormToolBean> getFormList() {
        List<CFormToolBean> list = new ArrayList<>();
        list.add(new CFormToolBean(CPDFWidget.WidgetType.Widget_TextField, R.drawable.tools_ic_form_textfield));
        list.add(new CFormToolBean(CPDFWidget.WidgetType.Widget_CheckBox, R.drawable.tools_ic_form_checkbox));
        list.add(new CFormToolBean(CPDFWidget.WidgetType.Widget_RadioButton, R.drawable.tools_ic_form_radiobutton));
        list.add(new CFormToolBean(CPDFWidget.WidgetType.Widget_ListBox, R.drawable.tools_ic_form_listbox));
        list.add(new CFormToolBean(CPDFWidget.WidgetType.Widget_ComboBox, R.drawable.tools_ic_form_pull_down_menu));
        list.add(new CFormToolBean(CPDFWidget.WidgetType.Widget_PushButton, R.drawable.tools_ic_form_button));
        list.add(new CFormToolBean(CPDFWidget.WidgetType.Widget_SignatureFields, R.drawable.tools_ic_form_sign));
        return list;
    }
}
