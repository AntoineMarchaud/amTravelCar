package com.amarchaud.travelcar.ui.car.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.data.repository.car.CarRepository
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.ui.car.search.model.CarListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val app: Application,
    private val carRepository: CarRepository
) : AndroidViewModel(app) {

    private val _cars = MutableStateFlow<List<CarListItem>>(emptyList())
    val cars = _cars.asStateFlow()

    private var _initialCarList: List<AppCar> = emptyList()

    init {

        /**
         * First method : call getCarsFlow which emit(db), and refresh if needed
         */
        viewModelScope.launch {
            carRepository.getCarsFlow()
                .onStart {
                    _cars.value = listOf(CarListItem.Loading)
                }.collect {
                    if (it == null) {
                        _cars.value =
                            listOf(CarListItem.Error(app.getString(R.string.search_no_car_error)))
                    } else {
                        _initialCarList = it
                        _cars.value = it.map { CarListItem.Car(it) }
                    }
                }
        }


        /**
         * Second method : stick to the DB and wait for changes
         */
        /*
        viewModelScope.launch {
            appRepository.getCarsFromDbFlow()
                .onStart {
                    _cars.value = listOf(CarListItem.Loading)
                    appRepository.update()
                }
                .collect {
                    _initialCarList = it
                    _cars.value = it.map { CarListItem.Car(it) }
                }
        }*/
    }

    fun filterWithSuggestions(search: String, suggestions: List<String>? = null) {
        viewModelScope.launch {
            if (search.isEmpty()) {
                _cars.value = _initialCarList.map { CarListItem.Car(it) }
            } else {
                val filterList = _initialCarList
                    .filter { oneCar ->
                        if (suggestions.isNullOrEmpty()) {
                            oneCar.contain(search)
                        } else {
                            oneCar.contain(search) || suggestions.any { oneCar.contain(it) }
                        }
                    }

                if (filterList.isNullOrEmpty()) {
                    _cars.value = listOf(CarListItem.Nothing)
                } else {
                    _cars.value = filterList.map { CarListItem.Car(it, search) }
                }
            }
        }
    }

    /**
     * return true if word in in one of car field
     */
    private fun AppCar.contain(word: String) = make.contains(word, true)
            || model.contains(word, true)
            || equipments?.any { it.contains(word, true) } == true
            || year.toString().contains(word, true)

}