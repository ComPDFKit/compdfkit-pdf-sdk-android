/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;

import java.util.ArrayList;
import java.util.List;

public class CStyleFragmentAdapter extends FragmentStateAdapter {

    private CAnnotStyle style;

    private List<CPropertiesFragmentBean> propertiesClass = new ArrayList<>();

    private List<Long> pageIds = new ArrayList<>();

    private CBasicPropertiesFragment.OnSwitchFragmentListener listener;

    public CStyleFragmentAdapter(CAnnotStyle style, CPropertiesFragmentBean fragmentBean, @NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.style = style;
        if (fragmentBean != null) {
            propertiesClass.add(fragmentBean);
        }
        pageIds.clear();
        for (CPropertiesFragmentBean propertiesClass : propertiesClass) {
            pageIds.add((long)propertiesClass.hashCode());
        }
    }

    public void addFragment(int index , CPropertiesFragmentBean bean){
        propertiesClass.add(index, bean);
        pageIds.add(index, (long) bean.hashCode());
        notifyItemInserted(index);
    }

    public void removeFragment(int index){
        propertiesClass.remove(index);
        pageIds.remove(index);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        CBasicPropertiesFragment fragment = CStyleFragmentDatas.createPropertiesFragment(propertiesClass.get(position).getFragmentClass(), style);
        if (fragment != null) {
            fragment.setSwitchFragmentListener(listener);
        }
        return fragment;
    }

    @Override
    public long getItemId(int position) {
        return pageIds.get(position);
    }

    @Override
    public boolean containsItem(long itemId) {
        return pageIds.contains(itemId);
    }

    @Override
    public int getItemCount() {
        return propertiesClass.size();
    }

    public int getTitleByIndex(int index){
        return propertiesClass.get(index).getTitleResId();
    }

    public void setSwitchFragmentListener(CBasicPropertiesFragment.OnSwitchFragmentListener listener) {
        this.listener = listener;
    }
}
