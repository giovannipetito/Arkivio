package it.giovanni.arkivio.restclient.retrofit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyRetrofitClient {

    companion object {

        fun getUsers(callback: IRetrofit) {

            val url = "https://jsonplaceholder.typicode.com/"

            val retrofit: Retrofit? = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: RetrofitService? = retrofit?.create(RetrofitService::class.java)

            service?.getUsers()?.enqueue(object : Callback<List<User?>?> {
                override fun onResponse(call: Call<List<User?>?>, response: Response<List<User?>?>) {
                    val list: List<User?>? = response.body()
                    callback.onRetrofitSuccess("onSuccess: Caricamento completato", list)
                }

                override fun onFailure(call: Call<List<User?>?>, t: Throwable?) {
                    callback.onRetrofitFailure("onFailure: Caricamento fallito")
                }
            })
        }
    }
}