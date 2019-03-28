package android.thaihn.okhttpandretrofitsample.retrofit.service

import android.thaihn.okhttpandretrofitsample.entity.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {

    @GET("search/repositories")
    fun searchUser(
        @Query("q") query: String,
        @Query("sort") sort: String,
        @Query("order") order: String
    ): Call<SearchResponse>
}
