package it.giovanni.arkivio.puntonet.retrofitgetpost

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users_worker_table")
data class User(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    var id: Int,

    @SerializedName("email")
    var email: String,

    @SerializedName("first_name")
    var firstName: String,

    @SerializedName("last_name")
    var lastName: String,

    @SerializedName("avatar")
    var avatar: String
)