package com.album.mobileapp.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import com.album.mobileapp.network.Repository
import com.album.mobileapp.network.RetrofitManager
import com.album.mobileapp.utils.*
import com.album.mobileapp.viewmodel.AlbumViewModel
import com.album.mobileapp.viewmodel.AlbumViewModelFactory
import kotlinx.android.synthetic.main.activity_search.*


class AlbumSearchActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var connectionLiveData: ConnectionLiveData
    private var toastAlert: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.album.mobileapp.R.layout.activity_search)

        initializeData()
        initializeListeners()
    }

    private fun initializeData() {
        connectionLiveData = ConnectionLiveData(this)

        val api = RetrofitManager.getApiInterface()
        val albumViewModelFactory = AlbumViewModelFactory(Repository(api))
        albumViewModel = ViewModelProviders.of(this, albumViewModelFactory).get(AlbumViewModel::class.java)

        rv_items.layoutManager = LinearLayoutManager(this)
        rv_items.setHasFixedSize(true)

        albumAdapter = AlbumAdapter(this)
        rv_items.adapter = albumAdapter
    }

    private fun initializeListeners() {
        sp_album_type.onItemSelectedListener = this

        et_search_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                albumViewModel.setSearchText(et_search_text.text.toString())
                hideKeyboard()
                true
            }
            false
        }

        rv_items.loadMore {
            if (albumViewModel.getResultLiveData().value?.status != Status.LOADING) {
                albumViewModel.loadMore()
            }
        }

        registerObservers()
    }

    private fun registerObservers() {
        albumViewModel.getResultLiveData().observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    if (it.data?.getAlbums() != null) {
                        albumAdapter.setAlbumList(it.data.getAlbums())
                    }
                    pb_loading.visibleGone(false)
                }
                Status.ERROR -> {
                    showToast(it.message ?: "")
                    pb_loading.visibleGone(false)
                }
                Status.LOADING -> {
                    pb_loading.visibleGone(true)
                }
            }
        })

        connectionLiveData.observe(this, Observer {
            albumViewModel.setNetworkState(it ?: false)
        })
        albumViewModel.setNetworkState(isConnected)
    }

    fun showToast(message: String) {
        toastAlert?.cancel()
        toastAlert = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toastAlert?.run { show() }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (view is TextView) {
            val v = view
            albumViewModel.setSearchType(v.text.toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}
