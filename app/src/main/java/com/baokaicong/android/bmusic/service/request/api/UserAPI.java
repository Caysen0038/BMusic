package com.baokaicong.android.bmusic.service.request.api;

import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.bean.AccountInfo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserAPI {

    @POST("user")
    @FormUrlEncoded
    Call<Result<String>> login(@Field("user") String user, @Field("password") String password);

    @GET("user")
    Call<Result<AccountInfo>> getUserInfo(@Header("user-token")String token);
}
