package com.traffipart.polanty.core.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.traffipart.polanty.BuildConfig
import com.traffipart.polanty.core.network.PerenualAuthInterceptor
import com.traffipart.polanty.core.network.PlantNetAuthInterceptor
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.data.remote.plant.PlantNetApi
import com.traffipart.polanty.data.remote.taxonomy.GbifApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Hilt module for providing network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val PLANTNET_BASE_URL = "https://my-api.plantnet.org/"
    private const val PERENUAL_BASE_URL = "https://perenual.com/"
    private const val GBIF_BASE_URL = "https://api.gbif.org/"

    /** Provides the global [Moshi] instance for JSON serialization/deserialization. */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /** Provides a logging interceptor that only logs in debug builds. */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }

    /** Provides a base [OkHttpClient.Builder] with shared timeout configurations. */
    @Provides
    fun provideBaseHttpClientBuilder(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

    /** Provides the [OkHttpClient] specialized for the PlantNet API. */
    @Provides
    @Singleton
    @PlantNetClient
    fun providePlantNetHttpClient(
        builder: OkHttpClient.Builder,
        plantNetAuthInterceptor: PlantNetAuthInterceptor,
    ): OkHttpClient = builder.addInterceptor(plantNetAuthInterceptor).build()

    /** Provides the [OkHttpClient] specialized for the Perenual API. */
    @Provides
    @Singleton
    @PerenualClient
    fun providePerenualHttpClient(
        builder: OkHttpClient.Builder,
        perenualAuthInterceptor: PerenualAuthInterceptor,
    ): OkHttpClient = builder.addInterceptor(perenualAuthInterceptor).build()

    @Provides
    @Singleton
    @GbifClient
    fun provideGbifHttpClient(builder: OkHttpClient.Builder): OkHttpClient = builder.build()

    /** Provides the [Retrofit] instance for the PlantNet API. */
    @Provides
    @Singleton
    @PlantNetClient
    fun providePlantNetRetrofit(
        moshi: Moshi,
        @PlantNetClient httpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(PLANTNET_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /** Provides the [Retrofit] instance for the Perenual API. */
    @Provides
    @Singleton
    @PerenualClient
    fun providePerenualRetrofit(
        moshi: Moshi,
        @PerenualClient httpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(PERENUAL_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    @GbifClient
    fun provideGbifRetrofit(
        moshi: Moshi,
        @GbifClient httpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(GBIF_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /** Provides the [PlantNetApi] service. */
    @Provides
    @Singleton
    fun providePlantApi(
        @PlantNetClient retrofit: Retrofit,
    ): PlantNetApi = retrofit.create(PlantNetApi::class.java)

    /** Provides the [PerenualApi] service. */
    @Provides
    @Singleton
    fun providePerenualApi(
        @PerenualClient retrofit: Retrofit,
    ): PerenualApi = retrofit.create(PerenualApi::class.java)

    @Provides
    @Singleton
    fun provideGbifApi(
        @GbifClient retrofit: Retrofit,
    ): GbifApi = retrofit.create(GbifApi::class.java)
}
