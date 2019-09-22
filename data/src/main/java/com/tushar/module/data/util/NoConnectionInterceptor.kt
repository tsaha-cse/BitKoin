package com.tushar.module.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoConnectionInterceptor @Inject constructor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnectionOn()) {
            throw NoConnectivityException()
        } else if (!isInternetAvailable()) {
            throw NoInternetException()
        } else {
            chain.proceed(chain.request())
        }
    }

    @SuppressWarnings("ReturnCount")
    private fun isConnectionOn(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val connection = connectivityManager.getNetworkCapabilities(network)
            return connection != null && (
                    connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            if (activeNetwork != null) {
                return (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                        activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
            }
            return false
        }
    }

    private fun isInternetAvailable(): Boolean = try {
        val timeoutMs = TIME_OUT_MS
        val socket = Socket()
        val sockAddress = InetSocketAddress(DNS_GOOGLE, PORT)
        socket.connect(sockAddress, timeoutMs)
        socket.close()
        true
    } catch (e: IOException) {
        false
    }
}

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No network available, please check your WiFi or data is turned on"
}

class NoInternetException : IOException() {
    override val message: String
        get() = "No internet available, please check your WiFi or Data connection"
}

const val TIME_OUT_MS: Int = 1500
const val PORT: Int = 53
const val DNS_GOOGLE: String = "8.8.8.8"
