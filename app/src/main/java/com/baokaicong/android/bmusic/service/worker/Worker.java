package com.baokaicong.android.bmusic.service.worker;

public interface Worker<T> {
    void start(T param);
    void cancel();
    void addListener(WorkerListener<T> listener);
    void removeListener(WorkerListener<T> listener);
    interface WorkerListener<T>{
        void onStart(T param);
        void onComplete(T param);
        void progress(int progress);
        void onError(T param);
    }
}
