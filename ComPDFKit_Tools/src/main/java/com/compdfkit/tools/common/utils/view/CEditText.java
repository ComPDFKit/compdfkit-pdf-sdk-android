/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnTextChangedListener;


public class CEditText extends FrameLayout {

    AppCompatEditText editText;

    AppCompatTextView tvTitle;

    AppCompatTextView tvTitleTag;

    private COnTextChangedListener textChangedListener;

    public CEditText(@NonNull Context context) {
        this(context, null);
    }

    public CEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttr(context, attrs);

    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CEditText);
        if (typedArray != null) {
            String title = typedArray.getString(R.styleable.CEditText_android_title);
            String hint = typedArray.getString(R.styleable.CEditText_android_hint);
            int imeOptions = typedArray.getInt(R.styleable.CEditText_android_imeOptions, EditorInfo.IME_ACTION_DONE);
            int inputType = typedArray.getInt(R.styleable.CEditText_android_inputType, EditorInfo.TYPE_CLASS_TEXT);
            boolean showKeyTag = typedArray.getBoolean(R.styleable.CEditText_showKeyTag, false);
            tvTitleTag.setVisibility(showKeyTag ? VISIBLE : GONE);
            tvTitle.setVisibility(TextUtils.isEmpty(title)?  GONE : VISIBLE);
            tvTitle.setText(title);
            editText.setImeOptions(imeOptions);
            editText.setInputType(inputType);
            editText.setHint(hint);
            typedArray.recycle();
        }
    }

    private void initView(Context context){
        inflate(context, R.layout.tools_layout_c_edit_text, this);
        editText = findViewById(R.id.edit_text);
        tvTitle = findViewById(R.id.tv_title);
        tvTitleTag = findViewById(R.id.tv_title_1);
        editText.addTextChangedListener(new TextWatcher() {
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
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN &&
                (event.getSource() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE &&
                (event.getButtonState() & MotionEvent.BUTTON_PRIMARY) != 0) {
                editText.requestFocus();
            }
            return false;
        });
    }

    public AppCompatEditText getEditText() {
        return editText;
    }

    public AppCompatTextView getTitle() {
        return tvTitle;
    }

    public void setTitle(String title) {
        this.tvTitle.setText(title);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener){
        editText.setOnEditorActionListener(listener);
    }

    public void addTextChangedListener(COnTextChangedListener textChangedListener){
       this.textChangedListener = textChangedListener;
    }

    public String getText() {
        if (TextUtils.isEmpty(editText.getText())){
            return "";
        }else {
            return editText.getText().toString();
        }
    }

    public void setText(String text){
        editText.setText(text);
    }


    public void setError(boolean error){
        editText.setBackgroundResource(error ? R.drawable.tools_bg_import_certificate_digital_id_error_item : R.drawable.tools_bg_import_certificate_digital_id_item);
    }
}
