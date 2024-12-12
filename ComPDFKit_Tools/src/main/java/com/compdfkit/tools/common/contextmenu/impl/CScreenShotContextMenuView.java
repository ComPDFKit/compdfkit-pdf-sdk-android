/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuScreenShotProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.image.CImageUtil;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.io.File;

public class CScreenShotContextMenuView implements ContextMenuScreenShotProvider {


    @Override
    public View getScreenShotContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, LayoutInflater inflater, RectF rectF) {
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        menuView.addItem(R.string.tools_exit, v -> {
            exitScreenShot(helper, pageView);
        });
        menuView.addItem(R.string.tools_share, v -> {
            Bitmap bitmap = pageView.getScreenshotBitmap();
            if (bitmap == null){
                exitScreenShot(helper, pageView);
                return;
            }
            String fileName = "screenshot_"+ CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT)+".png";
            String screenShotFilePath = CImageUtil.saveBitmap(pageView.getContext(), fileName, pageView.getScreenshotBitmap());
            CFileUtils.shareFile(menuView.getContext(), pageView.getContext().getString(R.string.tools_share), "image/*", new File(screenShotFilePath));
            exitScreenShot(helper, pageView);
        });
        return menuView;
    }

    private void exitScreenShot(CPDFContextMenuHelper helper, CPDFPageView pageView){
        pageView.clearScreenShotRect();
        CPDFReaderView readerView = helper.getReaderView();
        CPDFReaderView.ViewMode viewMode = readerView.getViewMode();
        if(viewMode == CPDFReaderView.ViewMode.PDFEDIT){
            readerView.setTouchMode(CPDFReaderView.TouchMode.EDIT);
            CPDFEditManager editManager = readerView.getEditManager();
            if (editManager != null && !editManager.isEditMode()) {
                editManager.enable();
                editManager.beginEdit(CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath);
            }
        }else {
            readerView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
        }
        helper.dismissContextMenu();
        Context context = readerView.getContext();

        if (context instanceof FragmentActivity){
            FragmentActivity fragmentActivity = (FragmentActivity) context;
            Fragment fragment = fragmentActivity.getSupportFragmentManager()
                    .findFragmentByTag("documentFragment");
            if (fragment != null && fragment instanceof  CPDFDocumentFragment){
                CPDFDocumentFragment documentFragment = (CPDFDocumentFragment) fragment;
                // Exit full screen mode
                documentFragment.screenManager.fillScreenChange();
            }
        }
    }

}
