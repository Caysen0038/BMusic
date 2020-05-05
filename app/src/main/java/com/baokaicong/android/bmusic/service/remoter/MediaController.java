package com.baokaicong.android.bmusic.service.remoter;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicList;
import com.baokaicong.android.bmusic.consts.PlayMode;

/**
 * 媒体控制器，负责处理媒体播放等操作
 * @param <T> 媒体类型
 * @author Caysen
 * @Date 2020 04 29
 */
public interface MediaController<T> {
    /**
     * 播放媒体开机
     */
    void open();

    /**
     * 播放媒体关机
     */
    void shutdown();

    /**
     * 加载列表
     * @param list
     */
    void load(MusicList list);

    /**
     * 添加媒体至列表指定序列
     * @param m
     * @param i
     */
    void add(T m,int i);

    /**
     * 播放列表媒体
     */
    void play();

    /**
     * 播放列表中指定序列媒体
     * @param i
     */
    void play(int i);

    /**
     * 播放指定媒体，若列表不存在该媒体则添加后播放
     * @param music
     */
    void play(Music music);

    /**
     * 暂停
     */
    void pause();

    /**
     * 播放下一个媒体
     */
    void next();

    /**
     * 播放上一个媒体
     */
    void pre();

    /**
     * 跳转进度
     * @param rate
     */
    void jump(int rate);

    /**
     * 切换播放模式
     * @param mode
     */
    void playMode(PlayMode mode);
}
