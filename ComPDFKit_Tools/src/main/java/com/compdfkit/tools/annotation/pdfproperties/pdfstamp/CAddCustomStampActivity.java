/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean.CTextStampBean;
import com.compdfkit.tools.annotation.pdfproperties.pdfstamp.data.CStampDatas;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.stamp.CPDFStampTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CAddCustomStampActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_THEME_ID = "extra_theme_id";
    private CToolBar cToolBar;

    private CPDFStampTextView stampTextView;

    private AppCompatEditText etText;

    private AppCompatImageView ivShapeNone;

    private AppCompatImageView ivShapeRect;

    private AppCompatImageView ivShapeLeftTriangle;

    private AppCompatImageView ivShapeRightTriangle;

    private ColorListView colorListView;

    private Switch swDate;

    private Switch swTime;

    private AppCompatButton btnSave;

    private List<View> shapeTypeViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(EXTRA_THEME_ID)) {
            int themeId = getIntent().getIntExtra(EXTRA_THEME_ID, CPDFApplyConfigUtil.getInstance().getGlobalThemeId());
            setTheme(themeId);
        } else {
            setTheme(CPDFApplyConfigUtil.getInstance().getGlobalThemeId());
        }
        setContentView(R.layout.tools_properties_stamp_style_add_custom_activity);
        cToolBar = findViewById(R.id.tool_bar);
        stampTextView = findViewById(R.id.stamp_text_view);
        btnSave = findViewById(R.id.btn_save);
        etText = findViewById(R.id.et_text);
        ivShapeNone = findViewById(R.id.iv_shape_none);
        ivShapeRect = findViewById(R.id.iv_shape_rectangle);
        ivShapeLeftTriangle = findViewById(R.id.iv_shape_left_triangle);
        ivShapeRightTriangle = findViewById(R.id.iv_shape_right_triangle);
        colorListView = findViewById(R.id.color_list_view);
        swDate = findViewById(R.id.sw_date);
        swTime = findViewById(R.id.sw_time);
        shapeTypeViews = Arrays.asList(ivShapeNone, ivShapeRect, ivShapeLeftTriangle, ivShapeRightTriangle);
        colorListView.setSelectIndex(1);
        initListener();
    }

    private void initListener() {
        ivShapeRect.setSelected(true);
        ivShapeNone.setOnClickListener(this);
        ivShapeRect.setOnClickListener(this);
        ivShapeLeftTriangle.setOnClickListener(this);
        ivShapeRightTriangle.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        cToolBar.setBackBtnClickListener(v -> onBackPressed());
        colorListView.setOnColorSelectListener(color -> stampTextView.setColor(color));
        swDate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            stampTextView.setDateSwitch(isChecked);
            checkEnableSaveBtn();
        });
        swTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            stampTextView.setTimeSwitch(isChecked);
            checkEnableSaveBtn();
        });
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() <= 0) {
                    stampTextView.setContent("");
                } else {
                    stampTextView.setContent(s.toString());
                }
                checkEnableSaveBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_shape_none) {
            switchShapeType(ivShapeNone);
            stampTextView.setShape(CPDFStampAnnotation.TextStampShape.TEXTSTAMP_NONE);
        } else if (v.getId() == R.id.iv_shape_rectangle) {
            switchShapeType(ivShapeRect);
            stampTextView.setShape(CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RECT);
        } else if (v.getId() == R.id.iv_shape_left_triangle) {
            switchShapeType(ivShapeLeftTriangle);
            stampTextView.setShape(CPDFStampAnnotation.TextStampShape.TEXTSTAMP_LEFT_TRIANGLE);
        } else if (v.getId() == R.id.iv_shape_right_triangle) {
            switchShapeType(ivShapeRightTriangle);
            stampTextView.setShape(CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RIGHT_TRIANGLE);
        } else if (v.getId() == R.id.btn_save) {
            saveCustomStamp();
        }
    }

    private void switchShapeType(AppCompatImageView shapeView) {
        for (View shapeTypeView : shapeTypeViews) {
            shapeTypeView.setSelected(shapeView.getId() == shapeTypeView.getId());
        }
    }

    private void checkEnableSaveBtn() {
//        btnSave.setEnabled(
//                stampTextView.getDateSwitch() ||
//                stampTextView.getTimeSwitch());
    }

    private void saveCustomStamp() {
        String content = "";
        if (!TextUtils.isEmpty(stampTextView.getContent())){
            content = stampTextView.getContent();
        }else {
            if (stampTextView.getDateSwitch() || stampTextView.getTimeSwitch()){
                content = "";
            }else {
                content = "StampText";
            }
        }
        CTextStampBean textStampBean = new CTextStampBean(
                stampTextView.getBgColor(),
                stampTextView.getTextColor(),
                stampTextView.getLineColor(),
                content,
                stampTextView.getDateStr(),
                stampTextView.getDateSwitch(),
                stampTextView.getTimeSwitch(),
                stampTextView.getShapeId(),
                stampTextView.getTextStampColorId()
        );
        String savePath = CStampDatas.saveTextStamp(this, textStampBean);
        if (savePath != null) {
            Intent intent = new Intent();
            intent.putExtra("file_path", savePath);
            setResult(Activity.RESULT_OK, intent);
            onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("editText", TextUtils.isEmpty(etText.getText()) ? "" : etText.getText().toString());
        outState.putInt("color", colorListView.getSelectColor());
        outState.putInt("shape", stampTextView.getShape().id);
        outState.putBoolean("showDate", stampTextView.getDateSwitch());
        outState.putBoolean("showTime", stampTextView.getTimeSwitch());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        String content = savedInstanceState.getString("editText", "");
        int color = savedInstanceState.getInt("color", Color.TRANSPARENT);
        int shapeId = savedInstanceState.getInt("shape", CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RECT.id);
        boolean showDate = savedInstanceState.getBoolean("showDate", false);
        boolean showTime = savedInstanceState.getBoolean("showTime", false);
        etText.setText(content);
        colorListView.setSelectColor(color);
        CPDFStampAnnotation.TextStampShape shape = CPDFStampAnnotation.TextStampShape.valueOf(shapeId);
        stampTextView.setShape(shape);
        stampTextView.setColor(color);
        switch (shape) {
            case TEXTSTAMP_RECT:
                switchShapeType(ivShapeRect);
                break;
            case TEXTSTAMP_NONE:
                switchShapeType(ivShapeNone);
                break;
            case TEXTSTAMP_LEFT_TRIANGLE:
                switchShapeType(ivShapeLeftTriangle);
                break;
            case TEXTSTAMP_RIGHT_TRIANGLE:
                switchShapeType(ivShapeRightTriangle);
                break;
            default:break;
        }
        swDate.setChecked(showDate);
        swTime.setChecked(showTime);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
