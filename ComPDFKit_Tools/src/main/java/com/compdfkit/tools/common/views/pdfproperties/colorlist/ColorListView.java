/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.colorlist;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnColorSelectListener;

import java.util.ArrayList;
import java.util.List;

public class  ColorListView extends LinearLayout {

    private boolean showColorPicker = true;

    private boolean showTitle;

    private String title;

    private List<CColorItemBean> colorList = new ArrayList<>();

    private int selectColor;

    private ColorListAdapter colorListAdapter;

    private COnColorSelectListener onColorSelectListener;

    private OnColorPickerClickListener colorPickerClickListener;

    private int colorListResId = R.array.tools_annot_normal_colors;
    private AppCompatTextView tvTitle;

    public ColorListView(Context context) {
        this(context, null);
    }

    public ColorListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init(context);
    }

    private void initAttr(Context context, AttributeSet attributeSet){
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CColorListView);
        if (typedArray != null) {
            showColorPicker = typedArray.getBoolean(R.styleable.CColorListView_tools_show_color_picker, true);
            colorListResId = typedArray.getResourceId(R.styleable.CColorListView_tools_color_list,R.array.tools_annot_normal_colors);
            colorList = getColorListByResId();
            showTitle = typedArray.getBoolean(R.styleable.CColorListView_tools_show_title, true);
            title = typedArray.getString(R.styleable.CColorListView_android_title);
            typedArray.recycle();
        }
    }

    private void init(Context context){
        inflate(context, R.layout.tools_color_list_view_layout, this);
        RecyclerView rvColor = findViewById(R.id.rv_color);
        tvTitle = findViewById(R.id.tv_color_title);
        if (!showTitle){
            tvTitle.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
        colorListAdapter = new ColorListAdapter();
        colorListAdapter.setList(colorList);
        colorListAdapter.setOnItemClickListener((adapter, view, position) -> {
            CColorItemBean bean = adapter.list.get(position);
            if (bean.isColorPicker()){
                if (colorPickerClickListener != null) {
                    colorPickerClickListener.click();
                }
            }else {
                selectColor = bean.getColor();
                colorListAdapter.selectItem(position);
                if (onColorSelectListener != null) {
                    onColorSelectListener.color(bean.getColor());
                }
            }
        });
        rvColor.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvColor.setAdapter(colorListAdapter);
    }

    private List<CColorItemBean> getColorListByResId(){
        List<CColorItemBean> list = new ArrayList<>();
        if (colorListResId != -1){
            int[] colors = getResources().getIntArray(colorListResId);
            if (colors != null && colors.length >0){
                for (int color : colors) {
                    list.add(new CColorItemBean(color));
                }
            }
        }
        if (showColorPicker){
            list.add(CColorItemBean.colorPickerItem());
        }
        return list;
    }

    public void setSelectColor(@ColorInt int color){
        this.selectColor = color;
        if (colorListAdapter != null) {
            colorListAdapter.selectByColor(color);
        }
    }

    public void setSelectIndex(int index){
        if (colorListAdapter != null){
            colorListAdapter.selectIndex(index);
            this.selectColor = colorListAdapter.getSelectColor();
        }
    }

    public void showColorPicker(boolean show){
        this.showColorPicker = show;
        if (colorListAdapter != null) {
            colorListAdapter.setList(getColorListByResId());
            setSelectColor(selectColor);
        }
    }

    public int getSelectColor(){
        return selectColor;
    }

    public void setTitle(@StringRes int titleResId){
        if (tvTitle != null) {
            tvTitle.setText(titleResId);
        }
    }

    public void setTitle(String title){
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setOnColorSelectListener(COnColorSelectListener onColorSelectListener) {
        this.onColorSelectListener = onColorSelectListener;
    }

    public void setColorPickerClickListener(OnColorPickerClickListener colorPickerClickListener) {
        this.colorPickerClickListener = colorPickerClickListener;
    }

    public interface OnColorPickerClickListener{
        void click();
    }

}
