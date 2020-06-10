package com.album.mobileapp.network

import com.album.mobileapp.model.AlbumModel
import retrofit2.Call
import java.lang.RuntimeException

interface IRepository{
    fun getDetailsBy(search: String, type: String, page: Int) : Call<AlbumModel>
}

class Repository(val apiInterface: ApiInterface) : IRepository {

    override fun getDetailsBy(search: String, type: String, page: Int) : Call<AlbumModel> {
        when(type){
            "Album" -> {
                return apiInterface.searchByAlbum(search, page)
            }
            "Artist" -> {
                return apiInterface.searchByArtist(search, page)
            }
            "Song" -> {
                return apiInterface.searchByTrack(search, page)
            }
            else -> {
                throw RuntimeException("Search type:${type} not implemented")
            }
        }
    }
}
