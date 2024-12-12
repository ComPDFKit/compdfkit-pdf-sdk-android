/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfsound;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.voice.CMediaPlayManager;
import com.compdfkit.tools.common.utils.window.CBasePopupWindow;

import java.io.File;

public class CPlayVoicePopupWindow extends CBasePopupWindow implements CMediaPlayManager.IMediaPlayConstants {

    private Context context;

    private View rootView;

    private AppCompatImageView imageState;

    private AppCompatTextView tvTimeStart;

    private AppCompatImageView imageClose;

    AsyncTask<Void, Void, Boolean> updateTask = null;

    private CMediaPlayManager mediaPlayManager;

    boolean isPlay = false;

    String voicePath = null;

    private void setPlayState(boolean play) {
        isPlay = play;
        if (isPlay == true) {
            if (mediaPlayManager != null) {
                mediaPlayManager.mediaStart();
                imageState.setImageResource(R.drawable.tools_ic_record_stop);
            }
        } else {
            if (mediaPlayManager != null) {
                mediaPlayManager.mediaPause();
                imageState.setImageResource(R.drawable.tools_ic_play_arrow);
            }
        }
    }

    public CPlayVoicePopupWindow(Context context, View rootView) {
        super(context);
        setAnimationStyle(R.style.tools_popwindow_anim_style);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        this.context = context;
        this.rootView = rootView;
    }

    @Override
    protected View setLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.tools_properties_sound_play_window, null);
    }

    @Override
    protected void initResource() {

    }

    @Override
    protected void initListener() {
        imageState.setOnClickListener(this);
        imageClose.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        imageState = mContentView.findViewById(R.id.id_voice_recording_iv_state);
        tvTimeStart = mContentView.findViewById(R.id.id_voice_recording_tv_time);
        imageClose = mContentView.findViewById(R.id.iv_delete);
    }

    @Override
    protected void onClickListener(View view) {
        int id = view.getId();
        if (id == R.id.id_voice_recording_iv_state) {
            isPlay = !(mediaPlayManager == null ? false : mediaPlayManager.isPlaying());
            if (mediaPlayManager != null) {
                if (isPlay) {
                    mediaPlayManager.mediaStart();
                    imageState.setImageResource(R.drawable.tools_ic_record_stop);
                } else {
                    mediaPlayManager.mediaPause();
                    imageState.setImageResource(R.drawable.tools_ic_play_arrow);
                }
            } else {
                isPlay = false;
                imageState.setImageResource(R.drawable.tools_ic_play_arrow);
            }
        } else if (id == R.id.iv_delete) {
            dismiss();
        }
    }

    public void setVoicePath(String path) {
        voicePath = path;
    }

    String convert(long time) {
        int totalsecond = (int) (time / 1000);
        int h = totalsecond / 3600;
        int hs = totalsecond % 3600;
        int m = hs / 60;
        int ms = hs % 60;
        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m + ":" + (ms < 10 ? "0" : "") + ms;
    }

    private void startUpdateTask() {
        updateTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                while (true) {
                    if (mContext instanceof FragmentActivity) {
                        ((FragmentActivity) mContext).runOnUiThread(() -> {
                            if (mediaPlayManager != null && mediaPlayManager.getMediaPlayer() != null) {
                                tvTimeStart.setText(convert(mediaPlayManager.getMediaPlayer().getCurrentPosition()) + "");
                            }
                        });
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        updateTask.execute();
    }

    public void show() {
        mediaPlayManager = new CMediaPlayManager();
        mediaPlayManager.setMediaPlayConstants(this);
        mediaPlayManager.getMediaPlayer().setOnErrorListener((mediaPlayer, i, i1) -> true);
        mediaPlayManager.setPlayFile(voicePath);
        mediaPlayManager.mediaStart();
        startUpdateTask();
        if (mContext instanceof FragmentActivity) {
            ((FragmentActivity) mContext).runOnUiThread(() -> {
                showAtLocation(rootView, Gravity.BOTTOM, 0, CViewUtils.getActionBarSize(rootView.getContext()) + CDimensUtils.dp2px(rootView.getContext(), 32));
            });
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlay = false;
        setPlayState(isPlay);
    }

    @Override
    public void dismiss() {
        if (!TextUtils.isEmpty(voicePath)) {
            new File(voicePath).delete();
        }
        if (mediaPlayManager != null) {
            mediaPlayManager.onDestroy();
        }
        if (updateTask != null) {
            updateTask.cancel(true);
            updateTask = null;
        }
        super.dismiss();
    }
}
