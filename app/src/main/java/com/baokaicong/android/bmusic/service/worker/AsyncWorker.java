package com.baokaicong.android.bmusic.service.worker;

import android.os.AsyncTask;
import android.os.Handler;

import com.baokaicong.android.bmusic.bean.Music;

import java.util.ArrayList;
import java.util.List;

public abstract class AsyncWorker<T> extends AsyncTask<T,Integer, T> implements Worker<T> {
    private List<WorkerListener<T>> listenerList;
    private Handler handler;
    public AsyncWorker(){
        listenerList=new ArrayList<>();
        handler=new Handler();
    }

    @Override
    public void addListener(WorkerListener<T> listener) {
        if(!listenerList.contains(listener)){
            listenerList.add(listener);
        }
    }

    @Override
    public void removeListener(WorkerListener<T> listener) {
        listenerList.remove(listener);
    }

    protected void notifyStart(Music m){
        handler.post(()->{
            for(WorkerListener listener:listenerList){
                listener.onStart(m);
            }
        });
    }

    protected void notifyComplete(Music m){
        handler.post(()->{
            for(WorkerListener listener:listenerList){
                listener.onComplete(m);
            }
        });
    }

    protected void notifyError(Music m){
        handler.post(()->{
            for(WorkerListener listener:listenerList){
                listener.onError(m);
            }
        });
    }

    protected void reportProgress(int progress){
        handler.post(()->{
            for(WorkerListener listener:listenerList){
                listener.progress(progress);
            }
        });
    }

}
