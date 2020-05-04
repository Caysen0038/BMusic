package com.baokaicong.android.bmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.bean.DownloadInfo;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicList;
import com.baokaicong.android.bmusic.consts.ListenerTag;
import com.baokaicong.android.bmusic.service.listener.GlobalMusicPlayListener;
import com.baokaicong.android.bmusic.service.manager.MusicPlayManager;
import com.baokaicong.android.bmusic.service.remoter.MediaController;
import com.baokaicong.android.bmusic.service.player.StandardMusicPlayer;
import com.baokaicong.android.bmusic.service.player.MusicPlayer;
import com.baokaicong.android.bmusic.service.remoter.MusicRemoter;
import com.baokaicong.android.bmusic.service.remoter.MediaRemoter;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.bmusic.util.sql.DownloadSQLUtil;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayService extends Service implements MediaController<Music> {
    // 播放器
    private MusicPlayer musicPlayer;
    // 播放管理器
    private MusicPlayManager musicManager;
    // 播放器运行状态
    private boolean mediaRunning=false;
    // 是否自动播放
    private boolean autoPlay=false;
    // 进度通知Timer
    private Timer progressTimer;

    private DownloadSQLUtil downloadSQLUtil;
    // 媒体遥控器
    private MediaRemoter remoter;

    private ServiceMusicPlayerListener musicPlayerListener;

    private boolean watching=false;


    @Override
    public void onCreate() {
        super.onCreate();
        remoter=new MusicRemoter(this);
        musicPlayerListener=new ServiceMusicPlayerListener();
        downloadSQLUtil=new DownloadSQLUtil(this);
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
        musicPlayer= StandardMusicPlayer.newInstance();
        musicManager=new MusicPlayManager();
        musicPlayer.addMusicPlayerListner(this.musicPlayerListener);
        mediaRunning=true;
        BMContext.instance().getPlayInfo().setMusicList(musicManager.getMusicList());
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
        int i=-1;
        Music m=null;
        if((i=musicManager.getMusicPosition(music))!=-1){
            m=musicManager.getMusic(i);
        }else{
            musicManager.insertMusic(music,0);
            m=musicManager.getMusic(0);
        }
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

    /**
     * 加载播放音乐
     * @param m
     * @param auto
     */
    private void loadMusic(Music m,boolean auto){
        autoPlay=auto;
        String path=isMP3InfoExists(m);
        if(path!=null){
            musicPlayer.loadMedia(path,false);
            ToastUtil.showText(this,"播放本地音乐");
        }else{
            musicPlayer.loadMedia(m,false);
            ToastUtil.showText(this,"播放网络音乐");
        }

    }

    /**
     * 实时观察播放进度
     */
    private void watchProgress(){
        progressTimer=new Timer();
        progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(watching) {
                    int n=musicPlayer.getProgress();
                    notifyProgress(n/1000);
                }
            }
        },0,1000);
    }

    /**
     * 检查音乐文件是否存在于本地
     * @param music
     * @return
     */
    private String isMP3InfoExists(Music music){
        DownloadInfo info=downloadSQLUtil.getInfo(music.getMid());
        if(info==null){
            return null;
        }
        File file=new File(info.getPath());
        if(file.exists()){
            return info.getPath();
        }else{
            downloadSQLUtil.deleteInfo(music.getMid());
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaRunning){
            this.shutdown();
        }
        if(downloadSQLUtil!=null){
            downloadSQLUtil.close();
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
            watching=true;
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
            watching=false;
            next();
        }

        @Override
        public void onRelease() {
            watching=false;

        }

        @Override
        public void onError() {
            watching=false;
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

    /**
     * 通知播放
     * @param music
     */
    private void notifyPlay(Music music){
        BMContext.instance().getPlayInfo().setPlaying(true);
        List<GlobalMusicPlayListener> list=BMContext.instance().getListenerList(ListenerTag.MUSIC);
        synchronized (list){
            for(GlobalMusicPlayListener listener:list){
                listener.onPlay(music);
            }
        }
    }

    /**
     * 通知暂停
     * @param music
     */
    private void notifyPause(Music music){
        BMContext.instance().getPlayInfo().setPlaying(false);
        List<GlobalMusicPlayListener> list=BMContext.instance().getListenerList(ListenerTag.MUSIC);
        synchronized (list){
            for(GlobalMusicPlayListener listener:list){
                listener.onPause(music);
            }
        }
    }

    /**
     * 通知播放进度
     * @param p
     */
    private void notifyProgress(int p){
        BMContext.instance().getPlayInfo().setProgress(p);
        List<GlobalMusicPlayListener> list=BMContext.instance().getListenerList(ListenerTag.MUSIC);
        synchronized (list){
            for(GlobalMusicPlayListener listener:list){
                listener.progress(p);
            }
        }
    }

    /**
     * 通知切歌
     * @param music
     */
    private void notifySwitch(Music music){
        List<GlobalMusicPlayListener> list=BMContext.instance().getListenerList(ListenerTag.MUSIC);
        synchronized (list){
            for(GlobalMusicPlayListener listener:list){
                listener.onSwitch(music);
            }
        }
    }
}
