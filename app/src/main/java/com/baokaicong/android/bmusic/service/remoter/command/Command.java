package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.service.remoter.MediaController;

public interface Command<T> {
    void execute(MediaController receiver, T data);
}
