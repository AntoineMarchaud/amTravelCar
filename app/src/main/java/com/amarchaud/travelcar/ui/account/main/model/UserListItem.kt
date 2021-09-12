package com.amarchaud.travelcar.ui.account.main.model

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import org.threeten.bp.LocalDate

sealed class UserListItem {

    data class Photo(val uri: Uri) : UserListItem()
    data class NoPhoto(val firstLetter: Char) : UserListItem()
    data class Identity(val firstName: String, val lastName : String?) : UserListItem()
    data class Address(val address: String) : UserListItem()
    data class Birthday(val birthday: LocalDate) : UserListItem()

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UserListItem> =
            object : DiffUtil.ItemCallback<UserListItem>() {


                override fun areItemsTheSame(
                    oldItem: UserListItem,
                    newItem: UserListItem
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: UserListItem,
                    newItem: UserListItem
                ): Boolean =
                    oldItem == newItem
            }
    }
}