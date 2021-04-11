package it.giovanni.arkivio.restclient.volley

import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import it.giovanni.arkivio.App.Companion.context
import org.json.JSONException
import org.json.JSONObject

/**
 * I fondamenti di REST
 * Molto spesso i servizi web gestiscono database e le nostre app potrebbero aver bisogno di
 * interagire con questi ed effettuare quattro tipi di operazioni sui dati:
 * creazione, lettura, modifica e cancellazione.
 * Il pattern REST, sfruttando il protocollo HTTP, permette di effettuare tali operazioni da remoto.
 * Un servizio REST si baserà per lo più sull’inoltro di richieste nelle quali si specificheranno
 * almeno tre tipi di elementi:
 *
 * un URL, che rappresenta la risorsa cui dovremo accedere.
 *
 * un metodo HTTP che indicherà l’operazione da svolgere sui dati, da scegliere tipicamente tra:
 * GET (lettura), POST (inserimento), PUT (modifica) e DELETE (cancellazione).
 *
 * il corpo delle richieste conterrà dei dati che possono essere espressi in qualunque formato,
 * ma uno dei più utilizzati è JSON.
 *
 * Android dispone già di classi Java per gestire oggetti ed array JSON, rispettivamente JSONObject
 * e JSONArray. La prima permette di gestire le proprietà di un oggetto in modo simile ad una
 * struttura dati di tipo mappa, mentre la seconda rende un array fruibile in un ciclo con i metodi
 * length e getJSONObject.
 *
 * Volley fornisce due metodi pensati per le richieste basate su oggetti JSON: JSONArrayRequest e
 * JSONObjectRequest. Il primo richiede solo un URL da contattare e i due listener di successo ed
 * errore. Tale richiesta sarà effettuata con il metodo GET e il risultato ottenuto sarà un JSONArray.
 * Il secondo metodo permette di specificare il metodo HTTP, un URL, un oggetto JSON contenente
 * informazioni (opzionale) e i due soliti listener. Il risultato restituito sarà un JSONObject.
 */
class MyVolleyClient {

    companion object {

        const val url = "https://jsonplaceholder.typicode.com/posts"
        private val mRequestQueue: RequestQueue? = Volley.newRequestQueue(context)

        fun getPosts(callBack: IVolley) {
            val jsonArrayRequest = JsonArrayRequest(url, { response ->
                for (i in 0 until response.length()) {
                    try {
                        val jsonObject = response.getJSONObject(i)
                        val title: String = jsonObject.getString("title")

                        callBack.onVolleyGetSuccess(title)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }, {
                callBack.onVolleyFailure("Errore di rete")
            })
            mRequestQueue?.add(jsonArrayRequest)
        }

        fun addPosts(title: String?, text: String?, callBack: IVolley) {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("title", title)
                jsonObject.put("body", text)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val jsonObjectRequest = JsonObjectRequest(url, jsonObject, { response ->
                try {
                    val id = response.getString("id")
                    callBack.onVolleyPostSuccess("Elemento registrato con id $id")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, {
                callBack.onVolleyFailure("Errore di rete")
            })
            mRequestQueue?.add(jsonObjectRequest)
        }
    }
}