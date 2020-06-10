package com.album.mobileapp.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

interface IRepository{
    suspend fun getDetailsBy(search: String) : LiveData<List<String>>
}

class Repository : IRepository{

    private val resultsLiveData = MutableLiveData<List<String>>()

    override suspend fun getDetailsBy(search: String) : LiveData<List<String>> {
        val results: List<String> = listOf("One", "Two")
        return resultsLiveData.apply { postValue(results) }
    }
}