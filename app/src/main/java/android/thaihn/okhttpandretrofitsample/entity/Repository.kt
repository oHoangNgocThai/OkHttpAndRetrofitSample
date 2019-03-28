package android.thaihn.okhttpandretrofitsample.entity

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
        val defaultBranch: String?,

        @SerializedName("repos_url")
        val reposUrl: String?
)
