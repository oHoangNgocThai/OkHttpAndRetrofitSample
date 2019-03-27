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

    @Throws(IOException::class)
    fun downloadUrl(url: URL): String? {
        var connection: HttpsURLConnection? = null
        return try {
            connection = (url.openConnection() as? HttpsURLConnection)
            connection?.run {
                readTimeout = 3000
                connectTimeout = 3000
                requestMethod = "GET"
                doInput = true
                connect()

                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("Http error code: $responseCode")
                }

                // get body
                inputStream?.let { stream ->
                    readStream(stream, 500)
                }
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            connection?.inputStream?.close()
            connection?.disconnect()
        }
    }

    @Throws(IOException::class, UnsupportedEncodingException::class)
    private fun readStream(stream: InputStream, maxReadSize: Int): String? {
        val reader: Reader? = InputStreamReader(stream, "UTF-8")
        val rawBuffer = CharArray(maxReadSize)
        val buffer = StringBuffer()
        var readSize: Int = reader?.read(rawBuffer) ?: -1
        var maxReadBytes = maxReadSize
        while (readSize != -1 && maxReadBytes > 0) {
            if (readSize > maxReadBytes) {
                readSize = maxReadBytes
            }
            buffer.append(rawBuffer, 0, readSize)
            maxReadBytes -= readSize
            readSize = reader?.read(rawBuffer) ?: -1
        }
        return buffer.toString()
    }


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
