package it.giovanni.arkivio.puntonet.reactivex

import io.reactivex.rxjava3.core.Single
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.ApiResult
import it.giovanni.arkivio.puntonet.retrofitgetpost.UsersResponse
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Utilizzando RxJava3CallAdapterFactory, puoi sfruttare la potenza del modello di programmazione
 * reattiva di RxJava per gestire le richieste API asincrone, le trasformazioni dei dati e la
 * gestione degli errori in modo conciso e componibile.
 *
 * RxJava3CallAdapterFactory è una classe factory fornita da Retrofit che integra RxJava3 con Retrofit.
 * Ti consente di utilizzare i tipi reattivi di RxJava (Observable, Single, Completable, ecc.) come
 * tipi restituiti (return) per i tuoi metodi API.
 */
object ApiServiceClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val cacheInterceptor = Interceptor { chain ->
        val response: Response = chain.proceed(chain.request())
        val cacheControl = CacheControl.Builder()
            .maxAge(30, TimeUnit.DAYS)
            .build()
        response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .build()
    }

    private const val cacheSize = 10 * 1024 * 1024 // 10 MiB
    private val cacheDirectory = File(context.cacheDir, "cache")
    private val cache = Cache(cacheDirectory, cacheSize.toLong())

    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(cacheInterceptor)
        .cache(cache)
        .addInterceptor { chain: Interceptor.Chain ->
            val newRequest = chain.request().newBuilder()
                // .addHeader("x-rapidapi-key", BuildConfig.API_KEY)
                .addHeader("x-rapidapi-host", BuildConfig.BASE_URL)
                // .header("User-Agent", Utils.getDeviceName()")
                .addHeader("applicationId", BuildConfig.APPLICATION_ID)
                .addHeader("app_version", BuildConfig.VERSION_NAME)
                .addHeader("os_version", android.os.Build.VERSION.RELEASE)
                .build()
            chain.proceed(newRequest)
        }
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(loggingInterceptor)
            }
        }
        .build()

    /**
     * La property retrofit è un'istanza di Retrofit creata utilizzando Retrofit.Builder. Imposta
     * l'URL di base per le richieste API, assegna okHttpClient come client per effettuare le
     * richieste e aggiunge i factory converter e call adapter necessari (GsonConverterFactory e
     * RxJava3CallAdapterFactory) per gestire la conversione dei dati e l'integrazione RxJava.
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    private fun createApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // Lascia la gestione di Success/Error a RxJava3
    fun getUsersV1(page: Int): Single<UsersResponse> {
        val response: Single<UsersResponse> = createApiService().getUsers(page)
        return response
    }

    // Lascia la gestione di Success/Error a ApiResult
    fun getUsersV2(page: Int): ApiResult<Single<UsersResponse>> {
        return try {
            val response: Single<UsersResponse> = createApiService().getUsers(page)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage)
        }
    }
}