package com.album.mobileapp.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.VisibleForTesting
import com.album.mobileapp.model.AlbumModel
import com.album.mobileapp.network.IRepository
import com.album.mobileapp.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumViewModel(val repo: IRepository) : ViewModel() {

    private var pageNumber = 1
    private var searchType: String = ""
    private var searchText: String = ""
    private var isNetworkAvailable :Boolean = false
    private var oldAlbumModel: AlbumModel = AlbumModel()

    private var resultLiveData = MutableLiveData<Resource<AlbumModel>>()
    @VisibleForTesting
    var fetchAlbumCall : Call<AlbumModel>? = null

    fun getResultLiveData(): LiveData<Resource<AlbumModel>> = resultLiveData

    override fun onCleared() {
        super.onCleared()
        fetchAlbumCall?.cancel()
        fetchAlbumCall = null
    }

    fun setSearchType(type: String) {
        if(searchType == type) return

        searchType = type
        reset()
        getResults()
    }

    fun setSearchText(text: String) {
        searchText = text
        reset()
        getResults()
    }

    fun setNetworkState(isConnected: Boolean){
        isNetworkAvailable = isConnected
    }

    private fun getResults() {
        if (isNetworkAvailable) {
            if (searchText.isEmpty()) return

            resultLiveData.postValue(Resource.loading(null))

            fetchAlbumCall?.cancel()
            fetchAlbumCall = repo.getDetailsBy(searchText, searchType, pageNumber)
            fetchAlbumCall?.enqueue(object : Callback<AlbumModel> {
                override fun onResponse(
                    call: Call<AlbumModel>,
                    response: Response<AlbumModel>
                ) {
                    handleSuccessResponse(response)
                }

                override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
                    t.printStackTrace()
                    handleErrorResponse(t)
                }
            })
        }else{
            resultLiveData.postValue(Resource.error(null, "Please check network connection"))
        }
    }

    fun handleErrorResponse(t: Throwable) {
        resultLiveData.postValue(Resource.error(null, t.message?:"Error in response"))
    }

    fun handleSuccessResponse(response: Response<AlbumModel>) {
        val searchList = response.body()
        if(searchList != null) {
            if (isLoadMore()) {
                oldAlbumModel.appendAlbums(searchList)
            }else{
                oldAlbumModel = searchList
            }
            resultLiveData.postValue(Resource.success(oldAlbumModel))
            pageNumber++
        }
    }

    fun loadMore() {
        getResults()
    }

    private fun reset() {
        pageNumber = 1
    }

    private fun isLoadMore(): Boolean {
        return pageNumber > 1
    }
}

class AlbumViewModelFactory(var repo: IRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumViewModel(repo) as T
    }
}

