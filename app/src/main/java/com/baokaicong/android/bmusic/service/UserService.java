package com.baokaicong.android.bmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.bean.User;
import com.baokaicong.android.bmusic.bean.AccountInfo;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.service.request.RequestUtil;
import com.baokaicong.android.bmusic.service.request.api.UserAPI;
import com.baokaicong.android.bmusic.util.MessageUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserService extends Service {


    private UserAPI userAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        buildAPI();
    }

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

    public void login(User user, RequestCallback<String> callback){
        Call<Result<String>> call=userAPI.login(user.getName(), MessageUtil.MD5(user.getPassword()));
        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                callback.handleResult(response.body());
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                callback.handleError(t);
            }
        });
    }

    public void getUserInfo(String token,RequestCallback<AccountInfo> callback){
        Call<Result<AccountInfo>> call=userAPI.getUserInfo(token);
        call.enqueue(new Callback<Result<AccountInfo>>() {
            @Override
            public void onResponse(Call<Result<AccountInfo>> call, Response<Result<AccountInfo>> response) {
                callback.handleResult(response.body());
            }

            @Override
            public void onFailure(Call<Result<AccountInfo>> call, Throwable t) {
                callback.handleError(t);
            }
        });
    }




    private void buildAPI(){
        this.userAPI= RequestUtil.Instance().buildeRequestAPI(this,UserAPI.class);
    }
}
