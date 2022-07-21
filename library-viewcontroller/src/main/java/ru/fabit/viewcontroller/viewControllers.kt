package ru.fabit.viewcontroller

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

inline fun <reified T : ViewModel> Fragment.viewControllers() = viewModels<T>()

inline fun <reified T : ViewModel> ComponentActivity.viewControllers() = viewModels<T>()
