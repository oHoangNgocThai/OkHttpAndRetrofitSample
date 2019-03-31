package android.thaihn.okhttpandretrofitsample.retrofit.service

import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import android.thaihn.okhttpandretrofitsample.retrofit.repository.GithubDataSource
import io.reactivex.Observable

class GithubRemoteDataSource(
    private val githubApi: GithubService
) : GithubDataSource {

    override fun searchRepository(name: String): Observable<SearchResponse> {
        return githubApi.searchUser(name, "start", "desc")
    }
}
