package com.amarchaud.travelcar.ui.car.search.model

import androidx.recyclerview.widget.DiffUtil
import com.amarchaud.travelcar.domain.local.car.AppCar

sealed class CarListItem {

    object Loading : CarListItem()
    data class Car(val appCar: AppCar, val filter: String? = null) : CarListItem()
    data class Error(val error: String) : CarListItem()

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<CarListItem> =
            object : DiffUtil.ItemCallback<CarListItem>() {

                override fun getChangePayload(
                    oldItem: CarListItem,
                    newItem: CarListItem
                ): Any? {

                    if (oldItem is Car && newItem is Car) {
                        if (oldItem.filter != newItem.filter) {
                            return true
                        }
                    }

                    return super.getChangePayload(oldItem, newItem)
                }

                override fun areItemsTheSame(
                    oldItem: CarListItem,
                    newItem: CarListItem
                ): Boolean =
                    if (oldItem is Loading && newItem is Loading) {
                        oldItem == newItem
                    } else if (oldItem is Car && newItem is Car) {
                        oldItem.appCar.make == newItem.appCar.make && oldItem.appCar.model == newItem.appCar.model
                    } else if (oldItem is Error && newItem is Error) {
                        oldItem.error == newItem.error
                    } else {
                        false
                    }

                override fun areContentsTheSame(
                    oldItem: CarListItem,
                    newItem: CarListItem
                ): Boolean =
                    oldItem == newItem
            }
    }
}