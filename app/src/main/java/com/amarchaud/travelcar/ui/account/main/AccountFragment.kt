package com.amarchaud.travelcar.ui.account.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.databinding.FragmentMainAccountBinding
import com.amarchaud.travelcar.domain.local.user.AppUser
import com.amarchaud.travelcar.ui.account.modify.ModifyAccountActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentMainAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountFragmentViewModel by viewModels()

    companion object {
        const val TAG = "AccountFragment"

        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            modifyAccoutFab.setOnClickListener {
                goToModify.launch(Intent(requireContext(), ModifyAccountActivity::class.java).apply {
                    putExtra(ModifyAccountActivity.ARG_USER_IN, viewModel.user.value)
                })
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect {
                    handleUser(it)
                }
            }
        }
    }

    private fun handleUser(it: AppUser?) {
        with(binding) {
            if (it == null) { // no user yet
                noAccountText.isVisible = true
                userGroup.isVisible = false

                userPhoto.setImageURI(null)
                userCompleteName.text = null
                userAddress.text = null

            } else {
                noAccountText.isVisible = false
                userGroup.isVisible = true

                userPhoto.setImageURI(it.photoUri)
                userCompleteName.text = getString(R.string.double_string_with_space, it.firstName, it.lastName)
                userAddress.text = it.address
                userBirthday.text = it.birthday?.toString()
            }
        }
    }

    private val goToModify =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val appUser =
                    result.data?.getParcelableExtra<AppUser?>(ModifyAccountActivity.ARG_USER_SAVED)
                appUser?.let {
                    viewModel.updateUser(appUser)
                }
            }
        }
}