package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.service.remoter.RemoteReceiver;

public abstract class BaseCommand<T> implements Command<T>{
    @Override
    public void execute(RemoteReceiver receiver,T data) {
        work(receiver,data);
    }

    protected abstract void work(RemoteReceiver receiver,T data);
}
