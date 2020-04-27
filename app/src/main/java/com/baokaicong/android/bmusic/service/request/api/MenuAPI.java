package com.baokaicong.android.bmusic.service.request.api;

import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface MenuAPI {

    @GET("music/menu")
    Call<Result<List<MusicMenu>>> getUserMenus(@Header("user-token")String token);
}
