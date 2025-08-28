package com.compdfkit.tools.common.pdf.config;

import android.graphics.Color;

import java.io.Serializable;

/**
 * @classname:
 * @author: LiuXiaoLong
 * @date: 2025/2/7
 * description:
 */
public class CPDFWatermarkConfig implements Serializable {
    public boolean saveAsNewFile = true;

    public String outsideBackgroundColor = "";

    public int getOutsideBackgroundColor() throws Exception {
        try{
            return Color.parseColor(outsideBackgroundColor);
        }catch (Exception e){
            throw new Exception("outsideBackgroundColor is not a valid color");
        }
    }

}
