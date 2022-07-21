package ru.fabit.viewcontroller

interface StateView<State> {
    fun renderState(state: State)
}