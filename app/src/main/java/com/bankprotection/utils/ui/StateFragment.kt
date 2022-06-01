package com.bankprotection.utils.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bankprotection.application.BuildConfig
import kotlinx.coroutines.launch
import java.lang.RuntimeException

abstract class StateFragment<S : IViewState, I : IViewIntent, E : IViewEffect> : Fragment() {
    private val stateViewModels: MutableList<StateViewModel<S, I, E>> = mutableListOf()

    abstract fun render(viewState: S)
    open fun effect(effect: E) {}

    override fun onStart() {
        super.onStart()
        if (BuildConfig.DEBUG) {
            if (stateViewModels.isEmpty()) {
                throw RuntimeException(
                    "No StateViewModel<S, I> registered. Did you forget to register the view?"
                )
            }
        }
    }

    fun emitIntent(intent: I) {
        lifecycleScope.launch {
            for (stateViewModel in stateViewModels) {
                stateViewModel.emitIntent(intent)
            }
        }
    }

    protected fun registerStateViewModel(stateViewModel: StateViewModel<S, I, E>) {
        stateViewModels.add(stateViewModel)
        stateViewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            render(state)
        }
        stateViewModel.effectLiveData.observe(viewLifecycleOwner) { effect ->
            effect(effect)
        }
    }
}