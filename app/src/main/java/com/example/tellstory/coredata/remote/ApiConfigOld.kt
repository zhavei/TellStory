package com.example.tellstory.coredata.remote

import com.example.tellstory.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfigOld {

    fun getApiService(): ApiServiceOld {

        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val netWorkClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit by lazy {
            Retrofit.Builder().baseUrl(BuildConfig.URL).client(netWorkClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }

        return retrofit.create(ApiServiceOld::class.java)
    }
}