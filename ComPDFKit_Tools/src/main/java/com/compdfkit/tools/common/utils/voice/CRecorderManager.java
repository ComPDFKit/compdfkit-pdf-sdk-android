/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.voice;

import static com.compdfkit.tools.common.utils.CFileUtils.SOUND_FOLDER;
import static java.lang.Thread.sleep;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.compdfkit.tools.annotation.pdfproperties.pdfsound.CRecordVoicePopupWindow;
import com.compdfkit.tools.common.utils.date.CDateUtil;

import java.io.File;

public class CRecorderManager {
    private Context context;
    private final int BASE = 600;
    File voiceFile = null;
    MediaRecorder mediaRecorder = null;
    CRecordVoicePopupWindow.RecordDbCallback dbCallback = null;

    AsyncTask<Void, Void, Boolean> updateTask = null;

    public CRecorderManager(Context context) {
        this.context = context;
    }

    public void setDbCallback(CRecordVoicePopupWindow.RecordDbCallback callback) {
        dbCallback = callback;
    }

    public boolean startRecord() {
        if (null == mediaRecorder) {
            mediaRecorder = new MediaRecorder();
        }
        try {
            File folderFile = new File(context.getFilesDir().getAbsolutePath(), SOUND_FOLDER);
            folderFile.mkdirs();
            voiceFile = new File(folderFile, "sound_" + CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT) + ".wav");
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(voiceFile);
            } else {
                mediaRecorder.setOutputFile(voiceFile.getCanonicalPath());
            }
            mediaRecorder.setMaxDuration(Integer.MAX_VALUE);
            mediaRecorder.prepare();
            mediaRecorder.start();
            updateMicStatus();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    public void pauseOrResumeRecord(boolean isRecording) {
        if (mediaRecorder == null) {
            return;
        }
        try {
            if (isRecording == true) {
                mediaRecorder.resume();
            } else {
                mediaRecorder.pause();
            }
        } catch (IllegalStateException e) {

        }
    }

    public void endRecord() {
        if (updateTask != null) {
            updateTask.cancel(true);
        }
        if (mediaRecorder == null) {
            return;
        }
        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (Exception e) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public void updateMicStatus() {

        updateTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                while (true) {
                    if (dbCallback != null) {
                        double db = 0;
                        double ratio = 0;
                        try {
                            ratio = mediaRecorder.getMaxAmplitude() / BASE;
                        } catch (IllegalStateException e) {

                        }
                        if (ratio > 1) {
                            db = 20 * Math.log10(ratio);
                        }
                        dbCallback.onDbUpdate((int) (db * 10) / 34, System.currentTimeMillis());
                    }
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        updateTask.execute();
    }

    public String getVoiceFilePath() {
        try {
            if (voiceFile != null) {
                String path = voiceFile.getCanonicalPath();
                return path == null ? "" : path;
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }
}
