package com.baokaicong.android.bmusic.service.request;

import com.baokaicong.android.bmusic.bean.Result;

public class BaseRequestCallback<T> implements RequestCallback<T> {
    @Override
    public void handleResult(Result<T> result) {

    }

    @Override
    public void handleError(Throwable t) {

    }
}
