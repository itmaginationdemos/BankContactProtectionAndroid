package com.bankprotection.authentication.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.bankprotection.application.R
import com.bankprotection.application.databinding.LoginFragmentBinding
import com.bankprotection.application.ui.viewmodel.UserViewModel
import com.bankprotection.common.domain.getResultOrNull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
    }

    private val viewModel: UserViewModel by activityViewModels()
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedStateHandle: SavedStateHandle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        setState()
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.buttonLogIn.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(
                    context,
                    getString(R.string.invalid_credentials),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.login(
                    username = username,
                    password = password
                ).observe(viewLifecycleOwner) { loginLoadingResult ->
                    loginLoadingResult.getResultOrNull()?.let { result ->
                        if (result.isSuccess) {
                            savedStateHandle.set(LOGIN_SUCCESSFUL, true)
                            findNavController().popBackStack()
                        } else {
                            showErrorMessage()
                        }
                    }
                }
            }
        }
    }

    private fun showErrorMessage() {
        Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
    }

    private fun setState() {
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}