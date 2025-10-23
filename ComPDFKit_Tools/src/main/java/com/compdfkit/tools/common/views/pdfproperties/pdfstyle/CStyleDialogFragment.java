/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.interfaces.COnDialogDismissListener;
import com.compdfkit.tools.common.utils.CWrapHeightPageChangeCallback;
import com.compdfkit.tools.common.utils.dialog.CDialogFragmentUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;


public class CStyleDialogFragment extends CBasicBottomSheetDialogFragment implements CBasicPropertiesFragment.OnSwitchFragmentListener {

    protected ConstraintLayout clToolbar;

    protected AppCompatImageView ivPreviousFragment;

    protected AppCompatImageView ivClose;

    protected AppCompatTextView tvTitle;

    protected ConstraintLayout clViewPager;

    protected ViewPager2 viewPager;

    protected CAnnotStyle annotStyle;

    protected CStyleUIParams styleUIParams;

    protected CStyleViewModel viewModel;

    protected List<CAnnotStyle.OnAnnotStyleChangeListener> annotStyleChangeListenerList = new ArrayList<>();

    protected CStyleFragmentAdapter annotStyleFragmentAdapter;

    protected COnDialogDismissListener styleDialogDismissListener;

    protected CWrapHeightPageChangeCallback changeCallback;

    protected CWrapHeightPageChangeCallback.CViewHeightCallback dialogHeightCallback;

    public void setAnnotStyle(CAnnotStyle annotStyle) {
        this.annotStyle = annotStyle;
    }

    public void setStyleUiConfig(CStyleUIParams params){
        this.styleUIParams = params;
    }

    public CAnnotStyle getAnnotStyle() {
        return viewModel != null ? viewModel.getStyle() : null;
    }

    public static CStyleDialogFragment newInstance(CAnnotStyle style) {
        CStyleDialogFragment dialogFragment = new CStyleDialogFragment();
        dialogFragment.setAnnotStyle(style);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (annotStyle == null){
            dismiss();
            return;
        }
        if (styleUIParams == null){
            styleUIParams = CStyleUIParams.defaultStyle(getContext(), annotStyle.getType());
        }
        viewModel = new ViewModelProvider(getActivity()).get(CStyleViewModel.class);
        setStyle(STYLE_NORMAL, styleUIParams.theme);
        annotStyle.addStyleChangeListener(annotStyleChangeListenerList);
        viewModel.setStyle(annotStyle);
    }

    @Override
    protected int getStyle() {
        return styleUIParams.theme;
    }

    @Override
    protected float dimAmount() {
        if (!CViewUtils.isLandScape(getContext())) {
            return styleUIParams.dimAmount;
        } else {
            return 0.2F;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        if (!CViewUtils.isLandScape(getContext())) {
            if (styleUIParams.fillScreenHeight){
                CDialogFragmentUtil.setBottomSheetDialogFragmentFullScreen(requireActivity(), getDialog(), behavior);
            }else {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } else {
            if (styleUIParams.fillScreenHeight){
                CDialogFragmentUtil.setBottomSheetDialogFragmentFullScreen(requireActivity(), getDialog(), behavior);
            }
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }
    }

    @Override
    protected int layoutId() {
        if (styleUIParams.dialogStyleType == CStyleUIParams.DialogStyleType.Dialog) {
           return R.layout.tools_style_dialog_fragment;
        }else {
            return R.layout.tools_style_activity_type_dialog_fragment;
        }
    }

    @Override
    protected void onCreateView(View rootView) {
        ivPreviousFragment = rootView.findViewById(R.id.iv_tool_bar_previous);
        ivClose = rootView.findViewById(R.id.iv_tool_bar_close);
        tvTitle = rootView.findViewById(R.id.tv_tool_bar_title);
        viewPager = rootView.findViewById(R.id.view_pager);
        clToolbar = rootView.findViewById(R.id.cl_tool_bar);
        clViewPager = rootView.findViewById(R.id.cl_viewPager);
        ConstraintLayout clToolbar = rootView.findViewById(R.id.cl_tool_bar);
        if (viewModel.getStyle().getType() == CStyleType.ANNOT_STAMP){
            clToolbar.setElevation(0);
        }
    }

    @Override
    protected void onViewCreate() {
        applyStyleUIConfig();
        ivClose.setOnClickListener(v -> {
            dismiss();
        });
        ivPreviousFragment.setOnClickListener(v -> {
            previousFragment();
        });
        initViewPager();
    }

    private void applyStyleUIConfig(){
        if (styleUIParams != null) {
            clToolbar.setVisibility(styleUIParams.showToolbar ? View.VISIBLE : View.GONE);
            if (!styleUIParams.showToolbar) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) clViewPager.getLayoutParams();
                layoutParams.topMargin = 0;
                clViewPager.setLayoutParams(layoutParams);
            }
        }
    }


    private void initViewPager() {
        if (annotStyle != null) {
            annotStyleFragmentAdapter = new CStyleFragmentAdapter(annotStyle, createFragment(), getChildFragmentManager(), getLifecycle());
            annotStyleFragmentAdapter.setSwitchFragmentListener(this);
            viewPager.setAdapter(annotStyleFragmentAdapter);
            viewPager.setUserInputEnabled(false);
            viewPager.setOffscreenPageLimit(2);
            if (!styleUIParams.fillScreenHeight){
                changeCallback = new CWrapHeightPageChangeCallback(viewPager, getChildFragmentManager());
                changeCallback.setViewHeightCallback(dialogHeightCallback);
                viewPager.registerOnPageChangeCallback(changeCallback);
            }
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    ivPreviousFragment.setVisibility(position != 0 ? View.VISIBLE : View.GONE);
                    ivClose.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                    int titleResId = annotStyleFragmentAdapter.getTitleByIndex(position);
                    if (titleResId!=0){
                        tvTitle.setText(getString(annotStyleFragmentAdapter.getTitleByIndex(position)));
                    }
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getContext() == null || getView() == null){
            return;
        }
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        if (CViewUtils.isLandScape(getContext())) {
            CDialogFragmentUtil.setDimAmount(getDialog(), 0.4F);
            behavior.setSkipCollapsed(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            CDialogFragmentUtil.setDimAmount(getDialog(), styleUIParams.dimAmount);
            if (!styleUIParams.fillScreenHeight) {
                CDialogFragmentUtil.resetBottomSheetDialogFragment(getDialog(), behavior);
            }
        }
        if (!styleUIParams.fillScreenHeight) {
            if (changeCallback != null) {
                changeCallback.updatePagerHeight(getChildFragmentManager(), viewPager, viewPager.getCurrentItem());
            }
        }
    }

    @Override
    public void previousFragment() {
        viewPager.setCurrentItem(0);
        viewPager.postDelayed(() -> annotStyleFragmentAdapter.removeFragment(1), 200);
    }

    @Override
    public void showFragment(CPropertiesFragmentBean bean, CBasicPropertiesFragment.OnFragmentInitListener initListener) {
        annotStyleFragmentAdapter.addFragment(1, bean);
        viewPager.post(()->{
            viewPager.setCurrentItem(1);
            Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + annotStyleFragmentAdapter.getItemId(1));
            if (fragment != null && fragment instanceof CBasicPropertiesFragment) {
                if (initListener != null) {
                    initListener.init((CBasicPropertiesFragment) fragment);
                }
            }
        });
    }

    @Override
    public void dismissStyleDialog() {
        dismiss();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (styleDialogDismissListener != null) {
            styleDialogDismissListener.dismiss();
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (styleDialogDismissListener != null) {
            styleDialogDismissListener.dismiss();
        }
    }

    public void show(@Nullable FragmentManager fragmentManager){
        if (fragmentManager != null) {
            show(fragmentManager, "styleDialogFragment");
        }
    }

    protected CPropertiesFragmentBean createFragment(){
        return CStyleFragmentDatas.getStyleFragment(getAnnotStyle());
    }

    public void setDialogHeightCallback(CWrapHeightPageChangeCallback.CViewHeightCallback dialogHeightCallback) {
        this.dialogHeightCallback = dialogHeightCallback;
    }

    public void addAnnotStyleChangeListener(CAnnotStyle.OnAnnotStyleChangeListener annotStyleChangeListener) {
        this.annotStyleChangeListenerList.add(annotStyleChangeListener);
    }

    public void setStyleDialogDismissListener(COnDialogDismissListener styleDialogDismissListener) {
        this.styleDialogDismissListener = styleDialogDismissListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        changeCallback = null;
        dialogHeightCallback = null;
        if (annotStyle != null){
            annotStyle.cleanStyleChangeListener();
        }
    }
}
