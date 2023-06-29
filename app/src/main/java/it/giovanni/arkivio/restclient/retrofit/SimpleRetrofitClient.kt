package it.giovanni.arkivio.restclient.retrofit

import android.util.Log
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This class use Retrofit library to make an HTTP request to an external API, receive a list of
 * users as a response, and use a callback interface to handle the response in the calling code.
 *
 * The getUsers method takes an instance of IRetrofit interface as a parameter, which is used as a
 * callback to notify the caller of the success or failure of the network request.
 *
 * The url variable is the base URL of the external API, and a new Retrofit object is created with
 * this URL and a GsonConverterFactory for parsing the response JSON.
 *
 * RetrofitService interface is used to define the API endpoints, and a new instance of this interface
 * is created using the retrofit.create method.
 *
 * Then, a network request is made to get the list of users using the getUsers() method of the
 * RetrofitService interface. This request is made asynchronously and the response is handled in two
 * callback methods: onResponse and onFailure.
 *
 * In onResponse, the list of users is extracted from the response body and passed to the
 * onRetrofitSuccess method of the callback interface along with a success message.
 *
 * In onFailure, the failure message is passed to the onRetrofitFailure method of the callback interface.
 */

object SimpleRetrofitClient {

    fun getUsers(callback: IRetrofit) {

        val retrofit: Retrofit? = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: RetrofitService? = retrofit?.create(RetrofitService::class.java)

        service?.getUsers(0)?.enqueue(object : Callback<UsersResponse> {
            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                val usersResponse: UsersResponse? = response.body()
                callback.onRetrofitSuccess(usersResponse, "onSuccess: Caricamento completato")
            }

            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                t.printStackTrace()
                Log.e("[Throwable]", "Throwable: $t")
                callback.onRetrofitFailure("onFailure: Caricamento fallito")
            }
        })
    }
}