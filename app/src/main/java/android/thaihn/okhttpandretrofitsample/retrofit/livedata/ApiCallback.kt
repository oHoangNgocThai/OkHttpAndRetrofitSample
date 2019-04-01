package android.thaihn.okhttpandretrofitsample.retrofit.livedata

import retrofit2.Callback
import retrofit2.Response

abstract class ApiCallback<T> : Callback<Response<T>> {

    override fun onResponse(call: retrofit2.Call<Response<T>>, response: retrofit2.Response<Response<T>>) {
        val body = response.body()
        if (body != null) {
            handleResponseData(body)
        } else {
            handleError(call)
        }
    }

    override fun onFailure(call: retrofit2.Call<Response<T>>, t: Throwable) {
        if (t is java.lang.Exception) {
            handleException(t)
        } else {
            // Do something else
        }
    }

    abstract fun handleResponseData(t: Response<T>)

    abstract fun handleError(response: retrofit2.Call<Response<T>>)

    abstract fun handleException(ex: Exception)
}
