package com.misit.abpenergy.Utils

import android.content.Context
import android.net.ConnectivityManager

object ConnectivityUtil {
    fun isConnected(context: Context): Boolean {
        var koneksiStatus : Boolean = true
        val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }
}