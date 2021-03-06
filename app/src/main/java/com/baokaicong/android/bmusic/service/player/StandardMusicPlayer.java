package com.baokaicong.android.bmusic.service.player;

import android.media.MediaPlayer;

import com.baokaicong.android.bmusic.bean.Music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StandardMusicPlayer implements MusicPlayer {
    private MediaPlayer mediaPlayer;
    private boolean prepared=false;
    private Music music;
    private String path;
    private boolean loaded;
    private List<MusicPlayerListner> listnerList;

    public static StandardMusicPlayer newInstance(){
        return new StandardMusicPlayer();
    }

    private StandardMusicPlayer(){
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
                    loaded=true;
                    notifyLoadComplete();
                }
            });
            this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    loaded=false;
                    notifyPlayComplete();
                }
            });
            this.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    loaded=false;
                    notifyError();
                    return true;
                }
            });
            return true;
        } catch (IOException e) {
            loaded=false;
            notifyError();
            return false;
        }
    }

    @Override
    public boolean loadMedia(String path,boolean loop) {
        this.path=path;
        try {
            this.mediaPlayer.reset();
            this.mediaPlayer.setDataSource(path);
            this.mediaPlayer.setLooping(loop);
            this.mediaPlayer.prepareAsync();

            this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    prepared=true;
                    loaded=true;
                    notifyLoadComplete();
                }
            });
            this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    loaded=false;
                    notifyPlayComplete();
                }
            });
            this.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    loaded=false;
                    notifyError();
                    return true;
                }
            });
            return true;
        } catch (IOException e) {
            loaded=false;
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
            loaded=false;
            notifyPlayComplete();
        }
    }

    @Override
    public void release() {
        if(isPlaying()){
            this.mediaPlayer.release();
            loaded=false;
            notifyRelease();
        }
    }

    @Override
    public boolean isPlaying() {
        if(loaded){
            return this.mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public int getProgress() {
        if(isLoaded()){
            return this.mediaPlayer.getCurrentPosition();
        }else{
            return 0;
        }

    }

    @Override
    public boolean jump(int rate) {
        if(isLoaded()){
            this.mediaPlayer.seekTo(rate);
            return true;
        }
        return false;
    }

    @Override
    public Music getMedia() {
        return this.music;
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
