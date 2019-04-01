package android.thaihn.okhttpandretrofitsample.retrofit.livedata

import io.reactivex.Observer
import io.reactivex.annotations.Nullable
import io.reactivex.disposables.Disposable

//class ApiObserver<T>(private val changeListener: ChangeListener<T>) : Observer<DataWrapper<T>> {
//
//    fun onChanged(@Nullable tDataWrapper: DataWrapper<T>?) {
//        if (tDataWrapper != null) {
//            if (tDataWrapper.apiException != null) {
//                changeListener.onException(tDataWrapper.apiException)
//            } else {
//                changeListener.onSuccess(tDataWrapper.data)
//            }
//            return
//        }
//        //custom exceptionn to suite my use case
////        changeListener.onException(ValidationAPIException(ERROR_CODE, null))
//    }
//
//    interface ChangeListener<T> {
//        fun onSuccess(dataWrapper: T?)
//        fun onException(exception: Exception?)
//    }
//
//    companion object {
//        private val ERROR_CODE = 0
//    }
//}
