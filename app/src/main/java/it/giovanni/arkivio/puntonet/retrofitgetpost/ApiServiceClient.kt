package it.giovanni.arkivio.puntonet.retrofitgetpost

import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.BuildConfig
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.ApiResult

/**
 * Questa classe fornisce una factory per la creazione e la configurazione delle istanze
 * dell'interfaccia Retrofit ApiService che viene utilizzata per effettuare richieste API
 * a un server back-end. Definisce inoltre una funzione getListUsers che chiama il metodo
 * getListUsers dell'interfaccia ApiService e restituisce la response come oggetto ApiResult.
 *
 * - La keyword companion object viene usata per definire un oggetto singleton che ha lo stesso nome
 *   nome della classe che lo contiene (ApiServiceClient). È possibile accedere a questo oggetto
 *   dall'esterno della classe senza creare un'istanza della classe e si può accedere direttamente
 *   ai suoi membri come se fossero membri statici della classe in Java.
 * - loggingInterceptor è un'istanza di HttpLoggingInterceptor che logga i dati di request e
 *   response HTTP nella console. Il livello di logging è impostato su BODY, che logga l'intero
 *   body della request e della response. Questo è utile per il debug dei problemi di rete.
 * - cacheInterceptor è un'istanza di Interceptor che aggiunge un header Cache-Control alle
 *   response HTTP per abilitare il caching per un massimo di 30 giorni.
 * - cacheSize e cacheDirectory vengono utilizzati per definire la dimensione e la posizione della
 *   cache utilizzata da OkHttpClient.
 * - okHttpClient è un'istanza di OkHttpClient configurata con cacheInterceptor, cache e diversi
 *   header che vengono aggiunti a ogni request. Questi header includono l'API key, l'API host,
 *   l'dapplication ID, la versione dell'app e la versione del sistema operativo. Se l'app è in
 *   esecuzione in modalità di debug, al client viene aggiunto anche loggingInterceptor.
 * - retrofit è un'istanza di Retrofit configurata con la base URL dell'API, okHttpClient e un
 *   GsonConverterFactory che converte le response JSON in oggetti Kotlin.
 * - createApiService() è una funzione che restituisce un'istanza dell'interfaccia ApiService creata
 *   dall'istanza Retrofit.
 * - getListUsers(page: Int) è una funzione suspend che chiama il metodo getListUsers dell'interfaccia
 *   ApiService e restituisce la response come oggetto ApiResult. Se viene generata un'eccezione durante
 *   la chiamata API, la funzione restituisce un risultato Error con il messaggio di errore localizzato.
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

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun createApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    suspend fun getUsers(page: Int): ApiResult<UsersResponse> {
        return try {
            val usersResponse: UsersResponse = createApiService().getUsers(page)
            ApiResult.Success(usersResponse)
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage)
        }
    }

    suspend fun addUser(utente: Utente): ApiResult<UtenteResponse> {
        return try {
            val utenteResponse: UtenteResponse = createApiService().addUser(utente)
            ApiResult.Success(utenteResponse)
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage)
        }
    }
}