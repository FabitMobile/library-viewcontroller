package ru.fabit.viewcontroller

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner

fun View.parentLifecycle() = findViewTreeLifecycleOwner()