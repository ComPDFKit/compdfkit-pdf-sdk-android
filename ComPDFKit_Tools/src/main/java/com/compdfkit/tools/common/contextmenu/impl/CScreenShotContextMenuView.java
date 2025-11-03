/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuScreenShotProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.image.CImageUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CScreenShotContextMenuView implements ContextMenuScreenShotProvider {


    @Override
    public View getScreenShotContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, LayoutInflater inflater, RectF rectF) {
        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> globalConfig = CPDFApplyConfigUtil.getInstance().getGlobalContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> screenShotMenu = globalConfig.get("screenshot");

        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        if (screenShotMenu == null) {
            return menuView;
        }
        for (ContextMenuConfig.ContextMenuActionItem item : screenShotMenu) {
            switch (item.key) {
                case "exit":
                    menuView.addItem(R.string.tools_exit, v -> exitScreenShot(helper));
                    break;
                case "share":
                    menuView.addItem(R.string.tools_share, v -> {
                        try {
                            Bitmap bitmap = pageView.getScreenshotBitmap();
                            if (bitmap == null) {
                                exitScreenShot(helper);
                                return;
                            }
                            String fileName = "screenshot_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".png";
                            String screenShotFilePath = CImageUtil.saveBitmap(pageView.getContext(), fileName, bitmap);
                            CFileUtils.shareFile(menuView.getContext(), pageView.getContext().getString(R.string.tools_share), "image/*", new File(screenShotFilePath));
                            exitScreenShot(helper);
                        } catch (Exception e) {
                            exitScreenShot(helper);
                        }
                    });
                    break;
            }
        }
        return menuView;
    }

    private void exitScreenShot(CPDFContextMenuHelper helper) {
        CPDFReaderView readerView = helper.getReaderView();
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity != null) {
            Fragment fragment = fragmentActivity.getSupportFragmentManager()
                    .findFragmentByTag("documentFragment");
            if (fragment instanceof CPDFDocumentFragment) {
                CPDFDocumentFragment documentFragment = (CPDFDocumentFragment) fragment;
                documentFragment.exitScreenShot();
            }
        }
    }

}
