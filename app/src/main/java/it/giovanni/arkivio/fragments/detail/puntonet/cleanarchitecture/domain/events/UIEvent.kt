package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.domain.events

/**
 * Base class for all the UI-related events.
 */
abstract class UIEvent {
    open fun send() {
        UIEventBus.instance.send(this)
    }
}