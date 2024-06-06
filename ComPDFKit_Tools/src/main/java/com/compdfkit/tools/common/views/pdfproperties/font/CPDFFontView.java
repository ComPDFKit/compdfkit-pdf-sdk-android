/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.font;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.font.CPDFFont;
import com.compdfkit.core.font.CPDFFontName;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.views.pdfproperties.font.bean.CFontStyleItemBean;

import java.util.List;


public class CPDFFontView extends LinearLayout {

    private Spinner fontSpinner;

    private Spinner fontStyleSpinner;

    private CFontSpinnerAdapter fontAdapter;

    private CFontStyleSpinnerAdapter fontStyleSpinnerAdapter;

    private CFontChangeListener fontChangeListener;

    private CFontTypefaceListener typefaceListener;

    public CPDFFontView(Context context) {
        this(context, null);
    }

    public CPDFFontView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPDFFontView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.tools_cpdf_font_view, this);
        fontSpinner = view.findViewById(R.id.spinner_font);
        fontStyleSpinner = view.findViewById(R.id.spinner_font_style);
        if (!isInEditMode()) {
            fontAdapter = new CFontSpinnerAdapter(context);
            fontSpinner.setAdapter(fontAdapter);
            fontStyleSpinnerAdapter = new CFontStyleSpinnerAdapter(context);
            fontStyleSpinner.setAdapter(fontStyleSpinnerAdapter);
        }
    }

    private int fontCheck = 0;
    private int fontStyleCheck = 0;

    public void initFont(String psName) {
        fontCheck = 0;
        fontStyleCheck = 0;
        String familyName = CPDFFont.getFamilyName(psName);
        if (TextUtils.isEmpty(familyName)) {
            familyName = psName;
        }
        int index = fontAdapter.getPosition(familyName);
        fontSpinner.setSelection(index);
        CPDFFontName item = fontAdapter.getItem(index);
        List<CFontStyleItemBean> styleItemBeans = fontAdapter.getFontStyleList(item);
        fontStyleSpinnerAdapter.resetFontStyles(styleItemBeans);
        int styleIndex = 0;
        for (int i = 0; i < styleItemBeans.size(); i++) {
            if (styleItemBeans.get(i).getPsName().equals(psName)) {
                styleIndex = i;
                break;
            }
        }
        fontStyleSpinner.setSelection(styleIndex);

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++fontCheck > 1) {
                    CPDFFontName cpdfFontName = fontAdapter.getItem(position);
                    List<CFontStyleItemBean> list = fontAdapter.getFontStyleList(cpdfFontName);
                    if (fontChangeListener != null) {
                        fontChangeListener.font(list.get(0).getPsName());
                    }
                    fontStyleSpinnerAdapter.resetFontStyles(list);
                    resetFontStyle();
                    changeTypefaceListener(list.get(0).getPsName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fontStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++fontStyleCheck > 1) {
                    CFontStyleItemBean item = fontStyleSpinnerAdapter.getItem(position);
                    if (fontChangeListener != null) {
                        fontChangeListener.font(item.getPsName());
                    }
                    changeTypefaceListener(item.getPsName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void changeTypefaceListener(String psName) {
        if (typefaceListener != null) {
            Typeface typeface = CPDFTextAttribute.FontNameHelper.getTypeface(getContext(), psName);
            typefaceListener.typeface(typeface);
        }
    }

    private void resetFontStyle() {
        fontStyleSpinner.setSelection(0);
    }


    public void setFontChangeListener(CFontChangeListener fontChangeListener) {
        this.fontChangeListener = fontChangeListener;
    }

    public void setTypefaceListener(CFontTypefaceListener typefaceListener) {
        this.typefaceListener = typefaceListener;
    }

    public interface CFontChangeListener {
        void font(String psName);
    }


    public interface CFontTypefaceListener {
        void typeface(Typeface typeface);
    }

}
