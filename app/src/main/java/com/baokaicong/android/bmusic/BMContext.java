package com.baokaicong.android.bmusic;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BMContext {

    private Map<String, Activity> activityMap;
    private String dataRoot;
    private BMContext(){

    }
    public BMContext init(){
        activityMap=new HashMap<>();
        dataRoot=Environment.getExternalStorageDirectory().getPath()+File.separator+"BKC/";
        return this;
    }
    private static class Holder{
        private static final BMContext instance=new BMContext();
    }

    public static BMContext builder(){
        return Holder.instance.init();
    }

    public BMContext addActivity(String name, Activity activity){
        synchronized (this.activityMap){
            this.activityMap.put(name,activity);
        }

        return this;
    }

    public BMContext removeActivity(String name){
        synchronized (this.activityMap){
            this.activityMap.remove(name);
        }
        return this;
    }

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

    public String getDataRoot(){
        return this.dataRoot;
    }
}
