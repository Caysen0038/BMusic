package com.baokaicong.android.bmusic.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.consts.ListenerTag;
import com.baokaicong.android.bmusic.service.listener.GlobalMusicPlayListener;
import com.baokaicong.android.bmusic.service.remoter.command.JumpCommand;
import com.baokaicong.android.bmusic.ui.activity.MusicActivity;
import com.baokaicong.android.bmusic.ui.dialog.PlayListDialog;

import java.util.ArrayList;
import java.util.List;

public class BottomMusicView extends LinearLayout {
    private Context context;
    private ImageButton playButton;
    private ImageButton listButton;
    private ImageButton switchButton;
    private TextView songText;
    private TextView singerText;
    private List<BottomMusicListener> listenerList;
    private ObjectAnimator diskAnimator;
    private ImageView diskImg;
    private GlobalMusicPlayListener globalMusicPlayListener;
    private SeekBar progressBar;
    public interface BottomMusicListener{
        void onPlay();
        void onPause();
        void onJump(int rate);
        void onSwitch(Music music);
    }

    public BottomMusicView(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public BottomMusicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    public BottomMusicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initView();
    }

    private void initView(){
        listenerList=new ArrayList<>();
        LayoutInflater.from(context).inflate(R.layout.view_bottom_music,this,true);
        playButton=findViewById(R.id.bottom_music_play);
        switchButton=findViewById(R.id.bottom_music_switch);
        listButton=findViewById(R.id.bottom_music_list);
        songText=findViewById(R.id.bottom_music_song);
        singerText=findViewById(R.id.bottom_music_singer);
        progressBar=findViewById(R.id.bottom_progress_bar);

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

        diskImg=findViewById(R.id.bottom_disk_img);
        diskImg.setOnClickListener((v)->{
            if(BMContext.instance().getPlayInfo().getCurrentMusic()!=null){
                Intent intent=new Intent(getContext(), MusicActivity.class);
                getContext().startActivity(intent);
            }
        });
        diskAnimator=ObjectAnimator.ofFloat(diskImg, "rotation", 0.0f, 360.0f);
        diskAnimator.setDuration(5000);//设定转一圈的时间
        diskAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        diskAnimator.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        diskAnimator.setInterpolator(new LinearInterpolator());

        playButton.setOnClickListener((v)->{
            if(BMContext.instance().getPlayInfo().isPlaying()){
                notifyPause();
            }else{
                notifyPlay();
            }
        });

        listButton.setOnClickListener((v)->{
            PlayListDialog dialog=PlayListDialog.builder(getContext());
            dialog.show();
        });
        switchButton.setOnClickListener((v)->{
            notifySwitch(null);
        });
        globalMusicPlayListener=new BottomGlobalMusicPlayListener();
        BMContext.instance().registerListener(ListenerTag.MUSIC,globalMusicPlayListener);
        if(BMContext.instance().getPlayInfo().getCurrentMusic()!=null){
            globalMusicPlayListener.onSwitch(BMContext.instance().getPlayInfo().getCurrentMusic());
        }
        if(BMContext.instance().getPlayInfo().isPlaying()){
            globalMusicPlayListener.onPlay(BMContext.instance().getPlayInfo().getCurrentMusic());
        }


    }


    public void addBottomMusciListener(BottomMusicListener listener){
        synchronized (listenerList){
            listenerList.add(listener);
        }
    }
    public void removeBottomMusciListener(BottomMusicListener listener){
        synchronized (listenerList){
            listenerList.remove(listener);
        }
    }

    private void notifyPlay(){

        synchronized (listenerList){
            for(BottomMusicListener listener:listenerList){
                listener.onPlay();
            }
        }
    }

    private void notifyPause(){

        synchronized (listenerList){
            for(BottomMusicListener listener:listenerList){
                listener.onPause();
            }
        }
    }

    private void notifyJump(int rate){
        synchronized (listenerList){
            for(BottomMusicListener listener:listenerList){
                listener.onJump(rate);
            }
        }
    }

    private void notifySwitch(Music music){
        synchronized (listenerList){
            for(BottomMusicListener listener:listenerList){
                listener.onSwitch(music);
            }
        }
    }

    private class BottomGlobalMusicPlayListener implements GlobalMusicPlayListener{

        private boolean playing=false;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPlay(Music music) {
            playButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            if(playing){
                diskAnimator.resume();
            }else{
                diskAnimator.start();
                playing=true;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPause(Music music) {
            playButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            diskAnimator.pause();
        }

        @Override
        public void onSwitch(Music music) {
            songText.setText(music.getName());
            singerText.setText(music.getSinger());
            progressBar.setMax(music.getDuration());
            progressBar.setProgress(0);
            playing=false;
        }

        @Override
        public void progress(int p) {
            progressBar.setProgress(p);
        }
    }



    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        BMContext.instance().removeListener(ListenerTag.MUSIC,this);
    }

}
