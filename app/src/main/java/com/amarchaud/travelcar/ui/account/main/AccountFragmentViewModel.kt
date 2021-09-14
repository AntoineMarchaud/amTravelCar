package com.amarchaud.travelcar.ui.account.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.travelcar.data.repository.AppUserRepository
import com.amarchaud.travelcar.domain.models.AppUser
import com.amarchaud.travelcar.ui.account.main.models.AppUserUiModel
import com.amarchaud.travelcar.ui.account.main.models.UserListItemUiModel
import com.amarchaud.travelcar.ui.account.main.models.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountFragmentViewModel @Inject constructor(
    app: Application,
    private val appUserRepository: AppUserRepository
) : AndroidViewModel(app) {

    private val _userListInfoUiModel = MutableStateFlow<List<UserListItemUiModel>?>(null)
    val userInfoUiModel = _userListInfoUiModel.asStateFlow()

    var userUiModel: AppUserUiModel? = null
        private set

    init {
        viewModelScope.launch {
            appUserRepository.getUserFlow().collect { user ->
                userUiModel = user?.toUiModel()
                _userListInfoUiModel.update { transformUserToListItem(user) }
            }
        }
    }

    private fun transformUserToListItem(appUser: AppUser?): List<UserListItemUiModel> {

        if (appUser == null) {
            return emptyList()
        }

        val l = buildList {
            if (appUser.photoUri == null) {
                add(UserListItemUiModel.NoPhoto(appUser.firstName?.get(0) ?: '?'))
            } else {
               add(UserListItemUiModel.Photo(appUser.photoUri!!))
            }

            appUser.firstName?.let {
               add(UserListItemUiModel.Identity(it, appUser.lastName))
            }

            appUser.address?.let {
                add(UserListItemUiModel.Address(it))
            }

            appUser.birthday?.let {
                add(UserListItemUiModel.Birthday(it))
            }
        }

        return l
    }
}