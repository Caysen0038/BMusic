package com.baokaicong.android.bmusic.service.manager;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicList;
import com.baokaicong.android.bmusic.consts.PlayMode;

import java.util.LinkedList;
import java.util.List;

public class MusicPlayManager {
    private MusicList musicList;
    private int current;
    private PlayMode mode;
    public MusicPlayManager(){
        musicList=new MusicList();
        current=0;
        mode=PlayMode.LIST;
    }

    /**
     * 插入指定音乐至末尾
     * @param music
     */
    public void insertMusic(Music music){
        musicList.add(music);
    }

    /**
     * 插入指定音乐至指定位置
     * @param music
     * @param i
     */
    public void insertMusic(Music music,int i){
        switch(i){
            case -1:
                musicList.add(music);
                break;
            case 0:

            default:
                musicList.add(music,i);
                break;
        }

    }

    /**
     * 移除指定音乐
     * @param music
     */
    public void removeMusic(Music music){
        musicList.remove(music);
    }

    /**
     * 移除指定位置的音乐
     * @param i
     */
    public void removeMusic(int i){
        musicList.remove(i);
    }

    /**
     * 获取当前音乐
     * @return
     */
    public Music getCurrentMusic(){
        if(musicList.size()==0){
            return null;
        }
        return musicList.get(this.current);
    }

    /**
     * 获取下一首音乐
     * @return
     */
    public Music getNextMusic(){
        Music music=null;
        switch(mode){
            case REPEAT:
                if(musicList.size()>0){
                    music=musicList.get(current);
                }
            case LIST:
                if(musicList.size()>0){
                    current=++current%musicList.size();
                    music=musicList.get(current);
                }
                break;
            case NORMAL:
                if(musicList.size()>current+1) {
                    music=musicList.get(++current);
                }
                break;

        }
        return music;
    }

    /**
     * 获取前一首音乐
     * @return
     */
    public Music getPreMusic(){
        Music music=null;
        switch(mode){
            case REPEAT:
                if(musicList.size()>0){
                    music=musicList.get(current);
                }
            case LIST:
                if(current==0){
                    current=musicList.size();
                }
                current=current-1;
                music=musicList.get(current);
                break;
            case NORMAL:
                if(current-1>0) {
                    music=musicList.get(--current);
                }
                break;

        }
        return music;
    }

    /**
     * 更换加载指定音乐列表
     * @param list
     */
    public void loadList(MusicList list){
        if(this.musicList==null || !this.musicList.getId().equals(list.getId())){
            this.musicList.clear();
            for(Music m:list.getList()){
                this.musicList.add(m);
            }
        }
    }

    /**
     * 获取播放模式
     * @return
     */
    public PlayMode getPlayMode(){
        return this.getPlayMode();
    }

    /**
     * 切换播放模式
     * @param mode
     */
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

    /**
     * 设置并获取指定索引位的活动音乐
     * @param i
     * @return
     */
    public Music getMusic(int i){
        if(i>=0 && i<musicList.size()){
            current=i;
            return musicList.get(i);
        }
        return null;
    }

    /**
     * 获取当前索引位
     * @return
     */
    public int getCurrent(){
        return this.current;
    }
}
