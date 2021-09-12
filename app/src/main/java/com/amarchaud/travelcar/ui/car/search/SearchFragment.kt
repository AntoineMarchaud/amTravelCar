package com.amarchaud.travelcar.ui.car.search

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textservice.*
import android.view.textservice.SuggestionsInfo
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amarchaud.travelcar.databinding.FragmentSearchBinding
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.ui.car.detail_car.DetailCarActivity
import com.amarchaud.travelcar.ui.car.search.adapter.CarAdapter
import com.amarchaud.travelcar.ui.car.search.adapter.CarListener
import com.amarchaud.travelcar.utils.textChanges
import com.amarchaud.travelcar.utils.transition.TextColorTransition
import com.amarchaud.travelcar.utils.transition.TextSizeTransition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class SearchFragment : Fragment(), CarListener, SpellCheckerSession.SpellCheckerSessionListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchFragmentViewModel by viewModels()

    private val adapter = CarAdapter(this)

    private var _scs: SpellCheckerSession? = null

    companion object {
        const val TAG = "SearchFragment"

        const val SAVED_SEARCH = "saved_search"

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_SEARCH, binding.searchLayoutEditText.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSearchView()

        with(binding) {
            rvData.adapter = adapter

            savedInstanceState?.let {
                Log.d(TAG, "savedInstanceState")
                binding.searchLayoutEditText.setText(it.getString(SAVED_SEARCH))
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cars.collect {
                    adapter.submitList(it)
                }
            }
        }

        val tsm = getSystemService(requireContext(), TextServicesManager::class.java)
        _scs = tsm?.newSpellCheckerSession(null, Locale.getDefault(), this, true)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _scs?.close()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchView() {
        lifecycleScope.launchWhenStarted {
            with(binding) {
                searchLayoutEditText.textChanges().collectLatest {
                    if (!it.isNullOrEmpty()) {
                        if(_scs == null) {
                            viewModel.filterWithSuggestions(it.toString(), null)
                        } else {
                            _scs?.getSentenceSuggestions(arrayOf(TextInfo(it.toString())), 3)
                        }
                    } else {
                        viewModel.filterWithSuggestions(it.toString())
                    }
                }
            }
        }
    }

    override fun onCarClicked(
        appCar: AppCar,
        position: Int,
        carImage: Bitmap,
        vararg p: Pair<View, String>
    ) {

        /**
         * p0 : ImageView
         * p1 p2 p3 : TextView
         */
        val textColorTransition = TextColorTransition(requireContext())
        TransitionManager.beginDelayedTransition(binding.root, textColorTransition)

        val textSizeTransition = TextSizeTransition(requireContext())
        TransitionManager.beginDelayedTransition(binding.root, textSizeTransition)

        val bundle1 = Bundle()
        val bundle2 = Bundle()
        val bundle3 = Bundle()

        // Give initiale values to both Transitions
        textColorTransition.addExtraProperties(p[1].first as TextView, bundle1)
        textColorTransition.addExtraProperties(p[2].first as TextView, bundle2)
        textColorTransition.addExtraProperties(p[3].first as TextView, bundle3)

        textSizeTransition.addExtraProperties(p[1].first as TextView, bundle1)
        textSizeTransition.addExtraProperties(p[2].first as TextView, bundle2)
        textSizeTransition.addExtraProperties(p[3].first as TextView, bundle3)

        val intent = Intent(context, DetailCarActivity::class.java).apply {
            putExtra(DetailCarActivity.ARG_CAR, appCar)
            putExtra(DetailCarActivity.ARG_CAR_IMAGE_SAVED, carImage)
            putExtra(DetailCarActivity.ARG_NAME_TRANSITION_PICTURE, p[0].second) // unique name for the transition for each element
            putExtra(DetailCarActivity.ARG_NAME_TRANSITION_MAKE, p[1].second)
            putExtra(DetailCarActivity.ARG_NAME_TRANSITION_YEAR, p[2].second)
            putExtra(DetailCarActivity.ARG_NAME_TRANSITION_OPTIONS, p[3].second)

            putExtra(p[1].second, bundle1)
            putExtra(p[2].second, bundle2)
            putExtra(p[3].second, bundle3)
        }

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), *p)
        startActivity(intent, options.toBundle())
    }

    override fun onGetSuggestions(results: Array<out SuggestionsInfo>?) {
        Log.d(TAG, results.toString())
    }

    override fun onGetSentenceSuggestions(results: Array<out SentenceSuggestionsInfo>?) {

        if (results.isNullOrEmpty()) {
            viewModel.filterWithSuggestions(binding.searchLayoutEditText.text.toString())
            return
        }

        val suggestions = mutableListOf<String>()
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
                    suggestions.add(result.getSuggestionsInfoAt(i).getSuggestionAt(k))
                }
            }
        }

        Log.d(TAG, suggestions.toString())
        viewModel.filterWithSuggestions(binding.searchLayoutEditText.text.toString(), suggestions)
    }
}