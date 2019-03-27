package android.thaihn.okhttpandretrofitsample.network

import android.os.AsyncTask
import android.thaihn.okhttpandretrofitsample.network.entity.ResultNetwork
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class DownloadAsyncTask(
        private val callback: DownloadCallback<String>?
) : AsyncTask<String, Int, ResultNetwork>() {

    override fun onPreExecute() {
        callback?.apply {
            if (!isNetworkConnected()) {
                updateFromDownload(null)
                cancel(true)
            }
        }
    }

    override fun doInBackground(vararg params: String?): ResultNetwork? {
        var result: ResultNetwork? = null
        if (!isCancelled && params.isNotEmpty()) {
            val urlPath = params[0]
            result = try {
                val url = URL(urlPath)
                val resultString = downloadUrl(url)
                if (resultString != null) {
                    ResultNetwork(resultString, null)
                } else {
                    throw  IOException("No response received.")
                }
            } catch (ex: Exception) {
                ResultNetwork(null, ex)
            }
        }
        return result
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: ResultNetwork?) {
        callback?.apply {
            result?.exception?.also { exception ->
                updateFromDownload(exception.message)
                return
            }
            result?.value?.also { value ->
                updateFromDownload(value)
                return
            }
            finishDownloading()
        }
    }

    override fun onCancelled(result: ResultNetwork?) {
        super.onCancelled(result)
    }

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
                publishProgress(CONNECT_SUCCESS)
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("Http error code: $responseCode")
                }

                publishProgress(GET_INPUT_STREAM_SUCCESS, 0)
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
}