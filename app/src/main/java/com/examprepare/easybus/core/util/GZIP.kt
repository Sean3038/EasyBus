package com.examprepare.easybus.core.util

import okhttp3.Headers
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GZIP {
    private const val ENCODE_UTF_8 = "UTF-8"

    @JvmOverloads
    fun compress(str: String?, encoding: String? = ENCODE_UTF_8): ByteArray? {
        if (str == null || str.length == 0) {
            return null
        }
        val out = ByteArrayOutputStream()
        val gzipInputStream: GZIPOutputStream
        try {
            gzipInputStream = GZIPOutputStream(out)
            gzipInputStream.write(str.toByteArray(charset(encoding!!)))
            gzipInputStream.close()
        } catch (e: IOException) {
            println("gzip compress error")
        }
        return out.toByteArray()
    }

    /**
     * 字节数组解压
     */
    fun uncompress(bytes: ByteArray?): ByteArray? {
        if (bytes == null || bytes.size == 0) {
            return null
        }
        val out = ByteArrayOutputStream()
        val `in` = ByteArrayInputStream(bytes)
        try {
            val gzipInputStream = GZIPInputStream(`in`)
            val buffer = ByteArray(256)
            var n: Int
            while (gzipInputStream.read(buffer).also { n = it } >= 0) {
                out.write(buffer, 0, n)
            }
        } catch (e: IOException) {
            println("gzip uncompress error.")
        }
        return out.toByteArray()
    }
    /**
     * 字节数组解压至string，可选择encoding配置
     */
    /**
     * 字节数组解压至string
     */
    @JvmOverloads
    fun uncompressToString(bytes: ByteArray?, encoding: String? = ENCODE_UTF_8): String? {
        if (bytes == null || bytes.size == 0) {
            return null
        }
        val out = ByteArrayOutputStream()
        val `in` = ByteArrayInputStream(bytes)
        try {
            val ungzip = GZIPInputStream(`in`)
            val buffer = ByteArray(256)
            var n: Int
            while (ungzip.read(buffer).also { n = it } >= 0) {
                out.write(buffer, 0, n)
            }
            return out.toString(encoding)
        } catch (e: IOException) {
            println("gzip uncompress to string error")
        }
        return null
    }

    /**
     * 判断请求头是否存在gzip
     */
    fun isGzip(headers: Headers): Boolean {
        var gzip = false
        for (key in headers.names()) {
            if (key.equals("Accept-Encoding", ignoreCase = true) && headers.get(key)
                    ?.contains("gzip") == true || key.equals(
                    "Content-Encoding",
                    ignoreCase = true
                ) && headers.get(key)?.contains("gzip") == true
            ) {
                gzip = true
                break
            }
        }
        return gzip
    }
}