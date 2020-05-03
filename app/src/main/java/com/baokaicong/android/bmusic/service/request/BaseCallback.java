package com.baokaicong.android.bmusic.service.request;


import com.baokaicong.android.bmusic.bean.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseCallback<T> implements Callback<Result<T>> {
    private RequestCallback callback;
    public BaseCallback(RequestCallback<T> callback){
        this.callback=callback;
    }

    @Override
    public void onResponse(Call<Result<T>> call, Response<Result<T>> response) {
        callback.handleResult(response.body());
    }

    @Override
    public void onFailure(Call<Result<T>> call, Throwable t) {
        callback.handleError(t);
    }
}
