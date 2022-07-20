package ru.fabit.viewcontroller

import androidx.lifecycle.LifecycleOwner

fun LifecycleOwner.registerViewController(viewController: ViewController<*, *, *>) {
    lifecycle.addObserver(viewController)
}