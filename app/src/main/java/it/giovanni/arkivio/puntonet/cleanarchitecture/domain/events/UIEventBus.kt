package it.giovanni.arkivio.puntonet.cleanarchitecture.domain.events

/**
 * EventBus specialized in delivering [UIEvent] events.
 * Use for UI related events
 */
class UIEventBus : EventBus<UIEvent>("UIEventBus") {
    companion object {
        val instance = UIEventBus()
    }
}