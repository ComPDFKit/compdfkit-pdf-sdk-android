/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.basic.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

public abstract class CBasicThemeFragment extends Fragment {
    protected Context themedContext;

    @Nullable
    @Override
    public Context getContext() {
        return themedContext != null ? themedContext : super.getContext();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        themedContext = new ContextThemeWrapper(getContext(), CViewUtils.getThemeStyle(getContext(), themeResId()));
        LayoutInflater themedInflater = inflater.cloneInContext(themedContext);
        View view = themedInflater.inflate(layoutId(), container, false);
        onCreateView(view);
        return view;
    }


    protected int themeResId(){
        return R.attr.compdfkit_BottomSheetDialog_Theme;
    }

    protected abstract int layoutId();

    protected abstract void onCreateView(View rootView);

}
