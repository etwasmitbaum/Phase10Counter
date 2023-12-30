package com.tjEnterprises.phase10Counter.data.network.di

import com.tjEnterprises.phase10Counter.data.network.services.GetLatestReleaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UpdateCheckerModule {

    private val apiUrl = "https://api.github.com/repos/etwasmitbaum/Phase10Counter/releases/"

    @Provides
    @Singleton
    fun provideLatestReleaseService() : GetLatestReleaseService {
        // Only for debugging retrofit
        /*
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        */
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(ScalarsConverterFactory.create())
                //.client(client)
                .build()
        return retrofit.create(GetLatestReleaseService::class.java)
    }
}