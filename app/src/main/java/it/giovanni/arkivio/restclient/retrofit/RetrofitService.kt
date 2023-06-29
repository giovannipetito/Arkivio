package it.giovanni.arkivio.restclient.retrofit

import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.UsersResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This interface defines a Retrofit service and is annotated with @GET("users") to specify the
 * HTTP GET method and the endpoint URL path /users. It defines a method that retrieves a list of
 * User objects from the specified API endpoint using the Retrofit library.
 *
 * The method getUsers() is a Retrofit service method that returns a Call object with a parameterized
 * type of List<User?>?, indicating that it returns a list of nullable User objects.
 *
 * The Call object is used to send the network request and receive the response from the API endpoint.
 * It provides a enqueue() method that takes a Callback object as a parameter. The Callback object has
 * two methods: onResponse() and onFailure().
 *
 * The onResponse() method is called when the network request is successful and it receives the Response
 * object that contains the response data. The response data is retrieved using the body() method,
 * which returns the List<User?>? object in this case.
 *
 * The onFailure() method is called when the network request fails due to an error, and it receives a
 * Throwable object that describes the error.
 */
interface RetrofitService {

    @GET("/api/users")
    fun getUsers(@Query("page") page: Int): Call<UsersResponse>
}