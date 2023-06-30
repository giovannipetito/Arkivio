package it.giovanni.arkivio.restclient.asynchttp

import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header

/**
 * Libreria Async Http CLient
 * Questa libreria permette di effettuare richieste HTTP(s) asincrone rispetto al thread principale
 * dell’interfaccia grafica.
 *
 * HTTP (HyperText Transfer Protocol) è un protocollo di comunicazione che permette, in una
 * architettura client-server, di poter richiedere informazioni ad un determinato dispositivo
 * e quest’ultimo provvederà a rispondere. Tale protocollo viene usato ogni volta che usiamo
 * il nostro browser. La (s) sta per secure e denota la versione sicura del protocollo.
 *
 * Il protocollo HTTP e il formato JSON sono molto usati quando si vogliono creare sistemi di
 * comunicazione tra i dispositivi mobile e un server. Il nome tecnico è API, in quanto il software
 * presente sul server, ad esempio un programma scritto in PHP, mette a disposizione la possibilità
 * di ricevere ed inviare informazioni attraverso uno specifico formato. Ad esempio è possibile
 * richiedere al server i dati di un utente ed esso risponderà con un file JSON.
 */
object MyAsyncHttpClient {

    /**
     * Viene fatta una richiesta asincrona con il server che restituirà una risposta in formato
     * JSON che verrà convertito in un oggetto Java di tipo Reponse, grazie alla libreria GSON.
     */
    fun getIp(callback: IAsyncHttpClient) {
        val url = "https://api.ipify.org/?format=json"
        // val client = AsyncHttpClient()
        val client = AsyncHttpClient(true, 80, 443)
        val params = RequestParams()
        // params.put("key", "value")
        // params.put("more", "data")

        client[url, params, object : TextHttpResponseHandler() {
            /*
            override fun onStart() {
                super.onStart()
            }
            */

            override fun onSuccess(statusCode: Int, headers: Array<Header?>?, jsonResponse: String?) {
                // called when response HTTP status is: 200
                val gson = Gson()
                val response = gson.fromJson(jsonResponse, Response::class.java)
                callback.onAsyncHttpSuccess("onSuccess: Caricamento completato", response)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header?>?, jsonResponse: String?, t: Throwable?) {
                // called when response HTTP status is: 401, 403, 404, ecc.
                callback.onAsyncHttpFailure("onFailure: Caricamento fallito")
            }
        }]
    }
}