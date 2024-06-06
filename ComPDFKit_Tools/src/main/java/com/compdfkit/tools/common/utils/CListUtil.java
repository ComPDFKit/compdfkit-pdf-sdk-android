package com.compdfkit.tools.common.utils;


import java.util.ArrayList;
import java.util.List;

public class CListUtil {

    public static <T> List<T> distinct(List<T> list) {
        List<T> listNew = new ArrayList<T>();
        for (T item : list) {
            if (!listNew.contains(item)) {
                listNew.add(item);
            }
        }
        return listNew;
    }

}
