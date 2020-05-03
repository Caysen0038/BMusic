package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.service.remoter.MediaController;

public abstract class BaseCommand<T> implements Command<T>{
    @Override
    public void execute(MediaController receiver, T data) {
        work(receiver,data);
    }

    protected abstract void work(MediaController receiver, T data);
}
