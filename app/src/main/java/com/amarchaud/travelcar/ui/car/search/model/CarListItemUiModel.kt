package com.amarchaud.travelcar.ui.car.search.model

import androidx.recyclerview.widget.DiffUtil

sealed class CarListItemUiModel {

    data object Loading : CarListItemUiModel()
    data class Car(val appCar: AppCarUiModel, val filter: String? = null) : CarListItemUiModel()
    data object Nothing : CarListItemUiModel()
    data class Error(val error: String) : CarListItemUiModel()

    companion object {
        val CAR_DIFF_CALLBACK: DiffUtil.ItemCallback<Car> =
            object : DiffUtil.ItemCallback<Car>() {

                override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean {
                    return oldItem.appCar.make == newItem.appCar.make && oldItem.appCar.model == newItem.appCar.model
                }

                override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean {
                    return oldItem == newItem
                }
            }

        val DIFF_CALLBACK: DiffUtil.ItemCallback<CarListItemUiModel> =
            object : DiffUtil.ItemCallback<CarListItemUiModel>() {

                override fun getChangePayload(
                    oldItem: CarListItemUiModel,
                    newItem: CarListItemUiModel
                ): Any? {

                    if (oldItem is Car && newItem is Car) {
                        if (oldItem.filter != newItem.filter) {
                            return true
                        }
                    }

                    return super.getChangePayload(oldItem, newItem)
                }

                override fun areItemsTheSame(
                    oldItem: CarListItemUiModel,
                    newItem: CarListItemUiModel
                ): Boolean =
                    if (oldItem is Loading && newItem is Loading) {
                        true
                    } else if (oldItem is Car && newItem is Car) {
                        oldItem.appCar.make == newItem.appCar.make && oldItem.appCar.model == newItem.appCar.model
                    } else if (oldItem is Error && newItem is Error) {
                        oldItem.error == newItem.error
                    } else {
                        false
                    }

                override fun areContentsTheSame(
                    oldItem: CarListItemUiModel,
                    newItem: CarListItemUiModel
                ): Boolean =
                    oldItem == newItem
            }
    }
}