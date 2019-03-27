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
    val repoUrl: String?,

    @SerializedName("followers_url")
    val followersUrl: String?

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(login)
        parcel.writeInt(id)
        parcel.writeString(avatarUrl)
        parcel.writeString(url)
        parcel.writeString(repoUrl)
        parcel.writeString(followersUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Owner> {
        override fun createFromParcel(parcel: Parcel): Owner {
            return Owner(parcel)
        }

        override fun newArray(size: Int): Array<Owner?> {
            return arrayOfNulls(size)
        }
    }
}
