package com.example.tellstory.di

import android.content.Context
import com.example.tellstory.BuildConfig
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.local.TellStoryDatabase
import com.example.tellstory.coredata.remote.ApiConfig
import com.example.tellstory.coredata.remote.ApiServiceOld
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        //testing with chucker Interceptor
        /*val netWorkClient = OkHttpClient.Builder()
            .addInterceptor(
                ChuckerInterceptor.Builder(context = context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
            )
            .build()*/

        //with loggingInterceptor
        val netWorkClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit by lazy {
            Retrofit.Builder().baseUrl(BuildConfig.URL).client(netWorkClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }

        return retrofit
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ApiServiceOld {
        return retrofit.create(ApiServiceOld::class.java)
    }

}