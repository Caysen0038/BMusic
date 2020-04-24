package com.baokaicong.android.bmusic.service.player;

import android.content.Context;
import android.media.MediaPlayer;

import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HTTPMusicPlayer implements MusicPlayer {
    private MediaPlayer mediaPlayer;
    private boolean prepared=false;
    private Music music;
    private List<MusicPlayerListner> listnerList;

    private static class Holder{
        private static final HTTPMusicPlayer instance=new HTTPMusicPlayer();
    }

    public static HTTPMusicPlayer instance(){
        return Holder.instance;
    }

    private HTTPMusicPlayer(){
        this.mediaPlayer=new MediaPlayer();

        listnerList=new ArrayList<>();
    }

    @Override
    public boolean loadMedia(Music src,boolean loop) {
        this.music=src;
        try {
            this.mediaPlayer.reset();
            this.mediaPlayer.setDataSource(this.music.getUrl());
            this.mediaPlayer.setLooping(loop);
            this.mediaPlayer.prepareAsync();

            this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    prepared=true;
                    notifyLoadComplete();
                }
            });
            this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    notifyPlayComplete();
                }
            });
            return true;
        } catch (IOException e) {
            notifyError();
            return false;
        }

    }

    @Override
    public boolean play() {
        if(!isPlaying() && prepared){
            this.mediaPlayer.start();
            notifyPlay();
            return true;
        }
        return false;
    }

    @Override
    public void pause() {
        if(isPlaying()){
            this.mediaPlayer.pause();
            notifyPuase();
        }
    }

    @Override
    public void playComplete() {
        if(isPlaying()){
            this.mediaPlayer.stop();
            notifyPlayComplete();
        }
    }

    @Override
    public void release() {
        if(isPlaying()){
            this.mediaPlayer.release();
            notifyRelease();
        }
    }

    @Override
    public boolean isPlaying() {
        return this.mediaPlayer.isPlaying();
    }

    @Override
    public boolean jump(int rate) {
        return false;
    }

    @Override
    public void addMusicPlayerListner(MusicPlayerListner listner) {
        synchronized (listnerList){
            if(!listnerList.contains(listner)){
                listnerList.add(listner);
            }
        }
    }

    @Override
    public void removeMusicPlayerListner(MusicPlayerListner listner) {
        synchronized (listnerList){
            listnerList.remove(listner);
        }
    }

    private void notifyPlay(){
        synchronized (listnerList){
            for(MusicPlayerListner lisntner:listnerList){
                lisntner.onPlay();
            }
        }
    }
    private void notifyPuase(){
        synchronized (listnerList){
            for(MusicPlayerListner lisntner:listnerList){
                lisntner.onPause();
            }
        }
    }
    private void notifyPlayComplete(){
        synchronized (listnerList){
            for(MusicPlayerListner lisntner:listnerList){
                lisntner.onPlayComplete();
            }
        }
    }

    private void notifyLoadComplete(){
        synchronized (listnerList){
            for(MusicPlayerListner lisntner:listnerList){
                lisntner.onLoadComplete();
            }
        }
    }

    private void notifyError(){
        synchronized (listnerList){
            for(MusicPlayerListner lisntner:listnerList){
                lisntner.onError();
            }
        }
    }
    private void notifyRelease(){
        synchronized (listnerList){
            for(MusicPlayerListner lisntner:listnerList){
                lisntner.onRelease();
            }
        }
    }
}
