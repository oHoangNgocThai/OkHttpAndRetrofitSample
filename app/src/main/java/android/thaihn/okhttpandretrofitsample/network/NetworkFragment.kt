package android.thaihn.okhttpandretrofitsample.network

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.thaihn.okhttpandretrofitsample.R
import android.thaihn.okhttpandretrofitsample.databinding.FragmentNetworkBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class NetworkFragment : Fragment() {

    private var mDownloadCallback: DownloadCallback<String>? = null
    private var mDownloadTask: DownloadAsyncTask? = null
    private var mUrl: String? = null

    private lateinit var networkFragment: FragmentNetworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUrl = arguments?.getString(ExtraKeys.URL_KEY.name)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.let {
            mDownloadCallback = it as DownloadCallback<String>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        networkFragment = DataBindingUtil.inflate(inflater, R.layout.fragment_network, container, false)
        return networkFragment.root
    }

    override fun onDetach() {
        super.onDetach()
        // Cancel task when Fragment is destroyed.
        mDownloadCallback = null
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelDownload()
    }

    fun startDownload() {
        cancelDownload()
        mDownloadTask = DownloadAsyncTask(mDownloadCallback)
        mDownloadTask?.execute(mUrl)
    }

    fun cancelDownload() {
        mDownloadTask?.cancel(true)
        mDownloadTask = null
    }

    companion object {

        private val TAG = NetworkFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(fragmentManager: FragmentManager, url: String): NetworkFragment? {
            var networkFragment = fragmentManager.findFragmentByTag(NetworkFragment.TAG)
            return if (networkFragment == null) {
                networkFragment = NetworkFragment().apply {
                    arguments = Bundle().apply {
                        putString(ExtraKeys.URL_KEY.name, url)
                    }
                }
                fragmentManager.beginTransaction().add(networkFragment, TAG).commit()
                networkFragment
            } else {
                null
            }
        }

        enum class ExtraKeys {
            URL_KEY
        }
    }

}
