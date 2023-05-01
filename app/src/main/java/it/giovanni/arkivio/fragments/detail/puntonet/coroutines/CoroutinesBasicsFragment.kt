package it.giovanni.arkivio.fragments.detail.puntonet.coroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.CoroutinesBasicsLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoroutinesBasicsFragment : DetailFragment() {

    private var layoutBinding: CoroutinesBasicsLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.coroutines_title
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

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = CoroutinesBasicsLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        mainSync()
        mainAsync()
    }

    private fun mainSync() {
        taskSync1()
        taskSync2()
    }
    private fun taskSync1() {
        println("Hello\n")
    }
    private fun taskSync2() {
        println("World!\n")
    }

    /**
     * The suspend functions should only be called from a coroutine or another suspend function.
     * To create a coroutine we need to create a coroutine scope (GlobalScope) and then call the
     * launch function that is a coroutine builder.
     * The GlobalScope stays alive as long as the application stays alive.
     */
    private fun mainAsync() {
        // This is a coroutine.
        GlobalScope.launch {
            taskAsync2()
        }
        taskAsync1()
        // We need to keep the JVM alive (otherwise the mainAsync function will print only the taskAsync1 output: Hello):
        Thread.sleep(2000L)
    }
    private fun taskAsync1() {
        println("Hello\n")
        println("taskAsync1() thread: " + Thread.currentThread().name + "\n")
    }
    private suspend fun taskAsync2() {
        // Let's call the delay function to avoid the "World!Hello" output.
        delay(1000L) // The delay function will be available only inside the suspend function (or the coroutine).
        println("World!\n")

        // To prove that taskAsync2 is running on a different thread than taskAsync1.
        println("taskAsync2() thread: " + Thread.currentThread().name + "\n")

        /*
        withContext(Dispatchers.IO) {
            delay(1000L)
            println("World!\n")
            println("taskAsync2() thread: " + Thread.currentThread().name + "\n")
        }
        */
    }

    /**
     * After adding the delay, the first task which was run is taskAsync1(), and after that taskAsync1()
     * is successfully completed, we have called taskAsync2() because taskAsync2() was suspended, and
     * after that taskAsync1() was completed, then taskAsync2() was resumed
     */

    /**
     * When we synchronously run the function networkRequestSync in the app, it will block the main
     * thread and the UI might freeze and get unresponsive. That's all because the application will
     * block the main thread until networkRequestSync function is done.
     * All non-UI related stuff needs to be moved on a different thread.
     */
    private fun getApiDataSync() {
        val response = networkRequestSync()
        println(response)
    }
    private fun networkRequestSync(): Response {
        val response = Response()
        return response
    }

    /**
     * The Coroutines simplify asynchronous programming by writing syncronous code.
     * Now we run our function asynchronously without blocking UI thread only making two small
     * modifications for that:
     * - The first one is adding the suspend keyword in front of the function.
     * - The second one is optionally adding a withContext fun inside networkRequestAsync fun.
     *
     * The suspend keyword tells to Kotlin compiler that the function will run within a coroutine.
     * The suspend functions should only be called from a coroutine or another suspend function.
     * The withContext function is needed to specify on which thread the code inside it should run on.
     *
     * There a re four available dispatchers: Main, IO, Default, Unconfined.
     * - The Main dispatcher is optimized for the UI code or non-blocking code that executes fast.
     * - The IO dispatcher is optimized for network and disk operations.
     * - The Default dispatcher is optimized for CPU-intensive tasks and some bigger computations.
     *
     * By saying suspended we mean the the corresponding computation can be paused, removed from the
     * thread, and stored in memory. Meanwhile, the thread is free to be occupied with other activities.
     */
    private suspend fun getApiDataAsync() {
        val response = networkRequestSync()
        println(response)
    }
    private suspend fun networkRequestAsync(): Response {
        val response: Response
        withContext(Dispatchers.IO) {
            response = Response()
        }
        return response
    }

    class Response

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}