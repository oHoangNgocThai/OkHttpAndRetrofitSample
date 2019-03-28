package android.thaihn.okhttpandretrofitsample.okhttp

import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.ActivityOkHttpBinding
import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import android.thaihn.okhttpandretrofitsample.ui.RepositoryAdapter
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.net.URL

class OkHttpActivity : AppCompatActivity() {

    companion object {
        private val TAG = OkHttpActivity::class.java.simpleName
    }

    private lateinit var mBinding: ActivityOkHttpBinding

    private val mRepositoryAdapter = RepositoryAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ok_http)

        supportActionBar?.title = "OkHttp Sample"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.rvRepo.apply {
            adapter = mRepositoryAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }

        mBinding.apply {
            btnSearch.setOnClickListener {
                val name = edtInput.text.toString().trim()
                searchRepository(name)
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

    private fun searchRepository(name: String) {
        if (name.isEmpty()) {
            Toast.makeText(applicationContext, "Enter repository name", Toast.LENGTH_SHORT).show()
            return
        }

        SearchAsyncWithOkHttp().execute(name)
    }

    inner class SearchAsyncWithOkHttp : AsyncTask<String, Int, SearchResponse>() {

        override fun doInBackground(vararg params: String?): SearchResponse? {
            var responseSearch: SearchResponse? = null
            val key: String? = params[0]

            if (key != null) {
                val request = createRequest(key)

                // Call synchronous
                val response: Response = OkHttpClient().newCall(request).execute()
                Log.d(TAG, "Response:$response")

                // Call asynchronous
//                val request = createRequest(key)
//                OkHttpClient().newCall(request).enqueue(object : Callback {
//
//                    override fun onResponse(call: Call, response: Response) {
//                        val strResponse = response.body()?.string()?.trim()
//                        Log.d(TAG, "Response: $strResponse")
//                        if (strResponse != null) {
//                            responseSearch = Gson().fromJson(strResponse, SearchResponse::class.java)
//                        }
//                        return responseSearch
//                    }
//
//                    override fun onFailure(call: Call, e: IOException) {
//                        e.printStackTrace()
//                        Log.d(TAG, "onFailure: ${e.message}")
//                    }
//                })

                val strResponse = response.body()?.string()?.trim()
                if (strResponse != null) {
                    responseSearch = Gson().fromJson(strResponse, SearchResponse::class.java)
                }
            }
            return responseSearch
        }

        override fun onPostExecute(result: SearchResponse?) {
            Log.d(TAG, "result: $result")
            if (result != null) {
                result.items?.let {
                    mRepositoryAdapter.updateAllData(it)
                }
            }
        }

        private fun createRequest(key: String): Request {
            val url = URL(createUrl(key))

            return Request.Builder()
                    .header("Content-Type", "application/json")
                    .url(url)
                    .build()
        }

        private fun createUrl(key: String): String {
            return HttpUrl.parse("https://api.github.com/search/repositories")?.newBuilder()?.apply {
                addQueryParameter("q", key)
                addQueryParameter("sort", "")
                addQueryParameter("order", "desc")
            }?.build().toString()
        }
    }


}
