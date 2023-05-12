package it.giovanni.arkivio.restclient.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * The UsersResponse class is used as a wrapper for the list of User objects returned by the API call,
 * allowing it to be easily parsed and manipulated in the application code.
 *
 * It is a model class that represents the response received from an API call in the form of a JSON object.
 *
 * It implements the Serializable interface, which allows the class to be serialized and deserialized
 * so that it can be passed between different components of an Android application.
 *
 * The users field is a list of User objects, which represents the data returned from the API call.
 * The @SerializedName annotation is used to specify the name of the field in the JSON object, while
 */

data class UsersResponse(
    @SerializedName("users") var users: List<User?>?,
    var message: String?
) : Serializable