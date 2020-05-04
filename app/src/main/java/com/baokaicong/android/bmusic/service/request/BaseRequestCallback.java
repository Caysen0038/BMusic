package com.baokaicong.android.bmusic.service.request;

import android.content.Context;

import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.util.ToastUtil;

public abstract class BaseRequestCallback<T> implements RequestCallback<T> {
    private Context context;
    public BaseRequestCallback(Context context){
        this.context=context;
    }
    @Override
    public abstract void handleResult(Result<T> result);

    @Override
    public void handleError(Throwable t) {
        ToastUtil.showText(context,"请求错误");
    }
}
