package android.thaihn.okhttpandretrofitsample.retrofit.livedata

import android.arch.lifecycle.LiveData


//class SignInInteractor : GenericRequestHandler<User>() {
//    private val authService = APIService.getInstance().getServiceForApi(AuthService::class.java)
//    private var userId: String? = null
//    private var pinCode: String? = null
//
//    fun onAuthRequest(): LiveData<DataWrapper<User>> {
//        return doRequests()
//    }
//
//    override fun makeRequest(): Call<Response<User>> {
//        return authService.postUserPhoneLogin(RequestBody.UserPhoneLogin(userId, pinCode))
//    }
//
//    companion object {
//
//        fun createInstance(userId: String, pinCode: String): SignInInteractor {
//            val signInWithPinLoader = SignInInteractor()
//            signInWithPinLoader.userId = userId
//            signInWithPinLoader.pinCode = pinCode
//            return signInWithPinLoader
//        }
//    }
//}
