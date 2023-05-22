package it.giovanni.arkivio.fragments.detail.puntonet.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
import javax.inject.Singleton

/**
 * La classe ApiModule è un modulo Dagger che fornisce dipendenze per le richieste network di
 * un'applicazione Android usando Retrofit e OkHttp.
 * - L'annotazione @Module indica che questa classe è un modulo Dagger.
 * - L'annotazione @InstallIn(SingletonComponent::class) specifica che il modulo deve essere
 *   installato in SingletonComponent, che è il componente Hilt per le dipendenze con ambito singleton.
 * - I metodi annotati con @Provides forniscono le diverse dipendenze.
 */
@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

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

    /*
    @Provides
    @Singleton
    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cacheDirectory = File(application.applicationContext.cacheDir, "cache")
        return Cache(cacheDirectory, cacheSize.toLong())
    }
    */

    /**
     * provideCache() crea e fornisce un'istanza della classe Cache dalla libreria OkHttp. Specifica
     * la dimensione della cache e la directory in cui verranno archiviate le response memorizzate
     * nella cache.
     */
    @Provides
    @Singleton
    fun provideCache(): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cacheDirectory = File(context.cacheDir, "cache")
        return Cache(cacheDirectory, cacheSize.toLong())
    }

    /**
     * provideOkHttpClient() crea e fornisce un'istanza della classe OkHttpClient dalla libreria
     * OkHttp. Configura vari interceptor e header, come l'interceptor della cache, l'interceptor
     * di rete e header di request aggiuntive. LoggingInterceptor registra la request e la response
     * HTTP a scopo di debug. La condizione if (BuildConfig.DEBUG) garantisce che l'interceptor di
     * log venga aggiunto solo nelle build di debug.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val builder = OkHttpClient.Builder()
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

        return builder.build()
    }

    /**
     * provideRetrofit() crea e fornisce un'istanza della classe Retrofit. Specifica la Base URL per
     * l'API, l'istanza OkHttpClient da utilizzare per le richieste di rete e GsonConverterFactory
     * per convertire le response JSON in oggetti.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * provideApiService() crea e fornisce un'istanza dell'interfaccia ApiService.
     * Utilizza l'istanza Retrofit per creare un'implementazione dell'interfaccia.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}