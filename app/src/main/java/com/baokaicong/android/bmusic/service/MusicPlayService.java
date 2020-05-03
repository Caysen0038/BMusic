package com.baokaicong.android.bmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicList;
import com.baokaicong.android.bmusic.consts.ListenerTag;
import com.baokaicong.android.bmusic.service.listener.GlobalMusicPlayListener;
import com.baokaicong.android.bmusic.service.manager.MusicPlayManager;
import com.baokaicong.android.bmusic.service.remoter.MediaController;
import com.baokaicong.android.bmusic.service.player.HTTPMusicPlayer;
import com.baokaicong.android.bmusic.service.player.MusicPlayer;
import com.baokaicong.android.bmusic.service.remoter.MusicRemoter;
import com.baokaicong.android.bmusic.service.remoter.MediaRemoter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lombok.SneakyThrows;

public class MusicPlayService extends Service implements MediaController<Music> {

    private MusicPlayer musicPlayer;

    private MusicPlayManager musicManager;

    private boolean mediaRunning=false;

    private boolean autoPlay=false;

    private Timer progressTimer;

    private MediaRemoter remoter;
    private ServiceMusicPlayerListener musicPlayerListener;
    private boolean wathcing=false;


    @Override
    public void onCreate() {
        super.onCreate();
        remoter=new MusicRemoter(this);
        musicPlayerListener=new ServiceMusicPlayerListener();
    }


    @Override
    public IBinder onBind(Intent intent) {
        MusicBinder musicBinder=new MusicBinder(this.remoter);
        return musicBinder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void open() {
        musicPlayer=HTTPMusicPlayer.instance();
        musicManager=new MusicPlayManager();
        musicPlayer.addMusicPlayerListner(this.musicPlayerListener);
        mediaRunning=true;
        watchProgress();
    }

    @Override
    public void shutdown() {
        mediaRunning=false;
        musicPlayer.removeMusicPlayerListner(this.musicPlayerListener);
        musicPlayer.release();
        progressTimer.cancel();
    }

    @Override
    public void load(MusicList list) {
        if(!mediaRunning)
            return;
        musicManager.loadList(list);
    }

    @Override
    public void add(Music m, int i) {
        if(!mediaRunning)
            return;
        if(i<0){
            musicManager.insertMusic(m);
        }else{
            musicManager.insertMusic(m,i);
        }
    }

    @Override
    public void play() {
        if(!mediaRunning)
            return;
        if(musicPlayer.isLoaded()){
            musicPlayer.play();
        }else{
            Music m=musicManager.getCurrentMusic();
            if(m!=null){

                loadMusic(m,true);
            }
        }
    }

    @Override
    public void play(int i) {
        if(!mediaRunning){
            return;
        }

        Music m=musicManager.getMusic(i);
        if(m!=null && !m.equals(BMContext.instance().getPlayInfo().getCurrentMusic())){
            loadMusic(m,true);
        }

    }

    @Override
    public void play(Music music) {
        if(!mediaRunning){
            return;
        }
        musicManager.insertMusic(music,0);
        Music m=musicManager.getMusic(0);
        if(m!=null && !m.equals(BMContext.instance().getPlayInfo().getCurrentMusic())){
            loadMusic(m,true);
        }

    }

    @Override
    public void pause() {
        if(!mediaRunning)
            return;
        musicPlayer.pause();
    }

    @Override
    public void next() {
        if(!mediaRunning)
            return;
        Music m=musicManager.getNextMusic();
        if(m!=null){

            loadMusic(m,true);
        }

    }

    @Override
    public void pre() {
        if(!mediaRunning)
            return;
        Music m=musicManager.getPreMusic();
        if(m!=null){

            loadMusic(m,true);
        }
    }


    @Override
    public void jump(int rate) {
        if(!mediaRunning)
            return;
        musicPlayer.jump(rate*1000);
    }

    private void loadMusic(Music m,boolean auto){
        autoPlay=auto;
        musicPlayer.loadMedia(m,false);
    }

    /**
     * 实时观察播放进度
     */
    private void watchProgress(){
        progressTimer=new Timer();
        progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(wathcing) {
                    int n=musicPlayer.getProgress();
                    notifyProgress(n/1000);
                }
            }
        },0,1000);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaRunning){
            this.shutdown();
        }

    }

    /**
     * 播放器监听器
     */
    private class ServiceMusicPlayerListener implements MusicPlayer.MusicPlayerListner {

        @Override
        public void onLoadComplete() {
            notifySwitch(musicManager.getCurrentMusic());
            BMContext.instance().getPlayInfo().setCurrentMusic(musicManager.getCurrentMusic());
            wathcing=true;
            if(autoPlay){
                musicPlayer.play();
            }
        }

        @Override
        public void onPlay() {
            notifyPlay(musicPlayer.getMedia());
        }

        @Override
        public void onPause() {
            notifyPause(musicPlayer.getMedia());
        }

        @Override
        public void onPlayComplete() {
            wathcing=false;
            next();
        }

        @Override
        public void onRelease() {
            wathcing=false;

        }

        @Override
        public void onError() {
            wathcing=false;
        }

    }

    public static class MusicBinder extends Binder {
        private MediaRemoter remoter;

        private MusicBinder(MediaRemoter remoter){
            this.remoter=remoter;
        }

        public MediaRemoter getRemoter(){
            return this.remoter;
        }

    }

    private void notifyPlay(Music music){
        BMContext.instance().getPlayInfo().setPlaying(true);
        List<GlobalMusicPlayListener> list=BMContext.instance().getListenerList(ListenerTag.MUSIC);
        synchronized (list){
            for(GlobalMusicPlayListener listener:list){
                listener.onPlay(music);
            }
        }
    }

    private void notifyPause(Music music){
        BMContext.instance().getPlayInfo().setPlaying(false);
        List<GlobalMusicPlayListener> list=BMContext.instance().getListenerList(ListenerTag.MUSIC);
        synchronized (list){
            for(GlobalMusicPlayListener listener:list){
                listener.onPause(music);
            }
        }
    }

    private void notifyProgress(int p){
        BMContext.instance().getPlayInfo().setProgress(p);
        List<GlobalMusicPlayListener> list=BMContext.instance().getListenerList(ListenerTag.MUSIC);
        synchronized (list){
            for(GlobalMusicPlayListener listener:list){
                listener.progress(p);
            }
        }
    }

    private void notifySwitch(Music music){
        List<GlobalMusicPlayListener> list=BMContext.instance().getListenerList(ListenerTag.MUSIC);
        synchronized (list){
            for(GlobalMusicPlayListener listener:list){
                listener.onSwitch(music);
            }
        }
    }
}
