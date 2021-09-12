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
import com.amarchaud.travelcar.ui.account.main.adapter.UserAdapter
import com.amarchaud.travelcar.ui.account.modify.ModifyAccountActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentMainAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountFragmentViewModel by viewModels()

    private val adapter = UserAdapter()

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
                requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_nothing)
            }

            rvAccount.adapter = adapter
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userInfo.collect {
                    adapter.submitList(it)
                }
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