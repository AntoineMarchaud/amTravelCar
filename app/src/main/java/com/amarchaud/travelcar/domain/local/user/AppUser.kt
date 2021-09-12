package com.amarchaud.travelcar.domain.local.user

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class AppUser(
    var photoUri: Uri? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var address: String? = null,
    var birthday: LocalDate? = null
) : Parcelable