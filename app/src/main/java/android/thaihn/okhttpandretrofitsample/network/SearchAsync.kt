package android.thaihn.okhttpandretrofitsample.network

import android.os.AsyncTask
import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import android.thaihn.okhttpandretrofitsample.util.NetworkUtil
import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.IOException
import java.io.StringReader
import java.net.URL

class SearchAsync : AsyncTask<String, Int, SearchResponse>() {

    companion object {
        private val TAG = SearchAsync::class.java.simpleName
    }

    override fun doInBackground(vararg params: String?): SearchResponse? {
        var searchResponse: SearchResponse? = null
        if (!isCancelled && params.isNotEmpty()) {
            var result: String? = null
            val link = params[0]
            try {
                val url = URL(link)
                result = NetworkUtil.downloadUrl(url)
                result?.trim()
                if (result != null) {
                    val reader = JsonReader(StringReader(result)).apply {
                        isLenient = true
                    }
                    searchResponse = Gson().fromJson(reader, SearchResponse::class.java)
                } else {
                    throw  IOException("No response received.")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d(TAG, "Error: ${ex.message}")
            }
        }
        return searchResponse
    }

    override fun onPostExecute(result: SearchResponse?) {
        Log.d(TAG, "Response: $result")
    }
}