package ru.fabit.viewcontroller

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.fabit.storecoroutines.Store

abstract class ViewControllerForView<State, Action, View : StateView<State>>(
    protected val store: Store<State, Action>
) : LifecycleEventObserver {
    private var isFirstAttach = true
    protected var isAttach = false

    private var view: View? = null

    private var subscription: Job? = null

    protected open fun attach() {}

    protected open fun firstViewAttach() {}

    protected fun dispatchAction(action: Action) {
        store.dispatchAction(action)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                isAttach = true
                view = source as View
                subscription = source.lifecycleScope.launch {
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
            Lifecycle.Event.ON_DESTROY -> {
                store.dispose()
            }
            else -> {}
        }
    }
}