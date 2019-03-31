package android.thaihn.okhttpandretrofitsample.retrofit

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.thaihn.okhttpandretrofitsample.entity.Repository
import android.thaihn.okhttpandretrofitsample.retrofit.repository.GithubRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RetrofitViewModel() : ViewModel() {

    private val listRepository = MutableLiveData<List<Repository>>()
    private val error = MutableLiveData<String>()

    @SuppressLint("CheckResult")
    fun searchUser(name: String, githubRepository: GithubRepository) {
        githubRepository.searchRepository(name)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { it ->
                    it.items?.let { repos ->
                        listRepository.value = repos
                    }
                },
                { it ->
                    error.value = it.message
                })
    }

    fun getListRepository(): LiveData<List<Repository>> {
        return listRepository
    }

    fun getError(): LiveData<String> {
        return error
    }
}
