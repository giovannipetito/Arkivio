package it.giovanni.arkivio.restclient.retrofit

import it.giovanni.arkivio.puntonet.retrofitgetpost.UsersResponse

/**
 * IRetrofit is an interface used to define callback methods for handling successful and failed
 * Retrofit network requests.
 *
 * The interface contains two methods:
 *
 * onRetrofitSuccess: This method takes two parameters, a message string and a list of User objects.
 * This method is called when the network request is successful and returns a list of User objects.
 * The implementation of this method is defined in the class that implements this interface.
 *
 * onRetrofitFailure: This method takes a message string parameter. This method is called when the
 * network request fails. The implementation of this method is defined in the class that implements
 * this interface.
 *
 * By defining these two methods in an interface, it allows multiple classes to implement the interface
 * and handle the network request responses in their own way.
 */
interface IRetrofit {

    fun onRetrofitSuccess(usersResponse: UsersResponse?, message: String)

    fun onRetrofitFailure(message: String)
}