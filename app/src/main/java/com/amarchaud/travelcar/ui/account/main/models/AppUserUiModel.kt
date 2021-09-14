package com.amarchaud.travelcar.ui.account.main.models

import android.net.Uri
import android.os.Parcelable
import com.amarchaud.travelcar.domain.models.AppUser
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class AppUserUiModel(
    var photoUri: Uri? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var address: String? = null,
    var birthday: LocalDate? = null
) : Parcelable

fun AppUser.toUiModel() = AppUserUiModel(
    photoUri = this.photoUri,
    firstName = this.firstName,
    lastName = this.lastName,
    address = this.address,
    birthday = this.birthday,
)