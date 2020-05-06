package com.baokaicong.android.bmusic.service.request.api;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.MusicSearchData;
import com.baokaicong.android.bmusic.bean.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MusicAPI {
    @GET("music/search/{current}/{per}")
    Call<Result<MusicSearchData>> search(@Header("user-token")String token,
                                         @Path("current")int current,
                                         @Path("per")int per,
                                         @Query("keyword")String keyword);
}
