package com.baokaicong.android.bmusic.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.DownloadInfo;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicList;
import com.baokaicong.android.bmusic.consts.ListenerTag;
import com.baokaicong.android.bmusic.consts.PlayMode;
import com.baokaicong.android.bmusic.consts.PropertyField;
import com.baokaicong.android.bmusic.service.listener.GlobalMusicPlayListener;
import com.baokaicong.android.bmusic.service.manager.MusicPlayManager;
import com.baokaicong.android.bmusic.service.remoter.MediaController;
import com.baokaicong.android.bmusic.service.player.StandardMusicPlayer;
import com.baokaicong.android.bmusic.service.player.MusicPlayer;
import com.baokaicong.android.bmusic.service.remoter.MusicRemoter;
import com.baokaicong.android.bmusic.service.remoter.MediaRemoter;
import com.baokaicong.android.bmusic.ui.activity.MainActivity;
import com.baokaicong.android.bmusic.util.GsonUtil;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.bmusic.util.sql.DownloadSQLUtil;
import com.baokaicong.android.bmusic.util.sql.PropertySQLUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayService extends Service implements MediaController<Music> {
    private static final String NOTIFICATION_CHANNEL_ID="BM_19951102";
    private static final int NOTIFICATION_ID=951102;
    private static final String RECEIVER_REMOTE_ACTION="com.baokaicong.music.receiver.remote";
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

    private ServicePhoneStateListener phoneStateListener;
    private boolean open=false;
    private boolean watching=false;

    private Notification notification;
    private RemoteViews notificationViews;
    private NotificationManager notificationManager;
    private PropertySQLUtil propertySQLUtil;
    private boolean localMusic=false;

    @Override
    public void onCreate() {
        super.onCreate();
        remoter=new MusicRemoter(this);
        musicPlayerListener=new ServiceMusicPlayerListener();
        phoneStateListener=new ServicePhoneStateListener();

        // 监听通话
        TelephonyManager manager = (TelephonyManager) this
                .getSystemService(TELEPHONY_SERVICE);
        manager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

        downloadSQLUtil=new DownloadSQLUtil(this);
        propertySQLUtil=new PropertySQLUtil(this);

        initNotification();
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

    private void initNotification(){
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel =null;
        notificationViews=new RemoteViews(getPackageName(), R.layout.notification_main_control);
        // 点击显示主页面
        Intent intent=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationViews.setOnClickPendingIntent(R.id.notification_container,pendingIntent);
        // 下一首
        intent=new Intent(RECEIVER_REMOTE_ACTION);
        intent.putExtra("remote","next");
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationViews.setOnClickPendingIntent(R.id.notification_next,pendingIntent);
        // 上一首
        intent=new Intent(RECEIVER_REMOTE_ACTION);
        intent.putExtra("remote","pre");
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationViews.setOnClickPendingIntent(R.id.notification_pre,pendingIntent);
        // 播放暂停
        intent=new Intent(RECEIVER_REMOTE_ACTION);
        intent.putExtra("remote","play");
        pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationViews.setOnClickPendingIntent(R.id.notification_play,pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel =new NotificationChannel(NOTIFICATION_CHANNEL_ID, "BMusic", NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(null,null);
            notificationManager.createNotificationChannel(channel);
            notification = new Notification.Builder(this)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setCustomContentView(notificationViews)
                    .setSmallIcon(R.drawable.icon_bm_trans_32)
                    .setOngoing(true)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setPriority(Notification.PRIORITY_MAX)
                    .build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setCustomContentView(notificationViews)
                    .setSmallIcon(R.drawable.icon_bm_trans_32)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setOngoing(true);
            notification = notificationBuilder.build();
        }
        refreshNotification();
    }

    private void refreshNotification(){
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void open() {
        musicPlayer= StandardMusicPlayer.newInstance();
        musicManager=new MusicPlayManager();
        musicPlayer.addMusicPlayerListner(this.musicPlayerListener);
        mediaRunning=true;
        BMContext.instance().getPlayInfo().setMusicList(musicManager.getMusicList());
        BMContext.instance().getPlayInfo().setMode(musicManager.getPlayMode());
        watchProgress();
        loadHistory();
        open=false;
    }

    @Override
    public void shutdown() {
        mediaRunning=false;
        musicPlayer.removeMusicPlayerListner(this.musicPlayerListener);
        musicPlayer.release();
        progressTimer.cancel();
        saveHistoryMusic();
        saveHistoryList();
        open=false;
    }

    @Override
    public void load(MusicList list) {
        if(!mediaRunning)
            return;
        musicManager.loadList(list);
        BMContext.instance().getPlayInfo().setMusicListId(list.getId());
        saveHistoryList();
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

    @Override
    public void playMode(PlayMode mode) {
        musicManager.switchMode(mode);
        BMContext.instance().getPlayInfo().setMode(musicManager.getPlayMode());
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
            BMContext.instance().getPlayInfo().setLocalMusic(true);
        }else{
            musicPlayer.loadMedia(m,false);
            BMContext.instance().getPlayInfo().setLocalMusic(false);
        }
        saveHistoryMusic();
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

    /**
     * 加载播放历史
     */
    private void loadHistory(){
        String str=propertySQLUtil.getProperty(PropertyField.LAST_MUSIC_LIST);
        Log.i("播放列表",str);
        if(str!=null){
            try{
                JsonObject json= (JsonObject) new JsonParser().parse(str);
                List<Music> list=new ArrayList<>();
                List<String> listjson=GsonUtil.builder().fromJson(
                        json.getAsJsonPrimitive("list").getAsString(),
                        ArrayList.class);
                for(int i=0;i<listjson.size();i++){
                    list.add(GsonUtil.builder().fromJson(
                            GsonUtil.builder().toJson(listjson.get(i)),
                            Music.class));
                }
                String id=GsonUtil.builder().fromJson(
                        json.getAsJsonPrimitive("id").getAsString(),
                        String.class);
                MusicList musicList=new MusicList();
                musicList.setList(list);
                musicList.setId(id);
                load(musicList);
            }catch (Exception e){
                Log.i("播放列表","加载错误");
                e.printStackTrace();
            }

        }
        str=propertySQLUtil.getProperty(PropertyField.LAST_MUSIC);
        Log.i("播放音乐",str);
        if(str!=null){
            try{
                Music music=GsonUtil.builder().fromJson(str, Music.class);
                musicManager.setCurrentMusic(music);
                loadMusic(music,false);
            }catch (Exception e){
                Log.i("播放音乐","加载错误");
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存历史列表
     */
    private void saveHistoryList(){
        new Thread(()->{
            List<Music> list=musicManager.getMusicList();
            if(list!=null){
                Music[] musics=new Music[list.size()];
                musics=list.toArray(musics);
                String id=musicManager.getMusicListId();
                JsonObject json=new JsonObject();
                json.addProperty("id",id);
                json.addProperty("list",GsonUtil.builder().toJson(musics));
                propertySQLUtil.insertProperty(PropertyField.LAST_MUSIC_LIST, json.toString());
            }
        }).start();

    }

    /**
     * 保存历史音乐
     */
    private void saveHistoryMusic(){
        new Thread(()->{
            Music current=musicManager.getCurrentMusic();
            if(current!=null){
                propertySQLUtil.insertProperty(PropertyField.LAST_MUSIC, GsonUtil.builder().toJson(current));
            }
        }).start();
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
        if(propertySQLUtil!=null){
            if(open){
                saveHistoryList();
                saveHistoryMusic();
            }
            propertySQLUtil.close();
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
            notifyPause(musicPlayer.getMedia());
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

    /**
     * 通话状态监听器
     */
    private class ServicePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE:  // 闲置
                    if(BMContext.instance().getPlayInfo().isInterrupt()){
                        play();
                        BMContext.instance().getPlayInfo().setInterrupt(false);
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:  // 响铃

                case TelephonyManager.CALL_STATE_OFFHOOK:  // 接听
                    if(BMContext.instance().getPlayInfo().isPlaying()){
                        BMContext.instance().getPlayInfo().setInterrupt(true);
                        pause();
                    }
                    break;
            }
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
        notificationViews.setImageViewResource(R.id.notification_play,android.R.drawable.ic_media_pause);
        refreshNotification();
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
        notificationViews.setImageViewResource(R.id.notification_play,android.R.drawable.ic_media_play);
        refreshNotification();
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
        notificationViews.setTextViewText(R.id.notification_music,music.getName());
        notificationViews.setTextViewText(R.id.notification_singer,music.getSinger());
        refreshNotification();
    }
}
