package android.thaihn.okhttpandretrofitsample.network.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.ActivityConnectionBinding
import android.thaihn.okhttpandretrofitsample.network.*
import android.thaihn.okhttpandretrofitsample.util.NetworkUtil
import android.util.Log
import android.view.MenuItem

class ConnectionActivity : AppCompatActivity(),
    DownloadCallback<String> {

    companion object {
        private val TAG = ConnectionActivity::class.java.simpleName
    }

    private lateinit var mBinding: ActivityConnectionBinding

    private var networkFragment: NetworkFragment? = null

    private var mDownloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_connection)

        supportActionBar?.title = "Connection Sample"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        networkFragment = NetworkFragment.getInstance(
            supportFragmentManager,
            "https://api.github.com/search/repositories?q=mario?language:kotlin&sort=stars&order=desc"
        )

        mBinding.btnFetchData.setOnClickListener {
//            startDownload()
            SearchAsync().execute("https://api.github.com/search/repositories?q=mario?language:kotlin&sort=stars&order=desc")
        }

        mBinding.btnClearData.setOnClickListener {
            finishDownloading()
            mBinding.tvResult.text = ""
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

    override fun finishDownloading() {
        mDownloading = false
        networkFragment?.cancelDownload()
    }

    override fun isNetworkConnected(): Boolean {
        return NetworkUtil.isNetworkEnable(this)
    }

    override fun onProgressUpdate(code: Int, percentComplete: Int) {
        Log.d(TAG, "Code: $code")
        when (code) {
            // You can add UI behavior for progress updates here.
            ERROR -> {
                mBinding.tvStatus.text = "Status: ERROR"
            }
            CONNECT_SUCCESS -> {
                mBinding.tvStatus.text = "Status: CONNECT_SUCCESS"
            }
            GET_INPUT_STREAM_SUCCESS -> {
                mBinding.tvStatus.text = "Status: INPUT_STREAM_SUCCESS"
            }
            PROCESS_INPUT_STREAM_IN_PROGRESS -> {
                mBinding.tvStatus.text = "Status: PROCESS_INPUT_STREAM_IN_PROGRESS"
            }
            PROCESS_INPUT_STREAM_SUCCESS -> {
                mBinding.tvStatus.text = "Status: PROCESS_INPUT_STREAM_SUCCESS"
            }
        }
    }

    override fun updateFromDownload(result: String?) {
        if (result != null) {
            mBinding.tvResult.text = result
        } else {
            mBinding.tvResult.text = "Connection lost"
        }
    }

    private fun startDownload() {
        networkFragment?.apply {
            if (!mDownloading) {
                startDownload()
            }
        }
    }

}
