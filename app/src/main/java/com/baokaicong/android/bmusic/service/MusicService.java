package com.baokaicong.android.bmusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.request.BaseCallback;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.service.request.RequestUtil;
import com.baokaicong.android.bmusic.service.request.api.MenuAPI;
import com.baokaicong.android.bmusic.service.request.api.MusicAPI;

import java.util.List;

import retrofit2.Call;

public class MusicService extends Service {
    private MusicAPI musicAPI;
    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildAPI();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        CustomBinder binder=new CustomBinder(this);
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void search(String token, String keyword, RequestCallback<List<Music>> callback){
        Call<Result<List<Music>>> call=musicAPI.search(token,keyword);
        call.enqueue(new BaseCallback<>(callback));
    }

    private void buildAPI(){
        this.musicAPI= RequestUtil.Instance().buildeRequestAPI(this,MusicAPI.class);
    }
}
