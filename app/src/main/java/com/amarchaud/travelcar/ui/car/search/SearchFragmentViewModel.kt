package com.amarchaud.travelcar.ui.car.search

import android.app.Application
import android.util.Log
import android.view.textservice.SentenceSuggestionsInfo
import android.view.textservice.SpellCheckerSession
import android.view.textservice.SuggestionsInfo
import android.view.textservice.TextInfo
import android.view.textservice.TextServicesManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.domain.repository.CarRepository
import com.amarchaud.travelcar.ui.car.search.SearchFragment.Companion.TAG
import com.amarchaud.travelcar.ui.car.search.model.AppCarUiModel
import com.amarchaud.travelcar.ui.car.search.model.CarListItemUiModel
import com.amarchaud.travelcar.ui.car.search.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val app: Application,
    private val carRepository: CarRepository
) : AndroidViewModel(app), SpellCheckerSession.SpellCheckerSessionListener {

    private val _carsUiModel = MutableStateFlow<List<CarListItemUiModel>>(emptyList())
    val carsUiModel = _carsUiModel.asStateFlow()

    private var _initialCarList: List<AppCarUiModel> = emptyList()

    private var _scs: SpellCheckerSession? = null
    private var _lastSearch: CharSequence? = null

    init {
        val tsm = getSystemService(app, TextServicesManager::class.java)
        _scs = tsm?.newSpellCheckerSession(null, Locale.getDefault(), this, true)

        viewModelScope.launch {
            carRepository.getCarsFlow()
                .onStart {
                    _carsUiModel.update { listOf(CarListItemUiModel.Loading) }
                }.collect {
                    if (it == null) {
                        _carsUiModel.value =
                            listOf(CarListItemUiModel.Error(app.getString(R.string.search_no_car_error)))
                    } else {
                        _initialCarList = it.map { it.toUiModel() }
                        _carsUiModel.value = it.map { CarListItemUiModel.Car(it.toUiModel()) }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _scs?.close()
    }

    /**
     * called each time user change search edit text
     */
    fun manageSearch(search: CharSequence?) {
        _lastSearch = search

        if (!search.isNullOrEmpty()) {
            if (_scs == null) {
                filterWithSuggestions(search.toString(), null)
            } else {
                _scs?.getSentenceSuggestions(arrayOf(TextInfo(search.toString())), 3)
            }
        } else {
            filterWithSuggestions("")
        }
    }

    /**
     * create list of cars with "search" priority, otherwise "suggestions"
     */
    internal fun filterWithSuggestions(search: String, suggestions: List<String>? = null) {
        viewModelScope.launch {
            if (search.isEmpty()) {
                _carsUiModel.value = _initialCarList.map { CarListItemUiModel.Car(it) }
            } else {
                val filterList = _initialCarList
                    .filter { oneCar ->
                        if (suggestions.isNullOrEmpty()) {
                            oneCar.contain(search)
                        } else {
                            oneCar.contain(search) || suggestions.any { oneCar.contain(it) }
                        }
                    }

                if (filterList.isEmpty()) {
                    _carsUiModel.value = listOf(CarListItemUiModel.Nothing)
                } else {
                    _carsUiModel.value = filterList.map { CarListItemUiModel.Car(it, search) }
                }
            }
        }
    }

    /**
     * return true if word in in one of car field
     */
    private fun AppCarUiModel.contain(word: String) = make.contains(word, true)
        || model.contains(word, true)
        || equipments?.any { it.contains(word, true) } == true
        || year.toString().contains(word, true)

    override fun onGetSuggestions(results: Array<out SuggestionsInfo>?) {
        Log.d(TAG, results.toString())
    }

    override fun onGetSentenceSuggestions(results: Array<out SentenceSuggestionsInfo>?) {
        if (results.isNullOrEmpty()) {
            filterWithSuggestions(_lastSearch.toString())
            return
        }

        val suggestions = buildList {
            for (result in results) {
                val n = result.suggestionsCount
                for (i in 0 until n) {
                    val m = result.getSuggestionsInfoAt(i).suggestionsCount
                    if (result.getSuggestionsInfoAt(i).suggestionsAttributes and
                        SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO != SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO
                    ) {
                        continue
                    }
                    for (k in 0 until m) {
                        add(result.getSuggestionsInfoAt(i).getSuggestionAt(k))
                    }
                }
            }
        }

        Log.d(TAG, suggestions.toString())
        filterWithSuggestions(_lastSearch.toString(), suggestions)
    }
}