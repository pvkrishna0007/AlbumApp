package com.album.mobileapp.utils

import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  BASE_URL = "http://ws.audioscrobbler.com/";
 */
interface ApiInterface {

    // http://ws.audioscrobbler.com/
    // 2.0/?page=1&limit=1&method=album.search&album=believe&api_key=0449acff87718d36946c9f92eff9a358&format=json

    @GET("2.0/?method=album.search&format=json")
    fun searchByAlbum(@Query("album") albumSearchText: String,
                      @Query("page") page:Int = Constants.INITIAL_PAGE,
                      @Query("limit") limit: Int = Constants.PAGE_LIMIT,
                      @Query("api_key") apiKey: String = Constants.API_KEY)

}