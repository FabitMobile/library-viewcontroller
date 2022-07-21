package ru.fabit.viewcontroller

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.fabit.storecoroutines.BaseStore

abstract class ViewController<State, Action, View : StateView<State>>(
    protected val store: BaseStore<State, Action>
) : ViewModel(), LifecycleEventObserver {
    private var isFirstAttach = true
    protected var isAttach = false

    private var view: View? = null

    private var subscription: Job? = null

    protected fun attach() {}

    protected fun firstViewAttach() {}

    protected fun dispatchAction(action: Action) {
        store.dispatchAction(action)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                isAttach = true
                view = source as View
                subscription = viewModelScope.launch {
                    store.state.collect {
                        view?.renderState(it)
                    }
                }
                attach()
                if (isFirstAttach) {
                    isFirstAttach = false
                    firstViewAttach()
                }
            }
            Lifecycle.Event.ON_PAUSE -> {
                isAttach = false
                subscription?.cancel()
            }
            else -> {}
        }
    }

    override fun onCleared() {
        store.dispose()
    }
}