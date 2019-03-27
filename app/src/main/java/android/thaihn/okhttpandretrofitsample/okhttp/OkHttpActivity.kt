package android.thaihn.okhttpandretrofitsample.okhttp

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.ActivityOkHttpBinding
import android.view.MenuItem

class OkHttpActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityOkHttpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ok_http)

        supportActionBar?.title = "OkHttp Sample"
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
