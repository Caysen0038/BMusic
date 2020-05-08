package com.baokaicong.android.bmusic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
    private static class Holder{
        private static final Gson instance=new GsonBuilder()
                .disableHtmlEscaping()
                .create();
    }
    public static Gson builder(){
        return Holder.instance;
    }
}
