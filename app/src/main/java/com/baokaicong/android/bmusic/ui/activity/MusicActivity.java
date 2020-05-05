package com.baokaicong.android.bmusic.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.DownloadInfo;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.consts.ListenerTag;
import com.baokaicong.android.bmusic.consts.PlayMode;
import com.baokaicong.android.bmusic.service.listener.GlobalMusicPlayListener;
import com.baokaicong.android.bmusic.service.remoter.command.JumpCommand;
import com.baokaicong.android.bmusic.service.remoter.command.NextCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PauseCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PlayCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PlayModeCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PreCommand;
import com.baokaicong.android.bmusic.service.worker.MusicDownloadWorker;
import com.baokaicong.android.bmusic.service.worker.Worker;
import com.baokaicong.android.bmusic.ui.dialog.PlayListDialog;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.bmusic.util.sql.DownloadSQLUtil;

import java.io.File;

public class MusicActivity extends AppCompatActivity {
    private ImageButton back,share;
    private TextView musicName,musicSinger,startTime,endTime;
    private ImageView collectButton,downloadButton,menuButton;
    private SeekBar progressBar;
    private ImageView modeButton,preButton,nextButton,playButton,songListButton;
    private Handler handler;
    private GlobalMusicPlayListener globalMusicPlayListener;
    private ImageView diskImg;
    private ObjectAnimator diskAnimation;
    private DownloadSQLUtil downloadSQLUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        handler=new Handler();
        downloadSQLUtil=new DownloadSQLUtil(this);
        init();
    }

    private void init(){
        back=findViewById(R.id.music_back);
        share=findViewById(R.id.music_share);
        musicName=findViewById(R.id.music_name);
        musicSinger=findViewById(R.id.music_singer);
        startTime=findViewById(R.id.music_start_time);
        endTime=findViewById(R.id.music_end_time);
        collectButton=findViewById(R.id.music_collect);
        downloadButton=findViewById(R.id.music_download);
        menuButton=findViewById(R.id.music_menu);
        progressBar=findViewById(R.id.music_progress_bar);
        modeButton=findViewById(R.id.music_play_mode);
        preButton=findViewById(R.id.music_pre);
        nextButton=findViewById(R.id.music_next);
        playButton=findViewById(R.id.music_play);
        songListButton=findViewById(R.id.music_song_list);
        diskImg=findViewById(R.id.music_disk_img);

        diskAnimation=ObjectAnimator.ofFloat(diskImg, "rotation", 0.0f, 360.0f);
        diskAnimation.setDuration(5000);//设定转一圈的时间
        diskAnimation.setRepeatCount(Animation.INFINITE);//设定无限循环
        diskAnimation.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        diskAnimation.setInterpolator(new LinearInterpolator());

        modeButton.setImageResource(getModeImage());

        globalMusicPlayListener=new BottomGlobalMusicPlayListener();
        BMContext.instance().registerListener(ListenerTag.MUSIC,globalMusicPlayListener);



        playButton.setOnClickListener((v)->{
            if(BMContext.instance().getPlayInfo().isPlaying()){
                BMContext.instance().getRemoter().command(new PauseCommand(),null);
            }else{
                BMContext.instance().getRemoter().command(new PlayCommand(),null);
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean touching=false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(touching){
                    BMContext.instance().getRemoter().command(new JumpCommand(),progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                touching=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                touching=false;
            }
        });

        preButton.setOnClickListener((v)->{BMContext.instance().getRemoter().command(new PreCommand(),null);});
        nextButton.setOnClickListener((v)->{BMContext.instance().getRemoter().command(new NextCommand(),null);});
        modeButton.setOnClickListener((v)->{
            PopupMenu popupMenu = new PopupMenu(MusicActivity.this, modeButton);
            popupMenu.getMenuInflater().inflate(R.menu.dialog_mode_item, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    PlayMode mode=null;
                    int img=0;
                    switch (item.getItemId()){
                        case R.id.list:
                            mode=PlayMode.LIST;
                            img=R.drawable.icon_mode_list_30;
                            break;
                        case R.id.single:
                            mode=PlayMode.REPEAT;
                            img=R.drawable.icon_mode_repeate_30;
                            break;
                        case R.id.normal:
                            mode=PlayMode.NORMAL;
                            img=R.drawable.icon_mode_normal_30;
                            break;
                    }
                    modeButton.setImageResource(img);
                    BMContext.instance().getRemoter().command(new PlayModeCommand(),mode);
                    return true;
                }
            });
        });
        songListButton.setOnClickListener((v)->{
            PlayListDialog dialog=PlayListDialog.builder(this);
            dialog.show();
        });

        downloadButton.setOnClickListener((v)->{downloadMP3();});
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(BMContext.instance().getPlayInfo().getCurrentMusic()!=null){
            globalMusicPlayListener.onSwitch(BMContext.instance().getPlayInfo().getCurrentMusic());
        }
        if(BMContext.instance().getPlayInfo().isPlaying()){
            globalMusicPlayListener.onPlay(BMContext.instance().getPlayInfo().getCurrentMusic());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BMContext.instance().removeListener(ListenerTag.MUSIC,globalMusicPlayListener);
    }

    /**
     * 下载音乐
     */
    private void downloadMP3(){
        Music music=BMContext.instance().getPlayInfo().getCurrentMusic();
        if(music!=null){
            if(isMP3InfoExists(music)){
                ToastUtil.showText(this,music.getName()+"已下载");
            }else{
                String path=buildMusicFileName(music);
                DownloadInfo info=new DownloadInfo()
                        .setUserId(BMContext.instance().getUser().getId())
                        .setDate(System.currentTimeMillis()+"")
                        .setMid(music.getMid())
                        .setPath(path);
                downloadSQLUtil.insertInfo(info);
                MusicDownloadWorker worker=new MusicDownloadWorker();
                worker.addListener(new Worker.WorkerListener<Music>() {
                    @Override
                    public void onStart(Music param) {
                        ToastUtil.showText(MusicActivity.this,param.getName()+"开始下载");
                    }

                    @Override
                    public void onComplete(Music param) {
                        ToastUtil.showText(MusicActivity.this,param.getName()+"下载完成");
                    }

                    @Override
                    public void progress(int progress) {

                    }

                    @Override
                    public void onError(Music param) {

                    }
                });
                worker.download(music,path);
            }
        }
    }

    /**
     * 判断MP3是否本地存在
     * @param music
     * @return
     */
    private boolean isMP3InfoExists(Music music){
        DownloadInfo info=downloadSQLUtil.getInfo(music.getMid());
        if(info==null){
            return false;
        }
        File file=new File(info.getPath());
        if(file.exists()){
            return true;
        }else{
            downloadSQLUtil.deleteInfo(music.getMid());
            return false;
        }
    }

    private class BottomGlobalMusicPlayListener implements GlobalMusicPlayListener {
        private boolean playing=false;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPlay(Music music) {
            playButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            if(playing){
                diskAnimation.resume();
            }else{
                diskAnimation.start();
                playing=true;
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPause(Music music) {
            playButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            diskAnimation.pause();

        }

        @Override
        public void onSwitch(Music music) {
            musicName.setText(music.getName());
            musicSinger.setText(music.getSinger());
            startTime.setText("0:00");
            endTime.setText(parseTime(music.getDuration()));
            progressBar.setMax(music.getDuration());
            progressBar.setProgress(0);
            playing=false;
        }

        @Override
        public void progress(int p) {
            progressBar.setProgress(p);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    startTime.setText(parseTime(p));
                }
            });

        }
    }


    private String parseTime(int n){
        int m=n/60;
        int s=n%50;
        if(s<10){
            return m+":0"+s;
        }else{
            return m+":"+s;
        }

    }

    private String buildMusicFileName(Music music){
        return BMContext.instance().getDataRoot()+"Download/Music/"+music.getName()+" - "+music.getSinger()+"."+music.getSuffix();
    }

    private int getModeImage(){
        int img=0;
        switch (BMContext.instance().getPlayInfo().getMode()){
            case LIST:
                img=R.drawable.icon_mode_list_30;
                break;
            case REPEAT:
                img=R.drawable.icon_mode_repeate_30;
                break;
            case NORMAL:
                img=R.drawable.icon_mode_normal_30;
                break;
        }
        return img;
    }
}
