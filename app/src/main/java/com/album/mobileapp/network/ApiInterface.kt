package com.album.mobileapp.network

import android.arch.lifecycle.LiveData
import com.album.mobileapp.model.AlbumModel
import com.album.mobileapp.utils.Constants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  BASE_URL = "http://ws.audioscrobbler.com/";
 */
interface ApiInterface {

    // http://ws.audioscrobbler.com/
    // 2.0/?page=1&limit=1&method=album.search&album=believe
    // &api_key=0449acff87718d36946c9f92eff9a358&format=json

    @GET("2.0/?method=album.search&format=json")
    fun searchByAlbum(@Query("album") albumSearchText: String,
                      @Query("page") page:Int,
                      @Query("limit") limit: Int = Constants.PAGE_LIMIT,
                      @Query("api_key") apiKey: String = Constants.API_KEY
    ): Call<AlbumModel>

    // http://ws.audioscrobbler.com/
    // 2.0/?method=artist.search&artist=cher
    // &api_key=0449acff87718d36946c9f92eff9a358&format=json&limit=2

    @GET("2.0/?method=artist.search&format=json")
    fun searchByArtist(@Query("artist") artistSearchText: String,
                      @Query("page") page:Int,
                      @Query("limit") limit: Int = Constants.PAGE_LIMIT,
                      @Query("api_key") apiKey: String = Constants.API_KEY
    ): Call<AlbumModel>

    // http://ws.audioscrobbler.com/2.0/
    // ?method=track.search&track=Believe
    // &api_key=0449acff87718d36946c9f92eff9a358&format=json&limit=1

    @GET("2.0/?method=track.search&format=json")
    fun searchByTrack(@Query("track") artistSearchText: String,
                       @Query("page") page:Int,
                       @Query("limit") limit: Int = Constants.PAGE_LIMIT,
                       @Query("api_key") apiKey: String = Constants.API_KEY
    ): Call<AlbumModel>
}