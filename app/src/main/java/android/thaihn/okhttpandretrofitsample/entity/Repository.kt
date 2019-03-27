package android.thaihn.okhttpandretrofitsample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Repository(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String?,

    @SerializedName("full_name")
    val fullName: String?,

    @SerializedName("owner")
    val owner: Owner?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("folk")
    val folk: Boolean,

    @SerializedName("ssh_url")
    val sshUrl: String?,

    @SerializedName("language")
    val language: String?,

    @SerializedName("default_branch")
    val defaultBranch: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Owner::class.java.classLoader),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(fullName)
        parcel.writeParcelable(owner, flags)
        parcel.writeString(description)
        parcel.writeByte(if (folk) 1 else 0)
        parcel.writeString(sshUrl)
        parcel.writeString(language)
        parcel.writeString(defaultBranch)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Repository> {
        override fun createFromParcel(parcel: Parcel): Repository {
            return Repository(parcel)
        }

        override fun newArray(size: Int): Array<Repository?> {
            return arrayOfNulls(size)
        }
    }
}
