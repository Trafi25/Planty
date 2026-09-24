package com.traffipart.polanty.core.network

import com.traffipart.polanty.BuildConfig
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that appends the PlantNet API key to every outgoing request as a query parameter.
 */
class PlantNetAuthInterceptor
    @Inject
    constructor() : Interceptor {
        /**
         * Intercepts outgoing requests and appends the `api-key` query parameter.
         */
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
