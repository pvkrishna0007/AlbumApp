package com.album.mobileapp.network

import com.album.mobileapp.BuildConfig
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitManager {
    private var retrofit: Retrofit? = null
    private const val BASE_URL = "http://ws.audioscrobbler.com/"

    fun getApiInterface(): ApiInterface {
        return getRetrofitCustomClient(true).create(ApiInterface::class.java)!!
    }

    private val objectMapper: ObjectMapper
        get() {
            val mapper = ObjectMapper()
            mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            return mapper
        }


    private fun getRetrofitCustomClient(isLogEnabled: Boolean): Retrofit {

        val okHttpBuilder = OkHttpClient.Builder()

        if (isLogEnabled) {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                okHttpBuilder.addInterceptor(logging)
            }
        }
        okHttpBuilder.connectTimeout((30 * 1000).toLong(), TimeUnit.MILLISECONDS)
        okHttpBuilder.readTimeout(180, TimeUnit.SECONDS)
        okHttpBuilder.writeTimeout((830 * 1000).toLong(), TimeUnit.MILLISECONDS)

        val mapper = objectMapper
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .client(okHttpBuilder.build())
            .build()
    }

}
