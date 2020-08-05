package it.giovanni.arkivio.presenter

abstract class BasePresenter<V, M>(instanceView: V?, instanceModel: M) {

    var view = instanceView
    var model = instanceModel

    abstract fun cancelRequest()
}