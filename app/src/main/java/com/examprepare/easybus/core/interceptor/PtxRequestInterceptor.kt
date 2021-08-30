package com.examprepare.easybus.core.interceptor

import com.examprepare.easybus.Const
import com.examprepare.easybus.HMAC_SHA1
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*


class PtxRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        val appId = Const.PTX_APP_ID
        val appKey = Const.PTX_APP_KEY

        // 取得當下的UTC時間
        val xDate = getServerTime()
        val signDate = "x-date: $xDate"
        var signature = ""
        try {
            // 取得加密簽章
            signature = HMAC_SHA1(signDate, appKey)
        } catch (e1: SignatureException) {
            e1.printStackTrace()
        }
        Timber.i("X-Date :$xDate")
        Timber.i("Signature :$signature")
        val sAuth =
            "hmac username=\"$appId\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"$signature\""
        Timber.i(sAuth)

        val request: Request = original.newBuilder()
            .addHeader("Authorization", sAuth)
            .addHeader("x-date", xDate)
            .addHeader("Accept-Encoding", "gzip")
            .method(original.method(), original.body())
            .build()

        return chain.proceed(request)
    }

    // 取得當下UTC時間
    private fun getServerTime(): String {
        val calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormat.format(calendar.time)
    }
}