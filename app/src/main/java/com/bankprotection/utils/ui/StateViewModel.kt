package com.bankprotection.utils.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bankprotection.common.IDispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

abstract class StateViewModel<S : IViewState, I : IViewIntent, E : IViewEffect>(
    protected val dispatchers: IDispatchers,
    private val reducer: suspend (accumulator: S, value: S) -> S = { _, value -> value }
) : ViewModel() {
    private val _state by lazy { MutableStateFlow(getDefaultState()) }
    private val _effect = MutableSharedFlow<E>()
    private val intentFlow = MutableSharedFlow<I>(
        extraBufferCapacity = INTENT_FLOW_EXTRA_BUFFER_CAPACITY
    )
    val stateLiveData = _state.asLiveData()
    val effectLiveData = _effect.asLiveData()

    init {
        viewModelScope.launch(dispatchers.Default) {
            handleIntents()
        }
    }

    suspend fun emitIntent(intent: I) {
        intentFlow.emit(intent)
    }

    private suspend fun handleIntents() {
        intentFlow.collect { intent ->
            try {
                onIntent(intent)?.let { newState ->
                    setState(newState)
                }
            } catch (e: Exception) {
                Log.e("StateViewModel","Error: $e")
            }
        }
    }

    protected suspend fun setState(state: S) {
        val newState = reducer(_state.value, state)
        _state.emit(newState)
    }

    protected suspend fun setState(reduce: S.() -> S) {
        val newState = _state.value.reduce()
        _state.emit(newState)
    }

    protected suspend fun setEffect(effect: E) {
        _effect.emit(effect)
    }

    protected suspend fun setEffect(builder: () -> E) {
        _effect.emit(builder())
    }

    abstract suspend fun onIntent(intent: I): S?
    abstract fun getDefaultState(): S

    private companion object {
        private const val INTENT_FLOW_EXTRA_BUFFER_CAPACITY = 64
    }
}