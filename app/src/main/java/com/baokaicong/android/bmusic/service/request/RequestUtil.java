package com.baokaicong.android.bmusic.service.request;

import android.content.Context;

import com.baokaicong.android.bmusic.consts.SettingField;
import com.baokaicong.android.bmusic.util.sql.PropertySQLUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestUtil {
    private static final String baseURL="http://192.168.0.106:1102/";
    private GsonConverterFactory gsonConverterFactory;
    private CallAdapter.Factory callAdapterFactory;
    private OkHttpClient client;
    private RequestUtil(){}

    public void init(){
        buildGsonConverterFactory();
        buildCallAdapterFactory();
        buildOkHttpClient();
    }


    private static class Holder{
        private static RequestUtil instance=new RequestUtil(){
            {
                init();
            }
        };
    }

    public static RequestUtil Instance(){
        return Holder.instance;
    }

    public <T> T buildeRequestAPI(Context context, Class<T> apiClazz){
        return buildeRequestAPI(baseURL(context),apiClazz);
    }

    public <T> T buildeRequestAPI(String url, Class<T> apiClazz){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(gsonConverterFactory) //设置数据解析器
                .addCallAdapterFactory(callAdapterFactory)
                .build();
        return retrofit.create(apiClazz);
    }

    public String baseURL(Context context){
        PropertySQLUtil util=new PropertySQLUtil(context);
        String host=util.getProperty(SettingField.HOST_ADDRESS);
        String port=util.getProperty(SettingField.HOST_PORT);
        if(host==null || port==null){
            return baseURL;
        }
        return host+":"+port+"/";
    }

    private void buildGsonConverterFactory(){
        this.gsonConverterFactory=GsonConverterFactory.create();
    }

    private void buildCallAdapterFactory(){
        this.callAdapterFactory=RxJavaCallAdapterFactory.create();
    }

    private void buildOkHttpClient(){
        client=new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.MINUTES)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES) //设置超时
                .build();
    }
}
