package com.examprepare.easybus

import android.util.Base64
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.net.ssl.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        var connection: HttpURLConnection? = null
        val APIUrl = "https://ptx.transportdata.tw/MOTC/v2/Rail/TRA/Station?\$top=1&\$format=JSON"
        val APPID = Const.PTX_APP_ID
        val APPKey = Const.PTX_APP_KEY

        val xdate: String = getServerTime()
        val SignDate = "x-date: $xdate"
        var respond = ""

        var Signature = ""

        try {
            // 取得加密簽章
            Signature = sha(SignDate, APPKey)
        } catch (e1: SignatureException) {
            e1.printStackTrace()
        }

        println("Signature :$Signature")
        val sAuth =
            ("hmac username=\"" + APPID + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\""
                    + Signature + "\"")
        println(sAuth)
        try {
            val url = URL(APIUrl)
            if ("https".equals(url.getProtocol(), ignoreCase = true)) {
                ignoreSsl()
            }
            connection = url.openConnection() as HttpURLConnection
            connection.setRequestMethod("GET")
            connection.setRequestProperty("Authorization", sAuth)
            connection.setRequestProperty("x-date", xdate)
            connection.setRequestProperty("Accept-Encoding", "gzip")
            connection.setDoInput(true)
            connection.setDoOutput(true)
            respond =
                connection.getResponseCode().toString() + " " + connection.getResponseMessage()
            println("回傳狀態:$respond")
            val `in`: BufferedReader

            // 判斷來源是否為gzip
            if ("gzip" == connection.getContentEncoding()) {
                val reader = InputStreamReader(GZIPInputStream(connection.getInputStream()))
                `in` = BufferedReader(reader)
            } else {
                val reader = InputStreamReader(connection.getInputStream())
                `in` = BufferedReader(reader)
            }

            // 返回的數據已經過解壓
            val buffer = StringBuffer()
            // 讀取回傳資料
            var line: String
            var response = ""
            while (`in`.readLine().also { line = it } != null) {
                response += """
            $line
            
            """.trimIndent()
            }
            print(response)
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 取得當下UTC時間
    private fun getServerTime(): String {
        val calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormat.format(calendar.time)
    }

    @Throws(Exception::class)
    private fun trustAllHttpsCertificates() {
        val trustAllCerts: Array<TrustManager?> = arrayOfNulls<TrustManager>(1)
        val tm: TrustManager = miTM()
        trustAllCerts[0] = tm
        val sc: SSLContext = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
    }

    internal class miTM : TrustManager, X509TrustManager {

        override fun checkClientTrusted(
            chain: Array<out java.security.cert.X509Certificate>?,
            authType: String?
        ) {
            return
        }

        override fun checkServerTrusted(
            chain: Array<out java.security.cert.X509Certificate>?,
            authType: String?
        ) {
            return
        }

        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? {
            return null
        }
    }

    @Throws(Exception::class)
    fun ignoreSsl() {
        val hv = HostnameVerifier { urlHostName, session ->
            println("Warning: URL Host: " + urlHostName + " vs. " + session.peerHost)
            true
        }
        trustAllHttpsCertificates()
        HttpsURLConnection.setDefaultHostnameVerifier(hv)
    }

    @Throws(java.security.SignatureException::class)
    fun sha(xData: String, AppKey: String): String {
        try {

            // get an hmac_sha1 key from the raw key bytes
            val signingKey = SecretKeySpec(AppKey.toByteArray(charset("UTF-8")), "HmacSHA1")

            // get an hmac_sha1 Mac instance and initialize with the signing key
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(signingKey)

            // compute the hmac on input data bytes
            val rawHmac = mac.doFinal(xData.toByteArray(charset("UTF-8")))
            return String(Base64.encode(rawHmac, Base64.NO_WRAP))
        } catch (e: Exception) {
            throw SignatureException("Failed to generate HMAC : " + e.message)
        }

    }
}