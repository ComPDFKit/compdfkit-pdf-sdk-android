/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfsignature;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfsignature.data.CSignatureDatas;
import com.compdfkit.tools.common.utils.dialog.CImportImageDialogFragment;
import com.compdfkit.tools.common.utils.image.CBitmapUtil;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.view.colorpicker.ColorPickerDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.font.CPDFFontView;
import com.compdfkit.tools.common.views.pdfproperties.writing.CPDFSignatureEditView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class CAddSignatureActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_TITLE = "extra_title";

    public static final String EXTRA_SHOW_NONE_TYPE = "extra_show_none_type";

    public static final String EXTRA_SCREEN_ORIENTATION = "extra_screen_orientation";

    public static final String EXTRA_HIDE_TYPEFACE = "extra_hide_typeface";

    public static final String RESULT_NONE = "result_none";

    private AppCompatImageView ivAddDrawSignature;

    private AppCompatImageView ivAddTextSignature;

    private AppCompatImageView ivAddPicSignature;

    private AppCompatImageView ivNone;

    private CToolBar cToolBar;

    private CPDFSignatureEditView writingView;

    private AppCompatEditText editText;

    private CardView cardViewImport;

    private AppCompatImageView ivImport;

    private ColorListView colorListView;

    private AppCompatButton btnSave;

    private FloatingActionButton btnClean;

    private FloatingActionButton fabAddPic;

    private ConstraintLayout clThickness;

    private CPDFFontView fontView;

    private SeekBar sbThickness;

    private Uri imageUri;

    private boolean hideTypeface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenOrientation = getIntent().getIntExtra(EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(screenOrientation);
        setContentView(R.layout.tools_properties_signature_style_add_activity);
        writingView = findViewById(R.id.writing_view);
        editText = findViewById(R.id.et_text);
        cardViewImport = findViewById(R.id.card_import_image);
        fabAddPic = findViewById(R.id.fab_add_pic);
        ivImport = findViewById(R.id.iv_image);
        ivAddDrawSignature = findViewById(R.id.iv_add_draw_signature);
        ivAddTextSignature = findViewById(R.id.iv_add_text_signature);
        ivAddPicSignature = findViewById(R.id.iv_add_pic_signature);
        ivNone = findViewById(R.id.iv_none);
        cToolBar = findViewById(R.id.tool_bar);
        colorListView = findViewById(R.id.color_list_view);
        btnSave = findViewById(R.id.btn_save);
        btnClean = findViewById(R.id.btn_clean);
        clThickness = findViewById(R.id.cl_thickness);
        sbThickness = findViewById(R.id.seek_bar_thickness);
        fontView = findViewById(R.id.font_view);
        initView();
        initListener();
        switchTab(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_add_draw_signature) {
            switchTab(0);
        } else if (v.getId() == R.id.iv_add_text_signature) {
            switchTab(1);
        } else if (v.getId() == R.id.iv_add_pic_signature || v.getId() == R.id.fab_add_pic) {
            CViewUtils.hideKeyboard(editText);
            CImportImageDialogFragment importImageDialogFragment = CImportImageDialogFragment.newInstance();
            importImageDialogFragment.setImportImageListener(uri -> {
                if (uri != null) {
                    imageUri = uri;
                    Glide.with(this)
                            .load(uri)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(ivImport);
                    switchTab(2);
                }
                importImageDialogFragment.dismiss();
            });
            importImageDialogFragment.show(getSupportFragmentManager(), "importImage");
        } else if (v.getId() == R.id.iv_tool_bar_back) {
            onBackPressed();
        } else if (v.getId() == R.id.btn_save) {
            saveBitmap();
        } else if (v.getId() == R.id.btn_clean) {
            cleanSignature();
        } else if (v.getId() == R.id.iv_none){
            Intent intent = new Intent();
            intent.putExtra(RESULT_NONE, true);
            setResult(Activity.RESULT_OK, intent);
            onBackPressed();
        }
    }

    private void initListener() {
        fabAddPic.setOnClickListener(this);
        cToolBar.setBackBtnClickListener(this);
        ivAddDrawSignature.setOnClickListener(this);
        ivAddTextSignature.setOnClickListener(this);
        ivAddPicSignature.setOnClickListener(this);
        ivNone.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        colorListView.setOnColorSelectListener(this::setSignatureColor);
        colorListView.setColorPickerClickListener(() -> {
            ColorPickerDialogFragment colorPickerDialogFragment = ColorPickerDialogFragment.newInstance(getSignatureColor());
            colorPickerDialogFragment.setColorChangeListener(color -> setSignatureColor(color));
            colorPickerDialogFragment.show(getSupportFragmentManager(), "colorPickerDialogFragment");
        });
        sbThickness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                writingView.setLineWidth(Math.max(0, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        fontView.setTypefaceListener(typeface -> {
            editText.setTypeface(typeface);
        });

    }

    private void initView() {
        writingView.setAttribute(20,  Color.BLACK, 255);
        if (getIntent().hasExtra(EXTRA_TITLE)) {
            String title = getIntent().getStringExtra(EXTRA_TITLE);
            cToolBar.setTitle(title);
        }
        if (getIntent().hasExtra(EXTRA_SHOW_NONE_TYPE)){
            boolean showNoneType = getIntent().getBooleanExtra(EXTRA_SHOW_NONE_TYPE, false);
            ivNone.setVisibility(showNoneType ? View.VISIBLE : View.GONE);
        }
        hideTypeface = getIntent().getBooleanExtra(EXTRA_HIDE_TYPEFACE, false);
        fontView.initFont("Nimbus Sans");
    }

    private void switchTab(int selectPosition) {
        switch (selectPosition) {
            case 0:
                CViewUtils.hideKeyboard(editText);
                ivAddDrawSignature.setSelected(true);
                ivAddTextSignature.setSelected(false);
                ivAddPicSignature.setSelected(false);
                writingView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
                cardViewImport.setVisibility(View.GONE);
                colorListView.setVisibility(View.VISIBLE);
                clThickness.setVisibility(View.VISIBLE);
                colorListView.setSelectColor(getSignatureColor());
                fabAddPic.setVisibility(View.GONE);
                btnClean.setVisibility(View.VISIBLE);
                fontView.setVisibility(View.GONE);
                break;
            case 1:
                CViewUtils.showKeyboard(editText);
                ivAddDrawSignature.setSelected(false);
                ivAddTextSignature.setSelected(true);
                ivAddPicSignature.setSelected(false);
                writingView.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                cardViewImport.setVisibility(View.GONE);
                colorListView.setVisibility(View.VISIBLE);
                clThickness.setVisibility(View.GONE);
                colorListView.setSelectColor(getSignatureColor());
                fabAddPic.setVisibility(View.GONE);
                btnClean.setVisibility(View.VISIBLE);
                if (!hideTypeface) {
                    fontView.setVisibility(View.VISIBLE);
                }
                break;
            default:
                CViewUtils.hideKeyboard(editText);
                ivAddDrawSignature.setSelected(false);
                ivAddTextSignature.setSelected(false);
                ivAddPicSignature.setSelected(true);
                writingView.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                clThickness.setVisibility(View.GONE);
                fontView.setVisibility(View.GONE);
                cardViewImport.setVisibility(View.VISIBLE);
                colorListView.setVisibility(View.INVISIBLE);
                fabAddPic.setVisibility(View.VISIBLE);
                btnClean.setVisibility(View.GONE);
                break;
        }
    }

    private void cleanSignature() {
        if (ivAddDrawSignature.isSelected()) {
            writingView.cancelDraw();
            writingView.invalidate();
        } else if (ivAddTextSignature.isSelected()) {
            editText.setText("");
        } else {
            ivImport.setImageBitmap(null);
            imageUri = null;
        }
    }

    private void setSignatureColor(int color) {
        if (ivAddDrawSignature.isSelected()) {
            writingView.setLineColor(color);
        } else if (ivAddTextSignature.isSelected()) {
            editText.setTextColor(color);
        }
    }

    private int getSignatureColor() {
        int color = Color.BLACK;
        if (ivAddDrawSignature.isSelected()) {
            color = writingView.getLineColor();
        } else if (ivAddTextSignature.isSelected()) {
            color = editText.getCurrentTextColor();
        }
        return color;
    }

    private void saveBitmap() {
        editText.setCursorVisible(false);
        CViewUtils.hideKeyboard(editText);
        CThreadPoolUtils.getInstance().executeIO(() -> {
            String savePath;
            if (ivAddDrawSignature.isSelected()) {
                Bitmap resultBitmap = writingView.getBitmap();
                if (resultBitmap == null){
                    return;
                }
                savePath = CSignatureDatas.saveSignatureBitmap(this, resultBitmap);
            } else if (ivAddTextSignature.isSelected()) {
                if (editText.getText() != null && editText.getText().length() > 0) {
                    editText.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    editText.buildDrawingCache();
                    Bitmap bitmap = Bitmap.createBitmap(editText.getDrawingCache());
                    Bitmap resultBitmap = CBitmapUtil.cropTransparent(bitmap);
                    savePath = CSignatureDatas.saveSignatureBitmap(this, resultBitmap);
                } else {
                    savePath = null;
                }
            } else if (ivAddPicSignature.isSelected()) {
                if (imageUri != null) {
                    savePath = CSignatureDatas.saveSignatureImage(this, imageUri);
                } else {
                    savePath = null;
                }
            }else {
                savePath = "";
            }
            runOnUiThread(()->{
                try {
                    if (!TextUtils.isEmpty(savePath)) {
                        Intent intent = new Intent();
                        intent.putExtra("file_path", savePath);
                        setResult(Activity.RESULT_OK, intent);
                        onBackPressed();
                    }
                }catch (Exception e){

                }
            });
        });
    }
}
