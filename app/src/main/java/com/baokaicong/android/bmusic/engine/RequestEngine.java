package com.baokaicong.android.bmusic.engine;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestEngine {
    private static final String baseURL="http://gateway.baokaicong.com:1102";
    private GsonConverterFactory gsonConverterFactory;
    private CallAdapter.Factory callAdapterFactory;
    private RequestEngine(){}

    public void init(){
        buildGsonConverterFactory();
        buildCallAdapterFactory();
    }


    private static class Holder{
        private static RequestEngine instance=new RequestEngine(){
            {
                init();
            }
        };
    }

    public static RequestEngine Instance(){
        return Holder.instance;
    }

    public <T> T buildeRequestAPI(Class<T> apiClazz){
        return buildeRequestAPI(baseURL(),apiClazz);
    }

    public <T> T buildeRequestAPI(String url, Class<T> apiClazz){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(gsonConverterFactory) //设置数据解析器
                .addCallAdapterFactory(callAdapterFactory)
                .build();
        return retrofit.create(apiClazz);
    }

    public String baseURL(){
        return baseURL;
    }

    private void buildGsonConverterFactory(){
        this.gsonConverterFactory=GsonConverterFactory.create();
    }

    private void buildCallAdapterFactory(){
        this.callAdapterFactory=RxJavaCallAdapterFactory.create();
    }
}
