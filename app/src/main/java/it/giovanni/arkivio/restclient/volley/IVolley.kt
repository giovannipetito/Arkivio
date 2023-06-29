package it.giovanni.arkivio.restclient.volley

/**
 * This interface defines three callback methods related to the Volley library.
 *
 * onVolleyGetSuccess(message: String?): This method is called when the GET request to the server
 * is successful. The parameter message is a string that contains the success message, such as
 * "User fetched successfully."
 *
 * onVolleyPostSuccess(message: String?): This method is called when the POST request to the server
 * is successful. The parameter message is a string that contains the success message, such as
 * "User posted successfully."
 *
 * onVolleyFailure(message: String?): This method is called when the request to the server fails.
 * The parameter message is a string that contains the error message, such as "Network error occurred."
 *
 * By implementing this interface, the class that uses the Volley library can receive the success or
 * failure callbacks and handle them accordingly.
 */
interface IVolley {

    fun onVolleyGetSuccess(message: String?)
    fun onVolleyPostSuccess(message: String?)
    fun onVolleyFailure(message: String?)
}