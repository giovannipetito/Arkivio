package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.domain.events

import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors

/**
 * Base Event Bus class implementation.
 * Events dispatch/delivery are guaranteed to be sent in a serialized queue.
 */
open class EventBus<T>(val name: String) where T : Any {

    private val publisher: FlowableProcessor<T> = PublishProcessor.create<T>().toSerialized()

    private val processor: Flowable<T> = publisher.retry().onBackpressureBuffer().subscribeOn(eventBusScheduler)

    inline fun <reified E> listen(): Flowable<E> where E : T = listen(E::class.java)

    inline fun <reified E> listen(crossinline func: ((E) -> Unit)): Disposable where E : T = listen(E::class.java).subscribe {
            event -> func.invoke(event)
    }

    inline fun <reified E> listen(observeOnScheduler: Scheduler): Flowable<E> where E : T = listen(observeOnScheduler, E::class.java)

    inline fun <reified E> listen(observeOnScheduler: Scheduler, crossinline func: ((E) -> Unit)): Disposable where E : T = listen(observeOnScheduler, E::class.java).subscribe {
            event -> func.invoke(event)
    }

    fun <E> listen(eventType: Class<E>): Flowable<E> where E : T = listen(AndroidSchedulers.mainThread(), eventType)

    fun <E> listen(observeOnScheduler: Scheduler, eventType: Class<E>): Flowable<E> where E : T = observe(observeOnScheduler, eventType)

    fun observe() = observe(AndroidSchedulers.mainThread())

    @Synchronized
    fun send(event: T) {
        Log.d("[EVENTBUS]", "[$name] ---> ${event::class.java.name}")
        publisher.onNext(event)
    }

    fun observe(observeOnScheduler: Scheduler): Flowable<T> {
        Log.i("[EVENTBUS]", "[$name] observe: all")
        return processor.observeOn(observeOnScheduler)
    }

    private fun <E> observe(observeOnScheduler: Scheduler, eventClass: Class<E>): Flowable<E> where E : T {
        Log.i("[EVENTBUS]", "[$name] observe: ${eventClass.name}")
        return processor.ofType(eventClass).observeOn(observeOnScheduler)
    }

    companion object {
        val eventBusScheduler: Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
    }
}