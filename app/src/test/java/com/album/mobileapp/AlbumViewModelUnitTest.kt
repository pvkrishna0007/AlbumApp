package com.album.mobileapp

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.os.AsyncTask.execute
import com.album.mobileapp.model.Album
import com.album.mobileapp.model.AlbumMatches
import com.album.mobileapp.model.AlbumModel
import com.album.mobileapp.model.Results
import com.album.mobileapp.network.ApiInterface
import com.album.mobileapp.network.IRepository
import com.album.mobileapp.viewmodel.AlbumViewModel
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.*

import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AlbumViewModelUnitTest {

    // Run tasks synchronously
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    // Mock variables
    @Mock private lateinit var callMock: Call<AlbumModel>
    @Mock private lateinit var repoMock: IRepository
    @Mock private lateinit var observerMock: Observer<String>
    @Mock private lateinit var observerLoadingMock: Observer<Boolean>
    @Mock private lateinit var observerAlbumMock: Observer<AlbumModel>
    @Mock private lateinit var observerNetworkMock: Observer<Boolean>
    @Mock private lateinit var responseBody: ResponseBody

    @Before
    fun initializeResources(){
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun releaseResources(){

    }

    @Test
    fun validateFetchDataSuccessWithData(){
        //Assign
        val response = Response.success(AlbumModel())
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(true)
        }
        viewModel.getMessageLiveData().observeForever(observerMock)

        `when`(callMock.execute()).thenReturn(response)
        `when`(repoMock.getDetailsBy("Orange", "Album", 1))
            .thenReturn(callMock)

        //Act
        viewModel.setSearchText("Orange")
        viewModel.setSearchType("Album")
        val data = viewModel.fetchAlbumCall?.execute()!!
        val actualErrorBody = data.errorBody()
        val actualResponseBody = data.body()

        //Assert
        assertEquals(null, actualErrorBody)
        assertNotNull(actualResponseBody)

        viewModel.handleSuccessResponse(data)
        verify(observerMock).onChanged("Success")
    }

    @Test
    fun validateFetchDataSuccessWithoutData(){
        //Assign
        val albumModel: AlbumModel? = null
        val response = Response.success(albumModel)
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(true)
        }
        viewModel.getMessageLiveData().observeForever(observerMock)

        `when`(callMock.execute()).thenReturn(response)
        `when`(repoMock.getDetailsBy("Yellow", "Album", 1))
            .thenReturn(callMock)

        //Act
        viewModel.setSearchText("Yellow")
        viewModel.setSearchType("Album")
        val data = viewModel.fetchAlbumCall?.execute()
        val actualErrorBody = data?.errorBody()
        val actualResponseBody = data?.body()

        //Assert
        assertEquals(null, actualErrorBody)
        assertNull(actualResponseBody)

        viewModel.handleSuccessResponse(data!!)
        verify(observerMock).onChanged("NoData")
    }

    @Test
    fun validateFetchDataError(){
        //Assign
        val response = Response.error<AlbumModel>(404, responseBody)
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(true)
        }
        viewModel.getMessageLiveData().observeForever(observerMock)
        viewModel.getLoadingLiveData().observeForever(observerLoadingMock)

        `when`(callMock.execute()).thenReturn(response)
        `when`(repoMock.getDetailsBy("Tester", "Artist", 1))
            .thenReturn(callMock)

        //Act
        viewModel.setSearchText("Tester")
        viewModel.setSearchType("Artist")
        val data = viewModel.fetchAlbumCall?.execute()!!
        val actualErrorBody = data.errorBody()
        val actualResponseBody = data.body()

        //Assert
        assertEquals(responseBody, actualErrorBody)
        assertEquals(null, actualResponseBody)

        viewModel.handleErrorResponse(Exception("Error"))
        verify(observerLoadingMock).onChanged(false)
    }

    private fun getAlbumModel(): AlbumModel {
        val albumModel = AlbumModel()
            .apply {
                results = Results()
                    .apply{
                        albumMatches = AlbumMatches()
                            .apply { album = ArrayList<Album>()
                                .apply{ add(Album())} }
                    }
            }
        return albumModel
    }

    @Test
    fun validateFetchDataSuccessWithDataLoadMore()
    {
        //Assign
        val response = Response.success(getAlbumModel())
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(true)
        }
        viewModel.getMessageLiveData().observeForever(observerMock)
        viewModel.getSearchResultsLiveData().observeForever(observerAlbumMock)

        `when`(callMock.execute()).thenReturn(response)
        `when`(repoMock.getDetailsBy("Orange", "Album", 1))
            .thenReturn(callMock)
        `when`(repoMock.getDetailsBy("Orange", "Album", 2))
            .thenReturn(callMock)

        viewModel.setSearchType("Album")
        viewModel.setSearchText("Orange")
        viewModel.fetchAlbumCall?.execute()!!

        //Act
        viewModel.loadMore()
        val dataLoadMore = viewModel.fetchAlbumCall?.execute()!!
        val actualLoadMoreErrorBody = dataLoadMore.errorBody()
        val actualLoadMoreResponseBody = dataLoadMore.body()
        //Assert
        assertEquals(null, actualLoadMoreErrorBody)
        assertNotNull(actualLoadMoreResponseBody)

        viewModel.handleSuccessResponse(dataLoadMore)
        verify(observerMock).onChanged("Success")
    }

    @Test
    fun internetCheck()
    {
        //Assign
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(false)
        }
        viewModel.getMessageLiveData().observeForever(observerMock)
        viewModel.isNetworkAvailable().observeForever(observerNetworkMock)

        //Act
        viewModel.setSearchType("Album")

        //Assert
        verify(observerMock).onChanged("Please check network connection")
    }
}
