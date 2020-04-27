package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.service.remoter.RemoteReceiver;

public interface Command<T> {
    void execute(RemoteReceiver receiver,T data);
}
