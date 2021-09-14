package com.amarchaud.travelcar.ui.account.modify

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.travelcar.domain.models.AppUser
import com.amarchaud.travelcar.domain.repository.UserRepository
import com.amarchaud.travelcar.ui.account.main.models.AppUserUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyAccountFragmentViewModel @Inject constructor(
    app: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(app) {

    //Save state of screen
    var appUser: AppUserUiModel? = null

    fun somethingChanged() = appUser?.photoUri != null
            || !appUser?.firstName.isNullOrEmpty()
            || !appUser?.lastName.isNullOrEmpty()
            || !appUser?.address.isNullOrEmpty()
            || appUser?.birthday != null

    suspend fun eraseAccount() {
        userRepository.deleteUser()
    }

    fun manageUser() {
        appUser?.let {
            viewModelScope.launch {
                userRepository.manageUser(
                    AppUser(
                        photoUri = it.photoUri,
                        firstName = it.firstName,
                        lastName = it.lastName,
                        address = it.address,
                        birthday = it.birthday,
                    )
                )
            }
        }

    }
}