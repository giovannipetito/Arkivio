package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RxExampleLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import java.util.Locale

/**
 * This class demonstrates how to transform emitted items using the map operator. It creates an
 * Observable from a list of items and emits them one by one. Each emitted item is then transformed
 * by converting the text to uppercase using map.
 */
class RxExample4Fragment : DetailFragment() {

    private var layoutBinding: RxExampleLayoutBinding? = null
    private val binding get() = layoutBinding

    private val disposable = CompositeDisposable()

    private var message: String? = ""

    override fun getTitle(): Int {
        return R.string.rx_example4_title
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
        layoutBinding = RxExampleLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposable.add(
            getNotesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { note: Note ->

                    note.note = note.note.uppercase(Locale.getDefault())

                    // return
                    note
                }
                .subscribeWith(getNotesObserver())
        )
    }

    // This function creates an Observable using Observable.create that emits individual Note objects
    // from a list of Note items. It iterates through the list and calls onNext for each note. Finally,
    // it calls onComplete to indicate the completion of emissions.
    private fun getNotesObservable(): Observable<Note> {
        val notes: List<Note> = getNotes()
        return Observable.create { emitter: ObservableEmitter<Note> ->
            for (note in notes) {
                if (!emitter.isDisposed) {
                    emitter.onNext(note)
                }
            }
            if (!emitter.isDisposed) {
                emitter.onComplete()
            }
        }
    }

    // This function returns a DisposableObserver implementation that handles the emitted Note objects.
    private fun getNotesObserver(): DisposableObserver<Note> {
        return object :
            DisposableObserver<Note>() {
            override fun onNext(note: Note) {

                message = message + note.note + "\n"

                Log.d("[RX]", "Note: " + note.note)
            }

            override fun onError(error: Throwable) {
                Log.e("[RX]", "onError: " + error.message)
            }

            override fun onComplete() {
                val onCompleteMessage = "All notes are emitted!"
                message += onCompleteMessage
                binding?.labelRx?.text = message
                Log.d("[RX]", "onCompleteMessage: $onCompleteMessage")
            }
        }
    }

    private fun getNotes(): List<Note> {
        val notes: MutableList<Note> = ArrayList()
        notes.add(Note(1, "Wash the floor"))
        notes.add(Note(2, "Call mom"))
        notes.add(Note(3, "Prepare dinner"))
        notes.add(Note(4, "Watch TV"))
        return notes
    }

    internal class Note(var id: Int, var note: String)

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        disposable.dispose()
    }
}