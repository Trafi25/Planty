package com.traffipart.polanty.core.network

import com.traffipart.polanty.BuildConfig
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class PlantNetAuthInterceptor
    @Inject
    constructor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newUrl =
                originalRequest.url
                    .newBuilder()
                    .addQueryParameter("api-key", BuildConfig.PLANT_NET_API_KEY)
                    .build()
            val newRequest = originalRequest.newBuilder().url(newUrl).build()

            return chain.proceed(newRequest)
        }
    }
