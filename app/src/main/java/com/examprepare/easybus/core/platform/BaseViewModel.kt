package com.examprepare.easybus.core.platform


import androidx.lifecycle.ViewModel
import com.examprepare.easybus.core.exception.Failure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base ViewModel class with default Failure handling.
 * @see ViewModel
 * @see Failure
 */
abstract class BaseViewModel : ViewModel() {

    private val _failure = MutableStateFlow<Failure>(Failure.None)
    val failure: StateFlow<Failure> = _failure.asStateFlow()

    protected fun handleFailure(failure: Failure) {
        _failure.value = failure
    }

    fun onDismissFailure() {
        _failure.value = Failure.None
    }
}