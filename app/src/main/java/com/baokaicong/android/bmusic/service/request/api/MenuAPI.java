package com.baokaicong.android.bmusic.service.request.api;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MenuAPI {

    @GET("music/menus")
    Call<Result<List<MusicMenu>>> getUserMenus(@Header("user-token")String token);

    @POST("music/menu")
    @FormUrlEncoded
    Call<Result<Boolean>> addMenu(@Header("user-token")String token,
                                  @Field("name")String name);

    @DELETE("music/menu/{meid}")
    Call<Result<Boolean>> dropMenu(@Header("user-token")String token,
                                   @Path("meid")String meid);

    @PUT("music/menu/{meid}")
    Call<Result<Boolean>> renameMenu(@Header("user-token")String token,
                                     @Path("meid")String meid,
                                     @Query("name") String name
                                     );

    @PUT("music/menu/{meid}/{mid}")
    Call<Result<Boolean>> addMusic(@Header("user-token")String token,
                                   @Path("meid")String meid,
                                   @Path("mid")String mid);

    @DELETE("music/menu/{meid}/{mid}")
    Call<Result<Boolean>> dropMusic(@Header("user-token")String token,
                                   @Path("meid")String meid,
                                   @Path("mid")String mid);

    @GET("music/menu/{meid}")
    Call<Result<List<Music>>> getMenuMusics(@Header("user-token")String token,
                                            @Path("meid")String meid);
}
