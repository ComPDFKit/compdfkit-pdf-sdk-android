/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.compdfkit.core.annotation.CPDFSoundAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfproperties.pdfsound.CPlayVoicePopupWindow;
import com.compdfkit.tools.annotation.pdfproperties.pdfsound.CRecordVoicePopupWindow;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSoundContentProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.utils.threadpools.SimpleBackgroundTask;
import com.compdfkit.ui.proxy.CPDFSoundAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.List;
import java.util.Map;

public class CSoundContextMenuView implements ContextMenuSoundContentProvider {


    @Override
    public View createSoundContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFSoundAnnotImpl soundAnnotImpl) {
        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());

        CPDFSoundAnnotation soundAnnotation = soundAnnotImpl.onGetAnnotation();

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> annotationModeConfig = CPDFApplyConfigUtil.getInstance().getAnnotationModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> soundContent = annotationModeConfig.get("soundContent");

        if (soundContent == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : soundContent) {
            switch (contextMenuActionItem.key) {
                case "reply":
                    menuView.addItem(R.string.tools_reply, v -> {
                        new CPDFAnnotationManager().showAddReplyDialog(pageView, soundAnnotImpl, helper, true);
                        helper.dismissContextMenu();
                    });
                    break;
                case "viewReply":
                    menuView.addItem(R.string.tools_view_reply, v -> {
                        new CPDFAnnotationManager().showReplyDetailsDialog(pageView, soundAnnotImpl, helper);
                        helper.dismissContextMenu();
                    });
                    break;
                case "play":
                    if (soundAnnotation.isRecorded()) {
                        menuView.addItem(R.string.tools_play, v -> {
                            new SimpleBackgroundTask<String>(helper.getReaderView().getContext()) {
                                @Override
                                protected String onRun() {
                                    Context context = helper.getReaderView().getContext();
                                    return soundAnnotation.loadSoundSource(context.getCacheDir().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
                                }

                                @Override
                                protected void onSuccess(String result) {
                                    if (!TextUtils.isEmpty(result)) {
                                        helper.dismissContextMenu();
                                        Context context = helper.getReaderView().getContext();
                                        CPlayVoicePopupWindow playVoicePopupWindow = new CPlayVoicePopupWindow(context, helper.getReaderView());
                                        playVoicePopupWindow.setVoicePath(result);
                                        playVoicePopupWindow.show();
                                    }
                                }
                            }.execute();
                        });
                    }
                    break;
                case "record":
                    if (!soundAnnotation.isRecorded()){
                        menuView.addItem(R.string.tools_record, v -> {
                            helper.setPopupWindowDismissListener(null);
                            helper.dismissContextMenu();
                            CRecordVoicePopupWindow voicePopupWindow = new CRecordVoicePopupWindow(pageView.getContext(),
                                    helper.getReaderView(), helper.getReaderView().getReaderAttribute().getAnnotAttribute(), soundAnnotation);
                            voicePopupWindow.setRecordDoneCallback((done, path) -> {
                                if (done) {
                                    soundAnnotation.setSoundPath(path);
                                    soundAnnotImpl.onUpdateSoundIcon();
                                    pageView.invalidate();
                                }
                            });
                            voicePopupWindow.show();
                        });
                    }
                    break;
                case "delete":
                    menuView.addItem(R.string.tools_delete, v -> {
                        pageView.deleteAnnotation(soundAnnotImpl);
                        helper.dismissContextMenu();
                    });
                    break;
            }
        }
        return menuView;
    }
}
