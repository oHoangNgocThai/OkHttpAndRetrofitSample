package android.thaihn.okhttpandretrofitsample.retrofit

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.ActivityRetrofitBinding
import android.view.MenuItem

class RetrofitActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRetrofitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_retrofit)

        supportActionBar?.title = "Retrofit Sample"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
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
}
