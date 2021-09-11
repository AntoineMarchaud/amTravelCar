package com.amarchaud.travelcar.domain.local.user

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Parcelize
data class AppUser(
    val photoUri: Uri?,
    val firstName: String?,
    val lastName: String?,
    val address: String?,
    val birthday: LocalDate?
) : Parcelable