package com.album.mobileapp.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.VisibleForTesting
import com.album.mobileapp.model.AlbumModel
import com.album.mobileapp.network.IRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumViewModel(val repo: IRepository) : ViewModel() {

    private var pageNumber = 1

    private var searchType: String = ""
    private var searchText: String = ""
    private var searchResultsLiveData = MutableLiveData<AlbumModel>()
    private var loadingLiveData = MutableLiveData<Boolean>()
    private var messageLiveData = MutableLiveData<String>()
    private var isNetworkAvailable = MutableLiveData<Boolean>()
    @VisibleForTesting
    var fetchAlbumCall : Call<AlbumModel>? = null

    fun getSearchResultsLiveData(): LiveData<AlbumModel> = searchResultsLiveData
    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData
    fun getMessageLiveData(): LiveData<String> = messageLiveData
    fun isNetworkAvailable(): LiveData<Boolean> = isNetworkAvailable

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
        isNetworkAvailable.value = isConnected
    }

    private fun getResults() {
        if (isNetworkAvailable.value == true) {
            if (searchText.isEmpty()) return

            loadingLiveData.value = true

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
            messageLiveData.postValue("Please check network connection")
        }
    }

    fun handleErrorResponse(t: Throwable) {
        loadingLiveData.postValue(false)
        messageLiveData.postValue(t.message)
    }

    fun handleSuccessResponse(response: Response<AlbumModel>) {
        val searchList = response.body()
        if(searchList != null) {
            if (pageNumber == 1) {
                searchResultsLiveData.postValue(searchList)
            } else {
                val oldAlbumData = searchResultsLiveData.value?:AlbumModel()
                searchResultsLiveData.postValue(searchList.addAlbums(oldAlbumData))
            }
            messageLiveData.postValue("Success")
        }else{
            messageLiveData.postValue("NoData")
        }
        loadingLiveData.postValue(false)
    }

    fun loadMore() {
        pageNumber++
        getResults()
    }

    private fun reset() {
        pageNumber = 1
    }

}

class AlbumViewModelFactory(var repo: IRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumViewModel(repo) as T
    }
}

