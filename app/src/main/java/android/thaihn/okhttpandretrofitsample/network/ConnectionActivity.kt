package android.thaihn.okhttpandretrofitsample.network

import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.ActivityConnectionBinding
import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import android.thaihn.okhttpandretrofitsample.ui.RepositoryAdapter
import android.util.Log
import android.view.MenuItem
import com.google.gson.Gson
import okhttp3.HttpUrl
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ConnectionActivity : AppCompatActivity() {

    companion object {
        private val TAG = ConnectionActivity::class.java.simpleName
    }

    private lateinit var mBinding: ActivityConnectionBinding

    private val mRepositoryAdapter = RepositoryAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_connection)

        supportActionBar?.title = "Connection Sample"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.rvRepo.apply {
            adapter = mRepositoryAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }

        mBinding.apply {
            btnSearch.setOnClickListener {
                val name = edtInput.text.toString().trim()
                SearchRepositoryAsync().execute(name)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    inner class SearchRepositoryAsync : AsyncTask<String, Int, SearchResponse>() {

        override fun doInBackground(vararg params: String?): SearchResponse? {
            var searchResponse: SearchResponse? = null
            val key: String? = params[0]
            if (key != null) {
                try {
                    val urlString = createUrl(key)
                    val resultString = downloadUrl(urlString)
                    Log.d(TAG, "Response: $resultString")

                    resultString?.let {
                        searchResponse = Gson().fromJson(it, SearchResponse::class.java)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.d(TAG, "Exception: ${ex.message}")
                }
            }
            return searchResponse
        }

        override fun onPostExecute(result: SearchResponse?) {
            Log.d(TAG, "Result: $result")
            if (result != null) {
                result.items?.let {
                    mRepositoryAdapter.updateAllData(it)
                }
            }
        }

        private fun createUrl(key: String): String {
            return HttpUrl.parse("https://api.github.com/search/repositories")?.newBuilder()?.apply {
                addQueryParameter("q", key)
                addQueryParameter("sort", "")
                addQueryParameter("order", "desc")
            }?.build().toString()
        }

        @Throws(IOException::class)
        fun downloadUrl(urlStr: String): String {
            var result = ""
            var connection: HttpsURLConnection? = null
            try {
                val url = URL(urlStr)
                connection = url.openConnection() as HttpsURLConnection
                connection.run {
                    readTimeout = 6000
                    connectTimeout = 6000
                    requestMethod = "GET"
                    doInput = true
                    connect()

                    Log.d(TAG, "responseCode: $responseCode")
                    if (responseCode != HttpsURLConnection.HTTP_OK) {
                        throw IOException("HTTP error code: $responseCode")
                    }

                    inputStream?.let { stream ->
                        result = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                        stream.close()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                connection?.inputStream?.close()
                connection?.disconnect()
            }

            return result
        }
    }
}
