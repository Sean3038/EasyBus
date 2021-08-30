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
            //这里是网络拦截器，可以做错误处理
            val mediaType: MediaType? = response.body()!!.contentType()
            var data = response.body()!!.bytes()
            if (GZIP.isGzip(response.headers())) {
                //请求头显示有gzip，需要解压
                data = GZIP.uncompress(data)
            }

            //创建一个新的responseBody，返回进行处理
            response.newBuilder()
                .body(ResponseBody.create(mediaType, data))
                .build()
        } else {
            response
        }
    }
}