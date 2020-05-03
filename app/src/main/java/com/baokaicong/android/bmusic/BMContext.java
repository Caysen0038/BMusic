package com.baokaicong.android.bmusic;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.PlayInfo;
import com.baokaicong.android.bmusic.bean.User;
import com.baokaicong.android.bmusic.consts.ListenerTag;
import com.baokaicong.android.bmusic.service.remoter.MediaRemoter;
import com.baokaicong.android.bmusic.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BMContext {

    private Map<String, Activity> activityMap;
    private String dataRoot;
    private User user;
    private int version=1;
    private Map<ListenerTag, List> listenerMap;
    private MediaRemoter remoter;
    private PlayInfo playInfo;
    private BMContext(){

    }

    /**
     * 初始化上下文
     * @return
     */
    public BMContext init(){
        activityMap=new HashMap<>();
        dataRoot=Environment.getExternalStorageDirectory().getPath()+File.separator+"BKC/";
        listenerMap=new HashMap<>();
        playInfo=new PlayInfo();
        return this;
    }
    private static class Holder{
        private static final BMContext instance=new BMContext(){{
            init();
        }};
    }

    public static BMContext instance(){
        return Holder.instance;
    }

    public void setCurrentUser(User user){
        this.user=user;
    }

    /**
     * 添加Activity
     * @param name
     * @param activity
     * @return
     */
    public BMContext addActivity(String name, Activity activity){
        synchronized (this.activityMap){
            this.activityMap.put(name,activity);
        }

        return this;
    }

    /**
     * 删除Activity
     * @param name
     * @return
     */
    public BMContext removeActivity(String name){
        synchronized (this.activityMap){
            this.activityMap.remove(name);
        }
        return this;
    }

    /**
     * 中止所有Activity
     * @return
     */
    public BMContext finishAllActivity(){
        synchronized (this.activityMap){
            for(Activity activity:this.activityMap.values()){
                try{
                    activity.finish();
                }catch (Exception e){

                }
            }
            this.activityMap.clear();
        }
        return this;
    }

    /**
     * 获取文件根路径
     * @return
     */
    public String getDataRoot(){
        return this.dataRoot;
    }

    /**
     * 获取当前登录用户，若未登录则返回NULL
     * @return
     */
    public User getUser(){
        return this.user;
    }

    /**
     * 获取系统版本
     * @return
     */
    public int getVersion(){
        return this.version;
    }

    /**
     * 注册全局监听器
     * @param tag
     * @param listener
     * @param <T>
     */
    public <T> void registerListener(ListenerTag tag,T listener){
        synchronized (listenerMap){
            if(listenerMap.containsKey(tag)){
                listenerMap.get(tag).add(listener);
            }else{
                List<T> list=new ArrayList<>();
                list.add(listener);
                listenerMap.put(tag,list);
            }
        }
    }

    /**
     * 获取监听器列表
     * @param tag
     * @param <T>
     * @return
     */
    public <T> List<T> getListenerList(ListenerTag tag){
        synchronized (listenerMap){
            if(listenerMap.containsKey(tag)){
                return listenerMap.get(tag);
            }
            return new ArrayList<>();
        }

    }

    public <T> void removeListener(ListenerTag tag,T listener){
        synchronized (listenerMap){
            if(listenerMap.containsKey(tag)){
                listenerMap.get(tag).remove(listener);
            }
        }
    }


    public MediaRemoter getRemoter(){
        return this.remoter;
    }

    public void setRemoter(MediaRemoter remoter){
        this.remoter=remoter;
    }

    public PlayInfo getPlayInfo(){
        return this.playInfo;
    }

}
