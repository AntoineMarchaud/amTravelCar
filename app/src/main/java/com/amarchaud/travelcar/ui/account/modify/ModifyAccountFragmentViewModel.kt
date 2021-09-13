package com.amarchaud.travelcar.ui.account.modify

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.travelcar.data.repository.user.AppUserRepository
import com.amarchaud.travelcar.domain.local.user.AppUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyAccountFragmentViewModel @Inject constructor(
    private val app: Application,
    private val appUserRepository: AppUserRepository
) : AndroidViewModel(app) {

    //Save state of screen
    var appUser: AppUser? = null

    fun somethingChanged() = appUser?.photoUri != null
            || !appUser?.firstName.isNullOrEmpty()
            || !appUser?.lastName.isNullOrEmpty()
            || !appUser?.address.isNullOrEmpty()
            || appUser?.birthday != null

    suspend fun eraseAccount() {
        appUserRepository.deleteUser()
    }

    fun manageUser() {
        appUser?.let {
            viewModelScope.launch {
                appUserRepository.manageUser(it)
            }
        }

    }
}