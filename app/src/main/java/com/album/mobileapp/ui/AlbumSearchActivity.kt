package com.album.mobileapp.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import com.album.mobileapp.network.Repository
import com.album.mobileapp.network.RetrofitManager
import com.album.mobileapp.utils.ConnectionLiveData
import com.album.mobileapp.utils.isConnected
import com.album.mobileapp.utils.visibleGone
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

    private fun initializeData(){
        connectionLiveData = ConnectionLiveData(this)

        val api = RetrofitManager.getApiInterface()
        val albumViewModelFactory = AlbumViewModelFactory(Repository(api))
        albumViewModel = ViewModelProviders.of(this, albumViewModelFactory).get(AlbumViewModel::class.java)

        rv_items.layoutManager = LinearLayoutManager(this)
        rv_items.setHasFixedSize(true)

        albumAdapter = AlbumAdapter(this)
        rv_items.adapter = albumAdapter
    }

    private fun initializeListeners(){
        sp_album_type.onItemSelectedListener = this

        et_search_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                albumViewModel.setSearchText(et_search_text.text.toString())
                hideKeyboard()
                true
            }
            false
        }
        rv_items.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!albumViewModel.getLoadingLiveData().value!!) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == albumAdapter.itemCount - 1) {
                        //bottom of list!
                        albumViewModel.loadMore()
                    }
                }
            }
        })
        registerObservers()
    }

    private fun registerObservers(){
        albumViewModel.getSearchResultsLiveData().observe(this, Observer(){
            albumAdapter.setAlbumList(it!!.getAlbums())
        })
        albumViewModel.getLoadingLiveData().observe(this, Observer {
            pb_loading.visibleGone(it!!)
        })
        albumViewModel.getMessageLiveData().observe(this, Observer {
            toastAlert?.cancel()
            toastAlert = Toast.makeText(applicationContext, it?:"Default", Toast.LENGTH_SHORT)
            toastAlert?.run { show() }
        })

        connectionLiveData.observe(this, Observer {
            albumViewModel.setNetworkState(it?:false)
        })
        albumViewModel.setNetworkState(isConnected)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(view is TextView) {
            val v = view
            albumViewModel.setSearchType(v.text.toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun hideKeyboard() {
        // Check if no view has focus:
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    /*override fun onBackPressed() {
        //super.onBackPressed() //Disabled back press
    }*/
}
