package com.examprepare.easybus.core.interceptor

import com.examprepare.easybus.core.util.GZIP
import okhttp3.*
import java.io.IOException


class NetworkInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request: Request = chain.request()
        val response = chain.proceed(request)
        return if (response.code() == 200) {
            val mediaType: MediaType? = response.body()!!.contentType()
            var data = response.body()!!.bytes()
            if (GZIP.isGzip(response.headers())) {
                data = GZIP.uncompress(data)
            }
            response.newBuilder()
                .body(ResponseBody.create(mediaType, data))
                .build()
        } else {
            response
        }
    }
}