package com.sportstalk.datamodels

import android.webkit.URLUtil
import androidx.annotation.RestrictTo
import java.net.MalformedURLException

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ConfigUtils {

    @Throws
    fun validateEndpoint(urlStr: String): String {
        val newUrl = if(urlStr.endsWith("/")) urlStr else "${urlStr}/"
        return if(URLUtil.isValidUrl(newUrl)) {
            newUrl
        } else {
            throw MalformedURLException("Invalid URL")
        }
    }

}