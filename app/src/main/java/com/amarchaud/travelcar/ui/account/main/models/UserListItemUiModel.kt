package com.amarchaud.travelcar.ui.account.main.models

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import org.threeten.bp.LocalDate

sealed class UserListItemUiModel {

    data class Photo(val uri: Uri) : UserListItemUiModel()
    data class NoPhoto(val firstLetter: Char) : UserListItemUiModel()
    data class Identity(val firstName: String, val lastName : String?) : UserListItemUiModel()
    data class Address(val address: String) : UserListItemUiModel()
    data class Birthday(val birthday: LocalDate) : UserListItemUiModel()

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UserListItemUiModel> =
            object : DiffUtil.ItemCallback<UserListItemUiModel>() {


                override fun areItemsTheSame(
                    oldItem: UserListItemUiModel,
                    newItem: UserListItemUiModel
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: UserListItemUiModel,
                    newItem: UserListItemUiModel
                ): Boolean =
                    oldItem == newItem
            }
    }
}