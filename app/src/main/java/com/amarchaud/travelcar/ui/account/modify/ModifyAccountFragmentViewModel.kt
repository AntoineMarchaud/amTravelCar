package com.amarchaud.travelcar.ui.account.modify

import android.app.Application
import android.net.Uri
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
class ModifyAccountFragmentViewModel @Inject constructor(
    private val app: Application,
    private val appUserRepository: AppUserRepository
) : AndroidViewModel(app) {

    //Save state of screen
    var appUser: AppUser? = null


}