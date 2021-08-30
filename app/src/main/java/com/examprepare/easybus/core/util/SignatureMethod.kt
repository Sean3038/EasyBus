package com.examprepare.easybus

import android.util.Base64
import java.security.SignatureException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Throws(java.security.SignatureException::class)
fun HMAC_SHA1(xData: String, AppKey: String): String {
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