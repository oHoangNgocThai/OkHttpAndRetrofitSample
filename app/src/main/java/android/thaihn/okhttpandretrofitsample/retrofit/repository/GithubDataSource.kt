package android.thaihn.okhttpandretrofitsample.retrofit.repository

import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import io.reactivex.Observable

interface GithubDataSource {
    fun searchRepository(name: String): Observable<SearchResponse>
}
