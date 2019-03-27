package android.thaihn.okhttpandretrofitsample.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object NetworkUtil {

    fun isWifiEnable(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn: Boolean = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connMgr.activeNetwork
            val capabilities = connMgr.getNetworkCapabilities(network)
            isWifiConn = capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
        } else {
            connMgr.allNetworks.forEach { network ->
                connMgr.getNetworkInfo(network).apply {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        isWifiConn = isWifiConn or isConnected
                    }
                }
            }
        }
        return isWifiConn
    }

    fun isMobileEnable(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isMobileConn: Boolean = false


        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }
        return isMobileConn
    }

    fun isNetworkEnable(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn: Boolean = false
        var isMobileConn: Boolean = false
        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn = isWifiConn or isConnected
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }
        return isWifiConn or isMobileConn
    }

    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}
