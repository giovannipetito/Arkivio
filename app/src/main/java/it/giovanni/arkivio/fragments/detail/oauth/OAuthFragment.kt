package it.giovanni.arkivio.fragments.detail.oauth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.microsoft.aad.adal.*
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.UserFactory
import kotlinx.android.synthetic.main.oauth_layout.*
import org.json.JSONObject
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class OAuthFragment : DetailFragment() {

    // UI & Debugging Variables
    // private val TAG = CedoliniListFragment::class.java.simpleName
    private val TAG = "TOKENTAG"

    // Azure AD Constants
    // Authority is in the form of https://login.microsoftonline.com/yourtenant.onmicrosoft.com
    private val AUTHORITY = "https://login_layout.microsoftonline.com/common"

    // The clientID of your application is a unique identifier which can be obtained from the app registration portal
    private val CLIENT_ID = "c1497887-17d4-4bec-965c-99734850fcb2" // CLIENT_ID PIKSEL

    // Il valore {tenant} del percorso della richiesta può essere usato per controllare chi può
    // accedere all'applicazione. I valori consentiti sono gli identificatori dei tenant, ad esempio
    // 8eaef023-2b34-4da1-9baa-8bc8c9d6a490, contoso.onmicrosoft.com o common per i token indipendenti dai tenant.
    private val TENANT = "86fb8e1f-02bd-4434-96ae-aa3ef467ada8"

    // Resource URI of the endpoint which will be accessed
    private val RESOURCE_ID = "https://graph.microsoft.com/"
    // The Redirect URI of the application (Optional)
    private val REDIRECT_URI = "msauth://it.windtre.employee/GW9AYX3EVqFKA7b2vup0FlsTtpI%3D"

    // Microsoft Graph Constants
    private val MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me"

    // Azure AD Variables
    private var authContext: AuthenticationContext? = null
    private var authResult: AuthenticationResult? = null

    // Handler to do an interactive sign in and acquire token
    private var acquireTokenHandler: Handler? = null
    // Boolean variable to ensure invocation of interactive sign-in only once in case of multiple acquireTokenSilent call failures
    private val sIntSignInInvoked = AtomicBoolean()
    // Constant to send message to the acquireTokenHandler to do acquire token with Prompt.Auto
    private val MSG_INTERACTIVE_SIGN_IN_PROMPT_AUTO = 1
    // Constant to send message to the acquireTokenHandler to do acquire token with Prompt.Always
    private val MSG_INTERACTIVE_SIGN_IN_PROMPT_ALWAYS = 2

    // Constant to store user id in shared preferences
    private val USER_ID = "user_id"

    override fun getLayout(): Int {
        return R.layout.oauth_layout
    }

    override fun getTitle(): Int {
        return R.string.oauth_2_title
    }

    override fun getActionTitle(): Int {
        return R.string.logout
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authContext = AuthenticationContext(context, AUTHORITY, false)

        // Instantiate handler which can invoke interactive sign-in to get the Resource
        // sIntSignInInvoked ensures interactive sign-in is invoked one at a time

        acquireTokenHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (sIntSignInInvoked.compareAndSet(false, true)) {
                    if (msg.what == MSG_INTERACTIVE_SIGN_IN_PROMPT_AUTO) {
                        authContext!!.acquireToken(activity, RESOURCE_ID, CLIENT_ID, REDIRECT_URI, PromptBehavior.Auto, getAuthInteractiveCallback())
                    } else if (msg.what == MSG_INTERACTIVE_SIGN_IN_PROMPT_ALWAYS) {
                        authContext!!.acquireToken(activity, RESOURCE_ID, CLIENT_ID, REDIRECT_URI, PromptBehavior.Always, getAuthInteractiveCallback())
                    }
                }
            }
        }

        call_graph.setOnClickListener {
            onCallGraphClicked()
        }

        // ADAL Logging callback setup

        Logger.getInstance().setExternalLogger { tag, message, additionalMessage, level, errorCode ->
            // You can filter the logs depending on level or errorcode.
            Log.i(TAG, "$tag $message $additionalMessage $level $errorCode")
        }

        // Attempt an acquireTokenSilent call to see if we're signed in
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userId = preferences.getString(USER_ID, "")
        if (!TextUtils.isEmpty(userId)) {
            authContext!!.acquireTokenSilentAsync(RESOURCE_ID, CLIENT_ID, userId, getAuthSilentCallback())
        }
    }

    // Core Auth methods used by ADAL
    // ==================================
    // onActivityResult() - handles redirect from System browser
    // onCallGraphClicked() - attempts to get tokens for graph, if it succeeds calls graph & updates UI
    // callGraphAPI() - called on successful token acquisition which makes an HTTP request to graph
    // onSignOutClicked() - Signs user out of the app & updates UI

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authContext!!.onActivityResult(requestCode, resultCode, data)
    }

    /*
    * End user clicked call Graph API button, time for Auth
    * Use ADAL to get an Access token for the Microsoft Graph API
    */
    private fun onCallGraphClicked() {
        acquireTokenHandler!!.sendEmptyMessage(MSG_INTERACTIVE_SIGN_IN_PROMPT_AUTO)
    }

    private fun callGraphAPI() {
        Log.i(TAG, "Starting volley request to graph")

        // Make sure we have a token to send to graph
        if (authResult!!.accessToken == null)
            return

        val queue = Volley.newRequestQueue(context)
        val parameters = JSONObject()

        try {
            parameters.put("key", "value")
        } catch (e: Exception) {
            Log.i(TAG, "Failed to put parameters: $e")
        }

        val request = object : JsonObjectRequest(Method.GET, MSGRAPH_URL, parameters, Response.Listener { response ->
            // Successfully called graph, process data and send to UI
            Log.i(TAG, "Response: $response")
            updateGraphUI(response)
        }, Response.ErrorListener { error -> Log.i(TAG, "Error: $error") }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + authResult!!.accessToken
                UserFactory.getInstance().oAuthToken = authResult!!.accessToken
                Log.i(TAG, "1) authResult!!.accessToken: " + authResult!!.accessToken)
                return headers
            }
        }

        Log.i(TAG, "Adding HTTP GET to Queue, Request: $request")
        request.retryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(request)
    }

    override fun onActionClickListener() {
        onSignOutClicked()
    }

    private fun onSignOutClicked() {
        // End user has clicked the Sign Out button
        // Kill the token cache
        // Optionally call the signout endpoint to fully sign out the user account
        authContext!!.cache.removeAll()
        updateSignedOutUI()
    }

    // UI Helper methods
    // ================================
    // updateGraphUI() - Sets graph response in UI
    // updateSuccessUI() - Updates UI when token acquisition succeeds
    // updateSignedOutUI() - Updates UI when app sign out succeeds

    private fun updateGraphUI(response: JSONObject) {
        // Called on success from /me endpoint
        // Writes graph data to the UI
        graph_data.text = response.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun updateSuccessUI() {
        // Called on success from /me endpoint
        // Removed call Graph API button and paint Sign out
        actionLabelState(true)
        call_graph.visibility = View.GONE
        welcome.visibility = View.VISIBLE
        welcome.text = "Welcome, " + authResult!!.userInfo.givenName
        graph_data.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun updateSignedOutUI() {
        actionLabelState(false)
        call_graph.visibility = View.VISIBLE
        welcome.visibility = View.GONE
        graph_data.visibility = View.VISIBLE
        graph_data.text = "No Data"
    }

    // ADAL Callbacks
    // ======================
    // getActivity() - returns activity so we can acquireToken within a callback
    // getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
    // getAuthInteractiveCallback() - callback defined to handle acquireToken() case

    /* Callback used in for silent acquireToken calls.
    * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
    * else errors that we need to do an interactive request.
    */
    private fun getAuthSilentCallback(): AuthenticationCallback<AuthenticationResult> {
        return object : AuthenticationCallback<AuthenticationResult> {
            override fun onSuccess(authenticationResult: AuthenticationResult?) {
                if (authenticationResult == null || TextUtils.isEmpty(authenticationResult.accessToken)
                    || authenticationResult.status != AuthenticationResult.AuthenticationStatus.Succeeded) {
                    Log.i(TAG, "Silent acquire token Authentication Result is invalid, retrying with interactive")
                    // retry with interactive
                    acquireTokenHandler!!.sendEmptyMessage(MSG_INTERACTIVE_SIGN_IN_PROMPT_AUTO)

                    Log.i(TAG, "2) authenticationResult?.accessToken: " + authenticationResult?.accessToken)

                    return
                }
                // Successfully got a token, call graph now
                Log.i(TAG, "Successfully authenticated")
                authResult = authenticationResult // Store the authResult
                callGraphAPI() // call graph

                activity?.runOnUiThread { // update the UI to post call graph state
                    updateSuccessUI()
                }
            }

            override fun onError(exception: Exception) {
                // Failed to acquireToken
                Log.i(TAG, "Authentication failed: $exception")
                if (exception is AuthenticationException) {
                    val error = exception.code
                    logHttpErrors(exception)
                    // Tokens expired or no session, retry with interactive
                    if (error == ADALError.AUTH_REFRESH_FAILED_PROMPT_NOT_ALLOWED) {
                        acquireTokenHandler!!.sendEmptyMessage(MSG_INTERACTIVE_SIGN_IN_PROMPT_AUTO)
                    } else if (error == ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION) {
                        /*
                        Device is in Doze mode or App is in stand by mode. Wake up the app or show
                        an appropriate prompt for the user to take action. More information on this:
                        https://github.com/AzureAD/azure-activedirectory-library-for-android/wiki/Handle-Doze-and-App-Standby
                        */
                        Log.i(TAG, "Device is in doze mode or the app is in standby mode")
                    }
                    return
                }
                // Attempt an interactive on any other exception
                acquireTokenHandler!!.sendEmptyMessage(MSG_INTERACTIVE_SIGN_IN_PROMPT_AUTO)
            }
        }
    }

    private fun logHttpErrors(authException: AuthenticationException) {
        val httpResponseCode = authException.serviceStatusCode
        Log.i(TAG, "HTTP Response code: " + authException.serviceStatusCode)
        if (httpResponseCode < 200 || httpResponseCode > 300) {
            // logging http response headers in case of a http error.
            val headers = authException.httpResponseHeaders
            if (headers != null) {
                val sb = StringBuilder()
                for ((key, value) in headers) {
                    sb.append(key)
                    sb.append(":")
                    sb.append(value.toString())
                    sb.append("; ")
                }
                Log.i(TAG, "HTTP Response headers: $sb")
            }
        }
    }

    // Callback used for interactive request. If succeeds we use the access token to call the Microsoft Graph. Does not check cache
    private fun getAuthInteractiveCallback(): AuthenticationCallback<AuthenticationResult> {
        return object : AuthenticationCallback<AuthenticationResult> {
            override fun onSuccess(authenticationResult: AuthenticationResult?) {
                if (authenticationResult == null || TextUtils.isEmpty(authenticationResult.accessToken)
                    || authenticationResult.status != AuthenticationResult.AuthenticationStatus.Succeeded) {
                    Log.i(TAG, "Authentication Result is invalid")
                    return
                }
                // Successfully got a token, call graph now
                Log.i(TAG, "Successfully authenticated")
                Log.i(TAG, "ID Token: " + authenticationResult.idToken)

                Log.i(TAG, "3) authenticationResult.accessToken: " + authenticationResult.accessToken)
                Log.i(TAG, "4) authenticationResult.idToken: " + authenticationResult.idToken)

                authResult = authenticationResult // Store the auth result

                // Store User id to SharedPreferences to use it to acquire token silently later
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                preferences.edit().putString(USER_ID, authenticationResult.userInfo.userId).apply()

                callGraphAPI() // call graph

                activity?.runOnUiThread { // update the UI to post call graph state
                    updateSuccessUI()
                }

                sIntSignInInvoked.set(false) // set the sIntSignInInvoked boolean back to false
            }

            override fun onError(exception: Exception) {
                // Failed to acquireToken
                Log.i(TAG, "Authentication failed: $exception")
                if (exception is AuthenticationException) {
                    when (exception.code) {
                        ADALError.AUTH_FAILED_CANCELLED -> Log.i(TAG, "The user cancelled the authorization request")
                        ADALError.AUTH_FAILED_NO_TOKEN -> // In this case ADAL has found a token in cache but failed to retrieve it.
                            // Retry interactive with Prompt.Always to ensure we do an interactive sign in
                            acquireTokenHandler!!.sendEmptyMessage(MSG_INTERACTIVE_SIGN_IN_PROMPT_ALWAYS)
                        ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION ->
                            /*
                            Device is in Doze mode or App is in stand by mode. Wake up the app or show
                            an appropriate prompt for the user to take action. More information on this:
                            https://github.com/AzureAD/azure-activedirectory-library-for-android/wiki/Handle-Doze-and-App-Standby
                            */
                            Log.i(TAG, "Device is in doze mode or the app is in standby mode")
                        else -> {}
                    }
                }
                sIntSignInInvoked.set(false) // set the sIntSignInInvoked boolean back to false
            }
        }
    }
}