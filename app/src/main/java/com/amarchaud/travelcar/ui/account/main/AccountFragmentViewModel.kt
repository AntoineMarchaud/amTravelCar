package com.amarchaud.travelcar.ui.account.main

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.travelcar.data.repository.user.AppUserRepository
import com.amarchaud.travelcar.domain.local.user.AppUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountFragmentViewModel @Inject constructor(
    app: Application,
    private val appUserRepository: AppUserRepository
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<AppUser?>(null)
    val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            appUserRepository.getUserFlow().collect {
                _user.value = it
            }
        }
    }

    fun updateUser(appUser: AppUser) {
        viewModelScope.launch {
            appUserRepository.manageUser(appUser) // flow will update ui
        }
    }
}