package com.amarchaud.travelcar.ui.account.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.databinding.*
import com.amarchaud.travelcar.ui.account.main.model.UserListItem
import com.amarchaud.travelcar.utils.toLongDate
import android.content.Intent
import android.net.Uri


class UserAdapter : ListAdapter<UserListItem, UserAdapter.ViewHolder>(UserListItem.DIFF_CALLBACK) {

    private enum class Type {
        NO_USER,
        PHOTO,
        NO_PHOTO,
        NAME,
        ADDRESS,
        BIRTHDAY
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UserListItem.NoUser -> Type.NO_USER.ordinal
            is UserListItem.Photo -> Type.PHOTO.ordinal
            is UserListItem.NoPhoto -> Type.NO_PHOTO.ordinal
            is UserListItem.Identity -> Type.NAME.ordinal
            is UserListItem.Address -> Type.ADDRESS.ordinal
            is UserListItem.Birthday -> Type.BIRTHDAY.ordinal
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Type.NO_USER.ordinal -> ViewHolder.NoUser(ItemAccountNoUserBinding.inflate(layoutInflater, parent, false))
            Type.PHOTO.ordinal -> ViewHolder.Photo(ItemAccountPhotoBinding.inflate(layoutInflater, parent, false))
            Type.NO_PHOTO.ordinal -> ViewHolder.NoPhoto(ItemAccountNoPhotoBinding.inflate(layoutInflater, parent, false))
            Type.NAME.ordinal -> ViewHolder.Name(ItemAccountNameBinding.inflate(layoutInflater, parent, false))
            Type.ADDRESS.ordinal -> ViewHolder.Address(ItemAccountAddressBinding.inflate(layoutInflater, parent, false))
            Type.BIRTHDAY.ordinal -> ViewHolder.Birthday(ItemAccountBirthdayBinding.inflate(layoutInflater, parent, false))
            else -> throw Exception("unknown viewType : $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            when (holder) {
                is ViewHolder.NoUser -> {
                }
                is ViewHolder.Photo -> holder.bind(it as UserListItem.Photo)
                is ViewHolder.NoPhoto -> holder.bind(it as UserListItem.NoPhoto)
                is ViewHolder.Name -> holder.bind(it as UserListItem.Identity)
                is ViewHolder.Address -> holder.bind(it as UserListItem.Address)
                is ViewHolder.Birthday -> holder.bind(it as UserListItem.Birthday)
            }
        }
    }

    sealed class ViewHolder(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        class NoUser(binding: ItemAccountNoUserBinding) : ViewHolder(binding)

        class Photo(private val binding: ItemAccountPhotoBinding) : ViewHolder(binding) {
            fun bind(item: UserListItem.Photo) = with(binding) {
                accountPhoto.setImageURI(item.uri)
            }
        }

        class NoPhoto(private val binding: ItemAccountNoPhotoBinding) : ViewHolder(binding) {
            fun bind(item: UserListItem.NoPhoto) = with(binding) {
                firstLetter.text = item.firstLetter.toString()
            }
        }

        class Name(private val binding: ItemAccountNameBinding) : ViewHolder(binding) {
            fun bind(item: UserListItem.Identity) = with(binding) {
                if (item.lastName == null) {
                    accountName.text = item.firstName
                } else {
                    accountName.text = binding.root.context.getString(R.string.double_string_with_space, item.firstName, item.lastName)
                }
            }
        }

        class Address(private val binding: ItemAccountAddressBinding) : ViewHolder(binding) {
            fun bind(item: UserListItem.Address) = with(binding) {
                address.text = item.address
                goToMap.setOnClickListener {
                    val map = "https://maps.google.co.in/maps?q=${item.address}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(map))
                    binding.root.context.startActivity(intent)
                }
            }
        }

        class Birthday(private val binding: ItemAccountBirthdayBinding) : ViewHolder(binding) {
            fun bind(item: UserListItem.Birthday) = with(binding) {
                birthday.text = item.birthday.toLongDate()
            }
        }
    }
}