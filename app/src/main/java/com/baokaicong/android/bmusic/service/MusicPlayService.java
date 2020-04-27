package com.baokaicong.android.bmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.manager.MusicPlayManager;
import com.baokaicong.android.bmusic.service.remoter.RemoteReceiver;
import com.baokaicong.android.bmusic.service.player.HTTPMusicPlayer;
import com.baokaicong.android.bmusic.service.player.MusicPlayer;
import com.baokaicong.android.bmusic.service.remoter.MusicRemoter;
import com.baokaicong.android.bmusic.service.remoter.MediaRemoter;

import java.io.FileDescriptor;
import java.util.List;

public class MusicPlayService extends Service implements RemoteReceiver<Music> {

    private MusicPlayer musicPlayer;

    private MusicPlayManager musicManager;

    private boolean mediaRunning=false;

    private boolean autoPlay=false;

    private MediaRemoter remoter;
    private ServiceMusicPlayerListener musicPlayerListener;

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
    }

    @Override
    public void shutdown() {
        mediaRunning=false;
        musicPlayer.removeMusicPlayerListner(this.musicPlayerListener);
        musicPlayer.release();

    }

    @Override
    public void load(List<Music> music) {
        if(!mediaRunning)
            return;
        musicManager.loadList(music);
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
        Log.i("音乐服务","播放");
        if(!mediaRunning)
            return;
        if(musicPlayer.isPlaying()){
            musicPlayer.play();

        }else{
            Music m=musicManager.getCurrentMusic();
            loadMusic(m,true);
        }
        Log.i("音乐服务","播放成功");
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
        loadMusic(m,true);
    }

    @Override
    public void pre() {
        if(!mediaRunning)
            return;
        Music m=musicManager.getPreMusic();
        loadMusic(m,true);
    }


    @Override
    public void jump(int rate) {
        if(!mediaRunning)
            return;
        musicPlayer.jump(rate);
    }

    private void loadMusic(Music m,boolean auto){
        autoPlay=auto;
        musicPlayer.loadMedia(m,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaRunning){
            this.shutdown();
        }

    }
    int n=0;
    /**
     * 播放器监听器
     */
    private class ServiceMusicPlayerListener implements MusicPlayer.MusicPlayerListner {

        @Override
        public void onLoadComplete() {
            if(autoPlay){
                musicPlayer.play();
            }
        }

        @Override
        public void onPlay() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onPlayComplete() {
            next();
        }

        @Override
        public void onRelease() {

        }

        @Override
        public void onError() {

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

}
