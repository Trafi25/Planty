package com.traffipart.polanty.core.network

import com.traffipart.polanty.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor that appends the Perenual API key to every outgoing request as a query parameter.
 */
class PerenualAuthInterceptor
    @Inject
    constructor() : Interceptor {
        /**
         * Intercepts the request and adds the API key.
         *
         * @param chain The interceptor chain.
         * @return The modified response with the API key attached.
         */
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
