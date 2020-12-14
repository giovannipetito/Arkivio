package it.giovanni.arkivio.realtime

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import it.giovanni.arkivio.bean.user.Response
import it.giovanni.arkivio.bean.user.SignInResponse
import it.giovanni.arkivio.bean.user.UserResponse
import org.json.JSONObject
import java.lang.Exception

class RealtimeClient {

    companion object {

        private const val tokenUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyBAFXljudG6ubVR-6MiszC5lT4y2YlQqH8"
        private const val baseUrl = "https://arkivio-ebf7e.firebaseio.com/"
        private val client: AsyncHttpClient = AsyncHttpClient()

        fun get(url: String, params: RequestParams?, responseHandler: AsyncHttpResponseHandler?) {
            client.get(getAbsoluteUrl(url), params, responseHandler)
        }

        private fun post(url: String, params: RequestParams?, responseHandler: AsyncHttpResponseHandler?) {
            client.post(url, params, responseHandler)
        }

        private fun getAbsoluteUrl(relativeUrl: String): String {
            return baseUrl + relativeUrl
        }

        fun callRealtimeDatabase(callback: IRealtimeCallback) {
            getIdToken(callback)
        }

        private fun getIdToken(callback: IRealtimeCallback) {

            val params = RequestParams()
            params.put("email", "giovanni.petito88@gmail.com")
            params.put("password", "soundgarden06")
            params.put("returnSecureToken", true)

            post(tokenUrl, params, object : AsyncHttpResponseHandler() {

                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {

                    if (statusCode == 200) {
                        try {
                            val jSONObject = JSONObject(String(responseBody)).toString()
                            val gson: Gson? = GsonBuilder().serializeNulls().create()
                            val signInResponse: SignInResponse? = gson?.fromJson(jSONObject, SignInResponse::class.java)

                            getDatabase(signInResponse?.idToken, callback)

                        } catch (ex: IllegalStateException) {
                            ex.printStackTrace()
                        }
                    } else
                        callback.onRealtimeCallbackFailure("onSuccess: Caricamento fallito")
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                    callback.onRealtimeCallbackFailure("onFailure: Caricamento fallito")
                }
            })
        }

        private fun getDatabase(idToken: String?, callback: IRealtimeCallback) {

            get("response/.json?auth=$idToken", null, object : AsyncHttpResponseHandler() {

                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                    if (statusCode == 200) {
                        try {
                            val jSONObject = JSONObject(String(responseBody)).toString()
                            val gson: Gson? = GsonBuilder().serializeNulls().create()
                            val response: Response? = gson?.fromJson(jSONObject, Response::class.java)

                            callback.onRealtimeCallbackSuccess("onSuccess: Caricamento completato", response!!)

                        } catch (ex: Exception) {
                            callback.onRealtimeCallbackFailure("Exception: Caricamento fallito")
                            ex.printStackTrace()
                        }
                    } else
                        callback.onRealtimeCallbackFailure("statusCode != 200: Caricamento fallito")
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    callback.onRealtimeCallbackFailure("onFailure: Caricamento fallito")
                }
            })
        }
    }
}