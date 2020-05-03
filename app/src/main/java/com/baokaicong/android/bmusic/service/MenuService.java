package com.baokaicong.android.bmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.request.BaseCallback;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.service.request.RequestUtil;
import com.baokaicong.android.bmusic.service.request.api.MenuAPI;
import com.baokaicong.android.bmusic.util.sql.MusicMenuSQLUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuService extends Service {
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


    public void getMenuMusics(String token,String meid, RequestCallback<List<Music>> callback){
        Call<Result<List<Music>>> call=menuAPI.getMenuMusics(token,meid);
        call.enqueue(new BaseCallback<>(callback));
    }

    public void createMenu(String token,String name,RequestCallback<Boolean> callback){
        Call<Result<Boolean>> call=menuAPI.addMenu(token,name);
        call.enqueue(new BaseCallback<>(callback));
    }

    public void renameMenu(String token,String meid,String name,RequestCallback<Boolean> callback){
        Call<Result<Boolean>> call=menuAPI.renameMenu(token,meid,name);
        call.enqueue(new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.body()==null)
                    return;
                if(response.body().getData()){
                    MusicMenu menu=musicMenuSQLUtil.getMenu(meid);
                    menu.setName(name);
                    musicMenuSQLUtil.updateMenu(menu);
                    callback.handleResult(response.body());
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                callback.handleError(t);
            }
        });
    }

    public void dropMenu(String token,String meid,RequestCallback<Boolean> callback){
        Call<Result<Boolean>> call=menuAPI.dropMenu(token,meid);
        call.enqueue(new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.body()==null)
                    return;
                if(response.body().getData()){
                    musicMenuSQLUtil.deleteMenu(meid);
                    callback.handleResult(response.body());
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                callback.handleError(t);
            }
        });
    }

    public void dropMusic(String token,String meid,String mid,RequestCallback<Boolean> callback){
        Call<Result<Boolean>> call=menuAPI.dropMusic(token,meid,mid);
        call.enqueue(new BaseCallback<>(callback));
    }

    public void addMusic(String token,String meid,String mid,RequestCallback<Boolean> callback){
        Call<Result<Boolean>> call=menuAPI.addMusic(token,meid,mid);
        call.enqueue(new BaseCallback<>(callback));
    }

    private void buildAPI(){
        this.menuAPI= RequestUtil.Instance().buildeRequestAPI(this,MenuAPI.class);
    }
}
