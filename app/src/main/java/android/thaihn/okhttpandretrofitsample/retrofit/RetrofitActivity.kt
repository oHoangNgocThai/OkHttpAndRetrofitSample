package android.thaihn.okhttpandretrofitsample.retrofit

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.ActivityRetrofitBinding
import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import android.thaihn.okhttpandretrofitsample.retrofit.service.GithubService
import android.thaihn.okhttpandretrofitsample.ui.RepositoryAdapter
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitActivity : AppCompatActivity() {

    companion object {
        private val TAG = RetrofitActivity::class.java.simpleName
    }

    private lateinit var mBinding: ActivityRetrofitBinding

    private val mRepositoryAdapter = RepositoryAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_retrofit)

        supportActionBar?.title = "Retrofit Sample"
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

        val service = createService()
        val callSearch: Call<SearchResponse> = service.searchUser("mario", "start", "desc")
        callSearch.enqueue(object : Callback<SearchResponse> {

            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                Log.d(TAG, "Response: ${response.body()}")
                response.body()?.items?.let {
                    mRepositoryAdapter.updateAllData(it)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                t.printStackTrace()
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun createService(): GithubService {
        val BASE_URL = "https://api.github.com"

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(GithubService::class.java)
    }
}
