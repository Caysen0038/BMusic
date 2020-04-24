package com.baokaicong.android.bmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
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

    private Thread lifeThread;

    private boolean protecting=false;

    private boolean autoPlay=false;

    private MediaRemoter remoter;

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer=HTTPMusicPlayer.instance();
        remoter=new MusicRemoter(this);
        musicManager=new MusicPlayManager();
        //MusicRemoter.instance().addMedia(this);
        musicPlayer.addMusicPlayerListner(new ServiceMusicPlayerListener());
        Log.i("service","初始化");
    }


    @Override
    public IBinder onBind(Intent intent) {

        MusicBinder musicBinder=new MusicBinder(this.remoter);
        Log.i("service","绑定");
        return musicBinder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.i("service","解绑");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("service","执行命令");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void load(List<Music> music) {
        //musicPlayer.loadMedia(music,loop);
        musicManager.loadList(music);
        Log.i("service","加载列表");
    }

    @Override
    public void add(Music m, int i) {
        if(i<0){
            musicManager.insertMusic(m);
        }else{
            musicManager.insertMusic(m,i);
        }
        Log.i("service","添加");
    }

    @Override
    public void play() {
        if(musicPlayer.isPlaying()){
            musicPlayer.play();
        }else{
            Music m=musicManager.getCurrentMusic();
            loadMusic(m,true);
        }
        Log.i("service","播放");
    }

    @Override
    public void pause() {
        musicPlayer.pause();
        Log.i("service","暂停");
    }

    @Override
    public void next() {
        Music m=musicManager.getNextMusic();
        loadMusic(m,true);
        Log.i("service","下一首");
    }

    @Override
    public void pre() {
        Music m=musicManager.getPreMusic();
        loadMusic(m,true);
        Log.i("service","上一首");
    }


    @Override
    public void jump(int rate) {
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
        Log.i("service","销毁");
        this.musicPlayer.release();
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
