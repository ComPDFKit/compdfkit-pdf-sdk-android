/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.textfields;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnTextChangedListener;


public class CTextFieldsView extends FrameLayout {

    private AppCompatEditText etText;

    private COnTextChangedListener textChangedListener;

    public CTextFieldsView(@NonNull Context context) {
        this(context, null);
    }

    public CTextFieldsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CTextFieldsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
       LayoutInflater.from(context).inflate(R.layout.tools_properties_text_fields_layout, this);
       etText = findViewById(R.id.et_text);
       etText.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (textChangedListener != null) {
                   textChangedListener.onTextChanged(s, start, before, count);
               }
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
    }

    public void setText(String text){
        etText.setText(text);
    }

    public String getFieldName(){
        return TextUtils.isEmpty(etText.getText()) ? "" : etText.getText().toString();
    }

    public void setTextChangedListener(COnTextChangedListener textChangedListener) {
        this.textChangedListener = textChangedListener;
    }
}
