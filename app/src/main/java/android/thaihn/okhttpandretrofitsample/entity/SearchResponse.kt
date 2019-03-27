package android.thaihn.okhttpandretrofitsample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class SearchResponse(

        @SerializedName("total_count")
        val total_count: Int,

        @SerializedName("incomplete_results")
        val incomplete_results: Boolean,

        @SerializedName("items")
        val items: List<Repository>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.createTypedArrayList(Repository)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(total_count)
        parcel.writeByte(if (incomplete_results) 1 else 0)
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchResponse> {
        override fun createFromParcel(parcel: Parcel): SearchResponse {
            return SearchResponse(parcel)
        }

        override fun newArray(size: Int): Array<SearchResponse?> {
            return arrayOfNulls(size)
        }
    }
}
