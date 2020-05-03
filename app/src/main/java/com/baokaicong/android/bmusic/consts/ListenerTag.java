package com.baokaicong.android.bmusic.consts;

public enum ListenerTag {
    SETTING(1),
    MUSIC(2),
    VIDEO(3);

    private int i;
    ListenerTag(int i){
        this.i=i;
    }

    public int getIndex(){
        return this.i;
    }
}
