package com.practicum.playlistmaker.search.data

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class TimingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNs = System.nanoTime()

        val response = chain.proceed(request)

        val durationMs = (System.nanoTime() - startNs) / 1_000_000

        return response
    }
}
