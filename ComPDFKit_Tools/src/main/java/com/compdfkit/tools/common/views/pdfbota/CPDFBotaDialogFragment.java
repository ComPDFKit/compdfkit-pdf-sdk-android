/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfbota;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationlist.CPDFAnnotationListFragment;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.pdf.config.bota.CPDFBotaMenusConfig;
import com.compdfkit.tools.common.utils.glide.CPDFGlideInitializer;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.pdfbota.adapter.CBotaViewPagerAdapter;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfoutline.CPDFOutlineFragment;
import com.compdfkit.tools.viewer.pdfthumbnail.CPDFThumbnailFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


/**
 * The interface contains two PDF function lists, the PDF thumbnail list and the PDF outline list.
 * @see CPDFThumbnailFragment
 * @see CPDFOutlineFragment
 */
public class CPDFBotaDialogFragment extends CBasicBottomSheetDialogFragment {

    private TabLayout tabLayout;

    private CToolBar toolBar;

    private ViewPager2 viewPager2;

    private CPDFViewCtrl pdfView;

    private AppCompatImageView ivMenu;

    private ArrayList<CPDFBotaFragmentTabs> tabs = new ArrayList<>();

    private CPDFBotaMenusConfig menus = new CPDFBotaMenusConfig();

    public static CPDFBotaDialogFragment newInstance(){
        return new CPDFBotaDialogFragment();
    }

    public void initWithPDFView(CPDFViewCtrl pdfView){
        this.pdfView = pdfView;
    }

    public void setBotaDialogTabs(ArrayList<CPDFBotaFragmentTabs> tabs){
        this.tabs = tabs;
    }

    public void setMenus(CPDFBotaMenusConfig menus) {
        this.menus = menus;
    }

    public void setBotaDialogTab(CPDFBotaFragmentTabs tab){
        this.tabs.add(tab);
    }

    @Override
    protected float dimAmount() {
        return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_bota_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        CPDFGlideInitializer.register(getContext());
        tabLayout = rootView.findViewById(R.id.tab_layout);
        viewPager2 = rootView.findViewById(R.id.view_pager);
        toolBar = rootView.findViewById(R.id.tool_bar);
        ivMenu = rootView.findViewById(R.id.iv_menu);
        toolBar.setBackBtnClickListener(v -> dismiss());
        ivMenu.setOnClickListener(v -> {
            CPDFAnnotationListFragment annotationListFragment = getAnnotationFragment();
            if (annotationListFragment != null){
                annotationListFragment.showAnnotationMenu(ivMenu);
            }
        });
    }

    @Override
    protected void onViewCreate() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (tabs == null || tabs.size() == 0){
            if (savedInstanceState != null && savedInstanceState.containsKey("tabs")){
                tabs = (ArrayList<CPDFBotaFragmentTabs>) savedInstanceState.getSerializable("tabs");
            }
        }
        initViewPagers();
    }

    private void initViewPagers(){
        if (tabs.size() <= 1){
            tabLayout.setVisibility(View.GONE);
        }
        CBotaViewPagerAdapter boTaViewPagerAdapter = new CBotaViewPagerAdapter(getChildFragmentManager(), getLifecycle(), pdfView, tabs, menus);
        //Listen to thumbnail and outline click events, CPDFReaderView jumps to the corresponding page and dismisses the popup
        boTaViewPagerAdapter.setPDFDisplayPageIndexListener(pageIndex -> {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().setDisplayPageIndex(pageIndex);
            }
            dismiss();
        });
        viewPager2.setAdapter(boTaViewPagerAdapter);
        viewPager2.setCurrentItem(getDefaultSelectIndex(), false);
        if (!tabs.isEmpty()){
            toolBar.setTitle(tabs.get(getDefaultSelectIndex()).getTitle());
            if (menus != null && !menus.getAnnotations().getGlobal().isEmpty()){
                ivMenu.setVisibility(tabs.get(getDefaultSelectIndex()).getBotaType() == CPDFBOTA.ANNOTATION ? View.VISIBLE : View.GONE);
            }else {
                ivMenu.setVisibility(View.GONE);
            }
        }
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                toolBar.setTitle(tabs.get(position).getTitle());
                if (menus != null && !menus.getAnnotations().getGlobal().isEmpty()){
                    ivMenu.setVisibility(tabs.get(position).getBotaType() == CPDFBOTA.ANNOTATION ? View.VISIBLE : View.GONE);
                }else {
                    ivMenu.setVisibility(View.GONE);
                }
            }
        });
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(tabs.get(position).getTitle());
        });
        tabLayoutMediator.attach();
    }


    private int getDefaultSelectIndex(){
        int defaultSelectIndex = 0;
        if (tabs.size() <=1){
            return 0;
        }
        for (int i = 0; i < tabs.size(); i++) {
            CPDFBotaFragmentTabs tab = tabs.get(i);
            if (tab.isDefaultSelect()){
                return i;
            }
        }
        return defaultSelectIndex;
    }


    private @Nullable CPDFAnnotationListFragment getAnnotationFragment(){
        int annotationIndex = 0;
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).getBotaType() == CPDFBOTA.ANNOTATION) {
                annotationIndex = i;
                break;
            }
        }
        Fragment fragment = getChildFragmentManager().findFragmentByTag("f"+annotationIndex);
        if (fragment != null && fragment instanceof CPDFAnnotationListFragment){
           return (CPDFAnnotationListFragment) fragment;
        }else {
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("tabs", tabs);
        super.onSaveInstanceState(outState);
    }

}
