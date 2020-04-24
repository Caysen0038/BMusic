package com.baokaicong.android.bmusic.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.R;

import java.util.ArrayList;
import java.util.List;

public class BottomMusicView extends LinearLayout {

    private Context context;
    private ImageButton playButton;
    private ImageButton listButton;
    private TextView songText;
    private TextView singerText;
    private boolean playing=false;
    private List<BottomMusicListener> listenerList;

    public interface BottomMusicListener{
        void onPlay();
        void onPause();
        void onJump(int rate);
        void onSwitch(String name);
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
        listButton=findViewById(R.id.bottom_music_list);
        songText=findViewById(R.id.bottom_music_song);
        singerText=findViewById(R.id.bottom_music_singer);

        playButton.setOnClickListener((v)->{
            if(playing){
                notifyPause();
                playing=false;
            }else{
                notifyPlay();
                playing=true;
            }
        });

        listButton.setOnClickListener((v)->{
            notifySwitch(null);
        });
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

    private void notifySwitch(String name){
        synchronized (listenerList){
            for(BottomMusicListener listener:listenerList){
                listener.onSwitch(name);
            }
        }
    }
}
