package com.compdfkit.tools.common.pdf.config;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @classname:
 * @author: LiuXiaoLong
 * @date: 2025/2/7
 * description:
 */
public class CPDFWatermarkConfig implements Serializable {

    public List<String> types = new ArrayList<>(Arrays.asList("text", "image"));

    public boolean saveAsNewFile = true;

    public String outsideBackgroundColor = "";

    public String text;

    public String image;

    public int textSize = 12;

    public String textColor = "";

    public double scale = 1.0;

    public int rotation = 0;

    public int opacity = 255;

    public boolean isFront = false;

    public boolean isTilePage = false;

    public int getOutsideBackgroundColor() throws Exception {
        try{
            return Color.parseColor(outsideBackgroundColor);
        }catch (Exception e){
            throw new Exception("outsideBackgroundColor is not a valid color");
        }
    }

    public int getTextColor() {
        try{
            return Color.parseColor(textColor);
        }catch (Exception e){
            return Color.BLACK;
        }
    }

    public static CPDFWatermarkConfig fromMap(Map<String, Object> map){
        CPDFWatermarkConfig config = new CPDFWatermarkConfig();
        if (map.containsKey("types")){
            config.types = (List<String>) map.get("types");
        }
        if (map.containsKey("saveAsNewFile")){
            config.saveAsNewFile = (boolean) map.get("saveAsNewFile");
        }
        if (map.containsKey("outsideBackgroundColor")){
            Object color = map.get("outsideBackgroundColor");
            if (color != null){
                config.outsideBackgroundColor = (String) color;
            }
        }
        if (map.containsKey("text")){
            Object text = map.get("text");
            if (text != null){
                config.text = (String) text;
            }
        }
        if (map.containsKey("image")){
            Object image = map.get("image");
            if (image != null){
                config.image = (String) image;
            }
        }
        if (map.containsKey("textSize")){
            config.textSize = getInt(map, "textSize", 30);
        }
        if (map.containsKey("textColor")){
            config.textColor = (String) map.get("textColor");
        }
        if (map.containsKey("scale")){
            Object scaleObj = map.get("scale");
            if (scaleObj instanceof Integer){
                config.scale = ((Integer) scaleObj).doubleValue();
            }else if (scaleObj instanceof Double){
                config.scale = (Double) scaleObj;
            }
        }
        if (map.containsKey("rotation")){
            config.rotation = getInt(map, "rotation", 0);
        }
        if (map.containsKey("opacity")){
            config.opacity = getInt(map, "opacity", 255);
        }
        if (map.containsKey("isFront")){
            config.isFront = (boolean) map.get("isFront");
        }
        if (map.containsKey("isTilePage")){
            config.isTilePage = (boolean) map.get("isTilePage");
        }
        return config;
    }

    private static int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    private static float getFloat(Map<String, Object> map, String key, float defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }




}
