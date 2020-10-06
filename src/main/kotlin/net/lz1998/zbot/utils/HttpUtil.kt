package net.lz1998.zbot.utils

import okhttp3.OkHttpClient
import okhttp3.Request

object HttpUtil {
    var client = OkHttpClient()
    fun getString(url: String): String {
        val request = Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}