package com.traffipart.polanty.core.network

import com.traffipart.polanty.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class PerenualAuthInterceptor
    @Inject
    constructor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val url =
                request.url
                    .newBuilder()
                    .addQueryParameter("key", BuildConfig.PERENUAL_API_KEY)
                    .build()
            return chain.proceed(request.newBuilder().url(url).build())
        }
    }
