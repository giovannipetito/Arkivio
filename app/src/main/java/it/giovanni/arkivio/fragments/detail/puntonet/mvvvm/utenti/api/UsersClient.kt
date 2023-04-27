package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.utenti.api

import it.giovanni.arkivio.restclient.retrofit.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * In questo esempio, usersService è un servizio Retrofit che è stato definito per effettuare
 * richieste di rete (network request) a un'API che restituisce una lista di utenti.
 */

class UsersClient {

    companion object {

        fun getUsers(callback: IUsers) {

            // Create a Retrofit instance using the base URL of the API and add a converter factory for Gson.
            val retrofit: Retrofit? = Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create an instance of the UsersService interface using the Retrofit instance:
            val usersService: UsersService? = retrofit?.create(UsersService::class.java)

            usersService?.getUsers()?.enqueue(object : Callback<List<User?>?> {
                override fun onResponse(call: Call<List<User?>?>, response: Response<List<User?>?>) {
                    val users: List<User?>? = response.body()
                    callback.onSuccess("onSuccess: Caricamento completato", users)
                }

                override fun onFailure(call: Call<List<User?>?>, t: Throwable) {
                    callback.onFailure("onFailure: Caricamento fallito")
                }
            })
        }
    }
}