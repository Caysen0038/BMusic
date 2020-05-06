package com.baokaicong.android.bmusic.service.manager;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicList;
import com.baokaicong.android.bmusic.consts.PlayMode;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayManager {
    private String musicListId;
    private List<Music> musicList;
    private int current;
    private PlayMode mode;
    public MusicPlayManager(){
        musicList=new ArrayList<>();
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
                musicList.add(i,music);
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
                break;
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
                break;
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
        if(!list.getId().equals(this.musicListId)){
            this.musicList.clear();
            for(Music m:list.getList()){
                this.musicList.add(m);
            }
            this.musicListId=list.getId();
        }
    }

    /**
     * 获取播放模式
     * @return
     */
    public PlayMode getPlayMode(){
        return this.mode;
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
                break;
            default:
                this.mode=PlayMode.LIST;
                break;
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

    public List<Music> getMusicList(){
        return this.musicList;
    }

    public boolean contains(Music music){
        return this.musicList.contains(music);
    }

    public int getMusicPosition(Music music){
        return this.musicList.indexOf(music);
    }

}
