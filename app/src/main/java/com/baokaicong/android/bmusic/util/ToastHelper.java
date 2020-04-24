package com.baokaicong.android.bmusic.util;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {


    public static void showText(Context context,String text){
        showText(context,text, Toast.LENGTH_SHORT);
    }

    public static void showText(Context context,String text,int duration){
        Toast.makeText(context, text, duration).show();
    }
}
