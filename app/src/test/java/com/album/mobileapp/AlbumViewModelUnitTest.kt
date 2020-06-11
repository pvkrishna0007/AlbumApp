package com.album.mobileapp

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.album.mobileapp.model.Album
import com.album.mobileapp.model.AlbumMatches
import com.album.mobileapp.model.AlbumModel
import com.album.mobileapp.model.Results
import com.album.mobileapp.network.IRepository
import com.album.mobileapp.utils.Resource
import com.album.mobileapp.utils.Status
import com.album.mobileapp.viewmodel.AlbumViewModel
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
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
    @Mock private lateinit var observerResourceMock: Observer<Resource<AlbumModel>>
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
        val response = Response.success(getAlbumModel())
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(true)
        }
        viewModel.getResultLiveData().observeForever(observerResourceMock)

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
        assertEquals(viewModel.getResultLiveData().value?.status, Status.SUCCESS)
        assertEquals(viewModel.getResultLiveData().value?.data?.getAlbums()?.size, 1)
    }

    @Test
    fun validateFetchDataError(){
        //Assign
        val response = Response.error<AlbumModel>(404, responseBody)
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(true)
        }
        viewModel.getResultLiveData().observeForever(observerResourceMock)

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
        assertEquals(viewModel.getResultLiveData().value?.status, Status.ERROR)
    }

    private fun getAlbumModel(): AlbumModel {
        return AlbumModel()
            .apply {
                results = Results()
                    .apply {
                        albumMatches = AlbumMatches()
                            .apply { album = ArrayList<Album>()
                                .apply { add(Album()) } }
                    }
            }
    }

    @Test
    fun validateFetchDataSuccessWithDataLoadMore()
    {
        //Assign
        val response = Response.success(getAlbumModel())
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(true)
        }
        viewModel.getResultLiveData().observeForever(observerResourceMock)

        `when`(callMock.execute()).thenReturn(response)
        `when`(repoMock.getDetailsBy("Orange", "Album", 1))
            .thenReturn(callMock)
        `when`(repoMock.getDetailsBy("Orange", "Album", 2))
            .thenReturn(callMock)

        viewModel.setSearchType("Album")
        viewModel.setSearchText("Orange")
        val data = viewModel.fetchAlbumCall?.execute()!!
        viewModel.handleSuccessResponse(data)
        assertEquals(viewModel.getResultLiveData().value?.status, Status.SUCCESS)
        assertEquals(viewModel.getResultLiveData().value?.data?.getAlbums()?.size, 1)

        //Act
        viewModel.loadMore()
        val dataLoadMore = viewModel.fetchAlbumCall?.execute()!!
        val actualLoadMoreErrorBody = dataLoadMore.errorBody()
        val actualLoadMoreResponseBody = dataLoadMore.body()
        //Assert
        assertEquals(null, actualLoadMoreErrorBody)
        assertNotNull(actualLoadMoreResponseBody)

        viewModel.handleSuccessResponse(dataLoadMore)
        assertEquals(viewModel.getResultLiveData().value?.status, Status.SUCCESS)
        assertEquals(viewModel.getResultLiveData().value?.data?.getAlbums()?.size, 2)
    }

    @Test
    fun internetCheck()
    {
        //Assign
        val viewModel: AlbumViewModel = AlbumViewModel(repoMock).apply {
            setNetworkState(false)
        }
        viewModel.getResultLiveData().observeForever(observerResourceMock)
        //Act
        viewModel.setSearchType("Album")

        //Assert
        assertEquals(viewModel.getResultLiveData().value?.status, Status.ERROR)
        assertEquals(viewModel.getResultLiveData().value?.message, "Please check network connection")
    }
}
