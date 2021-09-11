package com.amarchaud.travelcar.ui.car.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.data.repository.car.AppCarRepository
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.ui.car.search.model.CarListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val app: Application,
    private val appRepository: AppCarRepository
) : AndroidViewModel(app) {

    private val _cars = MutableStateFlow<List<CarListItem>>(emptyList())
    val cars = _cars.asStateFlow()

    private var _initialCarList: List<AppCar> = emptyList()

    private var jobSearch: Job? = null

    init {

        /**
         * First method : call getCarsFlow which emit(db), and refresh if needed
         */
        viewModelScope.launch {
            appRepository.getCarsFlow()
                .onStart {
                    _cars.value = listOf(CarListItem.Loading)
                }.collect {
                    if (it == null) {
                        _cars.value =
                            listOf(CarListItem.Error(app.getString(R.string.no_car_error)))
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
        jobSearch?.cancel()
        jobSearch = viewModelScope.launch {
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

                _cars.value = filterList.map { CarListItem.Car(it, search) }
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