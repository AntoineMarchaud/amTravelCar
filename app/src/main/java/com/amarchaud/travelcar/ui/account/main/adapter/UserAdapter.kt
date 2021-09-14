package com.amarchaud.travelcar.ui.account.main.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.databinding.ItemAccountAddressBinding
import com.amarchaud.travelcar.databinding.ItemAccountBirthdayBinding
import com.amarchaud.travelcar.databinding.ItemAccountNameBinding
import com.amarchaud.travelcar.databinding.ItemAccountNoPhotoBinding
import com.amarchaud.travelcar.databinding.ItemAccountPhotoBinding
import com.amarchaud.travelcar.ui.account.main.models.UserListItemUiModel
import com.amarchaud.travelcar.utils.extensions.toLongDate
import com.bumptech.glide.Glide

class UserAdapter : ListAdapter<UserListItemUiModel, UserAdapter.ViewHolder>(UserListItemUiModel.DIFF_CALLBACK) {

    private enum class Type {
        PHOTO,
        NO_PHOTO,
        NAME,
        ADDRESS,
        BIRTHDAY
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UserListItemUiModel.Photo -> Type.PHOTO.ordinal
            is UserListItemUiModel.NoPhoto -> Type.NO_PHOTO.ordinal
            is UserListItemUiModel.Identity -> Type.NAME.ordinal
            is UserListItemUiModel.Address -> Type.ADDRESS.ordinal
            is UserListItemUiModel.Birthday -> Type.BIRTHDAY.ordinal
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
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
                is ViewHolder.Photo -> holder.bind(it as UserListItemUiModel.Photo)
                is ViewHolder.NoPhoto -> holder.bind(it as UserListItemUiModel.NoPhoto)
                is ViewHolder.Name -> holder.bind(it as UserListItemUiModel.Identity)
                is ViewHolder.Address -> holder.bind(it as UserListItemUiModel.Address)
                is ViewHolder.Birthday -> holder.bind(it as UserListItemUiModel.Birthday)
            }
        }
    }

    sealed class ViewHolder(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        class Photo(private val binding: ItemAccountPhotoBinding) : ViewHolder(binding) {
            fun bind(item: UserListItemUiModel.Photo) = with(binding) {
                Glide.with(binding.root.context)
                    .load(item.uri)
                    .into(binding.accountPhoto)
            }
        }

        class NoPhoto(private val binding: ItemAccountNoPhotoBinding) : ViewHolder(binding) {
            fun bind(item: UserListItemUiModel.NoPhoto) = with(binding) {
                firstLetter.text = item.firstLetter.toString()
            }
        }

        class Name(private val binding: ItemAccountNameBinding) : ViewHolder(binding) {
            fun bind(item: UserListItemUiModel.Identity) = with(binding) {
                if (item.lastName == null) {
                    accountName.text = item.firstName
                } else {
                    accountName.text = binding.root.context.getString(R.string.double_string_with_space, item.firstName, item.lastName)
                }
            }
        }

        class Address(private val binding: ItemAccountAddressBinding) : ViewHolder(binding) {
            fun bind(item: UserListItemUiModel.Address) = with(binding) {
                address.text = item.address
                goToMap.setOnClickListener {
                    val map = "https://maps.google.co.in/maps?q=${item.address}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(map))
                    binding.root.context.startActivity(intent)
                }
            }
        }

        class Birthday(private val binding: ItemAccountBirthdayBinding) : ViewHolder(binding) {
            fun bind(item: UserListItemUiModel.Birthday) = with(binding) {
                birthday.text = item.birthday.toLongDate()
            }
        }
    }
}