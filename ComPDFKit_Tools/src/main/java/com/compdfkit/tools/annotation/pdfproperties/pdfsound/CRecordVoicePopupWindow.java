/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfsound;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFSoundAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.voice.CRecorderManager;
import com.compdfkit.tools.common.utils.window.CBasePopupWindow;
import com.compdfkit.ui.attribute.CPDFAnnotAttribute;


public class CRecordVoicePopupWindow extends CBasePopupWindow {

    private final Context context;

    private View rootView;

    private AppCompatTextView tvRecordTime;

    private AppCompatImageView ivState;

    private AppCompatImageView ivDelete;

    private AppCompatImageView ivConfirm;

    private boolean isDone = false;

    private long stopTime = 0;

    private long pauseTime = 0;

    private long totalTime = 0;

    private long stopCountTime = 0;

    //是否正在录音
    private boolean isRecording = false;

    CRecorderManager recorderManager;

    RecordDoneCallback doneCallback = null;

    public CRecordVoicePopupWindow(Context context, View rootView, CPDFAnnotAttribute annotAttribute, CPDFSoundAnnotation soundAnnotation) {
        super(context);
        setAnimationStyle(R.style.tools_popwindow_anim_style);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        this.context = context;
        this.rootView = rootView;
    }

    public CRecordVoicePopupWindow(Context context, View rootView, CPDFAnnotAttribute annotAttribute) {
        super(context);
        setAnimationStyle(R.style.tools_popwindow_anim_style);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        this.context = context;
        this.rootView = rootView;
    }

    @Override
    protected View setLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.tools_properties_sound_record_voice, null);
    }

    @Override
    protected void initResource() {
        recorderManager = new CRecorderManager(mContext);
    }

    @Override
    protected void initListener() {
        ivState.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivConfirm.setOnClickListener(this);
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    protected void initView() {
        tvRecordTime = mContentView.findViewById(R.id.id_voice_recording_tv_time);
        ivState = mContentView.findViewById(R.id.id_voice_recording_iv_state);
        ivDelete = mContentView.findViewById(R.id.iv_delete);
        ivConfirm = mContentView.findViewById(R.id.iv_confirm);
    }

    @Override
    protected void onClickListener(View view) {
        int id = view.getId();
        if (id == R.id.id_voice_recording_iv_state) {
            isRecording = !isRecording;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorderManager.pauseOrResumeRecord(isRecording);
                if (isRecording) {
                    ivState.setImageResource(R.drawable.tools_ic_record_stop);
                    stopCountTime += (pauseTime - stopTime);
                } else {
                    ivState.setImageResource(R.drawable.tools_ic_play_arrow);
                    stopTime = System.currentTimeMillis();
                }
            }
        } else if (id == R.id.iv_confirm) {
            isDone = true;
            dismiss();
        } else if (id == R.id.iv_delete) {
            isDone = false;
            dismiss();
        }
    }

    public void show() {
        showAtLocation(rootView, Gravity.BOTTOM, 0, CViewUtils.getActionBarSize(rootView.getContext()) + CDimensUtils.dp2px(rootView.getContext(), 32));
        startRecord();
    }

    String convert(long time) {
        int totalsecond = (int)(time / 1000);
        int h = totalsecond / 3600;
        int hs = totalsecond % 3600;
        int m = hs / 60;
        int ms = hs % 60;
        return (h < 10 ? "0":"") + h+":" + (m < 10 ? "0" : "") + m + ":" + (ms < 10 ? "0" : "") + ms;
    }

    private boolean startRecord() {
        long startTime = System.currentTimeMillis();
        isRecording = recorderManager.startRecord();
        recorderManager.setDbCallback((voice, time)-> {
            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(mContext);
            if (fragmentActivity != null) {
                fragmentActivity.runOnUiThread(()->{
                    totalTime = time - startTime;
                    if (isRecording) {
                        long uitime = totalTime - (pauseTime > 0 ? stopCountTime : 0);
                        tvRecordTime.setText(convert(uitime));
                        ivState.setImageResource(R.drawable.tools_ic_record_stop);
                    } else {
                        pauseTime = System.currentTimeMillis();
                    }
                });
            }
        });
        return isRecording;
    }

    public void setRecordDoneCallback(RecordDoneCallback callback) {
        doneCallback = callback;
    }

    private void reset() {
        isDone = false;
        stopTime = 0;
        pauseTime = 0;
        totalTime = 0;
        stopCountTime = 0;
        isRecording = false;
        doneCallback = null;
        if (ivState != null) {
            ivState.setImageResource(R.drawable.tools_ic_record_stop);
        }
    }

    @Override
    public void dismiss() {
        recorderManager.endRecord();
        if (doneCallback != null) {
            doneCallback.onDone(isDone, recorderManager.getVoiceFilePath());
        }
        reset();
        super.dismiss();
    }

    public interface RecordDoneCallback {
        void onDone(boolean done, String path);
    }

    public interface RecordDbCallback {
        void onDbUpdate(int voice, long time);
    }
}
