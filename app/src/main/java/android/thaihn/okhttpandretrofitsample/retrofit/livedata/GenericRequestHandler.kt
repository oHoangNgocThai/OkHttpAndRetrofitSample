package android.thaihn.okhttpandretrofitsample.retrofit.livedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Response

abstract class GenericRequestHandler<T> {

    abstract fun makeRequest(): Call<Response<T>>

    fun doRequest(): LiveData<DataWrapper<T>> {
        val liveData = MutableLiveData<DataWrapper<T>>()

        val dataWrapper = DataWrapper<T>()

        makeRequest().enqueue(object : ApiCallback<T>(){

            override fun handleError(response: Call<Response<T>>) {
                // handle error from response
            }

            override fun handleException(ex: Exception) {
                dataWrapper.apiException = ex
                liveData.value = dataWrapper
            }

            override fun handleResponseData(t: Response<T>) {
                // parser data from data
            }
        })
        return liveData
    }
}
