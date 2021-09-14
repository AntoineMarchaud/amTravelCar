package com.amarchaud.travelcar.domain.models

import android.net.Uri
import org.threeten.bp.LocalDate

data class AppUser(
    var photoUri: Uri? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var address: String? = null,
    var birthday: LocalDate? = null
)