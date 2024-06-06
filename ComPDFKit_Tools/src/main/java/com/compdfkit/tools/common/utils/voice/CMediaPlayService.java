/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.voice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

public class CMediaPlayService extends Service implements MediaPlayer.OnCompletionListener {
    private IMediaPlayConstants iMediaPlayConstants = null;
    MyBinder mybinder = new MyBinder();

    static MediaPlayer mMediaPlayer = null;

     public class MyBinder extends Binder {
        public CMediaPlayService service;
        public CMediaPlayService get() {
            return CMediaPlayService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createPlayer();
    }

    public MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            createPlayer();
        }
        return mMediaPlayer;
    }

    private void createPlayer() {
         try {
             if (mMediaPlayer != null) {
                 mMediaPlayer.reset();
                 mMediaPlayer.release();
                 mMediaPlayer = null;
             }
             mMediaPlayer = new MediaPlayer();
             mMediaPlayer.setOnCompletionListener(this);
         } catch (Exception e) {
            e.printStackTrace();
         }
    }

    public boolean isPlaying() {
         if (mMediaPlayer == null) {
             return false;
         }
         return mMediaPlayer.isPlaying();
    }

    public void setMediaPlayConstants(IMediaPlayConstants iMediaPlayConstants) {
        this.iMediaPlayConstants = iMediaPlayConstants;
    }

    public void setPlayFile(String path) {
         try {
             if(mMediaPlayer != null && !TextUtils.isEmpty(path)) {
                 mMediaPlayer.reset();
                 mMediaPlayer.setDataSource(path);
                 mMediaPlayer.prepare();
             }
        } catch (Exception e) {

         }
    }

    public void mediaStart() {
         if (mMediaPlayer != null && !isPlaying()) {
             mMediaPlayer.start();
         }
    }

    public void mediaPause() {
        if (mMediaPlayer != null && isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void mediaStop() {
        if (mMediaPlayer != null && isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
         if (iMediaPlayConstants != null) {
             iMediaPlayConstants.onCompletion(mediaPlayer);
         }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        iMediaPlayConstants = null;
    }

    public interface IMediaPlayConstants {
        void onCompletion(MediaPlayer mp);
    }
}
