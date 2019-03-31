package android.thaihn.okhttpandretrofitsample.retrofit

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.thaihn.okhttpandretrofitsample.BuildConfig
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.ActivityRetrofitBinding
import android.thaihn.okhttpandretrofitsample.retrofit.repository.GithubRepository
import android.thaihn.okhttpandretrofitsample.retrofit.repository.RetrofitProvider
import android.thaihn.okhttpandretrofitsample.retrofit.service.GithubRemoteDataSource
import android.thaihn.okhttpandretrofitsample.retrofit.service.GithubService
import android.thaihn.okhttpandretrofitsample.ui.RepositoryAdapter
import android.view.MenuItem
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitActivity : AppCompatActivity() {

    companion object {
        private val TAG = RetrofitActivity::class.java.simpleName
    }

    private lateinit var mBinding: ActivityRetrofitBinding
    private lateinit var mViewModel: RetrofitViewModel

    private val mRepositoryAdapter = RepositoryAdapter(arrayListOf())

    private lateinit var mRepository: GithubRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_retrofit)
        mViewModel = ViewModelProviders.of(this).get(RetrofitViewModel::class.java)

        mRepository = GithubRepository(GithubRemoteDataSource(RetrofitProvider.providerGithubApi()))

        supportActionBar?.title = "Retrofit Sample"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.rvRepo.apply {
            adapter = mRepositoryAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }

        mViewModel.getError().observe(this, Observer {
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
        })

        mViewModel.getListRepository().observe(this, Observer {repos->
            repos?.let {
                mRepositoryAdapter.updateAllData(it)
            }
        })

        mBinding.apply {
            btnSearch.setOnClickListener {
                val name = edtInput.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(applicationContext, "Enter repository name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                mRepository?.let {
                    mViewModel.searchUser(name, it)
                }
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
//        if (name.isEmpty()) {
//            Toast.makeText(applicationContext, "Enter repository name", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val service = createService()
//        val callSearch: Call<SearchResponse> = service.searchUser("mario", "start", "desc")
//        callSearch.enqueue(object : Callback<SearchResponse> {
//
//            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
//                Log.d(TAG, "Response: ${response.body()}")
//                val errorBody = response.errorBody()
//                val responseCode = response.code()
//                response.body()?.items?.let {
//                    mRepositoryAdapter.updateAllData(it)
//                }
//            }
//
//            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
//                t.printStackTrace()
//                Log.d(TAG, "onFailure: ${t.message}")
//            }
//        })
    }

    private fun createService(): GithubService {
        val BASE_URL = "https://api.github.com"

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(GithubService::class.java)
    }
}
