package com.baokaicong.android.bmusic.service.remoter;

import com.baokaicong.android.bmusic.service.remoter.command.Command;

import java.util.List;

public interface MediaRemoter<T> {
    <B> void command(Command command,B data);
}
