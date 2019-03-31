package android.thaihn.okhttpandretrofitsample.retrofit.service

import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {

    @GET("search/repositories")
    fun searchUser(
        @Query("q") query: String,
        @Query("sort") sort: String,
        @Query("order") order: String
    ): Observable<SearchResponse>
}
