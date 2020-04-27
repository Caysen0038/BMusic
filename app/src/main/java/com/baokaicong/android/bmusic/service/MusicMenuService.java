package com.baokaicong.android.bmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.service.request.RequestUtil;
import com.baokaicong.android.bmusic.service.request.api.MenuAPI;
import com.baokaicong.android.bmusic.util.sql.MusicMenuSQLUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicMenuService extends Service {
    private MenuAPI menuAPI;
    private MusicMenuSQLUtil musicMenuSQLUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        musicMenuSQLUtil=new MusicMenuSQLUtil(this);
        buildAPI();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        CustomBinder binder=new CustomBinder(this);
        return binder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取歌单列表
     * @param token
     * @param callback
     */
    public void syncMenus(String token, RequestCallback<List<MusicMenu>> callback){
        Call<Result<List<MusicMenu>>> call=menuAPI.getUserMenus(token);
        call.enqueue(new Callback<Result<List<MusicMenu>>>() {
            @Override
            public void onResponse(Call<Result<List<MusicMenu>>> call, Response<Result<List<MusicMenu>>> response) {
                Result<List<MusicMenu>> result=response.body();
                if(result!=null && result.getCode().equals("000000") && result.getData()!=null){
                    musicMenuSQLUtil.insertMenuList(result.getData());
                }
                callback.handleResult(result);
            }

            @Override
            public void onFailure(Call<Result<List<MusicMenu>>> call, Throwable t) {
                callback.handleError(t);
            }
        });
    }

    private void buildAPI(){
        this.menuAPI= RequestUtil.Instance().buildeRequestAPI(MenuAPI.class);
    }
}
