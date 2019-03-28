package android.thaihn.okhttpandretrofitsample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Owner(

    @SerializedName("login")
    val login: String?,

    @SerializedName("id")
    val id: Int,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("repos_url")
    val repoUrl: String?

)
