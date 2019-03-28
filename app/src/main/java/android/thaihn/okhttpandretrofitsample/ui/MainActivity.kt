package android.thaihn.okhttpandretrofitsample.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.ActivityMainBinding
import android.thaihn.okhttpandretrofitsample.network.ConnectionActivity
import android.thaihn.okhttpandretrofitsample.okhttp.OkHttpActivity
import android.thaihn.okhttpandretrofitsample.retrofit.RetrofitActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mBinding.btnNetwork.setOnClickListener {
            startActivity(Intent(this, ConnectionActivity::class.java))
        }

        mBinding.btnOkhttp.setOnClickListener {
            startActivity(Intent(this, OkHttpActivity::class.java))
        }

        mBinding.btnRetrofit.setOnClickListener {
            startActivity(Intent(this, RetrofitActivity::class.java))
        }
    }
}
