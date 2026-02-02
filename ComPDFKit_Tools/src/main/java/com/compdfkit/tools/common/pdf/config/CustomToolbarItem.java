package com.compdfkit.tools.common.pdf.config;


import com.compdfkit.tools.common.views.CPDFToolBarMenuHelper;

import java.io.Serializable;

public class CustomToolbarItem implements Serializable {

    public String identifier;

    public String title;

    public String icon;

    public CPDFToolBarMenuHelper.ToolBarAction action;

}
