package com.amarchaud.travelcar.ui.account.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.travelcar.data.repository.user.AppUserRepository
import com.amarchaud.travelcar.domain.local.user.AppUser
import com.amarchaud.travelcar.ui.account.main.model.UserListItem
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

    private val _userListInfo = MutableStateFlow<List<UserListItem>?>(null)
    val userInfo = _userListInfo.asStateFlow()

    private val _user = MutableStateFlow<AppUser?>(null)
    var user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            appUserRepository.getUserFlow().collect {
                _user.value = it
                _userListInfo.value = transformUserToListItem(it)
            }
        }
    }

    fun updateUser(appUser: AppUser) {
        viewModelScope.launch {
            appUserRepository.manageUser(appUser) // flow will update ui
        }
    }

    private fun transformUserToListItem(appUser: AppUser?): List<UserListItem>? {

        if (appUser == null) {
            return emptyList()
        }

        val l = mutableListOf<UserListItem>()

        if (appUser.photoUri == null) {
            l.add(UserListItem.NoPhoto(appUser.firstName?.get(0) ?: '?'))
        } else {
            l.add(UserListItem.Photo(appUser.photoUri!!))
        }

        appUser.firstName?.let {
            l.add(UserListItem.Identity(it, appUser.lastName))
        }

        appUser.address?.let {
            l.add(UserListItem.Address(it))
        }

        appUser.birthday?.let {
            l.add(UserListItem.Birthday(it))
        }

        return l
    }
}