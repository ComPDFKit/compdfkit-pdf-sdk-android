/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.watermark.pdfproperties;


import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CImageResultContracts;
import com.compdfkit.tools.common.utils.activitycontracts.CPermissionResultLauncher;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.view.colorpicker.widget.ColorPickerView;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CWatermarkImageStyleFragment extends CBasicPropertiesFragment implements View.OnClickListener,
        ColorPickerView.COnColorChangeListener,ColorPickerView.COnColorAlphaChangeListener {

    private CSliderBar opacitySliderBar;

    private AppCompatImageView ivFromCamera;

    private AppCompatImageView ivFromAlbum;

    private Spinner pageRangeSpinner;

    private Switch swTile;

    private AppCompatImageView ivLocationTop;

    private AppCompatImageView ivLocationBottom;

    protected CPermissionResultLauncher permissionResultLauncher = new CPermissionResultLauncher(this);

    private ActivityResultLauncher<CImageResultContracts.RequestType> imageLauncher = registerForActivityResult(new CImageResultContracts(), result -> {
        if (result != null && viewModel != null) {
            File file = new File(getContext().getFilesDir(), CFileUtils.CACHE_FOLDER);
            String path = CFileUtils.copyFileToInternalDirectory(getContext(), result, file.getAbsolutePath(), "pic_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".png");
            viewModel.getStyle().setImagePath(path);
            dismissStyleDialog();
        }
    });

    @Override
    protected int layoutId() {
        return R.layout.tools_cpdf_security_watermark_image_style_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        opacitySliderBar = rootView.findViewById(R.id.slider_bar);
        swTile = rootView.findViewById(R.id.sw_tile);
        pageRangeSpinner = rootView.findViewById(R.id.spinner_page_range);
        ivLocationTop = rootView.findViewById(R.id.iv_location_top);
        ivLocationBottom = rootView.findViewById(R.id.iv_location_bottom);
        ivFromAlbum = rootView.findViewById(R.id.iv_from_album);
        ivFromCamera = rootView.findViewById(R.id.iv_from_camera);
        ivLocationTop.setOnClickListener(this);
        ivLocationBottom.setOnClickListener(this);
        ivFromAlbum.setOnClickListener(this);
        ivFromCamera.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CAnnotStyle annotStyle = viewModel.getStyle();
        if (annotStyle != null) {
            Map<String, Object> extraMap = annotStyle.getCustomExtraMap();
            CPageRange pageRange = CPageRange.AllPages;
            if (extraMap.containsKey("pageRange")){
                pageRange = CPageRange.valueOf((String) extraMap.get("pageRange"));
            }
            // init pageRange spinner adapter
            List<CPageRange> pageRanges = Arrays.asList(CPageRange.AllPages, CPageRange.CurrentPage);
            CWatermarkPageRangeAdapter pageRangeAdapter = new CWatermarkPageRangeAdapter(getContext(), pageRanges, pageRange);
            pageRangeSpinner.setAdapter(pageRangeAdapter);
            pageRangeSpinner.setSelection(pageRangeAdapter.getSelectItemIndex());
            pageRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CPageRange pageRange = pageRangeAdapter.list.get(position);
                    pageRangeAdapter.setSelectItem(pageRange);
                    if (viewModel != null && viewModel.getStyle() != null) {
                        Map<String, Object> extraMap = viewModel.getStyle().getCustomExtraMap();
                        extraMap.put("pageRange", pageRange.name());
                        viewModel.getStyle().setCustomExtraMap(extraMap);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            opacitySliderBar.setProgress(annotStyle.getTextColorOpacity());
            swTile.setChecked(annotStyle.isChecked());
            boolean isFront = true;
            if (extraMap.containsKey("front")){
                isFront = (boolean) extraMap.get("front");
            }
            ivLocationTop.setSelected(isFront);
            ivLocationBottom.setSelected(!isFront);
        }
        opacitySliderBar.setChangeListener((progress, percentageValue, isStop) -> opacity(progress));
        viewModel.addStyleChangeListener(this);
        swTile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (viewModel != null && viewModel.getStyle() != null) {
                viewModel.getStyle().setChecked(isChecked);
            }
        });
    }

    @Override
    public void color(int color) {
        if (viewModel != null && viewModel.getStyle() != null) {
            viewModel.getStyle().setFontColor(color);
        }
    }

    @Override
    public void opacity(int opacity) {
        if (viewModel.getStyle() != null) {
            viewModel.getStyle().setTextColorOpacity(opacity);
        }
    }

    @Override
    public void onClick(View v) {
         if (v.getId() == R.id.iv_location_top) {
            ivLocationTop.setSelected(true);
            ivLocationBottom.setSelected(false);
            if (viewModel != null && viewModel.getStyle() != null) {
                Map<String, Object> extraMap = viewModel.getStyle().getCustomExtraMap();
                extraMap.put("front", true);
                viewModel.getStyle().setCustomExtraMap(extraMap);
            }
        } else if (v.getId() == R.id.iv_location_bottom) {
            ivLocationTop.setSelected(false);
            ivLocationBottom.setSelected(true);
            if (viewModel != null && viewModel.getStyle() != null) {
                Map<String, Object> extraMap = viewModel.getStyle().getCustomExtraMap();
                extraMap.put("front", false);
                viewModel.getStyle().setCustomExtraMap(extraMap);
            }
        } else if (v.getId() == R.id.iv_from_album) {
             imageLauncher.launch(CImageResultContracts.RequestType.PHOTO_ALBUM);
         } else if (v.getId() == R.id.iv_from_camera) {
             if (!CPermissionUtil.checkManifestPermission(getContext(), Manifest.permission.CAMERA)){
                 imageLauncher.launch(CImageResultContracts.RequestType.CAMERA);
             }else {
                 permissionResultLauncher.launch(Manifest.permission.CAMERA, granted -> {
                     if (granted) {
                         imageLauncher.launch(CImageResultContracts.RequestType.CAMERA);
                     } else {
                         if (getActivity() != null) {
                             if (!CPermissionUtil.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                                 CPermissionUtil.showPermissionsRequiredDialog(getChildFragmentManager(), getActivity());
                             }
                         }
                     }
                 });
             }
         }
    }
}
