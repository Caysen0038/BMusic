package com.baokaicong.android.bmusic.service.manager;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.consts.PlayMode;

import java.util.LinkedList;
import java.util.List;

public class MusicPlayManager {
    private List<Music> list;
    private int current;
    private PlayMode mode;
    public MusicPlayManager(){
        list=new LinkedList<>();
        current=0;
        mode=PlayMode.LIST;
    }

    public void insertMusic(Music music){
        list.add(music);
    }

    public void insertMusic(Music music,int i){
        list.add(i,music);
    }

    public void removeMusic(Music music){
        list.remove(music);
    }

    public void removeMusic(int i){
        list.remove(i);
    }

    public Music getCurrentMusic(){
        if(list.size()==0){
            return null;
        }
        return list.get(this.current);
    }

    public Music getNextMusic(){
        Music music=null;
        switch(mode){
            case REPEAT:
                if(list.size()>0){
                    music=list.get(current);
                }
            case LIST:
                current=++current%list.size();
                music=list.get(current);
                break;
            case NORMAL:
                if(list.size()>current+1) {
                    music=list.get(++current);
                }
                break;

        }
        return music;
    }

    public Music getPreMusic(){
        Music music=null;
        switch(mode){
            case REPEAT:
                if(list.size()>0){
                    music=list.get(current);
                }
            case LIST:
                if(current==0){
                    current=list.size();
                }
                current=(current%list.size())-1;
                music=list.get(current);
                break;
            case NORMAL:
                if(current-1>0) {
                    music=list.get(--current);
                }
                break;

        }
        return music;
    }

    public void loadList(List<Music> list){
        this.list.clear();
        for(Music m:list){
            this.list.add(m);
        }
    }

    public PlayMode getPlayMode(){
        return this.getPlayMode();
    }

    public void switchMode(PlayMode mode){
        switch (mode){
            case LIST:
            case NORMAL:
            case REPEAT:
                this.mode=mode;
            default:
                this.mode=PlayMode.LIST;
        }
    }

}
