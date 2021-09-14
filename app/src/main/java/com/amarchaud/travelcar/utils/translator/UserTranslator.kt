package com.amarchaud.travelcar.utils.translator

import com.amarchaud.travelcar.data.models.EntityUser
import com.amarchaud.travelcar.domain.models.AppUser

object UserTranslator {

    fun AppUser.toEntityUser() = EntityUser(
        photoUri = this.photoUri,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        birthday = this.birthday
    )

    fun EntityUser.toAppUser() = AppUser(
        photoUri = this.photoUri,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        birthday = this.birthday
    )
}