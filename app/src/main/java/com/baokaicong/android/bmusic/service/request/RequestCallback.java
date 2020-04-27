package com.baokaicong.android.bmusic.service.request;


import com.baokaicong.android.bmusic.bean.Result;

public interface RequestCallback<T>{
    void handleResult(Result<T> result);
    void handleError(Throwable t);
}