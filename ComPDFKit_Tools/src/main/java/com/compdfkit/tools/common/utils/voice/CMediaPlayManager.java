/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.utils.voice;


import android.media.MediaPlayer;
import android.text.TextUtils;

public class CMediaPlayManager implements MediaPlayer.OnCompletionListener {
    private IMediaPlayConstants iMediaPlayConstants = null;

    MediaPlayer mMediaPlayer = null;

    public CMediaPlayManager(){
        createPlayer();
    }

    public MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            createPlayer();
        }
        return mMediaPlayer;
    }

    private void createPlayer(){
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {

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

    public void onDestroy(){
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
