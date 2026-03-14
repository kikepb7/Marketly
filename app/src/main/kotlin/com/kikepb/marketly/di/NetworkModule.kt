package com.kikepb.marketly.di

import com.kikepb.marketly.BuildConfig.DEBUG
import com.kikepb.marketly.productlist.data.remote.MarketlyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("baseUrl")
    fun provideBaseUrl(): String = ""

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logginInterceptor = HttpLoggingInterceptor().apply {
            level = BODY
        }
        val builder = OkHttpClient.Builder()

        if (DEBUG) builder.addInterceptor(interceptor = logginInterceptor)

        return builder
            .connectTimeout(timeout = 30, unit = SECONDS)
            .readTimeout(timeout = 30, unit = SECONDS)
            .writeTimeout(timeout = 30, unit = SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        @Named("baseUrl") baseUrl: String,
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideMarketlyService(retrofit: Retrofit): MarketlyService = retrofit.create(MarketlyService::class.java)
}