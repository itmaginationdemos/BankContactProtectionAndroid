package com.bankprotection.verification.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bankprotection.application.R
import com.bankprotection.application.databinding.VerificationFragmentBinding
import com.bankprotection.application.ui.viewmodel.UserViewModel
import com.bankprotection.authentication.ui.fragment.LoginFragment
import com.bankprotection.common.data.*
import com.bankprotection.utils.ui.DefaultViewEffect
import com.bankprotection.utils.ui.StateFragment
import com.bankprotection.verification.ui.viewmodel.VerificationIntent
import com.bankprotection.verification.ui.viewmodel.VerificationState
import com.bankprotection.verification.ui.viewmodel.VerificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationFragment :
    StateFragment<VerificationState, VerificationIntent, DefaultViewEffect>() {

    companion object {
        fun newInstance() = VerificationFragment()
    }

    private val userViewModel: UserViewModel by activityViewModels()
    private val verificationViewModel: VerificationViewModel by viewModels()
    private var _binding: VerificationFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        registerStateViewModel(verificationViewModel)
        _binding = VerificationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle

        registerListeners()
        init()

        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL).observe(
            currentBackStackEntry
        ) { success ->
            if (!success) {
                val startDestination = navController.graph.startDestinationId
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(startDestination, true)
                    .build()
                navController.navigate(startDestination, null, navOptions)
            }
        }
    }

    private fun registerListeners() {
        binding.buttonRequestVerification.setOnClickListener {
            emitIntent(
                VerificationIntent.RequestVerificationIntent(
                    phoneNumberBase = binding.editTextPhoneNumber.text.toString(),
                    prefix = "48"
                )
            )
        }
        binding.buttonConfirmCode.setOnClickListener {
            emitIntent(
                VerificationIntent.ConfirmValidGivenContactCode
            )
        }
    }

    private fun init() {
        emitIntent(
            VerificationIntent.InitVerificationIntent(
                getUserResult = userViewModel.getUser()
            )
        )
    }

    override fun render(viewState: VerificationState) {
        when (viewState) {
            VerificationState.IdleVerificationState ->
                renderIdleVerificationState()
            is VerificationState.ErrorVerificationState ->
                renderErrorVerificationState(viewState)
            is VerificationState.InitSuccessVerificationState ->
                renderInitSuccessVerificationState(viewState)
            VerificationState.LoadingVerificationState ->
                renderLoadingVerificationState()
            is VerificationState.RequestVerificationFailedState ->
                renderRequestVerificationFailed(viewState)
            VerificationState.NoPendingVerificationState ->
                renderNoPendingVerificationState()
            VerificationState.LoadingPendingVerificationState ->
                renderLoadingPendingVerification()
            is VerificationState.PendingVerificationFailedState ->
                renderPendingVerificationFailed(viewState)
            is VerificationState.PendingGiverVerificationState ->
                renderPendingGiverVerificationState(viewState)
            is VerificationState.PendingRequesterVerificationState ->
                renderPendingRequesterVerificationState(viewState)
            is VerificationState.VerificationSuccessVerificationState ->
                renderVerificationSuccessVerificationState()
        }
    }

    private fun renderIdleVerificationState() {
        setLoadingView(true)
    }

    private fun renderLoadingVerificationState() {
        setLoadingView(true)
    }

    private fun renderInitSuccessVerificationState(
        initSuccessVerificationState: VerificationState.InitSuccessVerificationState
    ) {
        if (initSuccessVerificationState.isStaff) {
            binding.layoutVerificationCodeRequest.visibility = View.VISIBLE
            binding.layoutLoading.root.visibility = View.GONE
        } else {
            binding.layoutVerificationCodeRequest.visibility = View.GONE
            emitIntent(VerificationIntent.StartPendingVerificationRefresh)
        }
    }

    private fun renderErrorVerificationState(
        errorVerificationState: VerificationState.ErrorVerificationState
    ) {
        setLoadingView(false)
        when (errorVerificationState.throwable) {
            is UnauthorizedNetworkException ->
                findNavController().navigate(R.id.loginFragment)
            else ->
                if (errorVerificationState.message?.isNotBlank() == true) {
                    Toast.makeText(
                        context,
                        errorVerificationState.message,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.unknown_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun setLoadingView(isLoading: Boolean) {
        binding.layoutLoading.root.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun renderRequestVerificationFailed(
        requestVerificationFailedState: VerificationState.RequestVerificationFailedState
    ) {
        setLoadingView(false)
        requestVerificationFailedState.message?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun renderLoadingPendingVerification() {
        setLoadingView(true)
        with(binding) {
            layoutProvideYourCode.visibility = View.GONE
            layoutVerifyCode.visibility = View.GONE
        }
    }

    private fun renderPendingVerificationFailed(
        pendingVerificationFailedState: VerificationState.PendingVerificationFailedState
    ) {
        setLoadingView(false)
        Toast.makeText(
            context,
            getString(R.string.unknown_error_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun renderPendingGiverVerificationState(
        state: VerificationState.PendingGiverVerificationState
    ) {
        setLoadingView(false)
        with(binding) {
            layoutVerificationSucceed.visibility = View.GONE
            layoutVerificationCodeRequest.visibility = View.GONE
            layoutNoPendingVerifications.visibility = View.GONE

            if (state.isStaff) {
                textViewContactCode.text = state.givenVerificationCode
                layoutProvideYourCode.visibility = View.GONE
                layoutVerifyCode.visibility = View.VISIBLE
            } else {
                textViewYourCode.text = state.givenVerificationCode
                layoutProvideYourCode.visibility = View.VISIBLE
                layoutVerifyCode.visibility = View.GONE
            }
        }
    }

    private fun renderPendingRequesterVerificationState(
        state: VerificationState.PendingRequesterVerificationState
    ) {
        setLoadingView(false)
        with(binding) {
            layoutVerificationSucceed.visibility = View.GONE
            layoutVerificationCodeRequest.visibility = View.GONE
            layoutNoPendingVerifications.visibility = View.GONE

            if (state.isStaff) {
                textViewYourCode.text = state.requesterVerificationCode
                layoutProvideYourCode.visibility = View.VISIBLE
                layoutVerifyCode.visibility = View.GONE
            } else {
                textViewContactCode.text = state.requesterVerificationCode
                layoutProvideYourCode.visibility = View.GONE
                layoutVerifyCode.visibility = View.VISIBLE
            }
        }

    }

    private fun renderVerificationSuccessVerificationState() {
        setLoadingView(false)
        with(binding) {
            layoutVerificationCodeRequest.visibility = View.GONE
            layoutNoPendingVerifications.visibility = View.GONE
            layoutProvideYourCode.visibility = View.GONE
            layoutVerifyCode.visibility = View.GONE
            layoutVerificationSucceed.visibility = View.VISIBLE
        }
    }

    private fun renderNoPendingVerificationState() {
        setLoadingView(false)
        with(binding) {
            layoutVerificationCodeRequest.visibility = View.GONE
            layoutProvideYourCode.visibility = View.GONE
            layoutVerifyCode.visibility = View.GONE
            layoutVerificationSucceed.visibility = View.GONE
            layoutNoPendingVerifications.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}