package it.giovanni.arkivio.fragments.detail.puntonet.paging

import dagger.Module
import dagger.Provides
import it.giovanni.arkivio.App
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.utils.Config
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object AppModule {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val cacheInterceptor = Interceptor { chain ->
        val response: okhttp3.Response = chain.proceed(chain.request())
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

    @Provides
    @Singleton
    fun provideCache(): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cacheDirectory = File(App.context.cacheDir, "cache")
        return Cache(cacheDirectory, cacheSize.toLong())
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(cache: Cache): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addNetworkInterceptor(cacheInterceptor)
            .cache(cache)
            .addInterceptor { chain: Interceptor.Chain ->
                val newRequest = chain.request().newBuilder()
                    // .addHeader("x-rapidapi-key", BuildConfig.API_KEY)
                    .addHeader("x-rapidapi-host", Config.BASE_URL2)
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

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Config.BASE_URL2)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    suspend fun getCharacters(page: Int): RickMortyResponse {
        return provideApiService(providesRetrofit(providesOkHttpClient(provideCache()))).getAllCharacters(page)
        // return createApiService().getAllCharacters(page)
    }
}