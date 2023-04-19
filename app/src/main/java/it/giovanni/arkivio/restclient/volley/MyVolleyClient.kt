package it.giovanni.arkivio.restclient.volley

import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import it.giovanni.arkivio.App.Companion.context
import org.json.JSONException
import org.json.JSONObject

/**
 * The MyVolleyClient class provides a simple way to interact with a remote API using Volley.
 *
 * It is a singleton object that provides methods to interact with a remote API using the Volley
 * library. It has two public methods, getPosts and addPosts, which respectively retrieve a list
 * of posts from the API and add a new post.
 *
 * url: a constant string that represents the URL of the API endpoint.
 *
 * mRequestQueue: a private RequestQueue object that represents the queue of requests to be sent to the API.
 *
 * getPosts: a public function that retrieves the list of posts from the API. It takes an instance of
 * IVolley as a parameter, which defines two callback functions, onVolleyGetSuccess and onVolleyFailure,
 * to be called respectively when the request succeeds or fails. It creates a new JsonArrayRequest object,
 * which is used to send a GET request to the API. The response is then parsed into JSON objects,
 * and the title of each post is retrieved and passed to the onVolleyGetSuccess callback function.
 *
 * addPosts: a public function that adds a new post to the API. It takes the title and text of the post
 * as parameters, as well as an instance of IVolley as a parameter, which defines two callback functions,
 * onVolleyPostSuccess and onVolleyFailure, to be called respectively when the request succeeds or fails.
 * It creates a new JsonObjectRequest object, which is used to send a POST request to the API with the
 * JSON representation of the new post. If the request succeeds, the ID of the new post is retrieved
 * from the response and passed to the onVolleyPostSuccess callback function.
 */
class MyVolleyClient {

    companion object {

        const val url = "https://jsonplaceholder.typicode.com/posts"
        private val mRequestQueue: RequestQueue = Volley.newRequestQueue(context)

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
            mRequestQueue.add(jsonArrayRequest)
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
            mRequestQueue.add(jsonObjectRequest)
        }
    }
}