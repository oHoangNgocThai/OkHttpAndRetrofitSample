package android.thaihn.okhttpandretrofitsample.retrofit.repository

import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import android.thaihn.okhttpandretrofitsample.retrofit.service.GithubRemoteDataSource
import io.reactivex.Observable

class GithubRepository(
    private val githubRemoteDataSource: GithubRemoteDataSource
) : GithubDataSource {

    override fun searchRepository(name: String): Observable<SearchResponse> {
        return githubRemoteDataSource.searchRepository(name)
    }
}
