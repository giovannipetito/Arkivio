package it.giovanni.arkivio.puntonet.coroutines

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.CoroutineRunBlockingLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * The runBlocking fun blocks the thread on which is running until the code inside is completed.
 * This function should not be used from another coroutine and it is designed to bridge regular
 * blocking code to libraries that are written in a suspending style. Its main purpose is to run
 * in the main functions and tests, so you should not use the runBlocking function in production.
 *
 * The withContext suspending function is used to switch context. It suspends until it completes
 * and returns the result.
 * This function will shift execution of the block into a different thread if a new dispatcher is
 * specified and it will get back to the original dispatcher when it completes. So this suspending
 * function is Cancelable by default and it immediately checks for a cancellation of the coroutine
 * in which it was called and it throws a CancellationException if it is not active.
 *
 * The withContext function can be used for example when you receive some network results and you
 * want to switch dispatcher from IO to Main to execute some UI code and update the UI.
 */
class CoroutineRunBlockingFragment : DetailFragment() {

    private var layoutBinding: CoroutineRunBlockingLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.coroutine_run_blocking_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
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

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = CoroutineRunBlockingLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        /*
        runBlocking {
            // The first Log statement is blocking a coroutine, so it will block the thread until
            // the block inside is completed, and only after that the second Log will be printed too.
            Log.i("[Coroutine]", "coroutineContext: " + this.coroutineContext.toString())
            delay(5000L)
        }

        GlobalScope.launch {
            // The second Log statement will not be executed before this delay is completed.
            Log.i("[Coroutine]", "coroutineContext: " + this.coroutineContext.toString())
        }
        */

        GlobalScope.launch(Dispatchers.IO) {
            Log.i("[Coroutine]", "coroutineContext: " + this.coroutineContext.toString())
            // The withContext function can change the dispatcher:
            withContext(Dispatchers.Main) {
                Log.i("[Coroutine]", "coroutineContext: " + this.coroutineContext.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}